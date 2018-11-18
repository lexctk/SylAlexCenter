package fr.sorbonne_u.sylalexcenter.application.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.AdmissionController;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationAdmissionI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationAdmissionNotificationI;

public class ApplicationAdmissionNotificationInboundPort extends AbstractInboundPort implements ApplicationAdmissionNotificationI {
	private static final long serialVersionUID = 1L;

	public ApplicationAdmissionNotificationInboundPort(ComponentI owner) throws Exception {
		super(ApplicationAdmissionNotificationI.class, owner);
	}
	
	public ApplicationAdmissionNotificationInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ApplicationAdmissionNotificationI.class, owner);
	}

	@Override
	public void acceptRequestTerminationNotification(ApplicationAdmissionI applicationAdmission) throws Exception {
		this.getOwner().handleRequestAsync(
			new AbstractComponent.AbstractService<Void>() {
				@Override
				public Void call() throws Exception {
					((AdmissionController)this.getOwner()).acceptRequestNotification(applicationAdmission);
					return null;
				}
			});
	}

}
