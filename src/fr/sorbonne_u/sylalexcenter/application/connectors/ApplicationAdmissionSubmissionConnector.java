package fr.sorbonne_u.sylalexcenter.application.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationAdmissionI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationAdmissionSubmissionI;

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class ApplicationAdmissionSubmissionConnector extends AbstractConnector implements ApplicationAdmissionSubmissionI {

	@Override
	public void setSubmissionInboundPortURI(ApplicationAdmissionI applicationAdmission) throws Exception {
		((ApplicationAdmissionSubmissionI)this.offering).setSubmissionInboundPortURI(applicationAdmission);
	}
}
