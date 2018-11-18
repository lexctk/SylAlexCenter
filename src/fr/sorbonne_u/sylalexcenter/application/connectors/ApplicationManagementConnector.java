package fr.sorbonne_u.sylalexcenter.application.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationManagementI;

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class ApplicationManagementConnector extends AbstractConnector implements ApplicationManagementI {

	@Override
	public void sendAdmissionRequest() throws Exception {
		// TODO Auto-generated method stub
		((ApplicationManagementI)this.offering).sendAdmissionRequest();
	}

}
