package fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces;

import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;

import java.util.ArrayList;

/**
 * The interface <code>RequestDispatcherServicesI</code> defines notification services that allow
 * the request dispatcher to send notifications to admission controller when AVM removal is complete or refused
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
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
