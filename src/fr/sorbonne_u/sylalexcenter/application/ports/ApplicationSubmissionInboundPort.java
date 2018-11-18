package fr.sorbonne_u.sylalexcenter.application.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationSubmissionHandlerI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationSubmissionI;

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class ApplicationSubmissionInboundPort extends AbstractInboundPort implements ApplicationSubmissionI {

	private static final long serialVersionUID = 1L;

	public ApplicationSubmissionInboundPort(ComponentI owner) throws Exception {
		super(ApplicationSubmissionI.class, owner);
	}

	public ApplicationSubmissionInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, ApplicationSubmissionI.class, owner);
	}
	
	// see RequestSubmissionInboundPort example
	@Override
	public void submitApplicationAndNotify(String appUri, int numCores) throws Exception {

		this.getOwner().handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((ApplicationSubmissionHandlerI) this.getOwner()).acceptApplicationSubmissionAndNotify(appUri, numCores);
				return null;
			}
		});
	}

}
