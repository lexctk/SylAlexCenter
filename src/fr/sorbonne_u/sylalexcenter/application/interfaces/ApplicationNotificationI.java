package fr.sorbonne_u.sylalexcenter.application.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public interface ApplicationNotificationI extends OfferedI, RequiredI {
	
	public void notifyApplicationAdmission(boolean accepted) throws Exception;

}
