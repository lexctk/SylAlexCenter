package fr.sorbonne_u.sylalexcenter.performancecontroller.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;
import fr.sorbonne_u.sylalexcenter.performancecontroller.PerformanceController;
import fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces.PerformanceControllerManagementI;

import java.util.ArrayList;

/**
 * The class <code>PerformanceControllerManagementInboundPort</code> defines
 * an inbound port that allows the performance controller component to connect
 * to request dispatcher, admission controller and computer components.
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
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
					public Void call() {
						performanceController.notifyAVMAdded(avmURI, allocationMap);
						return null;
					}
				});
	}

	@Override
	public void notifyAVMAddRefused(String appURI) throws Exception {
		final PerformanceController performanceController = (PerformanceController) this.owner;

		this.owner.handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() {
						performanceController.notifyAVMAddRefused(appURI);
						return null;
					}
				});
	}

	@Override
	public void notifyAVMRemoveRefused(String appURI) throws Exception {
		final PerformanceController performanceController = (PerformanceController) this.owner;

		this.owner.handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() {
						performanceController.notifyAVMRemoveRefused(appURI);
						return null;
					}
				});
	}

	@Override
	public void notifyAVMRemoveComplete(String vmURI, String appURI) throws Exception {
		final PerformanceController performanceController = (PerformanceController) this.owner;

		this.owner.handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() {
						performanceController.notifyAVMRemoveComplete(vmURI, appURI);
						return null;
					}
				});
	}
}
