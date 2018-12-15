package fr.sorbonne_u.sylalexcenter.performancecontroller.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;
import fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces.PerformanceControllerManagementI;

import java.util.ArrayList;

public class PerformanceControllerManagementConnector extends AbstractConnector implements PerformanceControllerManagementI {

	@Override
	public void doConnectionWithRequestDispatcherForDynamicState(String requestDispatcherDynamicStateInboundPortUri) throws Exception {

		((PerformanceControllerManagementI)this.offering).
				doConnectionWithRequestDispatcherForDynamicState(requestDispatcherDynamicStateInboundPortUri);
	}

	@Override
	public void doConnectionWithComputerForDynamicState(ArrayList<String> computerDynamicStateInboundPortUri) throws Exception {

		((PerformanceControllerManagementI) this.offering).
				doConnectionWithComputerForDynamicState(computerDynamicStateInboundPortUri);
	}

	@Override
	public void notifyAVMAdded(String avmURI, AllocationMap allocationMap) throws Exception {
		((PerformanceControllerManagementI) this.offering).notifyAVMAdded(avmURI, allocationMap);
	}

	@Override
	public void notifyAVMAddRefused(String appURI) throws Exception {
		((PerformanceControllerManagementI) this.offering).notifyAVMAddRefused(appURI);
	}

	@Override
	public void notifyAVMRemoveRefused(String appURI) throws Exception {
		((PerformanceControllerManagementI) this.offering).notifyAVMRemoveRefused(appURI);
	}

	@Override
	public void notifyAVMRemoveComplete(String vmURI, String appURI) throws Exception {
		((PerformanceControllerManagementI) this.offering).notifyAVMRemoveComplete(vmURI, appURI);
	}
}
