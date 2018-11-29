package fr.sorbonne_u.sylalexcenter.application.connectors;


import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationNotificationI;

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class ApplicationNotificationConnector extends AbstractConnector implements ApplicationNotificationI {

	@Override
	public void notifyApplicationAdmission (boolean isAccepted) throws Exception {
		
		((ApplicationNotificationI)this.offering).notifyApplicationAdmission(isAccepted);		
	}
}