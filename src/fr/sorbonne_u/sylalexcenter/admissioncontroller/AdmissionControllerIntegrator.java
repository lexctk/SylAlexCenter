package fr.sorbonne_u.sylalexcenter.admissioncontroller;

import java.util.ArrayList;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.connectors.RequestDispatcherManagementConnector;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.ports.RequestDispatcherManagementOutboundPort;

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class AdmissionControllerIntegrator extends AbstractComponent {

	protected ArrayList<ApplicationVMManagementOutboundPort> avmopList;
	protected RequestDispatcherManagementOutboundPort rdmop;
	
	protected ArrayList<String> applicationVMManagementInboundPortURIList;
	protected String requestDispatcherManagementInboundPortURI;
	
	ArrayList<AllocatedCore[]> allocatedCores;
	
	public AdmissionControllerIntegrator (
			String integratorURI, 
			ArrayList<String> applicationVMManagementInboundPortURIList,
			ArrayList<AllocatedCore[]> allocatedCores,
			String requestDispatcherManagementInboundPortURI
		) throws Exception {
		
		super(integratorURI, 1, 0);
		
		assert applicationVMManagementInboundPortURIList != null && applicationVMManagementInboundPortURIList.size() > 0 ;
		assert requestDispatcherManagementInboundPortURI != null;
		
		this.applicationVMManagementInboundPortURIList = new ArrayList<String>(applicationVMManagementInboundPortURIList);
		this.requestDispatcherManagementInboundPortURI = requestDispatcherManagementInboundPortURI;
		
		this.avmopList = new ArrayList<ApplicationVMManagementOutboundPort>();
		for (int i = 0; i< applicationVMManagementInboundPortURIList.size(); i++) {
			ApplicationVMManagementOutboundPort avmop = new ApplicationVMManagementOutboundPort(this);
			this.avmopList.add(avmop);
			this.addPort(avmop);
			this.avmopList.get(i).publishPort();			
		}	
		
		this.allocatedCores = allocatedCores;
		
		this.rdmop = new RequestDispatcherManagementOutboundPort(this);
		this.addPort(rdmop);
		this.rdmop.publishPort();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public void start() throws ComponentStartException {
		try {
			for (int i = 0; i< this.applicationVMManagementInboundPortURIList.size(); i++) {
				this.doPortConnection(this.avmopList.get(i).getPortURI(), this.applicationVMManagementInboundPortURIList.get(i),
						ApplicationVMManagementConnector.class.getCanonicalName());
			}
			this.doPortConnection(this.rdmop.getPortURI(), this.requestDispatcherManagementInboundPortURI,
					RequestDispatcherManagementConnector.class.getCanonicalName());
		} catch (Exception e) {
			System.err.println("AdmissionControllerIntegrator#start(): couldn't connect ports");
			System.err.println(e);
			throw new ComponentStartException(e);
		}
		
		try {
			this.execute();
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void execute() throws Exception {
		
		for (int i = 0; i< this.avmopList.size(); i++) {
			try {
				this.avmopList.get(i).allocateCores(this.allocatedCores.get(i));
			} catch (Exception e) {
				System.err.println("AdmissionControllerIntegrator#execute(): couldn't allocate to AVM out port");
				System.err.println(e);
				throw new ComponentStartException(e);
			}
		}
		super.execute();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void finalise() throws Exception {
		
		for (int i = 0; i< this.avmopList.size(); i++) { 
			if (this.avmopList.get(i).connected()) this.doPortDisconnection(this.avmopList.get(i).getPortURI());
		}
		if (this.rdmop.connected()) this.doPortDisconnection(this.rdmop.getPortURI());
		
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			for (int i = 0; i< this.avmopList.size(); i++) { 
				if (this.avmopList.get(i).isPublished()) this.avmopList.get(i).unpublishPort();
			}
			if (this.rdmop.isPublished()) this.rdmop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdownNow()
	 */
	@Override
	public void shutdownNow() throws ComponentShutdownException {
		try {
			for (int i = 0; i< this.avmopList.size(); i++) { 
				if (this.avmopList.get(i).isPublished()) this.avmopList.get(i).unpublishPort();
			}
			if (this.rdmop.isPublished()) this.rdmop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdownNow();
	}
	
}
