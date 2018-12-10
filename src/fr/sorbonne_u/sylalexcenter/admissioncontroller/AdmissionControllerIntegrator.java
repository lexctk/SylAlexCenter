package fr.sorbonne_u.sylalexcenter.admissioncontroller;

import java.util.ArrayList;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class AdmissionControllerIntegrator extends AbstractComponent {

	private ArrayList<ApplicationVMManagementOutboundPort> avmopList;
	
	private ArrayList<String> applicationVMManagementInboundPortURIList;
	
	private ArrayList<AllocationMap> allocatedMap;
	
	public AdmissionControllerIntegrator (
			String integratorURI, 
			ArrayList<String> applicationVMManagementInboundPortURIList,
			ArrayList<AllocationMap> allocatedMap
		) throws Exception {
		
		super(integratorURI, 1, 0);
		
		assert applicationVMManagementInboundPortURIList != null && applicationVMManagementInboundPortURIList.size() > 0 ;
		
		this.applicationVMManagementInboundPortURIList = new ArrayList<>(applicationVMManagementInboundPortURIList);
		
		this.avmopList = new ArrayList<>();
		for (int i = 0; i< applicationVMManagementInboundPortURIList.size(); i++) {
			ApplicationVMManagementOutboundPort avmop = new ApplicationVMManagementOutboundPort(this);
			this.avmopList.add(avmop);
			this.addPort(avmop);
			this.avmopList.get(i).publishPort();			
		}	
		
		this.allocatedMap = allocatedMap;
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
		} catch (Exception e) {
			throw new ComponentStartException("Couldn't connect ports" + e);
		}
		
		try {
			this.execute();
		} catch (Exception e) {
			throw new ComponentStartException("Error executing" + e);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void execute() throws Exception {
		
		for (int i = 0; i< this.avmopList.size(); i++) {
			try {
				this.avmopList.get(i).allocateCores(this.allocatedMap.get(i).getAllocatedCores());
			} catch (Exception e) {
				throw new ComponentStartException("Couldn't allocate to AVM out port" + e);
			}
		}
		super.execute();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void finalise() throws Exception {

		for (ApplicationVMManagementOutboundPort anAvmopList : this.avmopList) {
			if (anAvmopList.connected()) this.doPortDisconnection(anAvmopList.getPortURI());
		}
		
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			for (ApplicationVMManagementOutboundPort anAvmopList : this.avmopList) {
				if (anAvmopList.isPublished()) anAvmopList.unpublishPort();
			}
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
			for (ApplicationVMManagementOutboundPort anAvmopList : this.avmopList) {
				if (anAvmopList.isPublished()) anAvmopList.unpublishPort();
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdownNow();
	}
	
}
