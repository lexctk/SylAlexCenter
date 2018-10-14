package fr.sorbonne_u.sylalexcenter.requestdispatcher.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherManagementI;

public class RequestDispatcherManagementOutboundPort extends AbstractOutboundPort implements RequestDispatcherManagementI {

	private static final long serialVersionUID = 1L;

	public RequestDispatcherManagementOutboundPort(ComponentI owner) throws Exception {
		super(RequestDispatcherManagementI.class, owner);
		
		assert	owner != null && owner instanceof RequestDispatcher ;
	}
	
	public RequestDispatcherManagementOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestDispatcherManagementI.class, owner);
		
		assert	owner != null && owner instanceof RequestDispatcher ;
	}	

}
