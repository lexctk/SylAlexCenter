package fr.sorbonne_u.sylalexcenter.application.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationSubmissionI;

/**
 * The class <code>ApplicationSubmissionConnector</code> defines a connector associated with
 * the interface <code>ApplicationSubmissionI</code>
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public class ApplicationSubmissionConnector extends AbstractConnector implements ApplicationSubmissionI {

	@Override
	public void submitApplicationAndNotify(String appUri, int mustHaveCores) throws Exception {
		
		((ApplicationSubmissionI)this.offering).submitApplicationAndNotify(appUri, mustHaveCores);		
	}
}
