package fr.sorbonne_u.sylalexcenter.application.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationServicesI;

/**
 * The class <code>ApplicationServicesConnector</code> defines a connector associated with
 * the interface <code>ApplicationServicesI</code>
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public class ApplicationServicesConnector extends AbstractConnector implements ApplicationServicesI {

	@Override
	public void sendRequestForApplicationExecution(int coresToReserve) throws Exception {
		
		((ApplicationServicesI)this.offering).sendRequestForApplicationExecution(coresToReserve);
	}
}
