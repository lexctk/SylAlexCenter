package fr.sorbonne_u.sylalexcenter.admissioncontroller;

import java.util.ArrayList;
import java.util.HashMap;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.connectors.DataConnector;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
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
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationNotificationI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationSubmissionHandlerI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationSubmissionI;
import fr.sorbonne_u.sylalexcenter.application.ports.ApplicationNotificationOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.ports.ApplicationSubmissionInboundPort;
import fr.sorbonne_u.sylalexcenter.bcm.overrides.DynamicComponentCreationConnector;
import fr.sorbonne_u.sylalexcenter.bcm.overrides.DynamicComponentCreationI;
import fr.sorbonne_u.sylalexcenter.bcm.overrides.DynamicComponentCreationOutboundPort;

public class AdmissionController extends AbstractComponent 
	implements ApplicationSubmissionHandlerI, ComputerStateDataConsumerI  {
	
	public static int timer = 1000;
	
	protected int avmsPerApplication;
	protected int coresPerAVM;
		
	protected ArrayList<String> applicationVMManagementInboundPortURIList;
	protected ArrayList<String> applicationVMManagementOutboundPortURIList;	
	protected ArrayList<String> applicationVMRequestSubmissionInboundPortURIList;
	protected ArrayList<String> applicationVMRequestNotificationInboundPortURIList;
	
	protected ArrayList<String> requestDispatcherSubmissionInboundPortURIList;
	protected ArrayList<String> requestDispatcherSubmissionOutboundPortURIList;
	protected ArrayList<String> requestDispatcherNotificationInboundPortURIList;
	protected ArrayList<String> requestDispatcherNotificationOutboundPortURIList;
	
	protected ArrayList<String> computerServicesInboundPortURIList;
	protected ArrayList<String> computerStaticStateDataInboundPortURIList;
	protected ArrayList<String> computerDynamicStateDataInboundPortURIList;
	
	protected final String applicationVMURI = "";
	protected final String requestDispatcherURI = "";
	
	protected ArrayList<String> computersURIList;
	
	protected ArrayList<ComputerServicesOutboundPort> csopList;
	protected ArrayList<ComputerStaticStateDataOutboundPort> cssdopList;
	protected ArrayList<ComputerDynamicStateDataOutboundPort> cdsdopList;

	protected HashMap<String,ApplicationSubmissionInboundPort> asipMap;
	protected HashMap<String,ApplicationNotificationOutboundPort> anopMap;
	
	protected ArrayList<DynamicComponentCreationOutboundPort> portToApplicationVMList;
	protected ArrayList<DynamicComponentCreationOutboundPort> portToRequestDispatcherList;	
	protected ArrayList<ApplicationVMManagementOutboundPort> portToAVMList;
	
	protected final int numberOfComputers;
	protected final int numberOfApps;
	
	protected String dynamicComponentCreationInboundPortURI;
	protected DynamicComponentCreationOutboundPort dccop;
		
		
	public AdmissionController(
			ArrayList<String> computersURIList,			
			ArrayList<String> computerServicesInboundPortURIList,
			ArrayList<String> computerStaticStateDataInboundPortURIList,
			ArrayList<String> computerDynamicStateDataInboundPortURIList,
			ArrayList<String> appsURIList,
			ArrayList<String> applicationSubmissionInboundPortURIList,
			ArrayList<String> applicationNotificationInboundPortURIList,
			String dynamicComponentCreationInboundPortURI,
			int avmsPerApplication,
			int coresPerAVM) throws Exception {
		
		super(1, 1);
		
		assert computersURIList != null && computersURIList.size() > 0;
		assert computerServicesInboundPortURIList != null && computerServicesInboundPortURIList.size() > 0;	
		assert computerStaticStateDataInboundPortURIList != null && computerStaticStateDataInboundPortURIList.size() > 0;	
		assert computerDynamicStateDataInboundPortURIList != null && computerDynamicStateDataInboundPortURIList.size() > 0;	
		assert appsURIList != null && appsURIList.size() > 0;
		assert applicationSubmissionInboundPortURIList != null && applicationSubmissionInboundPortURIList.size() > 0;
		assert applicationNotificationInboundPortURIList != null && applicationNotificationInboundPortURIList.size() > 0;
		assert avmsPerApplication > 0;
		assert coresPerAVM > 0;
		
		this.numberOfComputers = computersURIList.size();
		this.numberOfApps = appsURIList.size();
		
		this.dynamicComponentCreationInboundPortURI = dynamicComponentCreationInboundPortURI;
		
		this.avmsPerApplication = avmsPerApplication;
		this.coresPerAVM = coresPerAVM;
	
		this.applicationVMManagementInboundPortURIList = new ArrayList<String>();
		this.applicationVMManagementOutboundPortURIList = new ArrayList<String>();
		this.applicationVMRequestSubmissionInboundPortURIList = new ArrayList<String>();
		this.applicationVMRequestNotificationInboundPortURIList = new ArrayList<String>();
		
		this.requestDispatcherSubmissionInboundPortURIList = new ArrayList<String>();
		this.requestDispatcherSubmissionOutboundPortURIList = new ArrayList<String>();
		this.requestDispatcherNotificationInboundPortURIList = new ArrayList<String>();
		this.requestDispatcherNotificationOutboundPortURIList = new ArrayList<String>();
		
		this.computersURIList = new ArrayList<String>();
		
		this.asipMap = new HashMap<String,ApplicationSubmissionInboundPort>();
		this.anopMap = new HashMap<String,ApplicationNotificationOutboundPort>();
		
		this.portToApplicationVMList = new ArrayList<DynamicComponentCreationOutboundPort>();
		this.portToRequestDispatcherList = new ArrayList<DynamicComponentCreationOutboundPort>();
		
		this.computerServicesInboundPortURIList = new ArrayList<String>();
		this.computerStaticStateDataInboundPortURIList = new ArrayList<String>();
		this.computerDynamicStateDataInboundPortURIList = new ArrayList<String>();
		
		this.csopList = new ArrayList<ComputerServicesOutboundPort>();
		this.cssdopList = new ArrayList<ComputerStaticStateDataOutboundPort>();
		this.cdsdopList = new ArrayList<ComputerDynamicStateDataOutboundPort>();
		
		this.addRequiredInterface(ApplicationVMManagementI.class);
		this.portToAVMList = new ArrayList<ApplicationVMManagementOutboundPort>();
		
		this.addRequiredInterface(ComputerServicesI.class);
		
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
				
		this.addOfferedInterface(ApplicationSubmissionI.class);
		this.addRequiredInterface(ApplicationNotificationI.class);
		
		for (int i = 0; i < numberOfApps; i++) {
			this.asipMap.put(appsURIList.get(i), new ApplicationSubmissionInboundPort(applicationSubmissionInboundPortURIList.get(i), this));		
			this.addPort(this.asipMap.get(appsURIList.get(i)));
			this.asipMap.get(appsURIList.get(i)).publishPort();		
			
			this.anopMap.put(appsURIList.get(i), new ApplicationNotificationOutboundPort(applicationNotificationInboundPortURIList.get(i), this));
			this.addPort(this.anopMap.get(appsURIList.get(i)));
			this.anopMap.get(appsURIList.get(i)).publishPort();	
		}													
		
		this.addRequiredInterface(DynamicComponentCreationI.class);
		
		// create outbound port for the Dynamic Component Creator
		this.addRequiredInterface(DynamicComponentCreationI.class);
		this.dccop = new DynamicComponentCreationOutboundPort(this);
		this.addPort(this.dccop);
		this.dccop.publishPort();
		
		assert this.csopList !=null && this.csopList.get(0) instanceof ComputerServicesOutboundPort;
		assert this.cssdopList !=null && this.cssdopList.get(0) instanceof DataRequiredI.PullI;
		assert this.cdsdopList !=null && this.cdsdopList.get(0) instanceof ControlledDataRequiredI.ControlledPullI;
		
		assert this.asipMap != null && this.asipMap.get(appsURIList.get(0)) instanceof ApplicationSubmissionI;
		assert this.anopMap != null && this.anopMap.get(appsURIList.get(0)) instanceof ApplicationNotificationI;		
	}
	
	@Override
	public void start() throws ComponentStartException {
		
		super.start();			
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
			
			this.doPortConnection(this.dccop.getPortURI(), this.dynamicComponentCreationInboundPortURI, 
					DynamicComponentCreationConnector.class.getCanonicalName());
			
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}	
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
			for (int i = 0; i < this.numberOfApps; i++) {
				if (this.portToApplicationVMList.get(i).connected()) {
					this.portToApplicationVMList.get(i).doDisconnection();
				}
			}
			for (int i = 0; i < this.numberOfApps; i++) {
				if (this.portToRequestDispatcherList.get(i).connected()) {
					this.portToRequestDispatcherList.get(i).doDisconnection();
				}
			}
		} catch (Exception e) {
			throw new ComponentShutdownException("Port disconnection error", e);
		}

		super.shutdown();
	}
	
	@Override
	public void acceptComputerStaticData(String computerURI, ComputerStaticStateI staticState) throws Exception {
		//numberOfProcessors = staticState.getNumberOfProcessors();
		//numberOfCores = staticState.getNumberOfCoresPerProcessor();	
	}
	
	@Override
	public void acceptComputerDynamicData(String computerURI, ComputerDynamicStateI currentDynamicState) throws Exception {
		//reservedCores = currentDynamicState.getCurrentCoreReservations();			
	}	
	
	@Override
	public void acceptApplicationSubmissionAndNotify (String appUri, String requestGeneratorSubmissionInboundPortURI, 
			String requestGeneratorNotificationInboundPortURI, int mustHaveCores) throws Exception {
				
		this.logMessage("Admission controller checking for available resources to execute " + appUri + " with " + mustHaveCores + " cores.");
		
		synchronized (this) {
			// Wait timer for multiple applications running simultaneously, and computer state information
			wait(timer);
			
			AllocatedCore[] allocatedCores = this.isResourceAvailable(mustHaveCores);
			
			if (allocatedCores.length > 0) {
				acceptApplication(appUri, requestGeneratorSubmissionInboundPortURI, requestGeneratorNotificationInboundPortURI, mustHaveCores, allocatedCores);
				this.anopMap.get(appUri).notifyApplicationAdmission(true);
				
			} else {
				rejectApplication(appUri);
				this.anopMap.get(appUri).notifyApplicationAdmission(false);
			} 
			
		}					
	}
	
	public AllocatedCore[] isResourceAvailable (int mustHaveCores) throws Exception {
		AllocatedCore[] allocatedCores = new AllocatedCore[0];

		for(ComputerServicesOutboundPort csop : this.csopList) {
			allocatedCores = csop.allocateCores(mustHaveCores) ;
			if(allocatedCores.length > 0) {
				return allocatedCores;
			}	
		}
		return allocatedCores;
	}

	public void acceptApplication(String appUri, String requestGeneratorSubmissionInboundPortURI, 
			String requestGeneratorNotificationInboundPortURI, int mustHaveCores, AllocatedCore[] allocatedCores) throws Exception {
		
		this.logMessage("Admission controller allowed application " + appUri + " to be executed.");
			
		deployComponents(appUri, requestGeneratorSubmissionInboundPortURI, 
				requestGeneratorNotificationInboundPortURI, avmsPerApplication);		
		this.logMessage("Admission controller deployed " + avmsPerApplication + " AVMs for " + appUri);
			
	}
	
	
	public void rejectApplication(String appUri) {
		this.logMessage("Admission controller can't accept application " + appUri + " because of lack of resources.");		
	}
	
	public void deployComponents(String appUri, String requestGeneratorSubmissionInboundPortURI, 
			String requestGeneratorNotificationInboundPortURI, int applicationVMCount) throws Exception {
						
		
		//TODO
	}
}