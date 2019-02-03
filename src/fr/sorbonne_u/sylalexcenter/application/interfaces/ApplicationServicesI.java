package fr.sorbonne_u.sylalexcenter.application.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
/**
 * The interface <code>ApplicationServicesI</code> defines
 * the request service used by the application component to send requests for execution
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public interface ApplicationServicesI extends OfferedI, RequiredI {
	void sendRequestForApplicationExecution(int coresToReserve) throws Exception;
}

