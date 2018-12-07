package fr.sorbonne_u.sylalexcenter.application.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.sylalexcenter.application.Application;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationServicesI;

public class ApplicationServicesInboundPort extends AbstractInboundPort implements ApplicationServicesI {

	private static final long serialVersionUID = 1L;

	public ApplicationServicesInboundPort(String uri, ComponentI owner) throws Exception {
		
		super(uri, ApplicationServicesI.class, owner);

		assert owner instanceof Application;
	}
		
	@Override
	public void sendRequestForApplicationExecution(int coresToReserve) throws Exception {		
		
		final Application app = (Application) this.owner;
		
		this.owner.handleRequestAsync(
			new AbstractComponent.AbstractService<Void>() {
				@Override
				public Void call() throws Exception {
					app.sendRequestForApplicationExecution(coresToReserve);
					return null;
				}
			});		
	}
}