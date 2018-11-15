package fr.sorbonne_u.sylalexcenter.application.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface ApplicationManagementI extends OfferedI, RequiredI {

	public void sendAdmissionRequest() throws Exception;
}