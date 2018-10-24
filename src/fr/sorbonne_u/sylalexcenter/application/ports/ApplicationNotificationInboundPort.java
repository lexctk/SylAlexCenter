package fr.sorbonne_u.sylalexcenter.application.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationNotificationHandlerI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationNotificationI;

public class ApplicationNotificationInboundPort extends	AbstractInboundPort implements ApplicationNotificationI {

	private static final long serialVersionUID = 1L;

	public ApplicationNotificationInboundPort(ComponentI owner) throws Exception {
		super(ApplicationNotificationI.class, owner);
	}

	public ApplicationNotificationInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, ApplicationNotificationI.class, owner);
	}

	// see RequestNotificationInboundPort example
	@Override
	public void notifyApplicationAdmission(final boolean accepted) throws Exception {
		
		this.getOwner().handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((ApplicationNotificationHandlerI) this.getOwner()).acceptApplicationAdmissionNotification (accepted);
				return null;
			}
		});		
		
	}

}
