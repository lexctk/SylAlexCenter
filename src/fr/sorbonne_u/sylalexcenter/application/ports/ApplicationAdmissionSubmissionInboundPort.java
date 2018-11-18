package fr.sorbonne_u.sylalexcenter.application.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.AdmissionController;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationAdmissionI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationAdmissionSubmissionI;

public class ApplicationAdmissionSubmissionInboundPort extends AbstractInboundPort implements ApplicationAdmissionSubmissionI {

	private static final long serialVersionUID = 1L;

	public ApplicationAdmissionSubmissionInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, ApplicationAdmissionSubmissionI.class, owner);
	}
	
	public ApplicationAdmissionSubmissionInboundPort(ComponentI owner)
			throws Exception {
		super(ApplicationAdmissionSubmissionI.class, owner);
	}

	@Override
	public void setSubmissionInboundPortURI(ApplicationAdmissionI applicationAdmission) throws Exception {
		this.getOwner().handleRequestSync(
			new AbstractComponent.AbstractService<Void>() {
				@Override
				public Void call() throws Exception {
					((AdmissionController)this.getOwner()).acceptRequestSubmissionAndNotify(applicationAdmission);
					return null;
				}
			});
	}
}
