package fr.sorbonne_u.sylalexcenter.admissioncontroller;

import java.util.ArrayList;
import java.util.HashMap;
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
import fr.sorbonne_u.datacenter.connectors.ControlledDataConnector;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerStateDataConsumerI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
import fr.sorbonne_u.datacenter.interfaces.ControlledDataRequiredI;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenter.software.connectors.RequestSubmissionConnector;
import fr.sorbonne_u.sylalexcenter.application.connectors.ApplicationManagementConnector;
import fr.sorbonne_u.sylalexcenter.application.connectors.ApplicationNotificationConnector;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationManagementI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationNotificationI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationSubmissionHandlerI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationSubmissionI;
import fr.sorbonne_u.sylalexcenter.application.ports.ApplicationManagementOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.ports.ApplicationNotificationOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.ports.ApplicationSubmissionInboundPort;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.RequestDispatcher;

public class AdmissionController extends AbstractComponent 
	implements ApplicationSubmissionHandlerI, ComputerStateDataConsumerI  {
	
	private static final String dynamicComponentCreationInboundPortURI = "";
	
	public static int timer = 1000;
	
	protected int numberOfComputers;
	protected int numberOfApps;
	protected int numberOfAVMsPerApp;
	protected int numberOfCoresPerAVM;

	protected ArrayList<String> computersURIList;
	protected ArrayList<String> computerServicesInboundPortURIList;
	protected ArrayList<String> computerStaticStateDataInboundPortURIList;
	protected ArrayList<String> computerDynamicStateDataInboundPortURIList;
	
	protected ArrayList<ComputerServicesOutboundPort> csopList;
	protected ArrayList<ComputerStaticStateDataOutboundPort> cssdopList;
	protected ArrayList<ComputerDynamicStateDataOutboundPort> cdsdopList;

	protected HashMap<String,String> applicationManagementInboundPortURIMap;
	protected HashMap<String,ApplicationManagementOutboundPort> amopMap;
	protected HashMap<String,ApplicationSubmissionInboundPort> asipMap;
	protected HashMap<String,String> applicationNotificationInboundPortURIMap;
	protected HashMap<String,ApplicationNotificationOutboundPort> anopMap;
	
	protected DynamicComponentCreationOutboundPort dccop;
//	protected ReflectionOutboundPort rop;
		
	public AdmissionController(
			ArrayList<String> computersURIList,			
			ArrayList<String> computerServicesInboundPortURIList,
			ArrayList<String> computerStaticStateDataInboundPortURIList,
			ArrayList<String> computerDynamicStateDataInboundPortURIList,
			ArrayList<String> appsURIList,
			ArrayList<String> applicationManagementInboundPortURIList,
			ArrayList<String> applicationSubmissionInboundPortURIList,
			ArrayList<String> applicationNotificationInboundPortURIList,
			int numberOfAVMsPerApp,
			int numberOfCoresPerAVM) throws Exception {
		
		super(1, 1);
		
		assert computersURIList != null && computersURIList.size() > 0;
		assert computerServicesInboundPortURIList != null && computerServicesInboundPortURIList.size() > 0;	
		assert computerStaticStateDataInboundPortURIList != null && computerStaticStateDataInboundPortURIList.size() > 0;	
		assert computerDynamicStateDataInboundPortURIList != null && computerDynamicStateDataInboundPortURIList.size() > 0;	
		
		assert appsURIList != null && appsURIList.size() > 0;
		assert applicationManagementInboundPortURIList != null && applicationManagementInboundPortURIList.size() > 0;
		assert applicationSubmissionInboundPortURIList != null && applicationSubmissionInboundPortURIList.size() > 0;
		assert applicationNotificationInboundPortURIList != null && applicationNotificationInboundPortURIList.size() > 0;

		assert numberOfAVMsPerApp > 0;
		assert numberOfCoresPerAVM > 0;
			
		this.numberOfComputers = computersURIList.size();
		this.numberOfApps = appsURIList.size();
		this.numberOfAVMsPerApp = numberOfAVMsPerApp;
		this.numberOfCoresPerAVM = numberOfCoresPerAVM;
		
		// Computers
		this.computersURIList = new ArrayList<String>();
		this.computerServicesInboundPortURIList = new ArrayList<String>();
		this.computerStaticStateDataInboundPortURIList = new ArrayList<String>();
		this.computerDynamicStateDataInboundPortURIList = new ArrayList<String>();
		
		// Computer Ports
		this.csopList = new ArrayList<ComputerServicesOutboundPort>();
		this.cssdopList = new ArrayList<ComputerStaticStateDataOutboundPort>();
		this.cdsdopList = new ArrayList<ComputerDynamicStateDataOutboundPort>();

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
			
			this.cdsdopList.add(new ComputerDynamicStateDataOutboundPort(this, computersURIList.get(i)));
			this.addPort(this.cdsdopList.get(i));
			this.cdsdopList.get(i).publishPort();
			
			this.computersURIList.add(computersURIList.get(i));
		}		
		
		// Application Ports
		this.applicationManagementInboundPortURIMap = new HashMap<String,String>();
		this.amopMap = new HashMap<String,ApplicationManagementOutboundPort>();
		
		this.asipMap = new HashMap<String,ApplicationSubmissionInboundPort>();
		
		this.applicationNotificationInboundPortURIMap = new HashMap<String,String>();
		this.anopMap = new HashMap<String,ApplicationNotificationOutboundPort>();
		
		this.addRequiredInterface(ApplicationManagementI.class);
		this.addOfferedInterface(ApplicationSubmissionI.class);
		this.addRequiredInterface(ApplicationNotificationI.class);
		
		for (int i = 0; i < numberOfApps; i++) {
			this.applicationManagementInboundPortURIMap.put(appsURIList.get(i), applicationManagementInboundPortURIList.get(i));
			
			this.amopMap.put(appsURIList.get(i), new ApplicationManagementOutboundPort(this));		
			this.addPort(this.amopMap.get(appsURIList.get(i)));
			this.amopMap.get(appsURIList.get(i)).publishPort();	
			
			this.asipMap.put(appsURIList.get(i), new ApplicationSubmissionInboundPort(applicationSubmissionInboundPortURIList.get(i), this));		
			this.addPort(this.asipMap.get(appsURIList.get(i)));
			this.asipMap.get(appsURIList.get(i)).publishPort();	
			
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
		
		this.tracer.setTitle("Admission Controller");
		this.tracer.setRelativePosition(2, 0);

		assert this.csopList !=null && this.csopList.get(0) instanceof ComputerServicesOutboundPort;
		assert this.cssdopList !=null && this.cssdopList.get(0) instanceof DataRequiredI.PullI;
		assert this.cdsdopList !=null && this.cdsdopList.get(0) instanceof ControlledDataRequiredI.ControlledPullI;
		
		assert this.asipMap != null && this.asipMap.get(appsURIList.get(0)) instanceof ApplicationSubmissionI;
		assert this.anopMap != null && this.anopMap.get(appsURIList.get(0)) instanceof ApplicationNotificationI;
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
				
				this.doPortConnection(this.cdsdopList.get(i).getPortURI(), this.computerDynamicStateDataInboundPortURIList.get(i),
						ControlledDataConnector.class.getCanonicalName());
				
				this.cdsdopList.get(i).startUnlimitedPushing(timer); 
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
				System.out.println("1");
				throw new ComponentStartException(e);
			}
		}
		 catch (Exception e) {
			    System.out.println("2");
				throw new ComponentStartException(e);
			}
		
		
		super.start();
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {
		
		try {		
			for (int i = 0; i < numberOfComputers; i++) {
				if (this.csopList.get(i).connected()) {
					this.csopList.get(i).doDisconnection();
				}
			}			
			for (ApplicationNotificationOutboundPort thisAnop : anopMap.values()) {
				if (thisAnop.connected()) {
					thisAnop.doDisconnection();
				}
			}
		} catch (Exception e) {
			throw new ComponentShutdownException("Port disconnection error", e);
		}

		super.shutdown();
	}
	
	@Override
	public void acceptComputerStaticData(String computerURI, ComputerStaticStateI staticState) throws Exception {
//		numberOfProcessors = staticState.getNumberOfProcessors();
//		numberOfCores = staticState.getNumberOfCoresPerProcessor();
	}
	
	@Override
	public void acceptComputerDynamicData(String computerURI, ComputerDynamicStateI currentDynamicState) throws Exception {
//		boolean [][] reservedCores = currentDynamicState.getCurrentCoreReservations();
	}	
	
	@Override
	public void acceptApplicationSubmissionAndNotify (String appUri, int mustHaveCores) throws Exception {
				
		this.logMessage("Admission controller checking for available resources to execute " + appUri + " with " + mustHaveCores + " cores.");
		
		synchronized (this) {
			// Wait timer for multiple applications running simultaneously, and computer state information
			wait(timer);
			
			AllocatedCore[] allocatedCores = this.isResourceAvailable(mustHaveCores);
			
			if (allocatedCores.length > 0) {
				acceptApplication(appUri, mustHaveCores, allocatedCores);
				this.anopMap.get(appUri).notifyApplicationAdmission(true);
				
			} else {
				rejectApplication(appUri);
				this.anopMap.get(appUri).notifyApplicationAdmission(false);
			} 
			
		}					
	}

	/**
	 * Check if a number of cores is available, 
	 * and allocate them if found. 
	 * 
	 * Return an array of AllocatedCore containing the data for each requested core; 
	 * return an empty array if no core is available.
	 * 
	 * @param mustHaveCores - number of cores needed to allocate
	 * @return an array of AllocatedCore[] containing the data for each requested core, or null if no core is available
	 * @throws Exception
	 */
	public AllocatedCore[] isResourceAvailable (int mustHaveCores) throws Exception {
		AllocatedCore[] allocatedCores = new AllocatedCore[0];

		for(ComputerServicesOutboundPort csop : this.csopList) {
			allocatedCores = csop.allocateCores(mustHaveCores) ;
			if (allocatedCores.length == mustHaveCores) {
				return allocatedCores;
			}
		}
		return allocatedCores;
	}

	
	/**
	 * Accept application - deploy needed components for the application
	 * 
	 * @param appUri
	 * @param requestGeneratorSubmissionInboundPortURI
	 * @param requestGeneratorNotificationInboundPortURI
	 * @param mustHaveCores
	 * @param allocatedCores
	 * @throws Exception
	 */
	public void acceptApplication(String appUri, int mustHaveCores, AllocatedCore[] allocatedCores) throws Exception {
		
		this.logMessage("Admission controller allowed application " + appUri + " to be executed.");
			
		deployComponents(appUri, numberOfAVMsPerApp);
	}
	
	
	/**
	 * Reject application - log event and do nothing
	 * 
	 * @param appUri
	 */
	public void rejectApplication(String appUri) {
		this.logMessage("Admission controller rejected application " + appUri + " because of lack of resources.");		
	}
	
	
	/**
	 * 
	 * If an application was accepted, deploy Request Dispatcher and the required
	 * number of AVM
	 * 
	 * @param appUri
	 * @param requestDispatcherSubmissionInboundPortURI
	 * @param requestDispatcherNotificationInboundPortURI
	 * @param applicationVMCount
	 * @throws Exception
	 */
	public void deployComponents(String appUri, int applicationVMCount) throws Exception {
		
		ReflectionOutboundPort rop = new ReflectionOutboundPort(this);
		addPort(rop);
		rop.publishPort();
		
		// Create URIs
		// --------------------------------------------------------------------
		ArrayList<String> avmURIList = new ArrayList<String>();
		
		ArrayList<String> avmManagementInboundPortURIList = new ArrayList<String>();
		ArrayList<String> avmRequestSubmissionInboundPortURIList = new ArrayList<String>();
		ArrayList<String> avmRequestNotificationInboundPortURIList = new ArrayList<String>();
		
		String rdURI = appUri + "-rd";
		String requestDispatcherManagementInboundPortURI = appUri + "-rdmip";
		String requestDispatcherSubmissionInboundPortURI = appUri + "-rdsip";
		String requestDispatcherNotificationInboundPortURI = appUri + "-rdnip";
		
		ArrayList<String> requestDispatcherSubmissionOutboundPortURIList = new ArrayList<String>();
		ArrayList<String> requestDispatcherNotificationOutboundPortURIList = new ArrayList<String>();
		
		for (int i = 0; i < applicationVMCount; i++) {
			avmURIList.add(appUri + "-avm" + i);
			avmManagementInboundPortURIList.add(appUri + "-avmip" + i);
			avmRequestSubmissionInboundPortURIList.add(appUri + "-avmrsip" + i);
			avmRequestNotificationInboundPortURIList.add(appUri + "-avmrnip" + i);
			
			requestDispatcherSubmissionOutboundPortURIList.add(appUri + "-rdsop" + i);
			requestDispatcherNotificationOutboundPortURIList.add(appUri + "-rdnop" + i);
		}
		
		// Deploy the request dispatcher
		// --------------------------------------------------------------------
		try {
			this.dccop.createComponent(RequestDispatcher.class.getCanonicalName(), new Object[] {
					rdURI, 
					requestDispatcherManagementInboundPortURI,
					requestDispatcherSubmissionInboundPortURI,
					requestDispatcherNotificationInboundPortURI,
					avmURIList,
					requestDispatcherSubmissionOutboundPortURIList,
					requestDispatcherNotificationOutboundPortURIList
			});
		} catch (Exception e) {
			System.err.println("Error creating Dispatcher ");
			System.err.println(e);
			throw new Exception(e);
		}
		
		// Deploy applicationVMCount AVM
		// --------------------------------------------------------------------
		for (int i = 0; i < applicationVMCount; i++) {
			try {
				this.dccop.createComponent(ApplicationVM.class.getCanonicalName(), new Object[] {
						avmURIList.get(i), 
						avmManagementInboundPortURIList.get(i), 
						avmRequestSubmissionInboundPortURIList.get(i), 
						avmRequestNotificationInboundPortURIList.get(i)
				});
			} catch (Exception e) {
				System.err.println("Error creating AVM " + i);
				System.err.println(e);
				throw new Exception(e);
			}
			
			try {
				rop.doConnection(avmURIList.get(i), ReflectionConnector.class.getCanonicalName());
			} catch (Exception e) {
				System.err.println("Error connecting Reflection Outbound Port for AVM ");
				System.err.println(e);
				throw new Exception(e);
			}
		}

		try {
			rop.doConnection(rdURI, ReflectionConnector.class.getCanonicalName());
		} catch (Exception e) {
			System.err.println("Error connecting Reflection Outbound Port for Dispatcher ");
			System.err.println(e);
			throw new Exception(e);
		}
		
		try {
			for (int i = 0; i < applicationVMCount; i++ ) {
				rop.doPortConnection(
						requestDispatcherSubmissionOutboundPortURIList.get(i),
						avmRequestSubmissionInboundPortURIList.get(i), 
						RequestSubmissionConnector.class.getCanonicalName());
			}
			

		} catch (Exception e) {
			System.err.println("AdmissionController#deployComponents(): Exception connecting Submission ports");
			throw new ComponentStartException(e);
		}
		
		this.amopMap.get(appUri).doConnectionWithDispatcherForSubmission(requestDispatcherSubmissionInboundPortURI);
		this.amopMap.get(appUri).doConnectionWithDispatcherForNotification(rop, requestDispatcherNotificationInboundPortURI);
		
		// Deploy the integrator
		// --------------------------------------------------------------------
		Object[] admissionControllerIntegrator = new Object[] {
				avmManagementInboundPortURIList,
				requestDispatcherManagementInboundPortURI
		};
		
		this.dccop.createComponent(AdmissionControllerIntegrator.class.getCanonicalName(), admissionControllerIntegrator);

		this.logMessage("Admission controller deployed components for " + appUri + ".");
	}
}