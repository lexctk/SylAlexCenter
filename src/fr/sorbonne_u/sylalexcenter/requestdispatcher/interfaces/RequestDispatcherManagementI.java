package fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces;

import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;

import java.util.ArrayList;

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
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
