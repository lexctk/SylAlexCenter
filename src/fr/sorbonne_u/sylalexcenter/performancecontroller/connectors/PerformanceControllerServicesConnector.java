package fr.sorbonne_u.sylalexcenter.performancecontroller.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces.PerformanceControllerServicesI;
/**
 * The class <code>PerformanceControllerServicesConnector</code> defines a connector associated with
 * the interface <code>PerformanceControllerServicesI</code>
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public class PerformanceControllerServicesConnector extends AbstractConnector implements PerformanceControllerServicesI {

	@Override
	public void requestAddCores(String appUri, AllocatedCore[] allocatedCore) throws Exception {

		((PerformanceControllerServicesI) this.offering).requestAddCores(appUri, allocatedCore);
	}

	@Override
	public void requestRemoveCores(String appUri, AllocatedCore[] removeCores) throws Exception {

		((PerformanceControllerServicesI) this.offering).requestRemoveCores(appUri, removeCores);
	}

	@Override
	public void requestAddAVM(String appURI, String performanceControllerURI) throws Exception {
		((PerformanceControllerServicesI) this.offering).requestAddAVM(appURI, performanceControllerURI);
	}

	@Override
	public void requestRemoveAVM(String appURI, String performanceControllerURI) throws Exception {
		((PerformanceControllerServicesI) this.offering).requestRemoveAVM(appURI, performanceControllerURI);
	}
}
