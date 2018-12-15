package fr.sorbonne_u.sylalexcenter.requestdispatcher.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherManagementI;

import java.util.ArrayList;

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class RequestDispatcherManagementOutboundPort extends AbstractOutboundPort implements RequestDispatcherManagementI {

	private static final long serialVersionUID = 1L;

	public RequestDispatcherManagementOutboundPort(ComponentI owner) throws Exception {
		super(RequestDispatcherManagementI.class, owner);
		
		assert owner instanceof RequestDispatcher;
	}

	@Override
	public void notifyDispatcherOfNewAVM(
			String appURI,
			String performanceControllerURI,
			ArrayList<AllocationMap> allocatedMap,
			String avmURI,
			String requestDispatcherSubmissionOutboundPortURI,
			String requestDispatcherNotificationInboundPortURI) throws Exception {

		((RequestDispatcherManagementI)this.connector).
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
		((RequestDispatcherManagementI)this.connector).notifyDispatcherNewAVMDeployed(avmURI);
	}

	@Override
	public void notifyDispatcherToRemoveAVM(String appURI, String performanceControllerURI) throws Exception {
		((RequestDispatcherManagementI)this.connector).notifyDispatcherToRemoveAVM(appURI, performanceControllerURI);
	}
}
