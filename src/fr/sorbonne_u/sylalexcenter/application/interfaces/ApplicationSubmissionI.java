package fr.sorbonne_u.sylalexcenter.application.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface ApplicationSubmissionI extends OfferedI, RequiredI {
	
	public void submitApplicationAndNotify(String appUri, int mustHaveCores) throws Exception;
}
