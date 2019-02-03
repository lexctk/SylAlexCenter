package fr.sorbonne_u.sylalexcenter.application.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.Application;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationManagementI;

/**
 * The class <code>ApplicationManagementInboundPort</code> defines
 * an inbound port that allows the application component to connect
 * to the request dispatcher component
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public class ApplicationManagementInboundPort extends AbstractInboundPort implements ApplicationManagementI {

	private static final long serialVersionUID = 1L;

	public ApplicationManagementInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, ApplicationManagementI.class, owner);
		
		assert owner instanceof Application;
	}

	@Override
	public void doConnectionWithDispatcherForSubmission(String requestDispatcherSubmissionInboundPortUri)
			throws Exception {
		
		final Application app = (Application) this.owner;
		
		this.owner.handleRequestAsync(
			new AbstractComponent.AbstractService<Void>() {
				@Override
				public Void call() throws Exception {
					app.doConnectionWithDispatcherForSubmission(requestDispatcherSubmissionInboundPortUri);
					return null;
				}
			});	
	}

	@Override
	public void doConnectionWithDispatcherForNotification(ReflectionOutboundPort ropDispatcher, String requestDispatcherNotificationOutboundPortUri) throws Exception {
		
		final Application app = (Application) this.owner;
		
		this.owner.handleRequestAsync(
			new AbstractComponent.AbstractService<Void>() {
				@Override
				public Void call() throws Exception {
					app.doConnectionWithDispatcherForNotification(ropDispatcher, requestDispatcherNotificationOutboundPortUri);
					return null;
				}
			});
		
	}

}
