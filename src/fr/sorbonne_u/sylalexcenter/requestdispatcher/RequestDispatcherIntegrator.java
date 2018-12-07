package fr.sorbonne_u.sylalexcenter.requestdispatcher;

import java.util.ArrayList;

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

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class RequestDispatcherIntegrator extends AbstractComponent {

	private ComputerServicesOutboundPort csop;
	private ArrayList<ApplicationVMManagementOutboundPort> avmopList;
	private RequestGeneratorManagementOutboundPort rgmop;
	private RequestDispatcherManagementOutboundPort rdmop;
	
	
	private String computerServicesInboundPortURI;
	private ArrayList<String> applicationVMManagementInboundPortURIList;
	private String requestGeneratorManagementInboundPortURI;
	private String requestDispatcherManagementInboundPortURI;
	
	public RequestDispatcherIntegrator (
			String computerServicesInboundPortURI,
			ArrayList<String> applicationVMManagementInboundPortURIList,
			String requestGeneratorManagementInboundPortURI, 
			String requestDispatcherManagementInboundPortURI
		) throws Exception {
		
		super(1, 0);
		
		assert computerServicesInboundPortURI != null;
		assert applicationVMManagementInboundPortURIList != null && applicationVMManagementInboundPortURIList.size() > 0 ;
		assert requestGeneratorManagementInboundPortURI != null;
		assert requestDispatcherManagementInboundPortURI != null;
		
		this.computerServicesInboundPortURI = computerServicesInboundPortURI;
		this.applicationVMManagementInboundPortURIList = new ArrayList<>(applicationVMManagementInboundPortURIList);
		this.requestGeneratorManagementInboundPortURI = requestGeneratorManagementInboundPortURI;
		this.requestDispatcherManagementInboundPortURI = requestDispatcherManagementInboundPortURI;
		
		this.csop = new ComputerServicesOutboundPort(this);
		this.addPort(this.csop);
		this.csop.publishPort();
		
		this.avmopList = new ArrayList<>();
		for (int i = 0; i< applicationVMManagementInboundPortURIList.size(); i++) {
			ApplicationVMManagementOutboundPort avmop = new ApplicationVMManagementOutboundPort(this);
			this.avmopList.add(avmop);
			this.addPort(avmop);
			this.avmopList.get(i).publishPort();			
		}

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
			
			for (int i = 0; i< this.applicationVMManagementInboundPortURIList.size(); i++) {
				this.doPortConnection(this.avmopList.get(i).getPortURI(), this.applicationVMManagementInboundPortURIList.get(i),
						ApplicationVMManagementConnector.class.getCanonicalName());
			}
			
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

		for (ApplicationVMManagementOutboundPort anAvmopList : this.avmopList) {
			AllocatedCore[] ac = this.csop.allocateCores(2);
			anAvmopList.allocateCores(ac);
		}
		
		// start generation
		this.rgmop.startGeneration();
		
		// wait 20 seconds
		Thread.sleep(500000L);
		
		// then stop the generation.
		this.rgmop.stopGeneration();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void finalise() throws Exception {
		
		this.doPortDisconnection(this.csop.getPortURI());
		for (ApplicationVMManagementOutboundPort anAvmopList : this.avmopList) {
			this.doPortDisconnection(anAvmopList.getPortURI());
		}
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
			for (ApplicationVMManagementOutboundPort anAvmopList : this.avmopList) {
				anAvmopList.unpublishPort();
			}
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
			for (ApplicationVMManagementOutboundPort anAvmopList : this.avmopList) {
				anAvmopList.unpublishPort();
			}
			this.rgmop.unpublishPort();
			this.rdmop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdownNow();
	}
	
}
