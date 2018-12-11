package fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;

public interface PerformanceControllerServicesI extends OfferedI, RequiredI {
	void requestAddCores (String appUri, AllocatedCore[] allocatedCore) throws Exception;

	void requestRemoveCores (String appUri, AllocatedCore[] removeCores) throws Exception;
}
