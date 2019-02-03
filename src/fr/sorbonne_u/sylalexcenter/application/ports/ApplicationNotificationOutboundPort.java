package fr.sorbonne_u.sylalexcenter.application.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationNotificationI;

/**
 * The class <code>ApplicationNotificationOutboundPort</code> defines
 * an outbound port that allows the application component to receive notifications
 * of admission
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
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