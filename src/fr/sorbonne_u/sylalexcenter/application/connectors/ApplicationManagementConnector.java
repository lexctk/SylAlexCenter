package fr.sorbonne_u.sylalexcenter.application.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationManagementI;

public class ApplicationManagementConnector extends AbstractConnector implements ApplicationManagementI {

	@Override
	public void sendAdmissionRequest() throws Exception {
		// TODO Auto-generated method stub
		((ApplicationManagementI)this.offering).sendAdmissionRequest();
	}

}
