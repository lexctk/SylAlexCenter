package fr.sorbonne_u.sylalexcenter.application.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationManagementI;

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class ApplicationManagementOutboundPort extends AbstractOutboundPort implements ApplicationManagementI {

	private static final long serialVersionUID = 1L;

	public ApplicationManagementOutboundPort(ComponentI owner) throws Exception {
		super(ApplicationManagementI.class, owner);
			
		assert owner != null;
	}
	
	public ApplicationManagementOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ApplicationManagementI.class, owner);
	
		assert uri != null && owner != null;
	}

	public void sendAdmissionRequest() throws Exception {
		((ApplicationManagementI)this.connector).sendAdmissionRequest();
	}
	
}