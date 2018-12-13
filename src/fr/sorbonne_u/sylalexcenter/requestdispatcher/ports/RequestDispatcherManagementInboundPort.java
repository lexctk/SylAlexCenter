package fr.sorbonne_u.sylalexcenter.requestdispatcher.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherManagementI;

import java.util.ArrayList;

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
		
		assert owner instanceof RequestDispatcher;
	}
	
	public RequestDispatcherManagementInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestDispatcherManagementI.class, owner);
		
		assert owner instanceof RequestDispatcher;
	}

	@Override
	public void notifyDispatcherOfNewAVM(
			String appURI,
			String performanceControllerURI,
			ArrayList<AllocationMap> allocatedMap,
			String avmURI,
			String requestDispatcherSubmissionOutboundPortURI,
			String requestDispatcherNotificationInboundPortURI) throws Exception {

		final RequestDispatcher rd = (RequestDispatcher) this.owner;

		this.owner.handleRequestAsync(
			new AbstractComponent.AbstractService<Void>() {
				@Override
				public Void call() throws Exception {
					rd.notifyDispatcherOfNewAVM(
							appURI,
							performanceControllerURI,
							allocatedMap,
							avmURI,
							requestDispatcherSubmissionOutboundPortURI,
							requestDispatcherNotificationInboundPortURI);
					return null;
				}
			});
	}

	@Override
	public void notifyDispatcherNewAVMDeployed(String avmURI) throws Exception {
		final RequestDispatcher rd = (RequestDispatcher) this.owner;

		this.owner.handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						rd.notifyDispatcherNewAVMDeployed(avmURI);
						return null;
					}
				});
	}
}
