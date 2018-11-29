package fr.sorbonne_u.sylalexcenter.application.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface ApplicationNotificationI extends OfferedI, RequiredI {
	
	public void notifyApplicationAdmission(boolean isAccepted) throws Exception;
}
