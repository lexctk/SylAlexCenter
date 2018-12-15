package fr.sorbonne_u.sylalexcenter.requestdispatcher.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherServicesI;

import java.util.ArrayList;

public class RequestDispatcherServicesOutboundPort extends AbstractOutboundPort implements RequestDispatcherServicesI {

	public RequestDispatcherServicesOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestDispatcherServicesI.class, owner);
	}

	public RequestDispatcherServicesOutboundPort(ComponentI owner) throws Exception {
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

		((RequestDispatcherServicesI) this.connector).notifyNewAVMPortsReady(
				appURI,
				performanceControllerURI,
				allocatedMap,
				avmURI,
				requestDispatcherSubmissionOutboundPortURI,
				requestDispatcherNotificationInboundPortURI);
	}

	@Override
	public void notifyAVMRemovalComplete(String vmURI, String appURI, String performanceControllerURI) throws Exception {
		((RequestDispatcherServicesI) this.connector).notifyAVMRemovalComplete(vmURI, appURI, performanceControllerURI);
	}

	@Override
	public void notifyAVMRemovalRefused(String appURI, String performanceControllerURI) throws Exception {
		((RequestDispatcherServicesI) this.connector).notifyAVMRemovalRefused(appURI, performanceControllerURI);
	}
}
