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
public interface ApplicationManagementI extends OfferedI, RequiredI {

	public void sendAdmissionRequest() throws Exception;
}