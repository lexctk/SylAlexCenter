package fr.sorbonne_u.sylalexcenter.tests;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.sorbonne_u.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.sorbonne_u.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.connectors.RequestDispatcherManagementConnector;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.ports.RequestDispatcherManagementOutboundPort;

public class RequestDispatcherIntegrator extends AbstractComponent {

	protected ComputerServicesOutboundPort csop;
	protected ApplicationVMManagementOutboundPort avmop;
	protected RequestGeneratorManagementOutboundPort rgmop;
	protected RequestDispatcherManagementOutboundPort rdmop;
	
	
	protected String computerServicesInboundPortURI;
	protected String applicationVMManagementInboundPortURI;
	protected String requestGeneratorManagementInboundPortURI; 
	protected String requestDispatcherManagementInboundPortURI;
	
	public RequestDispatcherIntegrator (
			String computerServicesInboundPortURI,
			String applicationVMManagementInboundPortURI,
			String requestGeneratorManagementInboundPortURI, 
			String requestDispatcherManagementInboundPortURI
		) throws Exception {
		
		super(1, 0);
		
		assert computerServicesInboundPortURI != null;
		assert applicationVMManagementInboundPortURI != null;
		assert requestGeneratorManagementInboundPortURI != null;
		assert requestDispatcherManagementInboundPortURI != null;
		
		this.computerServicesInboundPortURI = computerServicesInboundPortURI;
		this.applicationVMManagementInboundPortURI = applicationVMManagementInboundPortURI;
		this.requestGeneratorManagementInboundPortURI = requestGeneratorManagementInboundPortURI;
		this.requestDispatcherManagementInboundPortURI = requestDispatcherManagementInboundPortURI;
		
		this.csop = new ComputerServicesOutboundPort(this);
		this.addPort(this.csop);
		this.csop.publishPort();

		this.avmop = new ApplicationVMManagementOutboundPort(this);
		this.addPort(this.avmop);
		this.avmop.publishPort();		

		this.rgmop = new RequestGeneratorManagementOutboundPort(this);
		this.addPort(rgmop);
		this.rgmop.publishPort();		
		
		this.rdmop = new RequestDispatcherManagementOutboundPort(this);
		this.addPort(rdmop);
		this.rdmop.publishPort();
		
		
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public void start() throws ComponentStartException {
		super.start();

		try {
			this.doPortConnection(this.csop.getPortURI(), this.computerServicesInboundPortURI,
					ComputerServicesConnector.class.getCanonicalName());
			this.doPortConnection(this.avmop.getPortURI(), this.applicationVMManagementInboundPortURI,
					ApplicationVMManagementConnector.class.getCanonicalName());
			this.doPortConnection(this.rgmop.getPortURI(), this.requestGeneratorManagementInboundPortURI,
					RequestGeneratorManagementConnector.class.getCanonicalName());
			this.doPortConnection(this.rdmop.getPortURI(), this.requestDispatcherManagementInboundPortURI,
					RequestDispatcherManagementConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void execute() throws Exception {
		super.execute();
		
		AllocatedCore[] ac = this.csop.allocateCores(4);
		this.avmop.allocateCores(ac);
		this.rgmop.startGeneration();
		
		// wait 20 seconds
		Thread.sleep(2000L);
		
		// then stop the generation.
		this.rgmop.stopGeneration();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void finalise() throws Exception {
		
		this.doPortDisconnection(this.csop.getPortURI());
		this.doPortDisconnection(this.avmop.getPortURI());
		this.doPortDisconnection(this.rgmop.getPortURI());
		this.doPortDisconnection(this.rdmop.getPortURI());
		
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			this.csop.unpublishPort();
			this.avmop.unpublishPort();
			this.rgmop.unpublishPort();
			this.rdmop.unpublishPort();
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
			this.csop.unpublishPort();
			this.avmop.unpublishPort();
			this.rgmop.unpublishPort();
			this.rdmop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdownNow();
	}
	
}
