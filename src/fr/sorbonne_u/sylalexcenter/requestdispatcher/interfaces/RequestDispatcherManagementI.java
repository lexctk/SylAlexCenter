package fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces;

import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;

import java.util.ArrayList;

/**
 * The interface <code>RequestDispatcherManagementI</code> defines notification methods that allow
 * the request dispatcher to receive notifications from admission controller to deploy new AVM or
 * to remove an AVM
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public interface RequestDispatcherManagementI {

	void notifyDispatcherOfNewAVM (
			String appURI,
			String performanceControllerURI,
			ArrayList<AllocationMap> allocatedMap,
			String avmURI,
			String requestDispatcherSubmissionOutboundPortURI,
			String requestDispatcherNotificationInboundPortURI
	) throws Exception;

	void notifyDispatcherNewAVMDeployed(String avmURI) throws Exception;

	void notifyDispatcherToRemoveAVM (String appURI, String performanceControllerURI)throws Exception;
}
