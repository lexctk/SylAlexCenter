package fr.sorbonne_u.sylalexcenter.application.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;

/**
 * The interface <code>ApplicationManagementI</code> defines the connection services
 * offered to the application component in order to connect to the request dispatcher component.
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public interface ApplicationManagementI extends OfferedI, RequiredI {
	
	void doConnectionWithDispatcherForSubmission (String requestDispatcherSubmissionInboundPortUri)
			throws Exception;

	void doConnectionWithDispatcherForNotification(ReflectionOutboundPort ropDispatcher, String requestDispatcherNotificationOutboundPortUri) throws Exception;
}
