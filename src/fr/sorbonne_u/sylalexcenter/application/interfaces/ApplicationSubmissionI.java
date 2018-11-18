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
public interface ApplicationSubmissionI extends OfferedI, RequiredI {
	
	public void submitApplicationAndNotify (String appUri, int numCores) throws Exception;
	
}
