package fr.sorbonne_u.sylalexcenter.requestdispatcher.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherServicesHandlerI;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherServicesI;

import java.util.ArrayList;

public class RequestDispatcherServicesInboundPort extends AbstractInboundPort implements RequestDispatcherServicesI {

	public RequestDispatcherServicesInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestDispatcherServicesI.class, owner);
	}

	public RequestDispatcherServicesInboundPort(ComponentI owner) throws Exception {
		super(RequestDispatcherServicesI.class, owner);
	}

	@Override
	public void notifyNewAVMPortsReady(
			String appURI,
			String performanceControllerURI,
			ArrayList<AllocationMap> allocatedMap,
			String avmURI,
			String requestDispatcherSubmissionOutboundPortURI,
			String requestDispatcherNotificationInboundPortURI) throws Exception {

		final RequestDispatcherServicesHandlerI requestDispatcherServicesHandlerI = (RequestDispatcherServicesHandlerI) this.owner;

		this.owner.handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						requestDispatcherServicesHandlerI.acceptNotificationNewAVMPortsReady(
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
	public void notifyAVMRemovalComplete(String vmURI, String appURI, String performanceControllerURI) throws Exception {
		final RequestDispatcherServicesHandlerI requestDispatcherServicesHandlerI = (RequestDispatcherServicesHandlerI) this.owner;

		this.owner.handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						requestDispatcherServicesHandlerI.acceptNotificationAVMRemovalComplete(vmURI, appURI, performanceControllerURI);
						return null;
					}
				});
	}

	@Override
	public void notifyAVMRemovalRefused(String appURI, String performanceControllerURI) throws Exception {
		final RequestDispatcherServicesHandlerI requestDispatcherServicesHandlerI = (RequestDispatcherServicesHandlerI) this.owner;

		this.owner.handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						requestDispatcherServicesHandlerI.acceptNotificationAVMRemovalRefused(appURI, performanceControllerURI);
						return null;
					}
				});
	}
}
