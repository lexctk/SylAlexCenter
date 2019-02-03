package fr.sorbonne_u.sylalexcenter.admissioncontroller;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.connectors.DataConnector;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.components.pre.dcc.connectors.DynamicComponentCreationConnector;
import fr.sorbonne_u.components.pre.dcc.interfaces.DynamicComponentCreationI;
import fr.sorbonne_u.components.pre.dcc.ports.DynamicComponentCreationOutboundPort;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
import fr.sorbonne_u.datacenter.interfaces.ControlledDataRequiredI;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.sorbonne_u.datacenter.software.connectors.RequestNotificationConnector;
import fr.sorbonne_u.datacenter.software.connectors.RequestSubmissionConnector;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;
import fr.sorbonne_u.sylalexcenter.application.connectors.ApplicationManagementConnector;
import fr.sorbonne_u.sylalexcenter.application.connectors.ApplicationNotificationConnector;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationManagementI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationNotificationI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationSubmissionHandlerI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationSubmissionI;
import fr.sorbonne_u.sylalexcenter.application.ports.ApplicationManagementOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.ports.ApplicationNotificationOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.ports.ApplicationSubmissionInboundPort;
import fr.sorbonne_u.sylalexcenter.performancecontroller.PerformanceController;
import fr.sorbonne_u.sylalexcenter.performancecontroller.connectors.PerformanceControllerManagementConnector;
import fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces.PerformanceControllerManagementI;
import fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces.PerformanceControllerServicesHandlerI;
import fr.sorbonne_u.sylalexcenter.performancecontroller.ports.PerformanceControllerManagementOutboundPort;
import fr.sorbonne_u.sylalexcenter.performancecontroller.ports.PerformanceControllerServicesInboundPort;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.connectors.RequestDispatcherManagementConnector;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherServicesHandlerI;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.ports.RequestDispatcherManagementOutboundPort;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.ports.RequestDispatcherServicesInboundPort;
import fr.sorbonne_u.sylalexcenter.ringnetwork.RingNetwork;
import fr.sorbonne_u.sylalexcenter.ringnetwork.connectors.RingNetworkConnector;
import fr.sorbonne_u.sylalexcenter.ringnetwork.ports.RingNetworkInboundPort;
import fr.sorbonne_u.sylalexcenter.ringnetwork.ports.RingNetworkOutboundPort;
import fr.sorbonne_u.sylalexcenter.ringnetwork.utils.AvmInformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 *
 * The admission controller receives all application admission requests, and if enough resources are
 * available, deploys request generator for the application, deploys required number of AVM, a request
 * dispatcher and a performance controller for the application.
 *
 * Admission controller uses dynamic deployment for all components, and reflection port for dynamic
 * port connections between components.
 *
 * Admission controller also handles requests involving AVMs from the performance controllers
 * (adding new AVM and removing AVM), and is notified when performance controllers add or remove cores in order
 * to update its own allocation map information
 *
 * Admission controller maintains hash maps for each application:
 * Application URI -> ApplicationManagementInboundPortURI, ApplicationManagementOutboundPort
 *                 -> ApplicationNotificationInboundPortURI, ApplicationNotificationOutboundPort
 *                 -> Request Dispatcher URI
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public class AdmissionController extends AbstractComponent 
	implements ApplicationSubmissionHandlerI, PerformanceControllerServicesHandlerI, RequestDispatcherServicesHandlerI {
	
	private static final String dynamicComponentCreationInboundPortURI = "";
	
	private static final int timer = 1000;
	
	private final int numberOfComputers;
	private final int numberOfApps;
	private final int numberOfCoresPerAVM;

	/**
	 * Array Lists of URIs for all computers available (both multi JVM and mono JVM)
	 */
	private ArrayList<String> computersURIList;
	private ArrayList<String> computerServicesInboundPortURIList;
	private ArrayList<String> computerStaticStateDataInboundPortURIList;
	private ArrayList<String> computerDynamicStateDataInboundPortURIList;
	
	private ArrayList<ComputerServicesOutboundPort> csopList;
	private ArrayList<ComputerStaticStateDataOutboundPort> cssdopList;

	/**
	 * Application URI -> Application Management Inbound Port URI
	 */
	private HashMap<String,String> applicationManagementInboundPortURIMap;

	/**
	 * Application URI -> Application Notification Inbound Port URI
	 */
	private HashMap<String,String> applicationNotificationInboundPortURIMap;

	/**
	 * Application URI -> Application Management Outbound Port
	 */
	private HashMap<String,ApplicationManagementOutboundPort> amopMap;

	/**
	 * Application URI -> Application Notification Outbound Port
	 */
	private HashMap<String,ApplicationNotificationOutboundPort> anopMap;

	/**
	 * All information/requests sent to Performance Controller are sent via the management outbound port
	 *
	 * Performance Controller URI -> Performance Controller Management Outbound Port
	 */
	private HashMap<String, PerformanceControllerManagementOutboundPort> pcmopMap;

	/**
	 * Request Dispatcher URI -> Request Dispatcher Management Outbound Port
	 */
	private HashMap<String, RequestDispatcherManagementOutboundPort> rdmopMap;

	/**
	 * VM URI -> Application VM Management Outbound Port
	 */
	private HashMap<String, ApplicationVMManagementOutboundPort> avmopMap;

	/**
	 * Each application has its own request dispatcher
	 *
	 * Application URI -> Request Dispatcher URU
	 */
	private HashMap<String, String> rdURIMap;

	private DynamicComponentCreationOutboundPort dccop;

	private ReflectionOutboundPort rop;

	private RingNetwork ringNetwork;
	private RingNetworkInboundPort ringNetworkInboundPort;
	private RingNetworkOutboundPort ringNetworkOutboundPort;

	private int ringNetworkSize;
	private ArrayList<AvmInformation> ringNetworkAVM;
	private double ringNetworkMessageTime;

	/**
	 *
	 * @param computersURIList all computer URIs
	 * @param computerServicesInboundPortURIList all computer services inbound ports
	 * @param computerStaticStateDataInboundPortURIList all computer static state data inbound ports
	 * @param computerDynamicStateDataInboundPortURIList all computer dynamic state data inbound ports
	 * @param appsURIList list of all application URIs
	 * @param applicationManagementInboundPortURIList application management inbound ports
	 * @param applicationSubmissionInboundPortURIList application submission inbound ports
	 * @param applicationNotificationInboundPortURIList application notification inbound ports
	 * @param numberOfCoresPerAVM number of cores to remove/add per AVM
	 */
	public AdmissionController(
			ArrayList<String> computersURIList,
			ArrayList<String> computerServicesInboundPortURIList,
			ArrayList<String> computerStaticStateDataInboundPortURIList,
			ArrayList<String> computerDynamicStateDataInboundPortURIList,
			ArrayList<String> appsURIList,
			ArrayList<String> applicationManagementInboundPortURIList,
			ArrayList<String> applicationSubmissionInboundPortURIList,
			ArrayList<String> applicationNotificationInboundPortURIList,
			int numberOfCoresPerAVM) throws Exception {
		
		super(1, 1);
		
		assert computersURIList != null && computersURIList.size() > 0;
		assert computerServicesInboundPortURIList != null && computerServicesInboundPortURIList.size() > 0;	

		assert appsURIList != null && appsURIList.size() > 0;
		assert applicationManagementInboundPortURIList != null && applicationManagementInboundPortURIList.size() > 0;
		assert applicationSubmissionInboundPortURIList != null && applicationSubmissionInboundPortURIList.size() > 0;
		assert applicationNotificationInboundPortURIList != null && applicationNotificationInboundPortURIList.size() > 0;

		assert numberOfCoresPerAVM > 0;
			
		this.numberOfComputers = computersURIList.size();
		this.numberOfApps = appsURIList.size();
		this.numberOfCoresPerAVM = numberOfCoresPerAVM;
		
		// Computers
		this.computersURIList = new ArrayList<>();
		this.computerServicesInboundPortURIList = new ArrayList<>();
		this.computerStaticStateDataInboundPortURIList = new ArrayList<>();
		this.computerDynamicStateDataInboundPortURIList = new ArrayList<>();

		// Computer Ports
		this.csopList = new ArrayList<>();
		this.cssdopList = new ArrayList<>();

		this.addRequiredInterface(ComputerServicesI.class);
		this.addOfferedInterface(DataRequiredI.PushI.class);
		this.addRequiredInterface(DataRequiredI.PullI.class);
		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class);
		
		for (int i = 0; i < numberOfComputers; i++) {
			this.computerServicesInboundPortURIList.add(computerServicesInboundPortURIList.get(i));
			this.computerStaticStateDataInboundPortURIList.add(computerStaticStateDataInboundPortURIList.get(i));
			this.computerDynamicStateDataInboundPortURIList.add(computerDynamicStateDataInboundPortURIList.get(i));

			this.csopList.add(new ComputerServicesOutboundPort(this));
			this.addPort(this.csopList.get(i));
			this.csopList.get(i).publishPort();
			
			this.cssdopList.add(new ComputerStaticStateDataOutboundPort(this, computersURIList.get(i)));
			this.addPort(this.cssdopList.get(i));
			this.cssdopList.get(i).publishPort();

			this.computersURIList.add(computersURIList.get(i));
		}		
		
		// Application Ports
		this.applicationManagementInboundPortURIMap = new HashMap<>();
		this.amopMap = new HashMap<>();

		HashMap<String, ApplicationSubmissionInboundPort> asipMap = new HashMap<>();
		
		this.applicationNotificationInboundPortURIMap = new HashMap<>();
		this.anopMap = new HashMap<>();
		
		this.addRequiredInterface(ApplicationManagementI.class);
		this.addOfferedInterface(ApplicationSubmissionI.class);
		this.addRequiredInterface(ApplicationNotificationI.class);
		
		for (int i = 0; i < numberOfApps; i++) {
			this.applicationManagementInboundPortURIMap.put(appsURIList.get(i), applicationManagementInboundPortURIList.get(i));
			
			this.amopMap.put(appsURIList.get(i), new ApplicationManagementOutboundPort(this));		
			this.addPort(this.amopMap.get(appsURIList.get(i)));
			this.amopMap.get(appsURIList.get(i)).publishPort();	
			
			asipMap.put(appsURIList.get(i), new ApplicationSubmissionInboundPort(applicationSubmissionInboundPortURIList.get(i), this));
			this.addPort(asipMap.get(appsURIList.get(i)));
			asipMap.get(appsURIList.get(i)).publishPort();
			
			this.applicationNotificationInboundPortURIMap.put(appsURIList.get(i), applicationNotificationInboundPortURIList.get(i));
			
			this.anopMap.put(appsURIList.get(i), new ApplicationNotificationOutboundPort(this));
			this.addPort(this.anopMap.get(appsURIList.get(i)));
			this.anopMap.get(appsURIList.get(i)).publishPort();	
		}													
		
		// create outbound port for the Dynamic Component Creator
		this.addRequiredInterface(DynamicComponentCreationI.class);
		this.dccop = new DynamicComponentCreationOutboundPort(this);
		this.addPort(this.dccop);
		this.dccop.publishPort();

		this.addRequiredInterface(PerformanceControllerManagementI.class);
		this.pcmopMap = new HashMap<>();
		this.rdmopMap = new HashMap<>();
		this.avmopMap = new HashMap<>();

		this.rdURIMap = new HashMap<>();

		rop = new ReflectionOutboundPort(this);
		addPort(rop);
		rop.publishPort();
		
		this.tracer.setTitle("Admission Controller");
		this.tracer.setRelativePosition(2, 0);

		// Ring Network
		this.ringNetworkInboundPort = new RingNetworkInboundPort("ac-ringnip", this);
		this.addPort(this.ringNetworkInboundPort);
		this.ringNetworkInboundPort.publishPort();

		this.ringNetworkOutboundPort = new RingNetworkOutboundPort("ac-ringnop", this);
		this.addPort(this.ringNetworkOutboundPort);
		this.ringNetworkOutboundPort.publishPort();

		this.ringNetwork = new RingNetwork();
		this.ringNetwork.setRingInboundPortURI("ac-ringnip");
		this.ringNetwork.setRingOutboundPortURI("ac-ringnop");
		this.ringNetworkAVM = new ArrayList<>();
		this.ringNetworkSize = 0;

		assert this.csopList !=null;
		assert this.cssdopList !=null;
		assert this.anopMap != null;
	}
	
	/**
	 * Connect computer outbound ports to inbound URIs.
	 * Connect application outbound ports to application inbound URI.
	 * Connect dynamic component creator outbound port to inbound URI.
	 */
	@Override
	public void start() throws ComponentStartException {
		try {
			for (int i = 0; i < numberOfComputers; i++) {
				this.doPortConnection(this.csopList.get(i).getPortURI(), this.computerServicesInboundPortURIList.get(i),
						ComputerServicesConnector.class.getCanonicalName());

				this.doPortConnection(this.cssdopList.get(i).getPortURI(), this.computerStaticStateDataInboundPortURIList.get(i),
						DataConnector.class.getCanonicalName());
			}
			
			for (HashMap.Entry<String, ApplicationManagementOutboundPort> entry : amopMap.entrySet()) {
				this.doPortConnection(entry.getValue().getPortURI(), 
						applicationManagementInboundPortURIMap.get(entry.getKey()),
						ApplicationManagementConnector.class.getCanonicalName());
			}
			
			for (HashMap.Entry<String, ApplicationNotificationOutboundPort> entry : anopMap.entrySet()) {
				this.doPortConnection(entry.getValue().getPortURI(), 
						applicationNotificationInboundPortURIMap.get(entry.getKey()),
						ApplicationNotificationConnector.class.getCanonicalName());
			}
			try	{
				this.dccop.doConnection (dynamicComponentCreationInboundPortURI + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
						DynamicComponentCreationConnector.class.getCanonicalName());
				
			} catch (Exception e) {
				throw new ComponentStartException(e);
			}
		}
		 catch (Exception e) {
				throw new ComponentStartException(e);
			}
		this.logMessage("Admission Controller starting with " + this.numberOfApps + " apps.");
		super.start();
	}

	/**
	 * Disconnect ports
	 */
	@Override
	public void finalise() throws Exception {
		
		try {
			for (int i = 0; i < numberOfComputers; i++) {
				if (this.csopList.get(i).connected()) this.csopList.get(i).doDisconnection();
			}
			if (this.dccop.connected()) this.dccop.doDisconnection();
		} catch (Exception e) {
			throw new ComponentShutdownException("Admission Controller port disconnection error", e);
		}

		super.finalise();
	}

	/**
	 * Unpublish ports
	 */
	@Override
	public void shutdown() throws ComponentShutdownException {
		
		try {		
			if (this.dccop.isPublished()) this.dccop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException("Admission Controller port un-publish error", e);
		}

		super.shutdown();
	}

	/**
	 * Check if enough cores are available, and allocate them if found.
	 * @param numberOfAVMs: number of AVMs needed to allocate
	 * @return an array of AllocatedCore[] containing the data for each requested core, or null if no core is available
	 * @throws Exception: exception
	 */
	private ArrayList<AllocationMap> isResourceAvailable (int numberOfAVMs) throws Exception {
		ArrayList<AllocationMap> allocatedMap = new ArrayList<>();
		
		// have to check for each AVM to get core allocation map
		for (int i = 0; i < numberOfAVMs; i++) {
			
			AllocatedCore[] allocatedCoresAVM;

			for (int j = 0; j < this.csopList.size(); j++) {
				ComputerServicesOutboundPort csop = this.csopList.get(j);
				allocatedCoresAVM = csop.allocateCores(this.numberOfCoresPerAVM);

				if (allocatedCoresAVM.length == this.numberOfCoresPerAVM) {
					allocatedMap.add(new AllocationMap (
							this.computersURIList.get(j),
							csop,
							allocatedCoresAVM));
					break;
				}

				csop.releaseCores(allocatedCoresAVM);
			}			
		}
		
		// if enough cores for each AVM
		if (allocatedMap.size() == numberOfAVMs) {
			return allocatedMap;
		}

		for (AllocationMap allocated : allocatedMap) {
			ComputerServicesOutboundPort csop = allocated.getCsop();
			csop.releaseCores(allocated.getAllocatedCores());
		}

		return null;
	}

	/**
	 * Calculate the number of AVM needed by application, based on the number of cores required and
	 * numberOfCoresPerAVM parameter.
	 *
	 * Accept or reject application based on resources
	 *
	 * @param appUri application URI
	 * @param mustHaveCores number of cores required by application
	 */
	@Override
	public void acceptApplicationSubmissionAndNotify (String appUri, int mustHaveCores) throws Exception {
		
		// find out how many AVM are needed based on number of cores needed (divide and round up)
		int numberOfAVMs = (int) Math.ceil((double)mustHaveCores / this.numberOfCoresPerAVM	);
		
		this.logMessage("Admission controller checking for available resources to execute " 
				+ appUri + " with " + mustHaveCores + " cores and " + numberOfAVMs + " AVMs.");
						
		synchronized (this) {
			// Wait timer for multiple applications running simultaneously, and computer state information
			wait(timer);

			ArrayList<AllocationMap> allocatedCores = this.isResourceAvailable(numberOfAVMs);

			if (allocatedCores != null && allocatedCores.size() > 0) {
				acceptApplication(appUri, numberOfAVMs, allocatedCores);
				this.anopMap.get(appUri).notifyApplicationAdmission(true);
				
			} else {
				rejectApplication(appUri);
				this.anopMap.get(appUri).notifyApplicationAdmission(false);
			} 
			
		}					
	}

	/**
	 * Accept application: log event and call deploy components
	 * @param appUri: application URI
	 * @param numberOfAVMs: number of AVMs needed
	 * @param allocatedCores: number of cores allocated
	 * @throws Exception: exception
	 */
	private void acceptApplication(String appUri, int numberOfAVMs, ArrayList<AllocationMap> allocatedCores) throws Exception {

		this.logMessage("Admission controller allowed application " + appUri + " to be executed.");
			
		deployComponents(appUri, numberOfAVMs, allocatedCores);
	}

	/**
	 * Reject application: log event and do nothing
	 * 
	 * @param appUri: application uri
	 */
	private void rejectApplication(String appUri) {
		this.logMessage("Admission controller rejected application " + appUri + " because of lack of resources.");		
	}

	/**
	 * Deploy Request Dispatcher, the required number of AVM and a Performance Controller
	 *
	 * @param appUri: application uri
	 * @param applicationVMCount: number of AVM that need to be deployed
	 * @param allocatedMap: number of cores that need to be allocated
	 * @throws Exception: creating components, connecting ports
	 */
	private void deployComponents(String appUri, int applicationVMCount, ArrayList<AllocationMap> allocatedMap) throws Exception {

		HashMap<String, AllocationMap> allocationMap = new HashMap<>();
		
		// Create URIs
		// --------------------------------------------------------------------
		ArrayList<String> avmURIList = new ArrayList<>();
		
		ArrayList<String> avmManagementInboundPortURIList = new ArrayList<>();
		ArrayList<String> avmRequestSubmissionInboundPortURIList = new ArrayList<>();
		ArrayList<String> avmRequestNotificationInboundPortURIList = new ArrayList<>();
		ArrayList<String> avmRequestNotificationOutboundPortURIList = new ArrayList<>();

		String rdURI = appUri + "-rd";
		String requestDispatcherManagementInboundPortURI = appUri + "-rdmip";
		String requestDispatcherServicesInboundPortURI = appUri + "-rdsvip";
		String requestDispatcherSubmissionInboundPortURI = appUri + "-rdsip";
		String requestDispatcherNotificationOutboundPortURI = appUri + "-rdnop";
		String requestDispatcherDynamicStateDataInboundPortURI = appUri + "-rddsdip";
		
		ArrayList<String> requestDispatcherSubmissionOutboundPortURIList = new ArrayList<>();
		ArrayList<String> requestDispatcherNotificationInboundPortURIList = new ArrayList<>();

		for (int i = 0; i < applicationVMCount; i++) {
			avmURIList.add(appUri + "-avm" + i);
			avmManagementInboundPortURIList.add(appUri + "-avmip" + i);
			avmRequestSubmissionInboundPortURIList.add(appUri + "-avmrsip" + i);
			avmRequestNotificationInboundPortURIList.add(appUri + "-avmrnip" + i);
			avmRequestNotificationOutboundPortURIList.add(appUri + "-avmrnop" + i);

			requestDispatcherNotificationInboundPortURIList.add(appUri + "-rdnip" + i);
			requestDispatcherSubmissionOutboundPortURIList.add(appUri + "-rdsop" + i);
		}

		RequestDispatcherServicesInboundPort rdsvip = new RequestDispatcherServicesInboundPort(requestDispatcherServicesInboundPortURI, this);
		this.addPort(rdsvip);
		rdsvip.publishPort();
		
		// Deploy the request dispatcher
		// --------------------------------------------------------------------
		try {
			this.dccop.createComponent(RequestDispatcher.class.getCanonicalName(), new Object[] {
					rdURI,
					avmURIList,
					requestDispatcherManagementInboundPortURI,
					requestDispatcherServicesInboundPortURI,
					requestDispatcherSubmissionInboundPortURI,
					requestDispatcherSubmissionOutboundPortURIList,
					requestDispatcherNotificationInboundPortURIList,
					requestDispatcherNotificationOutboundPortURI,
					requestDispatcherDynamicStateDataInboundPortURI
			});
		} catch (Exception e) {
			throw new Exception("Error creating Dispatcher " + e);
		}

		this.rdURIMap.put(appUri,rdURI);

		this.rdmopMap.put(rdURI, new RequestDispatcherManagementOutboundPort(this));
		this.addPort(this.rdmopMap.get(rdURI));
		this.rdmopMap.get(rdURI).publishPort();

		try {
			this.doPortConnection(this.rdmopMap.get(rdURI).getPortURI(),
					requestDispatcherManagementInboundPortURI,
					RequestDispatcherManagementConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new Exception ("Error connecting request dispatcher management ports " + e);
		}

		// Deploy applicationVMCount AVM
		// --------------------------------------------------------------------
		for (int i = 0; i < applicationVMCount; i++) {
			try {
				this.dccop.createComponent(ApplicationVM.class.getCanonicalName(), new Object[] {
						avmURIList.get(i), 
						avmManagementInboundPortURIList.get(i), 
						avmRequestSubmissionInboundPortURIList.get(i), 
						avmRequestNotificationInboundPortURIList.get(i),
						avmRequestNotificationOutboundPortURIList.get(i)
				});
			} catch (Exception e) {
				throw new Exception("Error creating AVM " + i + " " + e);
			}

			this.avmopMap.put(avmURIList.get(i), new ApplicationVMManagementOutboundPort(this));
			this.addPort(avmopMap.get(avmURIList.get(i)));
			avmopMap.get(avmURIList.get(i)).publishPort();

			try {
				this.doPortConnection(this.avmopMap.get(avmURIList.get(i)).getPortURI(), avmManagementInboundPortURIList.get(i),
						ApplicationVMManagementConnector.class.getCanonicalName());
			} catch (Exception e) {
				throw new Exception ("Error connecting avm management ports " + e);
			}

			try {
				rop.doConnection(avmURIList.get(i), ReflectionConnector.class.getCanonicalName());

				rop.doPortConnection(
						avmRequestNotificationOutboundPortURIList.get(i),
						requestDispatcherNotificationInboundPortURIList.get(i),
						RequestNotificationConnector.class.getCanonicalName());
			} catch (Exception e) {
				throw new Exception("Error connecting Reflection Outbound Port for AVM " + e);
			}

			allocationMap.put(avmURIList.get(i), allocatedMap.get(i));
		}

		try {
			rop.doConnection(rdURI, ReflectionConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new Exception("Error connecting Reflection Outbound Port for Dispatcher " + e);
		}
		
		try {
			for (int i = 0; i < applicationVMCount; i++ ) {
				rop.doPortConnection(
						requestDispatcherSubmissionOutboundPortURIList.get(i),
						avmRequestSubmissionInboundPortURIList.get(i), 
						RequestSubmissionConnector.class.getCanonicalName());
			}
		} catch (Exception e) {
			throw new Exception("Exception connecting RD/AVM ports" + e);
		}
		
		this.amopMap.get(appUri).doConnectionWithDispatcherForSubmission(requestDispatcherSubmissionInboundPortURI);
		this.amopMap.get(appUri).doConnectionWithDispatcherForNotification(rop, requestDispatcherNotificationOutboundPortURI);

		// Deploy the Performance Controller
		// --------------------------------------------------------------------
		String performanceControllerURI = appUri + "-pc";
		String performanceControllerManagementInboundPortURI = appUri + "-pcmip";
		String performanceControllerServicesInboundPortURI = appUri + "-pcsip";

		String performanceControllerRingNetworkInboundPortURI = appUri + "-ringnip";
		String performanceControllerRingNetworkOutboundPortURI = appUri + "-ringnop";

		PerformanceControllerServicesInboundPort pcsip = new PerformanceControllerServicesInboundPort(performanceControllerServicesInboundPortURI, this);
		this.addPort(pcsip);
		pcsip.publishPort();

		try {
			this.dccop.createComponent(PerformanceController.class.getCanonicalName(), new Object[] {
					performanceControllerURI,
					performanceControllerManagementInboundPortURI,
					performanceControllerServicesInboundPortURI,
					appUri,
					rdURI,
					computersURIList,
					allocationMap,
					performanceControllerRingNetworkInboundPortURI,
					performanceControllerRingNetworkOutboundPortURI
			});
		} catch (Exception e) {
			throw new Exception("Error creating Performance Controller " + e);
		}

		this.pcmopMap.put(performanceControllerURI, new PerformanceControllerManagementOutboundPort(this));
		this.addPort(this.pcmopMap.get(performanceControllerURI));
		this.pcmopMap.get(performanceControllerURI).publishPort();

		try {
			rop.doConnection(performanceControllerURI, ReflectionConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new Exception("Error connecting Reflection Outbound Port for Performance Controller " + e);
		}

		try {
			this.doPortConnection(this.pcmopMap.get(performanceControllerURI).getPortURI(),
					performanceControllerManagementInboundPortURI,
					PerformanceControllerManagementConnector.class.getCanonicalName());

		} catch (Exception e) {
			throw new Exception ("Error connecting performance controller ports " + e);
		}

		this.pcmopMap.get(performanceControllerURI).doConnectionWithRequestDispatcherForDynamicState(requestDispatcherDynamicStateDataInboundPortURI);
		this.pcmopMap.get(performanceControllerURI).doConnectionWithComputerForDynamicState(computerDynamicStateDataInboundPortURIList);

		for (int i = 0; i < applicationVMCount; i++) {
			try {
				this.avmopMap.get(avmURIList.get(i)).allocateCores(allocatedMap.get(i).getAllocatedCores());
			} catch (Exception e) {
				throw new ComponentStartException("Couldn't allocate cores to AVM out port" + e);
			}
		}

		addToRingNetwork(
				performanceControllerRingNetworkInboundPortURI,
				performanceControllerRingNetworkOutboundPortURI,
				performanceControllerURI
		);
		this.logMessage("Admission controller deployed components for " + appUri + ".");
	}

	/**
	 * Connect to Ring Network:
	 *
	 * Ring Network Outbound Port -> connect to Performance Controller Ring Network Inbound Port
	 *
	 * Performance Controller Ring Network Outbound Port -> connect to Ring Network Inbound Port
	 *
	 * @param performanceControllerRingNetworkInboundPortURI Performance Controller Ring Network Inbound Port URI
	 * @param performanceControllerRingNetworkOutboundPortURI Performance Controller Ring Network Outbound Port
	 * @param performanceControllerURI Performance Controller URI
	 */
	private void addToRingNetwork (String performanceControllerRingNetworkInboundPortURI,
	                               String performanceControllerRingNetworkOutboundPortURI,
	                               String performanceControllerURI) throws Exception{

		if (this.ringNetworkSize == 0) {
			// there are no performance controllers on network
			try {
				this.ringNetworkOutboundPort.doConnection(performanceControllerRingNetworkInboundPortURI,
						RingNetworkConnector.class.getCanonicalName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				this.rop.doConnection(this.ringNetwork.getPerformanceControllerURI(), ReflectionConnector.class.getCanonicalName());
				this.rop.doPortConnection(
						this.ringNetwork.getRingOutboundPortURI(),
						performanceControllerRingNetworkInboundPortURI,
						RingNetworkConnector.class.getCanonicalName()
				);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			this.rop.doConnection(performanceControllerURI, ReflectionConnector.class.getCanonicalName());

			this.rop.doPortConnection(
					performanceControllerRingNetworkOutboundPortURI,
					this.ringNetwork.getRingInboundPortURI(),
					RingNetworkConnector.class.getCanonicalName()
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.ringNetwork.setRingOutboundPortURI(performanceControllerRingNetworkOutboundPortURI);
		this.ringNetwork.setPerformanceControllerURI(performanceControllerURI);

		if (this.ringNetworkSize == 1) {
			this.ringNetworkMessageTime = System.nanoTime();
			checkRingNetwork();
			this.ringNetworkOutboundPort.receiveAVMBuffer(this.ringNetworkAVM);
		}
	}


	private void checkRingNetwork() {
		this.scheduleTask(
			new AbstractComponent.AbstractTask() {

				@Override
				public void run() {
					try {
						if (bufferUsed()) addAVMToBuffer();
						if (bufferNotUsed()) removeAVMFromBuffer();

						checkRingNetwork();
					} catch (Exception e) {
						throw new RuntimeException (e);
					}
				}

			}, timer*6, TimeUnit.MILLISECONDS);
	}

	private boolean bufferUsed() {
		for (AvmInformation avmInformation : this.ringNetworkAVM) {
			if (avmInformation.isFree()) {
				return false;
			}
		}
		return true;
	}

	private boolean bufferNotUsed() {
		for (AvmInformation avmInformation : this.ringNetworkAVM) {
			if (!avmInformation.isFree()) {
				return false;
			}
		}
		return true;
	}

	//TODO : add an AVM to buffer
	private void addAVMToBuffer () {

	}

	//TODO : remove an AVM from buffer
	private void removeAVMFromBuffer () {

	}

	/**
	 * Accept a request from performance controller to add cores for an
	 * application, and update allocation map
	 *
	 * @param appUri application URI
	 * @param allocatedCore number of cores added
	 */
	@Override
	public void acceptRequestAddCores(String appUri, AllocatedCore[] allocatedCore) throws Exception {
		this.avmopMap.get(appUri).allocateCores(allocatedCore);

		this.logMessage("Admission controller added " + allocatedCore.length + " cores for " + appUri);
	}

	/**
	 * Accept a request from performance controller to remove cores from an
	 * application -> log message
	 * @param appUri application URI
	 * @param removeCores number of cores removed from application
	 */
	@Override
	public void acceptRequestRemoveCores(String appUri, AllocatedCore[] removeCores) {

		this.logMessage("Admission controller removed " + removeCores.length + " cores for " + appUri);
	}

	/**
	 * Receive a request from performance controller to add an AVM to an
	 * application.
	 *
	 * If enough resources, allocate and notify request dispatcher of the
	 * new allocation map.
	 *
	 * Otherwise reject the request and notify performance controller
	 *
	 * @param appURI application URI
	 * @param performanceControllerURI Performance Controller URI
	 */
	@Override
	public void acceptRequestAddAVM(String appURI, String performanceControllerURI) throws Exception {
		this.logMessage("Admission controller received request to add AVM for " + appURI);

		ArrayList<AllocationMap> allocatedCores = this.isResourceAvailable(this.numberOfCoresPerAVM);

		if (allocatedCores != null && allocatedCores.size() > 0) {
			notifyDispatcherOfNewAVM(appURI, performanceControllerURI, allocatedCores);
		} else {
			this.logMessage("---> Not enough resources, refused new AVM for " + appURI);
			this.pcmopMap.get(performanceControllerURI).notifyAVMAddRefused (appURI);
		}
	}

	/**
	 * Receive a request from performance controller to remove an AVM from an application
	 *
	 * If it's possible to remove (if the application has more than one AVM), notify request
	 * dispatcher that an AVM needs to be removed.
	 *
	 * Otherwise notify performance controller that removal is not possible.
	 * @param appURI application URI
	 * @param performanceControllerURI Performance Controller URI
	 */
	@Override
	public void acceptRequestRemoveAVM(String appURI, String performanceControllerURI) throws Exception {
		this.logMessage("Admission controller received request to remove an AVM from " + appURI);

		if (this.avmopMap.size() > 1) {
			notifyDispatcherToRemoveAVM(appURI, performanceControllerURI);
		} else {
			this.logMessage("---> Not possible to remove any AVM from " + appURI);
			this.pcmopMap.get(performanceControllerURI).notifyAVMRemoveRefused(appURI);
		}
	}

	/**
	 * Notify the request dispatcher of an application there's a new AVM available.
	 *
	 * Dispatcher will need to create new ports to connect to the new AVM. Until then, do nothing else.
	 *
	 * @param appUri application URI
	 * @param performanceControllerURI Performance Controller URI
	 * @param allocatedMap core allocation map for the new AVM
	 */
	private void notifyDispatcherOfNewAVM (String appUri, String performanceControllerURI, ArrayList<AllocationMap> allocatedMap) throws Exception {

		this.logMessage("---> Notifying dispatcher of new AVM for " + appUri);

		int avmIndex = this.avmopMap.size();

		String avmURI = appUri + "-avm" + avmIndex;
		String requestDispatcherSubmissionOutboundPortURI = appUri + "-rdsop" + avmIndex;
		String requestDispatcherNotificationInboundPortURI = appUri + "-rdnip" + avmIndex;

		// Tell dispatcher to create new ports
		// --------------------------------------------------------------------
		this.rdmopMap.get(this.rdURIMap.get(appUri)).notifyDispatcherOfNewAVM (
				appUri,
				performanceControllerURI,
				allocatedMap,
				avmURI,
				requestDispatcherSubmissionOutboundPortURI,
				requestDispatcherNotificationInboundPortURI
		);
	}

	/**
	 * Receive notification from request dispatcher that new ports requested
	 * by notifyDispatcherOfNewAVM method are ready
	 *
	 * Deploy the new AVM, connect it to the new request dispatcher ports and inform
	 * both Performance Controller and Request Dispatcher that the AVM was added.
	 *
	 * @param appUri application URI
	 * @param performanceControllerURI Performance Controller URI
	 * @param allocatedMap core allocation map for the new AVM
	 * @param avmURI new avm URI
	 * @param requestDispatcherSubmissionOutboundPortURI new request dispatcher submission outbound port
	 * @param requestDispatcherNotificationInboundPortURI new request dispatcher notification inbound port
	 */
	@Override
	public void acceptNotificationNewAVMPortsReady(
			String appUri,
			String performanceControllerURI,
			ArrayList<AllocationMap> allocatedMap,
			String avmURI,
			String requestDispatcherSubmissionOutboundPortURI,
			String requestDispatcherNotificationInboundPortURI) throws Exception {

		this.logMessage("---> Dispatcher created new ports, deploying AVM for " + appUri);
		int avmIndex = this.avmopMap.size();

		// Create URIs
		// --------------------------------------------------------------------
		String avmManagementInboundPortURI = appUri + "-avmip" + avmIndex;
		String avmRequestSubmissionInboundPortURI = appUri + "-avmrsip" + avmIndex;
		String avmRequestNotificationInboundPortURI = appUri + "-avmrnip" + avmIndex;
		String avmRequestNotificationOutboundPortURI = appUri + "-avmrnop" + avmIndex;

		// Deploy new AVM
		// --------------------------------------------------------------------
		try {
			this.dccop.createComponent(ApplicationVM.class.getCanonicalName(), new Object[] {
					avmURI,
					avmManagementInboundPortURI,
					avmRequestSubmissionInboundPortURI,
					avmRequestNotificationInboundPortURI,
					avmRequestNotificationOutboundPortURI
			});
		} catch (Exception e) {
			throw new Exception ("Error creating new AVM " + avmIndex + " " + e);
		}

		this.avmopMap.put(avmURI, new ApplicationVMManagementOutboundPort(this));
		this.addPort(avmopMap.get(avmURI));
		avmopMap.get(avmURI).publishPort();

		try {
			this.doPortConnection(this.avmopMap.get(avmURI).getPortURI(), avmManagementInboundPortURI,
					ApplicationVMManagementConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new Exception ("Error connecting new AVM management ports " + e);
		}

		// Reflection Port
		// --------------------------------------------------------------------
		try {
			rop.doConnection(avmURI, ReflectionConnector.class.getCanonicalName());

			rop.doPortConnection(
					avmRequestNotificationOutboundPortURI,
					requestDispatcherNotificationInboundPortURI,
					RequestNotificationConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new Exception ("Error connecting new AVM to dispatcher for notification " + e);
		}

		try {
			rop.doConnection(this.rdURIMap.get(appUri), ReflectionConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new Exception("Error connecting Reflection Outbound Port for Dispatcher " + e);
		}

		try {
			rop.doPortConnection(
					requestDispatcherSubmissionOutboundPortURI,
					avmRequestSubmissionInboundPortURI,
					RequestSubmissionConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new Exception ("Error connecting new AVM to dispatcher for submission" + e);
		}

		try {
			this.avmopMap.get(avmURI).allocateCores(allocatedMap.get(0).getAllocatedCores());
		} catch (Exception e) {
			throw new Exception ("Couldn't allocate cores to new AVM out port" + e);
		}

		// Send information to Performance Controller
		try {
			this.pcmopMap.get(performanceControllerURI).notifyAVMAdded(avmURI, allocatedMap.get(0));
		} catch (Exception e) {
			throw new Exception ("Couldn't notify performance controller that new AVM was added" + e);
		}

		// Tell dispatcher it can start forwarding requests to new AVM
		// --------------------------------------------------------------------
		this.rdmopMap.get(this.rdURIMap.get(appUri)).notifyDispatcherNewAVMDeployed(avmURI);

		this.logMessage("---> Finished deployment for new AVM for " + appUri);
	}

	/**
	 * Receive notification from request dispatcher that an AVM is ready to be removed (it has no more
	 * requests in queue, and request dispatcher is no longer forwarding new requests to it).
	 *
	 * Log event and notify performance controller that AVM removal is complete.
	 *
	 * @param vmURI avm URI
	 * @param appURI application URI
	 * @param performanceControllerURI performance controller URI
	 */
	@Override
	public void acceptNotificationAVMRemovalComplete(String vmURI, String appURI, String performanceControllerURI) throws Exception {
		this.avmopMap.get(vmURI).destroyComponent();
		this.logMessage("---> AVM " + vmURI + " removed from " + appURI);
		this.pcmopMap.get(performanceControllerURI).notifyAVMRemoveComplete(vmURI, appURI);
	}

	/**
	 * Receive notification from request dispatcher that it's not possible to remove another AVM
	 * from an application. At this stage, this only occurs when there is another removal request
	 * pending (only the dispatcher has that information)
	 *
	 * @param appURI application URI
	 * @param performanceControllerURI performance controller URI
	 */
	@Override
	public void acceptNotificationAVMRemovalRefused(String appURI, String performanceControllerURI) throws Exception {
		this.logMessage("---> Not possible to remove any AVM from " + appURI);
		this.pcmopMap.get(performanceControllerURI).notifyAVMRemoveRefused(appURI);
	}

	/**
	 * Notify the request dispatcher of an application that it needs to remove an AVM.
	 *
	 * Request Dispatcher will need to check which AVM it should remove (based on usage),
	 * stop forwarding requests to that AVM, and wait for the AVM to finish its request
	 * queue before removal is possible
	 *
	 * @param appURI application URI
	 * @param performanceControllerURI performance controller URI
	 */
	private void notifyDispatcherToRemoveAVM(String appURI, String performanceControllerURI) throws Exception {
		this.logMessage("---> Notifying dispatcher to remove an AVM from " + appURI);

		// Tell dispatcher to remove AVM
		// --------------------------------------------------------------------
		this.rdmopMap.get(this.rdURIMap.get(appURI)).notifyDispatcherToRemoveAVM (
				appURI,
				performanceControllerURI
		);
	}
}