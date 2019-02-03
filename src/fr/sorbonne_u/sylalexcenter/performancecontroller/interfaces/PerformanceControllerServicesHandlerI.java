package fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces;

import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;

/**
 * The interface <code>PerformanceControllerServicesHandlerI</code> defines the methods
 * used by the performance controller component in order to communicate with the
 * admission controller when an AVM is added or removed, or when cores are added or removed.
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public interface PerformanceControllerServicesHandlerI {
	void acceptRequestAddCores (String appUri, AllocatedCore[] allocatedCore) throws Exception;

	void acceptRequestRemoveCores (String appUri, AllocatedCore[] removeCores) throws Exception;

	void acceptRequestAddAVM (String appURI, String performanceControllerURI) throws Exception;

	void acceptRequestRemoveAVM(String appURI, String performanceControllerURI) throws Exception;
}
