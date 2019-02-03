package fr.sorbonne_u.sylalexcenter.application.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.Application;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationManagementI;

/**
 * The class <code>ApplicationManagementOutboundPort</code> defines
 * an outbound port that allows the application component to connect
 * to the request dispatcher component
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public class ApplicationManagementOutboundPort extends AbstractOutboundPort implements ApplicationManagementI {

	private static final long serialVersionUID = 1L;

	public ApplicationManagementOutboundPort(ComponentI owner) throws Exception {
		super(ApplicationManagementI.class, owner);
		
		assert owner instanceof Application;
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
