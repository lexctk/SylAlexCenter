package fr.sorbonne_u.sylalexcenter.application.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationNotificationHandlerI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationNotificationI;

public class ApplicationNotificationInboundPort extends	AbstractInboundPort implements ApplicationNotificationI {

	private static final long serialVersionUID = 1L;

	public ApplicationNotificationInboundPort(String uri, ComponentI owner) throws Exception {
			
		super(uri, ApplicationNotificationI.class, owner);

		assert uri != null && owner instanceof ApplicationNotificationHandlerI;
	}

	@Override
	public void notifyApplicationAdmission(boolean isAccepted) throws Exception {
		
		final ApplicationNotificationHandlerI appNotificationHandlerI = (ApplicationNotificationHandlerI) this.owner;
		
		this.owner.handleRequestAsync(
			new AbstractComponent.AbstractService<Void>() {
				@Override
				public Void call() throws Exception {
					appNotificationHandlerI.acceptApplicationAdmissionNotification(isAccepted);
					return null;
				}
			});		
	}
}