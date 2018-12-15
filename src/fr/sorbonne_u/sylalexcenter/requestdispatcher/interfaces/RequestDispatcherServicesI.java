package fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces;

import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;

import java.util.ArrayList;

public interface RequestDispatcherServicesI {

	void notifyNewAVMPortsReady (
			String appURI,
			String performanceControllerURI,
			ArrayList<AllocationMap> allocatedMap,
			String avmURI,
			String requestDispatcherSubmissionOutboundPortURI,
			String requestDispatcherNotificationInboundPortURI
	) throws Exception;

	void notifyAVMRemovalComplete (String vmURI, String appURI, String performanceControllerURI) throws Exception;

	void notifyAVMRemovalRefused(String appURI, String performanceControllerURI) throws Exception;
}
