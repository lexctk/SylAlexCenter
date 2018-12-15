package fr.sorbonne_u.sylalexcenter.performancecontroller.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces.PerformanceControllerServicesI;

public class PerformanceControllerServicesOutboundPort extends AbstractOutboundPort implements PerformanceControllerServicesI {


	public PerformanceControllerServicesOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, PerformanceControllerServicesI.class, owner);
	}

	public PerformanceControllerServicesOutboundPort(ComponentI owner) throws Exception {
		super(PerformanceControllerServicesI.class, owner);
	}

	@Override
	public void requestAddCores(String appUri, Computer.AllocatedCore[] allocatedCore) throws Exception {
		((PerformanceControllerServicesI) this.connector).requestAddCores(appUri, allocatedCore);
	}

	@Override
	public void requestRemoveCores(String appUri, Computer.AllocatedCore[] removeCores) throws Exception {
		((PerformanceControllerServicesI) this.connector).requestRemoveCores(appUri, removeCores);
	}

	@Override
	public void requestAddAVM(String appURI, String performanceControllerURI) throws Exception {
		((PerformanceControllerServicesI) this.connector).requestAddAVM(appURI, performanceControllerURI);
	}

	@Override
	public void requestRemoveAVM(String appURI, String performanceControllerURI) throws Exception {
		((PerformanceControllerServicesI) this.connector).requestRemoveAVM(appURI, performanceControllerURI);
	}

}
