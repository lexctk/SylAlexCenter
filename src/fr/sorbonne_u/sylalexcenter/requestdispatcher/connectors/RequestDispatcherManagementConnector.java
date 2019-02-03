package fr.sorbonne_u.sylalexcenter.requestdispatcher.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherManagementI;

import java.util.ArrayList;

/**
 * The class <code>RequestDispatcherManagementConnector</code> defines a connector associated with
 * the interface <code>RequestDispatcherManagementI</code>
 *
 * Allows request dispatcher to communicate with admission controller when AVM are added or removed
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
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

	@Override
	public void notifyDispatcherToRemoveAVM(String appURI, String performanceControllerURI) throws Exception {
		((RequestDispatcherManagementI)this.offering).notifyDispatcherToRemoveAVM(appURI, performanceControllerURI);
	}
}
