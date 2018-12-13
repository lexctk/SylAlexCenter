package fr.sorbonne_u.sylalexcenter.requestdispatcher.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherManagementI;

import java.util.ArrayList;

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class RequestDispatcherManagementConnector extends AbstractConnector implements RequestDispatcherManagementI {

	@Override
	public void notifyDispatcherOfNewAVM(
			String appURI,
			String performanceControllerURI,
			ArrayList<AllocationMap> allocatedMap,
			String avmURI,
			String requestDispatcherSubmissionOutboundPortURI,
			String requestDispatcherNotificationInboundPortURI) throws Exception {

		((RequestDispatcherManagementI)this.offering).
				notifyDispatcherOfNewAVM(
						appURI,
						performanceControllerURI,
						allocatedMap,
						avmURI,
						requestDispatcherSubmissionOutboundPortURI,
						requestDispatcherNotificationInboundPortURI);
	}

	@Override
	public void notifyDispatcherNewAVMDeployed(String avmURI) throws Exception {
		((RequestDispatcherManagementI)this.offering).notifyDispatcherNewAVMDeployed(avmURI);
	}
}
