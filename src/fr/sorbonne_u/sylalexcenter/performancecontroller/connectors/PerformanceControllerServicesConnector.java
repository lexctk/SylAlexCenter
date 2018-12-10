package fr.sorbonne_u.sylalexcenter.performancecontroller.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces.PerformanceControllerServicesI;

public class PerformanceControllerServicesConnector extends AbstractConnector implements PerformanceControllerServicesI {

	@Override
	public void requestAddCores(String appUri, AllocatedCore[] allocatedCore) throws Exception {

		((PerformanceControllerServicesI) this.offering).requestAddCores(appUri, allocatedCore);
	}
}
