package fr.sorbonne_u.sylalexcenter.application.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;

public interface ApplicationManagementI extends OfferedI, RequiredI {
	
	public void doConnectionWithDispatcherForSubmission (String requestDispatcherSubmissionInboundPortUri) 
			throws Exception;

	public void doConnectionWithDispatcherForNotification(ReflectionOutboundPort ropDispatcher, String requestDispatcherNotificationOutboundPortUri) throws Exception;
}
