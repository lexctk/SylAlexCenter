package fr.sorbonne_u.sylalexcenter.application.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationAdmissionI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationAdmissionNotificationI;

public class ApplicationAdmissionNotificationOutboundPort extends AbstractOutboundPort
		implements ApplicationAdmissionNotificationI {

	private static final long serialVersionUID = 1L;
	
	public ApplicationAdmissionNotificationOutboundPort(ComponentI owner)
			throws Exception {
		super(ApplicationAdmissionNotificationI.class, owner);
	}

	public ApplicationAdmissionNotificationOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, ApplicationAdmissionNotificationI.class, owner);
	}

	@Override
	public void acceptRequestTerminationNotification(ApplicationAdmissionI requestAdmission) throws Exception {
		((ApplicationAdmissionNotificationI)this.connector).acceptRequestTerminationNotification(requestAdmission);
	}

}
