package fr.sorbonne_u.datacenter.hardware.computers.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;

/**
 * The interface <code>ComputerServicesI</code> defines the services offered by
 * <code>Computer</code> components (allocating cores).
 *
 * TODO: add the de-allocation of cores.
 *
 * <p>
 * Created on : April 9, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface ComputerServicesI extends OfferedI, RequiredI {
	/**
	 * allocate one core on this computer and return an instance of
	 * <code>AllocatedCore</code> containing the processor number, the core number
	 * and a map giving the URI of the processor inbound ports; return null if no
	 * core is available.
	 *
	 *
	 * @return an instance of <code>AllocatedCore</code> with the data about the
	 *         allocated core.
	 * @throws Exception exception
	 */
	AllocatedCore allocateCore() throws Exception;

	/**
	 * allocate up to <code>numberRequested</code> cores on this computer and return
	 * and array of <code>AllocatedCore</code> containing the data for each
	 * requested core; return an empty array if no core is available.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	numberRequested &gt; 0
	 * post	return.length &gt;= 0 and return.length &lt;= numberRequested
	 * </pre>
	 *
	 * @param numberRequested number of cores to be allocated.
	 * @return an array of instances of <code>AllocatedCore</code> with the data
	 *         about the allocated cores.
	 * @throws Exception exception
	 */
	AllocatedCore[] allocateCores(final int numberRequested) throws Exception;

	boolean increaseFrequency(int coreNo, String processorURI) throws Exception;

	boolean decreaseFrequency(int coreNo, String processorURI) throws Exception;

	void releaseCores (AllocatedCore[] allocateCores) throws Exception;
}
