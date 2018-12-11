package fr.sorbonne_u.sylalexcenter.admissioncontroller;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
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
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
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
import fr.sorbonne_u.sylalexcenter.performancecontroller.connectors.PerformanceControllerServicesConnector;
import fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces.PerformanceControllerManagementI;
import fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces.PerformanceControllerServicesHandlerI;
import fr.sorbonne_u.sylalexcenter.performancecontroller.ports.PerformanceControllerManagementOutboundPort;
import fr.sorbonne_u.sylalexcenter.performancecontroller.ports.PerformanceControllerServicesInboundPort;
import fr.sorbonne_u.sylalexcenter.performancecontroller.ports.PerformanceControllerServicesOutboundPort;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.connectors.RequestDispatcherManagementConnector;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.ports.RequestDispatcherManagementOutboundPort;

import java.util.ArrayList;
import java.util.HashMap;

public class AdmissionController extends AbstractComponent 
	implements ApplicationSubmissionHandlerI, PerformanceControllerServicesHandlerI {
	
	private static final String dynamicComponentCreationInboundPortURI = "";
	
	private static final int timer = 1000;
	
	private final int numberOfComputers;
	private final int numberOfApps;
	private final int numberOfCoresPerAVM;

	private ArrayList<String> computersURIList;
	private ArrayList<String> computerServicesInboundPortURIList;
	private ArrayList<String> computerStaticStateDataInboundPortURIList;
	private ArrayList<String> computerDynamicStateDataInboundPortURIList;
	
	private ArrayList<ComputerServicesOutboundPort> csopList;
	private ArrayList<ComputerStaticStateDataOutboundPort> cssdopList;

	private HashMap<String,String> applicationManagementInboundPortURIMap;
	private HashMap<String,String> applicationNotificationInboundPortURIMap;
	
	private HashMap<String,ApplicationManagementOutboundPort> amopMap;
	private HashMap<String,ApplicationNotificationOutboundPort> anopMap;

	private HashMap<String, PerformanceControllerManagementOutboundPort> pcmopMap;
	private HashMap<String, RequestDispatcherManagementOutboundPort> rdmopMap;
	private HashMap<String, ApplicationVMManagementOutboundPort> avmopMap;

	private DynamicComponentCreationOutboundPort dccop;
		
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
		
		this.tracer.setTitle("Admission Controller");
		this.tracer.setRelativePosition(2, 0);

		assert this.csopList !=null;
		assert this.cssdopList !=null;
		assert this.anopMap != null;
	}
	
	/**
	 * Connect computer outbound ports to inbound URIs
	 * 
	 * Connect application notification outbound port to application notification inbound URI
	 * 
	 * Connect dynamic component creator outbound port to inbound URI
	 * (see Javadoc fr.sorbonne_u.components.pre.dcc)
	 * A component that wants to create another component on a remote JVM has to 
	 * create an outbound port DynamicComponentCreationOutboundPort and connect it 
	 * to the inbound port of the dynamic component creator running on the 
	 * remote virtual machine. Create port on start()
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
	 * Check if a number of cores is available, and allocate them if found.
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
							this.numberOfCoresPerAVM,
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
	 *
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
	 * Reject application - log event and do nothing
	 * 
	 * @param appUri: application uri
	 */
	private void rejectApplication(String appUri) {
		this.logMessage("Admission controller rejected application " + appUri + " because of lack of resources.");		
	}

	/**
	 * If an application was accepted, deploy Request Dispatcher and the required
	 * number of AVM
	 * @param appUri: application uri
	 * @param applicationVMCount: number of AVM that need to be deployed
	 * @param allocatedMap: number of cores that need to be allocated
	 * @throws Exception: creating components, connecting ports
	 */
	private void deployComponents(String appUri, int applicationVMCount, ArrayList<AllocationMap> allocatedMap) throws Exception {

		ReflectionOutboundPort rop = new ReflectionOutboundPort(this);
		addPort(rop);
		rop.publishPort();

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
		
		// Deploy the request dispatcher
		// --------------------------------------------------------------------
		try {
			this.dccop.createComponent(RequestDispatcher.class.getCanonicalName(), new Object[] {
					rdURI,
					avmURIList,
					requestDispatcherManagementInboundPortURI,
					requestDispatcherSubmissionInboundPortURI,
					requestDispatcherSubmissionOutboundPortURIList,
					requestDispatcherNotificationInboundPortURIList,
					requestDispatcherNotificationOutboundPortURI,
					requestDispatcherDynamicStateDataInboundPortURI
			});
		} catch (Exception e) {
			throw new Exception("Error creating Dispatcher " + e);
		}

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
					allocationMap
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

		this.logMessage("Admission controller deployed components for " + appUri + ".");
	}

	@Override
	public void acceptRequestAddCores(String appUri, AllocatedCore[] allocatedCore) throws Exception {
		this.avmopMap.get(appUri).allocateCores(allocatedCore);

		this.logMessage("Admission controller added " + allocatedCore.length + " cores for " + appUri);
	}

	@Override
	public void acceptRequestRemoveCores(String appUri, AllocatedCore[] removeCores) throws Exception {

		this.logMessage("Admission controller removed " + removeCores.length + " cores for " + appUri);
	}
}