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
import fr.sorbonne_u.sylalexcenter.ringnetwork.ports.RingNetworkInboundPort;
import fr.sorbonne_u.sylalexcenter.ringnetwork.ports.RingNetworkOutboundPort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The class <code>PerformanceController</code> implements a performance controller component
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * Each application approved by the admission controller has its own performance controller
 * component that periodically checks the application usage information (using a scheduled task every 24s)
 *
 * Usage is compared to pre-defined min and max thresholds, and based on the the difference, different upgrade or
 * downgrade scenarios are applied. The higher/lower the difference, the higher/lower the usage level
 *
 * There are 3 levels:
 * - Level 0: increase or decrease frequency (request is made directly to computer component)
 * - Level 1: add or remove cores (request is made directly to computer component)
 * - Level 2: add or remove AVM (request is made to admission controller)
 *
 * Should upgrades or downgrades fail for Levels 0 and 1, the next level upgrade or downgrade is attempted.
 *
 * Should upgrade or downgrade fail for Level 2, no further action is taken, as there is no other way to
 * upgrade or downgrade resources
 *
 * Performance Controllers are a part of a Ring Network, along with the Admission Controller
 * in order to coordinate upgrade and downgrades
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public class PerformanceController extends AbstractComponent implements
		PerformanceControllerManagementI,
		RequestDispatcherStateDataConsumerI,
		ComputerStateDataConsumerI {

	// Setup
	// -----------------------------------------------------------------
	private final int numberOfCoresToChange = 1;

	private static final int timer = 4000;

	private static final double executionTimeThresholdMinFreq = 5E9;
	private static final double executionTimeThresholdMinCore = 3E9;
	private static final double executionTimeThresholdMinAVM = 1E9;


	private static final double executionTimeThresholdMaxFreq = 1E10;
	private static final double executionTimeThresholdMaxCore = 3E10;
	private static final double executionTimeThresholdMaxAVM = 5E10;

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

	private RingNetworkInboundPort ringNetworkInboundPort;
	private RingNetworkOutboundPort ringNetworkOutboundPort;

	/**
	 * Constructor. Set up ports and interfaces
	 *
	 * @param performanceControllerURI performance controller URI
	 * @param performanceControllerManagementInboundPortURI performance controller management inbound port URI
	 * @param performanceControllerServicesInboundPortURI performance controller services inbound port URI
	 * @param appURI application URI
	 * @param requestDispatcherURI request dispatcher URI
	 * @param computersURIList list of all computer URI available
	 * @param allocationMap allocation map for avm for this application
	 * @param ringNetworkInboundPortURI inbound port for ring network
	 * @param ringNetworkOutboundPortURI outbound port for ring network
	 */
	public PerformanceController (
			String performanceControllerURI,
			String performanceControllerManagementInboundPortURI,
			String performanceControllerServicesInboundPortURI,
			String appURI,
			String requestDispatcherURI,
			ArrayList<String> computersURIList,
			HashMap<String, AllocationMap> allocationMap,
			String ringNetworkInboundPortURI,
			String ringNetworkOutboundPortURI
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

		// Ring Network
		this.ringNetworkInboundPort = new RingNetworkInboundPort(ringNetworkInboundPortURI, this);
		this.addPort(this.ringNetworkInboundPort);
		this.ringNetworkInboundPort.publishPort();

		this.ringNetworkOutboundPort = new RingNetworkOutboundPort(ringNetworkOutboundPortURI, this);
		this.addPort(this.ringNetworkOutboundPort);
		this.ringNetworkOutboundPort.publishPort();
	}

	/**
	 * Connect service in/out ports
	 *
	 * @throws ComponentStartException if error connecting ports or starting check usage scheduled task
	 */
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

	/**
	 * Connect with request dispatcher in order to receive its dynamic state
	 * @param requestDispatcherDynamicStateInboundPortUri request dispatcher dynamic state inbound port URI
	 */
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

	/**
	 * Connect with computer in order to receive its dynamic state
	 * @param computerDynamicStateDataInboundPortURIList computer dynamic state data inbound port URI
	 */
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

	/**
	 * Receive notification that an AVM was added
	 * @param avmURI new AVM URI
	 * @param allocationMap allocation map for the new AVM
	 */
	@Override
	public void notifyAVMAdded(String avmURI, AllocationMap allocationMap) {
		this.availableAVMsCount++;
		this.allocationMap.put(avmURI, allocationMap);
		this.logMessage("---> Success! New AVM added. ");
	}

	/**
	 * Receive notification that the request to add a new AVM was refused
	 * @param appURI application URI
	 */
	@Override
	public void notifyAVMAddRefused(String appURI) {
		this.logMessage("---> Request to add a new AVM was refused. ");
	}

	/**
	 * Receive notification that the request to remove an AVM was refused
	 * @param appURI application URI
	 */
	@Override
	public void notifyAVMRemoveRefused(String appURI) {
		this.logMessage("---> Request to remove an AVM was refused. ");
	}

	/**
	 * Receive notification that the request to remove an AVM was completed.
	 *
	 * Remove the old AVM from the allocation map.
	 * @param vmURI URI of removed AVM
	 * @param appURI application URI
	 */
	@Override
	public void notifyAVMRemoveComplete(String vmURI, String appURI) {
		this.availableAVMsCount--;
		this.allocationMap.remove(vmURI);
		this.logMessage("---> AVM " + vmURI + " was successfully removed. ");
	}

	/**
	 * Accept dynamic data from the request dispatcher.
	 *
	 * Every 1s, the request dispatcher pushes information on average request execution time,
	 * total number of requests submitted and total number of requests terminated
	 *
	 * Calculate the next exponential moving average based on the values.
	 * @param requestDispatcherURI URI of the request dispatcher
	 * @param currentDynamicState request dispatcher dynamic state
	 */
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

	/**
	 * Accept static data from computer
	 *
	 * @param computerURI URI of the computer sending the data.
	 * @param staticState static state of this computer.
	 */
	@Override
	public void acceptComputerStaticData(String computerURI, ComputerStaticStateI staticState) {
		//numberOfProcessors = staticState.getNumberOfProcessors();
		//numberOfCores = staticState.getNumberOfCoresPerProcessor();
	}

	/**
	 * Accept dynamic data from computer
	 *
	 * @param computerURI URI of the computer sending the data.
	 * @param currentDynamicState current dynamic state of this computer.
	 */
	@Override
	public void acceptComputerDynamicData(String computerURI, ComputerDynamicStateI currentDynamicState) {
		//currentDynamicState.getCurrentCoreFrequencies();
	}

	/**
	 * Check application usage
	 *
	 * Scheduled task runs every 24s in order to compare application usage to defined thresholds.
	 * If usage is low, apply one of the downgrade scenarios, if usage is high, apply one of the
	 * upgrade scenarios.
	 *
	 */
	private void checkUsage() {
		this.scheduleTask(
			new AbstractComponent.AbstractTask() {

				@Override
				public void run() {
					try {
						int usageHigh = isUsageHigh();
						if (usageHigh >= 0) applyUpgrades(usageHigh);

						int usageLow = isUsageLow();
						if (usageLow >= 0) applyDowngrades(usageLow);

						checkUsage();
					} catch (Exception e) {
						throw new RuntimeException (e);
					}
				}

			}, timer*6, TimeUnit.MILLISECONDS);
	}

	/**
	 * Check usage level
	 * @return -1: usage is not high
	 *          0: usage is moderately high, apply scenario level 0 (increase frequency)
	 *          1: usage is very high, apply scenario level 1 (add cores)
	 *          2: usage is extremely high, apply scenario level 2 (add AVM)
	 */
	private int isUsageHigh() {
		if (this.exponentialAverageExecutionTime > executionTimeThresholdMaxAVM) return 2;
		if (this.exponentialAverageExecutionTime > executionTimeThresholdMaxCore) return 1;
		if (this.exponentialAverageExecutionTime > executionTimeThresholdMaxFreq) return 0;

		return -1;
	}

	/**
	 * Check usage level
	 * @return -1: usage is not low
	 *          0: usage is moderately low, apply scenario level 0 (increase frequency)
	 *          1: usage is very low, apply scenario level 1 (add cores)
	 *          2: usage is extremely low, apply scenario level 2 (add AVM)
	 */
	private int isUsageLow() {
		if (this.exponentialAverageExecutionTime < executionTimeThresholdMinAVM) return 2;
		if (this.exponentialAverageExecutionTime < executionTimeThresholdMinCore) return 1;
		if (this.exponentialAverageExecutionTime < executionTimeThresholdMinFreq) return 0;

		return -1;
	}

	/**
	 * Apply different upgrade scenarios based on usage:
	 *           level 0, increase frequency
	 *           level 1, add cores
	 *           level 2, add AVM
	 *
	 * @param usage usage level
	 */
	private void applyUpgrades(int usage) throws Exception {
		if (upgradeRequestInProgress || downgradeRequestInProgress) return;

		this.logMessage("Upgrading resources");

		switch (usage) {
			case 0:
				// Frequency change
				this.logMessage("---> Trying to increase frequency ");
				int frequency = increaseFrequency();
				if (frequency > 0) {
					this.logMessage("---> Increased frequency of " + frequency + " cores");
					return;
				}
				this.logMessage("---> Couldn't increase frequency ");
			case 1:
				// Core Change
				this.logMessage("---> Trying to add cores ");
				int cores = addCores();
				if (cores > 0) {
					this.logMessage("---> Added " + cores + " cores to AVM");
					return;
				}
				this.logMessage("---> Couldn't add cores to any AVM ");
			case 2:
				// Add AVM
				this.logMessage("---> Trying to add an AVM ");
				addAVM();
		}
	}

	/**
	 * Apply different downgrade scenarios based on usage:
	 *           level 0, decrease frequency
	 *           level 1, remove cores
	 *           level 2, remove AVM
	 *
	 * @param usage usage level
	 */
	private void applyDowngrades(int usage) throws Exception {
		if (upgradeRequestInProgress || downgradeRequestInProgress) return;

		this.logMessage("Downgrading resources");

		switch (usage) {
			case 0:
				// Frequency change
				this.logMessage("---> Trying to decrease frequency ");
				int frequency = decreaseFrequency();
				if (frequency > 0) {
					this.logMessage("---> Decreased frequency of " + frequency + " cores");
					return;
				}
				this.logMessage("---> Couldn't decrease frequency ");
			case 1:
				// Core Change
				this.logMessage("---> Trying to remove cores ");
				int cores = removeCores();
				if (cores > 0) {
					this.logMessage("---> Removed " + cores + " cores from AVM");
					return;
				}
				this.logMessage("---> Couldn't remove cores from any AVM ");
			case 2:
				// Remove AVM
				this.logMessage("---> Trying to remove an AVM ");
				removeAVM();
		}
	}

	/**
	 * increase frequency
	 * @return 0 if not possible to increase, number of cores for which we increased frequency otherwise
	 */
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

	/**
	 * decrease frequency
	 * @return 0 if not possible to decrease, number of cores for which we decreased frequency otherwise
	 */
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

	/**
	 * Add cores
	 * @return 0 if not possible to add, number of cores added otherwise
	 */
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

	/**
	 * Remove cores
	 * @return 0 if not possible to remove, number of cores removed otherwise
	 */
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

	/**
	 * Send request to admission controller to add a new AVM
	 */
	private void addAVM() throws Exception {
		this.pcsop.requestAddAVM(this.appURI, this.performanceControllerURI);
	}

	/**
	 * Send request to admission controller to remove an AVM, if there are more than 1 available, or
	 * refuse AVM remove request otherwise.
	 */
	private void removeAVM() throws Exception {
		if (this.availableAVMsCount > 1) {
			this.pcsop.requestRemoveAVM(this.appURI, this.performanceControllerURI);
		} else {
			this.logMessage("---> Can't remove any AVMs ");
		}
	}

}
