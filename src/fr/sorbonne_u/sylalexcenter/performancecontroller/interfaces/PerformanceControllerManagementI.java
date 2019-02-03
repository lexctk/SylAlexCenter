package fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces;

import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;

import java.util.ArrayList;
/**
 * The interface <code>PerformanceControllerManagementI</code> defines the connection services
 * offered to the performance controller component in order to connect to the request dispatcher
 * and computer component, as well as the notification services in order to communicate with the
 * admission controller when AVM are added or removed
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public interface PerformanceControllerManagementI {

	void doConnectionWithRequestDispatcherForDynamicState(String requestDispatcherDynamicStateInboundPortUri) throws Exception;

	void doConnectionWithComputerForDynamicState(ArrayList<String> computerDynamicStateInboundPortUri) throws Exception;

	void notifyAVMAdded(String avmURI, AllocationMap allocationMap) throws Exception;

	void notifyAVMAddRefused(String appURI) throws Exception;

	void notifyAVMRemoveRefused(String appURI) throws Exception;

	void notifyAVMRemoveComplete(String vmURI, String appURI) throws Exception;
}
