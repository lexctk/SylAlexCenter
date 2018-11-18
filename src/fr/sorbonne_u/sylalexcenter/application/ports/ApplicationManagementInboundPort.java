package fr.sorbonne_u.sylalexcenter.application.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.sylalexcenter.application.Application;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationManagementI;

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class ApplicationManagementInboundPort extends AbstractInboundPort implements ApplicationManagementI {

	private static final long serialVersionUID = 1L;
	
	public ApplicationManagementInboundPort(ComponentI owner) throws Exception {
		super(ApplicationManagementI.class, owner);

		assert owner != null && owner instanceof Application;
	}

	public ApplicationManagementInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ApplicationManagementI.class, owner);

		assert	owner != null && owner instanceof Application;
	}

	@Override
	public void sendAdmissionRequest() throws Exception {
		this.getOwner().handleRequestSync(
			new AbstractComponent.AbstractService<Void>() {
				@Override
				public Void call() throws Exception {
						((Application)this.getOwner()).sendAdmissionRequest();
						return null;
				}
		}) ;
		
	}
	
}