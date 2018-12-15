package fr.sorbonne_u.sylalexcenter.performancecontroller.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;
import fr.sorbonne_u.sylalexcenter.performancecontroller.PerformanceController;
import fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces.PerformanceControllerManagementI;

import java.util.ArrayList;

public class PerformanceControllerManagementOutboundPort extends AbstractOutboundPort implements PerformanceControllerManagementI {

	public PerformanceControllerManagementOutboundPort(ComponentI owner) throws Exception {
		super(PerformanceControllerManagementI.class, owner);
		assert owner instanceof PerformanceController;
	}

	public PerformanceControllerManagementOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, PerformanceControllerManagementI.class, owner);
		assert owner instanceof PerformanceController;
	}

	@Override
	public void doConnectionWithRequestDispatcherForDynamicState (String requestDispatcherDynamicStateInboundPortUri) throws Exception {
		((PerformanceControllerManagementI) this.connector).
				doConnectionWithRequestDispatcherForDynamicState (requestDispatcherDynamicStateInboundPortUri);
	}

	@Override
	public void doConnectionWithComputerForDynamicState(ArrayList<String> computerDynamicStateInboundPortUri) throws Exception {
		((PerformanceControllerManagementI) this.connector).
				doConnectionWithComputerForDynamicState (computerDynamicStateInboundPortUri);
	}

	@Override
	public void notifyAVMAdded(String avmURI, AllocationMap allocationMap) throws Exception {
		((PerformanceControllerManagementI) this.connector).
				notifyAVMAdded (avmURI, allocationMap);
	}

	@Override
	public void notifyAVMAddRefused(String appURI) throws Exception {
		((PerformanceControllerManagementI) this.connector).notifyAVMAddRefused(appURI);
	}

	@Override
	public void notifyAVMRemoveRefused(String appURI) throws Exception {
		((PerformanceControllerManagementI) this.connector).notifyAVMRemoveRefused(appURI);
	}

	@Override
	public void notifyAVMRemoveComplete(String vmURI, String appURI) throws Exception {
		((PerformanceControllerManagementI) this.connector).notifyAVMRemoveComplete(vmURI, appURI);
	}
}
