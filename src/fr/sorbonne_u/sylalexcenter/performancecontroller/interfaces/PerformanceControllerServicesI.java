package fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;

/**
 * The interface <code>PerformanceControllerServicesI</code> defines the methods
 * used by the performance controller component in order to communicate with the
 * admission controller when an AVM is added or removed, or when cores are added or removed.
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public interface PerformanceControllerServicesI extends OfferedI, RequiredI {
	void requestAddCores (String appUri, AllocatedCore[] allocatedCore) throws Exception;

	void requestRemoveCores (String appUri, AllocatedCore[] removeCores) throws Exception;

	void requestAddAVM (String appURI, String performanceControllerURI) throws Exception;

	void requestRemoveAVM(String appURI, String performanceControllerURI) throws Exception;
}
