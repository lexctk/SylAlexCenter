package fr.sorbonne_u.sylalexcenter.application.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationNotificationI;

public class ApplicationNotificationOutboundPort extends AbstractOutboundPort implements ApplicationNotificationI {

	private static final long serialVersionUID = 1L;

	public ApplicationNotificationOutboundPort(ComponentI owner) throws Exception {
		
		super(ApplicationNotificationI.class, owner);
	}

	@Override
	public void notifyApplicationAdmission(boolean isAccepted) throws Exception {
		
		((ApplicationNotificationI)this.connector).notifyApplicationAdmission(isAccepted);		
	}
}