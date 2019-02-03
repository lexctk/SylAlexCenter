package fr.sorbonne_u.sylalexcenter.application.interfaces;

/**
 * The interface <code>ApplicationSubmissionHandlerI</code> defines
 * the admission submission service offered by the application component.
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public interface ApplicationSubmissionHandlerI {
	
	void acceptApplicationSubmissionAndNotify (String appUri, int mustHaveCores) throws Exception;
}
