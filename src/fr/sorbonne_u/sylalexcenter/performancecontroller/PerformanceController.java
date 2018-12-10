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
import fr.sorbonne_u.datacenter.interfaces.ControlledDataRequiredI;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.AllocationMap;
import fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces.PerformanceControllerManagementI;
import fr.sorbonne_u.sylalexcenter.performancecontroller.ports.PerformanceControllerManagementInboundPort;
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

	private static final int timer = 4000;

	private static final int queueThresholdMax = 15;
	private static final double executionTimeThresholdMax = 1.7E10;

	private static final int queueThresholdMin = 5;
	private static final double executionTimeThresholdMin = 5E9;

	private String requestDispatcherURI;
	private String appURI;

	private int availableAVMsCount;
	private double exponentialAverageExecutionTime;
	private int totalRequestSubmitted;
	private int totalRequestTerminated;

	private HashMap<String, AllocationMap> allocationMap;

	private RequestDispatcherDynamicStateDataOutboundPort rddsdop;
	private ArrayList<ComputerDynamicStateDataOutboundPort> cdsdopList;

	public PerformanceController (
			String performanceControllerURI,
			String appURI,
			String requestDispatcherURI,
			String performanceControllerManagementInboundPortURI,
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

		this.allocationMap = new HashMap<>(allocationMap);

		this.addOfferedInterface(PerformanceControllerManagementI.class);
		PerformanceControllerManagementInboundPort pcmip = new PerformanceControllerManagementInboundPort(performanceControllerManagementInboundPortURI, this);
		this.addPort(pcmip);
		pcmip.publishPort();

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
		this.tracer.setRelativePosition(3, 0);
	}

	@Override
	public void start() throws ComponentStartException {
		this.toggleTracing();
		this.toggleLogging();

		super.start();

		try {
			checkUsage();
		} catch (Exception e) {
			throw new RuntimeException (e);
		}
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
		this.logMessage("Average execution time " + this.appURI + " with "
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

				}, timer*7, TimeUnit.MILLISECONDS);
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

	private void applyUpgrades() {
		this.logMessage("Will apply upgrades");

		int frequency = increaseFrequency();

		if (frequency > 0) {
			this.logMessage("---> Increased frequency of " + frequency + " cores");
		} else {
			this.logMessage("---> Couldn't increase frequency ");
		}

	}

	private void applyDowngrades() {
		this.logMessage("Will apply downgrades");

		int frequency = decreaseFrequency();

		if (frequency > 0) {
			this.logMessage("---> Decreased frequency of " + frequency + " cores");
		} else {
			this.logMessage("---> Couldn't decrease frequency ");
		}
	}

	private int increaseFrequency() {
		int num = 0;

		// try to increase frequency for each core allocated to each AVM
		for (Map.Entry<String, AllocationMap> entry : allocationMap.entrySet()) {
			AllocationMap value = entry.getValue();

			AllocatedCore[] allocatedCores = value.getAllocatedCores();

			for (AllocatedCore allocatedCore : allocatedCores) {
				try {
					if (value.getCsop().increaseFrequency(allocatedCore.coreNo, allocatedCore.processorURI)) {
						num++;
					}
				} catch (Exception e) {
					throw new RuntimeException("Couldn't increase frequency of " + allocatedCore.processorURI + " " + e);
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
					if (value.getCsop().decreaseFrequency(allocatedCore.coreNo, allocatedCore.processorURI)) {
						num++;
					}
				} catch (Exception e) {
					throw new RuntimeException("Couldn't decrease frequency " + allocatedCore.processorURI + " " + e);
				}
			}
		}
		return num;
	}
}
