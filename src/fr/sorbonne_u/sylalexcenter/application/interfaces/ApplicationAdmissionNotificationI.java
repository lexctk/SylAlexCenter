package fr.sorbonne_u.sylalexcenter.application.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface ApplicationAdmissionNotificationI extends OfferedI, RequiredI {
	public void	acceptRequestTerminationNotification(ApplicationAdmissionI applicationAdmission) throws Exception ;
}
