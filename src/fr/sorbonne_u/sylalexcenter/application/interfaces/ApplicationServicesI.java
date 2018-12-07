package fr.sorbonne_u.sylalexcenter.application.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface ApplicationServicesI extends OfferedI, RequiredI {
	void sendRequestForApplicationExecution(int coresToReserve) throws Exception;
}
