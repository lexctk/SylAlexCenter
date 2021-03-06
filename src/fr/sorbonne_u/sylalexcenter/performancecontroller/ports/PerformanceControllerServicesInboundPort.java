package fr.sorbonne_u.sylalexcenter.performancecontroller.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces.PerformanceControllerServicesHandlerI;
import fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces.PerformanceControllerServicesI;

/**
 * The class <code>PerformanceControllerServicesInboundPort</code> defines
 * an inbound port that allows the performance controller component to connect
 * to admission controller.
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public class PerformanceControllerServicesInboundPort extends AbstractInboundPort implements PerformanceControllerServicesI {

	public PerformanceControllerServicesInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, PerformanceControllerServicesI.class, owner);
		assert uri != null;
		assert owner instanceof PerformanceControllerServicesHandlerI;
	}

	public PerformanceControllerServicesInboundPort(ComponentI owner) throws Exception {
		super(PerformanceControllerServicesI.class, owner);
		assert owner instanceof PerformanceControllerServicesHandlerI;
	}

	@Override
	public void requestAddCores(String appUri, AllocatedCore[] allocatedCore) throws Exception {
		final PerformanceControllerServicesHandlerI performanceControllerServicesHandlerI = (PerformanceControllerServicesHandlerI) this.owner;

		this.owner.handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						performanceControllerServicesHandlerI.acceptRequestAddCores(appUri, allocatedCore);
						return null;
					}
				});
	}

	@Override
	public void requestRemoveCores(String appUri, AllocatedCore[] removeCores) throws Exception {
		final PerformanceControllerServicesHandlerI performanceControllerServicesHandlerI = (PerformanceControllerServicesHandlerI) this.owner;

		this.owner.handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						performanceControllerServicesHandlerI.acceptRequestRemoveCores(appUri, removeCores);
						return null;
					}
				});
	}

	@Override
	public void requestAddAVM(String appURI, String performanceControllerURI) throws Exception {
		final PerformanceControllerServicesHandlerI performanceControllerServicesHandlerI = (PerformanceControllerServicesHandlerI) this.owner;

		this.owner.handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						performanceControllerServicesHandlerI.acceptRequestAddAVM(appURI, performanceControllerURI);
						return null;
					}
				});
	}

	@Override
	public void requestRemoveAVM(String appURI, String performanceControllerURI) throws Exception {
		final PerformanceControllerServicesHandlerI performanceControllerServicesHandlerI = (PerformanceControllerServicesHandlerI) this.owner;

		this.owner.handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						performanceControllerServicesHandlerI.acceptRequestRemoveAVM(appURI, performanceControllerURI);
						return null;
					}
				});
	}
}
