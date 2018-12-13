package fr.sorbonne_u.sylalexcenter.performancecontroller.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;
import fr.sorbonne_u.sylalexcenter.performancecontroller.PerformanceController;
import fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces.PerformanceControllerManagementI;

import java.util.ArrayList;

public class PerformanceControllerManagementInboundPort extends AbstractInboundPort implements PerformanceControllerManagementI {

	public PerformanceControllerManagementInboundPort(ComponentI owner) throws Exception {

		super(PerformanceControllerManagementI.class, owner);

		assert owner instanceof PerformanceController;
	}

	public PerformanceControllerManagementInboundPort(String uri, ComponentI owner) throws Exception {

		super(uri, PerformanceControllerManagementI.class, owner);

		assert owner instanceof PerformanceController;
	}
	@Override
	public void doConnectionWithRequestDispatcherForDynamicState(String requestDispatcherDynamicStateInboundPortUri) throws Exception {

		final PerformanceController performanceController = (PerformanceController) this.owner;

		this.owner.handleRequestAsync(
			new AbstractComponent.AbstractService<Void>() {
				@Override
				public Void call() throws Exception {
					performanceController.doConnectionWithRequestDispatcherForDynamicState(requestDispatcherDynamicStateInboundPortUri);
					return null;
				}
			});
	}

	@Override
	public void doConnectionWithComputerForDynamicState(ArrayList<String> computerDynamicStateInboundPortUri) throws Exception {
		final PerformanceController performanceController = (PerformanceController) this.owner;

		this.owner.handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						performanceController.doConnectionWithComputerForDynamicState(computerDynamicStateInboundPortUri);
						return null;
					}
				});
	}

	@Override
	public void notifyAVMAdded(String avmURI, AllocationMap allocationMap) throws Exception {
		final PerformanceController performanceController = (PerformanceController) this.owner;

		this.owner.handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						performanceController.notifyAVMAdded(avmURI, allocationMap);
						return null;
					}
				});
	}

	@Override
	public void notifyAVMRefused(String appURI) throws Exception {
		final PerformanceController performanceController = (PerformanceController) this.owner;

		this.owner.handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						performanceController.notifyAVMRefused(appURI);
						return null;
					}
				});
	}
}
