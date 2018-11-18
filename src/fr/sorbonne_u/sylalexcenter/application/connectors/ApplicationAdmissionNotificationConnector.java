package fr.sorbonne_u.sylalexcenter.application.connectors;


import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationAdmissionI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationAdmissionNotificationI;

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class ApplicationAdmissionNotificationConnector extends AbstractConnector implements ApplicationAdmissionNotificationI {

	@Override
	public void acceptRequestTerminationNotification(ApplicationAdmissionI applicationAdmission) throws Exception {
		((ApplicationAdmissionNotificationI)this.offering).acceptRequestTerminationNotification(applicationAdmission);

	}
}