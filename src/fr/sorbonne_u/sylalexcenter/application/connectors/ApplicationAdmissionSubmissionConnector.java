package fr.sorbonne_u.sylalexcenter.application.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationSubmissionI;

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class ApplicationAdmissionSubmissionConnector extends AbstractConnector implements ApplicationSubmissionI {

@Override
public void submitApplicationAndNotify(String appUri, int mustHaveCores) throws Exception {
	
	((ApplicationSubmissionI)this.offering).submitApplicationAndNotify(appUri, mustHaveCores);		
}
}
