package fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces;

import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;

import java.util.ArrayList;

public interface PerformanceControllerManagementI {

	void doConnectionWithRequestDispatcherForDynamicState(String requestDispatcherDynamicStateInboundPortUri) throws Exception;

	void doConnectionWithComputerForDynamicState(ArrayList<String> computerDynamicStateInboundPortUri) throws Exception;

	void notifyAVMAdded(String avmURI, AllocationMap allocationMap) throws Exception;

	void notifyAVMRefused(String appURI) throws Exception;
}
