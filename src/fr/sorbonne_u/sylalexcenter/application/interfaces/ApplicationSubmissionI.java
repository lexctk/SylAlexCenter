package fr.sorbonne_u.sylalexcenter.application.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
/**
 * The interface <code>ApplicationSubmissionI</code> defines
 * the admission submission service used by the application component.
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public interface ApplicationSubmissionI extends OfferedI, RequiredI {
	
	void submitApplicationAndNotify(String appUri, int mustHaveCores) throws Exception;
}
