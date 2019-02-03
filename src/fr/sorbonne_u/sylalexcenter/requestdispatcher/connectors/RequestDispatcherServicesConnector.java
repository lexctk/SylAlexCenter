package fr.sorbonne_u.sylalexcenter.requestdispatcher.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherServicesI;

import java.util.ArrayList;

/**
 * The class <code>RequestDispatcherServicesConnector</code> defines a connector associated with
 * the interface <code>RequestDispatcherServicesI</code>
 *
 * Allows request dispatcher to notify admission controller when AVM removal is complete or refused
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public class RequestDispatcherServicesConnector extends AbstractConnector
		implements RequestDispatcherServicesI {

	@Override
	public void notifyNewAVMPortsReady(
			String appURI,
			String performanceControllerURI,
			ArrayList<AllocationMap> allocatedMap,
			String avmURI,
			String requestDispatcherSubmissionOutboundPortURI,
			String requestDispatcherNotificationInboundPortURI) throws Exception {

		((RequestDispatcherServicesI) this.offering).notifyNewAVMPortsReady(appURI,
				performanceControllerURI,
				allocatedMap,
				avmURI,
				requestDispatcherSubmissionOutboundPortURI,
				requestDispatcherNotificationInboundPortURI);

	}

	@Override
	public void notifyAVMRemovalComplete(String vmURI, String appURI, String performanceControllerURI) throws Exception {
		((RequestDispatcherServicesI) this.offering).notifyAVMRemovalComplete(vmURI, appURI, performanceControllerURI);
	}

	@Override
	public void notifyAVMRemovalRefused(String appURI, String performanceControllerURI) throws Exception {
		((RequestDispatcherServicesI) this.offering).notifyAVMRemovalRefused(appURI, performanceControllerURI);
	}
}
