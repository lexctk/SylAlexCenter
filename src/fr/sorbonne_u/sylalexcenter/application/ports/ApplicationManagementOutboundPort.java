package fr.sorbonne_u.sylalexcenter.application.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.Application;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationManagementI;

public class ApplicationManagementOutboundPort extends AbstractOutboundPort implements ApplicationManagementI {

	private static final long serialVersionUID = 1L;

	public ApplicationManagementOutboundPort(ComponentI owner) throws Exception {
		super(ApplicationManagementI.class, owner);
		
		assert owner != null && owner instanceof Application;
	}

	public ApplicationManagementOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, ApplicationManagementI.class, owner);
		
		assert owner != null && owner instanceof Application;
	}

	@Override
	public void doConnectionWithDispatcherForSubmission(String requestDispatcherSubmissionInboundPortUri)
			throws Exception {
		((ApplicationManagementI)this.connector).
			doConnectionWithDispatcherForSubmission(requestDispatcherSubmissionInboundPortUri);
	}

	@Override
	public void doConnectionWithDispatcherForNotification(ReflectionOutboundPort ropDispatcher, String requestDispatcherNotificationOutboundPortUri) throws Exception {
		((ApplicationManagementI)this.connector).
			doConnectionWithDispatcherForNotification(ropDispatcher, requestDispatcherNotificationOutboundPortUri);

	}

}
