package fr.sorbonne_u.sylalexcenter.requestdispatcher.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherServicesI;

import java.util.ArrayList;

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
}
