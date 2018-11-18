package fr.sorbonne_u.sylalexcenter.admissioncontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.interfaces.AdmissionControlerManagementI;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.ports.AdmissionControlerManagementInboundPort;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationAdmissionI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationAdmissionNotificationHandlerI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationAdmissionSubmissionHandlerI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationNotificationI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationSubmissionI;
import fr.sorbonne_u.sylalexcenter.application.ports.ApplicationNotificationInboundPort;
import fr.sorbonne_u.sylalexcenter.application.ports.ApplicationSubmissionInboundPort;
import fr.sorbonne_u.sylalexcenter.bcm.overrides.DynamicComponentCreationConnector;
import fr.sorbonne_u.sylalexcenter.bcm.overrides.DynamicComponentCreationI;
import fr.sorbonne_u.sylalexcenter.bcm.overrides.DynamicComponentCreationOutboundPort;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.connectors.RequestDispatcherManagementConnector;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.ports.RequestDispatcherManagementOutboundPort;
import fr.sorbonne_u.sylalexcenter.utils.ComputerInfo;
import fr.sorbonne_u.sylalexcenter.utils.ComputerURI;

/**
 * The class <code>AdmissionController</code> implements an admission controller.
 * 
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * The admission controller component will receive requests from an application, check
 * available resources, and if possible, deploy application vm and request dispatcher.
 *
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class AdmissionController extends AbstractComponent 
implements AdmissionControlerManagementI, ApplicationAdmissionSubmissionHandlerI, ApplicationAdmissionNotificationHandlerI {
	
	public static int DEBUG_LEVEL = 2;
	
	protected String admissionControlerURI;
	
	private static final int numberOfCores = 2;
	private static final int numberOfAVM = 2;
	private int count = 0;
	
	private Map<String, RequestDispatcherManagementOutboundPort> rdmopMap;
	private Map<String, ComputerInfo> computerInfo;
	
	private Map<String, List<AllocationMap>> allocationMap;
	
	private String dynamicComponentCreationInboundPortURI;
	private DynamicComponentCreationOutboundPort dccop;
	
	private AdmissionControlerManagementInboundPort acmip;

	private ApplicationSubmissionInboundPort asip;
	private ApplicationNotificationInboundPort anip;
	
	private Map<String, ApplicationVMManagementOutboundPort> avmopMap;
	
	private String applicationVMManagementInboundPortURI = "avmip";
	private String applicationVMRequestSubmissionInboundPortURI = "avmsip";
	private String applicationVMRequestNotificationInboundPortURI = "avmnip";
	
	private static final String vmURI = "avm";

	public AdmissionController (
			int numberOfComputers,
			List<Computer> computers,
			List<ComputerURI> computerURIsAll,
			List<ComputerMonitor> computerMonitors,
			String admissionControlerURI,
			String admissionControlerManagementInboundURI, 
			String dynamicComponentCreationInboundPortURI,
			String applicationSubmissionInboundPortURI,
			String applicationNotificationInboundPortURI
		) throws Exception {
		
		super(1, 1);
		
		assert numberOfComputers > 0;
		assert admissionControlerURI != null;
		assert admissionControlerURI != null;
		assert admissionControlerManagementInboundURI != null;
		assert dynamicComponentCreationInboundPortURI != null;
		assert applicationSubmissionInboundPortURI != null;
		assert applicationNotificationInboundPortURI != null;
		
		this.admissionControlerURI = admissionControlerURI;
		
		this.acmip = new AdmissionControlerManagementInboundPort(admissionControlerManagementInboundURI, this);
		this.addPort(this.acmip);
		this.acmip.publishPort();
		
		this.addOfferedInterface(ApplicationSubmissionI.class);
		this.asip = new ApplicationSubmissionInboundPort(applicationSubmissionInboundPortURI, this);
		this.addPort(this.asip);
		this.asip.publishPort();

		this.addOfferedInterface(ApplicationNotificationI.class);
		this.anip = new ApplicationNotificationInboundPort(applicationNotificationInboundPortURI, this);
		this.addPort(this.anip);
		this.anip.publishPort();

		this.addRequiredInterface(DynamicComponentCreationI.class);
		this.dynamicComponentCreationInboundPortURI = dynamicComponentCreationInboundPortURI;
		this.dccop = new DynamicComponentCreationOutboundPort(this);
		this.addPort(this.dccop);
		this.dccop.publishPort();
		
		this.rdmopMap = new HashMap<>();
		this.computerInfo = new HashMap<>();
		this.avmopMap = new HashMap<>();
		this.allocationMap = new HashMap<>();
		
		ComputerServicesOutboundPort csop;
		
		for (int i = 0; i < numberOfComputers; i++) {
			csop = new ComputerServicesOutboundPort(this);
			addPort(csop);
			csop.publishPort();
			computerInfo.put(computerURIsAll.get(i).getComputerUri(), new ComputerInfo(computerURIsAll.get(i), computers.get(i), csop));
			doPortConnection(csop.getPortURI(),computerURIsAll.get(i).getComputerServicesInboundPortURI(), ComputerServicesConnector.class.getCanonicalName());
		}
	}

	// Component life-cycle
	// -------------------------------------------------------------------------
	@Override
	public void start() throws ComponentStartException {
		super.start();
		
		try {
			this.doPortConnection(this.dccop.getPortURI(), dynamicComponentCreationInboundPortURI, 
					DynamicComponentCreationConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
		
	}
	
	@Override
	public void execute() throws Exception {
		super.execute();
	}
	
	@Override
	public void finalise() throws Exception {
		if (this.dccop.connected()) this.doPortDisconnection(this.dccop.getPortURI());
		super.finalise();
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {

		try {
			if (this.acmip.isPublished()) this.acmip.unpublishPort();
			if (this.asip.isPublished()) this.asip.unpublishPort();
			if (this.anip.isPublished()) this.anip.unpublishPort();
			if (this.dccop.isPublished()) this.dccop.unpublishPort();
			
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}

		super.shutdown();
	}

	// Communication
	// -------------------------------------------------------------------------
	@Override
	public void acceptRequestNotification(ApplicationAdmissionI applicationAdmission) throws Exception {
		
		this.allocationMap.get(applicationAdmission.getRequestDispatcherURI()).stream().forEach(arg0 -> {
			try {
				arg0.freeCores();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		this.allocationMap.remove(applicationAdmission.getRequestDispatcherURI());
		
		this.logMessage ("Admission controller " + this.admissionControlerURI + " freeing up resources after application terminated");
	}

	@Override
	public String acceptRequestSubmissionAndNotify (ApplicationAdmissionI applicationAdmission) throws Exception {
		assert applicationAdmission != null;
		
		List<AllocationMap> coreAllocation = allocateCores();
		
		if (coreAllocation != null) {
			String requestSubmissionInboundPortURI = deployComponents(applicationAdmission, coreAllocation);
			if (AdmissionController.DEBUG_LEVEL == 2) {
				this.logMessage ("Admission controller " + this.admissionControlerURI + " accepted application " + applicationAdmission.getApplicationManagementInboundPortURI() +
						"and required notification of application execution progress");
			}
			return requestSubmissionInboundPortURI;
		} else {
			if (AdmissionController.DEBUG_LEVEL == 2) {
				this.logMessage ("Admission controller " + this.admissionControlerURI + " rejected application " + applicationAdmission.getApplicationManagementInboundPortURI() +
						"because there aren't enough resources");
			}
			return null;
		}
	}
	
	// Methods
	// -------------------------------------------------------------------------
	private List<AllocationMap> allocateCores () throws Exception {
		
		List<AllocationMap> newAllocation = new ArrayList<>();
		
		AllocatedCore[] cores;
		
		for(int k = 0; k < numberOfAVM; k++) {
			for(ComputerInfo c: computerInfo.values()) {
				cores = c.getCsop().allocateCores(numberOfCores);
				if(cores.length == numberOfCores) {
					newAllocation.add(new AllocationMap(c.getComputer(), cores, ""));
					break;
				}
				for(AllocatedCore allocatedCore: cores) {
					c.getComputer().releaseCore(allocatedCore);
				}
			}
		}
		if (numberOfAVM == newAllocation.size()) {
			return newAllocation;
		}
		
		for (AllocationMap allocation: newAllocation) {
			allocation.freeCores();
		}
		return null;
		
	}
	
	public String deployComponents(ApplicationAdmissionI applicationAdmission, List<AllocationMap> coreAllocation) throws Exception {
		this.logMessage("AdmissionController starting component deployment");
		
		String rdURI = "rd_" + count;

		String requestDispatcherManagementInboundPortURI = "requestDispatcherManagementInboundPortURI_" + count;
		String requestDispatcherSubmissionInboundPortURI = "requestDispatcherSubmissionInboundPortURI_" + count;
		String requestDispatcherNotificationInboundPortURI = applicationAdmission.getRequestNotificationPortURI();
		applicationAdmission.setRequestDispatcherURI(rdURI);
		
		count++;
		
		List<String> avmURI = new ArrayList<>();
		List<String> avmManagementInboundPortURI = new ArrayList<>();
		List<String> avmRequestSubmissionInboundPortURI = new ArrayList<>();
		List<String> avmRequestNotificationInboundPortURI = new ArrayList<>();
		
		for(int i = 0; i < numberOfAVM; i++) {
			avmURI.add(vmURI + "_" + i);
			avmManagementInboundPortURI.add(applicationVMManagementInboundPortURI + "_" + i);
			avmRequestSubmissionInboundPortURI.add(applicationVMRequestSubmissionInboundPortURI + "_" + i);
			avmRequestNotificationInboundPortURI.add(applicationVMRequestNotificationInboundPortURI + "_" + i);
		}
		
		applicationAdmission.setRequestSubmissionPortURI(requestDispatcherSubmissionInboundPortURI);
		
		synchronized (dccop) {
			Object [] requestDispatcher = new Object[] {
					rdURI,
					requestDispatcherManagementInboundPortURI,
					requestDispatcherSubmissionInboundPortURI,
					requestDispatcherNotificationInboundPortURI,
					avmURI,
					avmRequestSubmissionInboundPortURI,
					avmRequestNotificationInboundPortURI,
			};
			
			dccop.createComponent(RequestDispatcher.class.getCanonicalName(), requestDispatcher);		
			
			RequestDispatcherManagementOutboundPort rdmop = new RequestDispatcherManagementOutboundPort(this);
			addPort(rdmop);
			rdmop.publishPort();
			doPortConnection(rdmop.getPortURI(), requestDispatcherManagementInboundPortURI, RequestDispatcherManagementConnector.class.getCanonicalName());
			rdmopMap.put(rdURI, rdmop);
			
			for(int i = 0; i < numberOfAVM; i++) {
				Object[] avm = new Object[] {
						avmURI.get(i),
						avmManagementInboundPortURI.get(i),
						avmRequestSubmissionInboundPortURI.get(i),
						avmRequestNotificationInboundPortURI.get(i)
				};
				
				dccop.createComponent(ApplicationVM.class.getCanonicalName(), avm);	
			}
			
			ApplicationVMManagementOutboundPort avmmop;
			allocationMap.put(rdURI, new ArrayList<>());
			
			for(int i = 0; i < numberOfAVM; i++) {
				avmmop = new ApplicationVMManagementOutboundPort(this);
				addPort(avmmop);
				avmmop.publishPort();
				doPortConnection(avmmop.getPortURI(), avmManagementInboundPortURI.get(i), ApplicationVMManagementConnector.class.getCanonicalName());
				avmopMap.put(avmURI.get(i), avmmop);
				
				coreAllocation.get(i).setVMUri(avmURI.get(i));
				avmmop.allocateCores(coreAllocation.get(i).getCores());
				
				allocationMap.get(rdURI).add(coreAllocation.get(i));
			}
			dccop.startComponents();
			dccop.executeComponents();
		}
		this.logMessage("AdmissionController component deployment done");
		return requestDispatcherSubmissionInboundPortURI;
	}
}
