package fr.sorbonne_u.sylalexcenter.application.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationAdmissionI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationAdmissionSubmissionI;

public class ApplicationAdmissionSubmissionOutboundPort extends AbstractOutboundPort
		implements ApplicationAdmissionSubmissionI {

	private static final long serialVersionUID = 1L;

	public ApplicationAdmissionSubmissionOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, ApplicationAdmissionSubmissionI.class, owner);
	}
	
	public ApplicationAdmissionSubmissionOutboundPort(ComponentI owner)
			throws Exception {
		super(ApplicationAdmissionSubmissionI.class, owner);
	}

	@Override
	public void setSubmissionInboundPortURI(ApplicationAdmissionI applicationAdmission) throws Exception {
		
		((ApplicationAdmissionSubmissionI)this.connector).setSubmissionInboundPortURI(applicationAdmission);
	}
}
