package fr.sorbonne_u.sylalexcenter.application.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationSubmissionHandlerI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationSubmissionI;

public class ApplicationSubmissionInboundPort extends AbstractInboundPort implements ApplicationSubmissionI {

	private static final long serialVersionUID = 1L;
	
	public ApplicationSubmissionInboundPort(ComponentI owner) throws Exception {
			
		super(ApplicationSubmissionI.class, owner);
		
		assert owner instanceof ApplicationSubmissionHandlerI;
	}

	public ApplicationSubmissionInboundPort(String uri, ComponentI owner ) throws Exception {
		
		super(uri, ApplicationSubmissionI.class, owner);

		assert uri != null && owner instanceof ApplicationSubmissionHandlerI;
	}

	@Override
	public void submitApplicationAndNotify(String appUri, int mustHaveCores) throws Exception {
		
		final ApplicationSubmissionHandlerI appSubmissionHandler = (ApplicationSubmissionHandlerI) this.owner;

		this.owner.handleRequestAsync(
			new AbstractComponent.AbstractService<Void>() {
				@Override
				public Void call() throws Exception {
					appSubmissionHandler.acceptApplicationSubmissionAndNotify(appUri, mustHaveCores);
					return null;
				}
			});		
	}
}