package fr.sorbonne_u.sylalexcenter.application.interfaces;

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public interface ApplicationSubmissionHandlerI {
	
	public void	acceptApplicationSubmissionAndNotify (String appUri, String requestGeneratorSubmissionInboundPortURI, 
			String requestGeneratorNotificationInboundPortURI, int mustHaveCores) throws Exception;
}
