package fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces;

import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;

import java.util.ArrayList;

/**
 * The interface <code>RequestDispatcherServicesHandlerI</code> defines notification services that allow
 * the request dispatcher to send notifications to admission controller when AVM removal is complete or refused
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public interface RequestDispatcherServicesHandlerI {

	void acceptNotificationNewAVMPortsReady (
			String appURI,
			String performanceControllerURI,
			ArrayList<AllocationMap> allocatedMap,
			String avmURI,
			String requestDispatcherSubmissionOutboundPortURI,
			String requestDispatcherNotificationInboundPortURI
	) throws Exception;

	void acceptNotificationAVMRemovalComplete(String vmURI, String appURI, String performanceControllerURI) throws Exception;

	void acceptNotificationAVMRemovalRefused(String appURI, String performanceControllerURI) throws Exception;
}
