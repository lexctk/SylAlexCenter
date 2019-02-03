package fr.sorbonne_u.sylalexcenter.application.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationSubmissionI;

/**
 * The class <code>ApplicationSubmissionOutboundPort</code> defines
 * an outbound port that allows the application component to send admission requests to
 * the admission controller.
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public class ApplicationSubmissionOutboundPort extends AbstractOutboundPort implements ApplicationSubmissionI {

	private static final long serialVersionUID = 1L;

	public ApplicationSubmissionOutboundPort(ComponentI owner) throws Exception {
	
		super(ApplicationSubmissionI.class, owner);
	}

	@Override
	public void submitApplicationAndNotify(String appUri, int mustHaveCores) throws Exception {
		
		((ApplicationSubmissionI)this.connector).submitApplicationAndNotify(appUri, mustHaveCores);		
	}
}