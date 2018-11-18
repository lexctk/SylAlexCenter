package fr.sorbonne_u.sylalexcenter.requestdispatcher.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherManagementI;

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class RequestDispatcherManagementInboundPort extends AbstractInboundPort implements RequestDispatcherManagementI {

	private static final long serialVersionUID = 1L;

	public RequestDispatcherManagementInboundPort(ComponentI owner) throws Exception {
		super(RequestDispatcherManagementI.class, owner);
		
		assert	owner != null && owner instanceof RequestDispatcher ;
	}
	
	public RequestDispatcherManagementInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestDispatcherManagementI.class, owner);
		
		assert	owner != null && owner instanceof RequestDispatcher ;
	}	

}
