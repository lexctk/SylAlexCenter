package fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces;

import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;

public interface PerformanceControllerServicesHandlerI {
	void acceptRequestAddCores (String appUri, AllocatedCore[] allocatedCore) throws Exception;

	void acceptRequestRemoveCores (String appUri, AllocatedCore[] removeCores) throws Exception;

	void acceptRequestAddAVM (String appURI, String performanceControllerURI) throws Exception;
}
