package fr.sorbonne_u.sylalexcenter.admissioncontroller;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerStateDataConsumerI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerStaticStateDataI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
import fr.sorbonne_u.datacenter.interfaces.ControlledDataOfferedI;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenter.software.connectors.RequestNotificationConnector;
import fr.sorbonne_u.datacenter.software.connectors.RequestSubmissionConnector;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionInboundPort;

/**
 * The class <code>AdmissionController</code> implements an admission controller.
 * 
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * The admission controller component will receive requests from an application, check
 * available resources, and if possible, deploy application vm, request generator and 
 * request dispatcher.
 *
 */
public class AdmissionController extends AbstractComponent implements ComputerStateDataConsumerI, RequestSubmissionHandlerI, RequestNotificationHandlerI {
	
	protected String acURI;
	
	protected ComputerServicesOutboundPort csop;
	protected ComputerStaticStateDataOutboundPort cssdop;
	protected ComputerDynamicStateDataOutboundPort cdsdop;
	
	protected RequestSubmissionInboundPort rsip;
	protected RequestNotificationInboundPort rnip;
	
	protected String computerServicesInboundPortURI;
	protected String computerStaticStateDataInboundPortURI;
	protected String computerDynamicStateDataInboundPortURI;
	
	private ApplicationVM applicationVM;


	public AdmissionController (
			String acURI, 
			String computersURI, //single computer for now
			String computerServicesInboundPortURI,
			String computerStaticStateDataInboundPortURI,
			String computerDynamicStateDataInboundPortURI,
			String requestSubmissionInboundPortURI,
			String requestNotificationInboundPortURI
		) throws Exception {
		
		super(1, 1);
		
		assert acURI != null;
		assert computerServicesInboundPortURI != null;
		assert computerStaticStateDataInboundPortURI != null;
		assert computerDynamicStateDataInboundPortURI != null;
		assert requestSubmissionInboundPortURI != null;
		assert requestNotificationInboundPortURI != null;
		
		this.acURI = acURI;
		this.computerServicesInboundPortURI = computerServicesInboundPortURI;
		this.computerStaticStateDataInboundPortURI = computerStaticStateDataInboundPortURI;
		this.computerDynamicStateDataInboundPortURI = computerDynamicStateDataInboundPortURI;
		
		this.addRequiredInterface(ComputerServicesI.class);
		this.csop = new ComputerServicesOutboundPort (this);
		this.addPort(this.csop);
		this.csop.publishPort();			

		this.addRequiredInterface(ComputerStaticStateDataI.class);
		this.cssdop = new ComputerStaticStateDataOutboundPort (this, computersURI);
		this.addPort(this.cssdop);
		this.cssdop.publishPort();

		this.addRequiredInterface(ControlledDataOfferedI.ControlledPullI.class);
		this.cdsdop = new ComputerDynamicStateDataOutboundPort (this, computersURI);
		this.addPort(cdsdop);
		this.cdsdop.publishPort();
		
		this.addOfferedInterface(RequestSubmissionI.class);
		this.rsip = new RequestSubmissionInboundPort(requestSubmissionInboundPortURI, this);
		this.addPort(this.rsip);
		this.rsip.publishPort();

		this.addOfferedInterface(RequestNotificationI.class);
		this.rnip = new RequestNotificationInboundPort(requestNotificationInboundPortURI, this);
		this.addPort(this.rnip);
		this.rnip.publishPort();	
	
	}

	// Component life-cycle
	// -------------------------------------------------------------------------
	@Override
	public void start() throws ComponentStartException {
		super.start();
		
		try {
			this.doPortConnection(this.csop.getPortURI(), computerServicesInboundPortURI,
					RequestSubmissionConnector.class.getCanonicalName());
			this.doPortConnection(this.cssdop.getPortURI(), computerStaticStateDataInboundPortURI,
					RequestNotificationConnector.class.getCanonicalName());
			this.doPortConnection(this.cdsdop.getPortURI(), computerDynamicStateDataInboundPortURI,
					RequestNotificationConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
		
	}
	
	public void deploy() throws Exception {
		
		String applicationVMManagementInboundPortURI = "avmip";
		String applicationVMRequestSubmissionInboundPortURI = "avmrsip";
		String applicationVMRequestNotificationInboundPortURI = "avmrnip";
				
		// Deploy an AVM
		// --------------------------------------------------------------------
 		String vmURI = "avm0";
 		
		try {
			this.applicationVM = new ApplicationVM (
					vmURI, 
					applicationVMManagementInboundPortURI, 
					applicationVMRequestSubmissionInboundPortURI, 
					applicationVMRequestNotificationInboundPortURI
			);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//this.addDeployedComponent(this.applicationVM);

		this.applicationVM.toggleTracing();
		this.applicationVM.toggleLogging();
				
	}

	@Override
	public void acceptComputerStaticData(String computerURI, ComputerStaticStateI staticState) throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void acceptComputerDynamicData(String computerURI, ComputerDynamicStateI currentDynamicState)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
