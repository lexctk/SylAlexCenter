package fr.sorbonne_u.sylalexcenter.application.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
/**
 * The interface <code>ApplicationNotificationHandlerI</code> defines
 * the admission notification service offered by the application component.
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public interface ApplicationNotificationI extends OfferedI, RequiredI {
	
	void notifyApplicationAdmission(boolean isAccepted) throws Exception;
}
