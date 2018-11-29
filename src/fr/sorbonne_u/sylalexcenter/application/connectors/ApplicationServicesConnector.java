package fr.sorbonne_u.sylalexcenter.application.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationServicesI;

public class ApplicationServicesConnector extends AbstractConnector implements ApplicationServicesI {

	@Override
	public void sendRequestForApplicationExecution(int coresToReserve) throws Exception {
		
		((ApplicationServicesI)this.offering).sendRequestForApplicationExecution(coresToReserve);
	}
}
