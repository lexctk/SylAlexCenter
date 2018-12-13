package fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces;

import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;

import java.util.ArrayList;

public interface RequestDispatcherServicesHandlerI {

	void acceptNotificationNewAVMPortsReady (
			String appURI,
			String performanceControllerURI,
			ArrayList<AllocationMap> allocatedMap,
			String avmURI,
			String requestDispatcherSubmissionOutboundPortURI,
			String requestDispatcherNotificationInboundPortURI
	) throws Exception;
}
