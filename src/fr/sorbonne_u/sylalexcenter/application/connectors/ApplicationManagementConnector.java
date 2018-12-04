package fr.sorbonne_u.sylalexcenter.application.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationManagementI;

public class ApplicationManagementConnector extends AbstractConnector implements ApplicationManagementI {

	@Override
	public void doConnectionWithDispatcherForSubmission(String requestDispatcherSubmissionInboundPortUri)
			throws Exception {
		
		((ApplicationManagementI)this.offering).
			doConnectionWithDispatcherForSubmission(requestDispatcherSubmissionInboundPortUri);
		
	}

	@Override
	public void doConnectionWithDispatcherForNotification(ReflectionOutboundPort ropDispatcher, String requestDispatcherNotificationOutboundPortUri) throws Exception {
		
		((ApplicationManagementI)this.offering).doConnectionWithDispatcherForNotification(ropDispatcher, requestDispatcherNotificationOutboundPortUri);
		
	}

}
