package fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces;

import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;

import java.util.ArrayList;

public interface PerformanceControllerManagementI {

	void doConnectionWithRequestDispatcherForDynamicState(String requestDispatcherDynamicStateInboundPortUri) throws Exception;

	void doConnectionWithComputerForDynamicState(ArrayList<String> computerDynamicStateInboundPortUri) throws Exception;

	void notifyAVMAdded(String avmURI, AllocationMap allocationMap) throws Exception;

	void notifyAVMAddRefused(String appURI) throws Exception;

	void notifyAVMRemoveRefused(String appURI) throws Exception;

	void notifyAVMRemoveComplete(String vmURI, String appURI) throws Exception;
}
