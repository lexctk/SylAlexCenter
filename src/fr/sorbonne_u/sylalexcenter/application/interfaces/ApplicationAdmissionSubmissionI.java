package fr.sorbonne_u.sylalexcenter.application.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface ApplicationAdmissionSubmissionI extends OfferedI, RequiredI {
	
	public void setSubmissionInboundPortURI(ApplicationAdmissionI applicationAdmission) throws Exception;
}
