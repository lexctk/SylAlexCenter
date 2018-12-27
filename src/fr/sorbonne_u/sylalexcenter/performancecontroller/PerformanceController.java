package fr.sorbonne_u.sylalexcenter.performancecontroller;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.datacenter.connectors.ControlledDataConnector;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerStateDataConsumerI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.sorbonne_u.datacenter.interfaces.ControlledDataRequiredI;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;
import fr.sorbonne_u.sylalexcenter.performancecontroller.connectors.PerformanceControllerServicesConnector;
import fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces.PerformanceControllerManagementI;
import fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces.PerformanceControllerServicesI;
import fr.sorbonne_u.sylalexcenter.performancecontroller.ports.PerformanceControllerManagementInboundPort;
import fr.sorbonne_u.sylalexcenter.performancecontroller.ports.PerformanceControllerServicesOutboundPort;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherStateDataConsumerI;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.ports.RequestDispatcherDynamicStateDataOutboundPort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PerformanceController extends AbstractComponent implements
		PerformanceControllerManagementI,
		RequestDispatcherStateDataConsumerI,
		ComputerStateDataConsumerI {

	// Setup
	// -----------------------------------------------------------------
	private final int numberOfCoresToChange = 2;

	private static final int timer = 4000;

	private static final int queueThresholdMax = 20;
	private static final double executionTimeThresholdMax = 3E10;

	private static final int queueThresholdMin = 5;
	private static final double executionTimeThresholdMin = 3E9;

	// Component info
	// -----------------------------------------------------------------
	private String requestDispatcherURI;
	private String appURI;
	private String performanceControllerURI;

	private PerformanceControllerServicesOutboundPort pcsop;
	private String performanceControllerServicesInboundPortURI;

	private RequestDispatcherDynamicStateDataOutboundPort rddsdop;
	private ArrayList<ComputerDynamicStateDataOutboundPort> cdsdopList;

	// Statistics
	// -----------------------------------------------------------------
	private int availableAVMsCount;
	private double exponentialAverageExecutionTime;
	private int totalRequestSubmitted;
	private int totalRequestTerminated;

	private boolean upgradeRequestInProgress;
	private boolean downgradeRequestInProgress;

	// Allocation map for the AVMs.
	// -----------------------------------------------------------------
	private HashMap<String, AllocationMap> allocationMap;

	public PerformanceController (
			String performanceControllerURI,
			String performanceControllerManagementInboundPortURI,
			String performanceControllerServicesInboundPortURI,
			String appURI,
			String requestDispatcherURI,
			ArrayList<String> computersURIList,
			HashMap<String, AllocationMap> allocationMap
	) throws Exception {
		super(performanceControllerURI, 1, 1);

		this.availableAVMsCount = 0;
		this.exponentialAverageExecutionTime = 0;
		this.totalRequestSubmitted = 0;
		this.totalRequestTerminated = 0;

		this.appURI = appURI;

		this.requestDispatcherURI = requestDispatcherURI;

		this.performanceControllerURI = performanceControllerURI;

		this.allocationMap = new HashMap<>(allocationMap);

		this.addOfferedInterface(PerformanceControllerManagementI.class);
		PerformanceControllerManagementInboundPort pcmip = new PerformanceControllerManagementInboundPort(performanceControllerManagementInboundPortURI, this);
		this.addPort(pcmip);
		pcmip.publishPort();

		this.performanceControllerServicesInboundPortURI = performanceControllerServicesInboundPortURI;

		this.addRequiredInterface(PerformanceControllerServicesI.class);
		this.pcsop = new PerformanceControllerServicesOutboundPort(this);
		this.addPort(this.pcsop);
		pcsop.publishPort();

		this.addRequiredInterface(DataRequiredI.PullI.class);
		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class);

		this.rddsdop = new RequestDispatcherDynamicStateDataOutboundPort(this, requestDispatcherURI);
		this.addPort(this.rddsdop);
		this.rddsdop.publishPort();

		this.cdsdopList = new ArrayList<>();
		for (int i = 0; i < computersURIList.size(); i++) {
			this.cdsdopList.add(new ComputerDynamicStateDataOutboundPort(this, computersURIList.get(i)));
			this.addPort(this.cdsdopList.get(i));
			this.cdsdopList.get(i).publishPort();
		}

		this.tracer.setTitle(performanceControllerURI);
		this.tracer.setRelativePosition(2, 1);

		this.upgradeRequestInProgress = false;
		this.downgradeRequestInProgress = false;
	}

	@Override
	public void start() throws ComponentStartException {
		this.toggleTracing();
		this.toggleLogging();

		try {
			this.doPortConnection(this.pcsop.getPortURI(),
					this.performanceControllerServicesInboundPortURI,
					PerformanceControllerServicesConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException ("Error connecting performance controller service ports " + e);
		}

		try {
			checkUsage();
		} catch (Exception e) {
			throw new RuntimeException (e);
		}
		super.start();
	}

	@Override
	public void doConnectionWithRequestDispatcherForDynamicState (String requestDispatcherDynamicStateInboundPortUri) throws Exception {
		this.doPortConnection(
				this.rddsdop.getPortURI(),
				requestDispatcherDynamicStateInboundPortUri,
				ControlledDataConnector.class.getCanonicalName());
		try {
			this.rddsdop.startUnlimitedPushing(timer);

		} catch (Exception e) {
			throw new ComponentStartException("Unable to start pushing dynamic data from the request dispatcher " + e);
		}
	}

	@Override
	public void doConnectionWithComputerForDynamicState (ArrayList<String> computerDynamicStateDataInboundPortURIList) throws Exception {

		for (int i = 0; i < cdsdopList.size(); i++) {
			this.doPortConnection(
					this.cdsdopList.get(i).getPortURI(),
					computerDynamicStateDataInboundPortURIList.get(i),
					ControlledDataConnector.class.getCanonicalName());
			try {
				this.cdsdopList.get(i).startUnlimitedPushing(timer);

			} catch (Exception e) {
				throw new ComponentStartException("Unable to start pushing dynamic data from the computer " + e);
			}
		}
	}

	@Override
	public void notifyAVMAdded(String avmURI, AllocationMap allocationMap) {
		this.availableAVMsCount++;
		this.allocationMap.put(avmURI, allocationMap);
		this.logMessage("---> Success! New AVM added. ");
	}

	@Override
	public void notifyAVMAddRefused(String appURI) {
		this.logMessage("---> Request to add a new AVM was refused. ");
	}

	@Override
	public void notifyAVMRemoveRefused(String appURI) {
		this.logMessage("---> Request to remove an AVM was refused. ");
	}

	@Override
	public void notifyAVMRemoveComplete(String vmURI, String appURI) {
		this.availableAVMsCount--;
		this.allocationMap.remove(vmURI);
		this.logMessage("---> AVM " + vmURI + " was successfully removed. ");
	}

	@Override
	public synchronized void acceptRequestDispatcherDynamicData (String requestDispatcherURI, RequestDispatcherDynamicStateI currentDynamicState) {
		if (!requestDispatcherURI.equals(this.requestDispatcherURI)) return;

		this.availableAVMsCount = currentDynamicState.getAvailableAVMsCount();
		this.exponentialAverageExecutionTime = currentDynamicState.getExponentialAverageExecutionTime();
		this.totalRequestSubmitted = currentDynamicState.getTotalRequestSubmitted();
		this.totalRequestTerminated = currentDynamicState.getTotalRequestTerminated();

		int queue = this.totalRequestSubmitted - this.totalRequestTerminated;
		int sum = 0;

		for (Map.Entry<String, AllocationMap> entry : allocationMap.entrySet()) {
			sum += entry.getValue().getNumberOfCoresPerAVM();
		}
		this.logMessage("Avg exec time " + this.appURI + " with "
				+ sum + " cores and "
				+ this.availableAVMsCount +  " AVMs: "
				+ this.exponentialAverageExecutionTime + " "
				+ " queue size " + queue);
	}

	@Override
	public void acceptComputerStaticData(String computerURI, ComputerStaticStateI staticState) {
		//numberOfProcessors = staticState.getNumberOfProcessors();
		//numberOfCores = staticState.getNumberOfCoresPerProcessor();
	}

	@Override
	public void acceptComputerDynamicData(String computerURI, ComputerDynamicStateI currentDynamicState) {
		//currentDynamicState.getCurrentCoreFrequencies();
	}

	private void checkUsage() {
		this.scheduleTask(
				new AbstractComponent.AbstractTask() {

					@Override
					public void run() {
						try {
							if (isUsageHigh()) applyUpgrades();
							if (isUsageLow()) applyDowngrades();

							checkUsage();
						} catch (Exception e) {
							throw new RuntimeException (e);
						}
					}

				}, timer*6, TimeUnit.MILLISECONDS);
	}

	private boolean isUsageHigh() {
		int queue = this.totalRequestSubmitted - this.totalRequestTerminated;

		return (this.exponentialAverageExecutionTime > executionTimeThresholdMax) &&
				(queue > queueThresholdMax);
	}

	private boolean isUsageLow() {
		int queue = this.totalRequestSubmitted - this.totalRequestTerminated;

		return (this.exponentialAverageExecutionTime < executionTimeThresholdMin) &&
				(queue < queueThresholdMin);
	}

	private void applyUpgrades() throws Exception {
		if (upgradeRequestInProgress) return;

		this.logMessage("Upgrading resources");

		// Frequency change
		this.logMessage("---> Trying to increase frequency ");
		int frequency = increaseFrequency();
		if (frequency > 0) {
			this.logMessage("---> Increased frequency of " + frequency + " cores");
			return;
		}
		this.logMessage("---> Couldn't increase frequency ");

		// Core Change
		this.logMessage("---> Trying to add cores ");
		int cores = addCores();
		if (cores > 0) {
			this.logMessage("---> Added " + cores + " cores to AVM");
			return;
		}
		this.logMessage("---> Couldn't add cores to any AVM ");

		// Add AVM
		this.logMessage("---> Trying to add an AVM ");
		addAVM();
	}

	private void applyDowngrades() throws Exception {
		if (downgradeRequestInProgress) return;

		this.logMessage("Downgrading resources");

		// Frequency change
		this.logMessage("---> Trying to decrease frequency ");
		int frequency = decreaseFrequency();
		if (frequency > 0) {
			this.logMessage("---> Decreased frequency of " + frequency + " cores");
			return;
		}
		this.logMessage("---> Couldn't decrease frequency ");

		// Core Change
		this.logMessage("---> Trying to remove cores ");
		int cores = removeCores();
		if (cores > 0) {
			this.logMessage("---> Removed " + cores + " cores from AVM");
			return;
		}
		this.logMessage("---> Couldn't remove cores from any AVM ");

		// Remove AVM
		this.logMessage("---> Trying to remove an AVM ");
		removeAVM();
	}

	private int increaseFrequency() {
		int num = 0;

		// try to increase frequency for each core allocated to each AVM
		for (Map.Entry<String, AllocationMap> entry : allocationMap.entrySet()) {
			AllocationMap value = entry.getValue();

			AllocatedCore[] allocatedCores = value.getAllocatedCores();

			for (AllocatedCore allocatedCore : allocatedCores) {
				try {
					if (value.getCsop().increaseFrequency(allocatedCore.coreNo, allocatedCore.processorNo)) {
						num++;
					}
				} catch (Exception e) {
					throw new RuntimeException("Couldn't increase frequency of " + allocatedCore.processorNo + " " + e);
				}
			}
		}
		return num;
	}

	private int decreaseFrequency() {
		int num = 0;

		// try to decrease frequency for each core allocated to each AVM
		for (Map.Entry<String, AllocationMap> entry : allocationMap.entrySet()) {
			AllocationMap value = entry.getValue();

			AllocatedCore[] allocatedCores = value.getAllocatedCores();

			for (AllocatedCore allocatedCore : allocatedCores) {
				try {
					if (value.getCsop().decreaseFrequency(allocatedCore.coreNo, allocatedCore.processorNo)) {
						num++;
					}
				} catch (Exception e) {
					throw new RuntimeException("Couldn't decrease frequency " + allocatedCore.processorNo + " " + e);
				}
			}
		}
		return num;
	}

	private int addCores() throws Exception {
		int num = 0;

		for (Map.Entry<String, AllocationMap> entry : allocationMap.entrySet()) {
			AllocationMap value = entry.getValue();

			ComputerServicesOutboundPort csop = value.getCsop();
			AllocatedCore[] allocatedNewCores = csop.allocateCores(this.numberOfCoresToChange);
			num = allocatedNewCores.length;

			if (num > 0) {
				// Allocate cores to AVM
				this.pcsop.requestAddCores(entry.getKey(), allocatedNewCores);

				entry.getValue().addNewCores(allocatedNewCores);
			}
		}
		return num;
	}

	private int removeCores() throws Exception {

		for (Map.Entry<String, AllocationMap> entry : allocationMap.entrySet()) {
			AllocationMap value = entry.getValue();


			AllocatedCore[] allocatedCores = value.getAllocatedCores();
			if (allocatedCores.length - this.numberOfCoresToChange > 0) {

				AllocatedCore[] removeCores = new AllocatedCore[this.numberOfCoresToChange];

				for (int i = 0; i<this.numberOfCoresToChange; i++) {
					removeCores[i] = allocatedCores[allocatedCores.length-1-i];
				}
				ComputerServicesOutboundPort csop = value.getCsop();
				csop.releaseCores(removeCores);

				// Notify admission controller
				this.pcsop.requestRemoveCores(entry.getKey(), removeCores);

				entry.getValue().removeCores(this.numberOfCoresToChange);

				return this.numberOfCoresToChange;
			}
		}
		return 0;
	}

	private void addAVM() throws Exception {
		this.pcsop.requestAddAVM(this.appURI, this.performanceControllerURI);
	}

	private void removeAVM() throws Exception {
		if (this.availableAVMsCount > 1) {
			this.pcsop.requestRemoveAVM(this.appURI, this.performanceControllerURI);
		} else {
			this.logMessage("---> Can't remove any AVMs ");
		}
	}

}
