package fr.sorbonne_u.datacenter.hardware.processors.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import fr.sorbonne_u.datacenter.hardware.processors.UnacceptableFrequencyException;
import fr.sorbonne_u.datacenter.hardware.processors.UnavailableFrequencyException;

/**
 * The interface <code>ProcessorManagementI</code> defines methods for the
 * management (actuation) of a processor, such as modifying the frequency of
 * cores.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * The method <code>setCoreFrequency</code> changes the frequency of a given
 * core if the new frequency is admissible and currently possible, otherwise it
 * raises exceptions.
 * 
 * <p>
 * <strong>Invariant</strong>
 * </p>
 * 
 * <pre>
 * invariant true
 * </pre>
 * 
 * <p>
 * Created on : January 28, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface ProcessorManagementI extends OfferedI, RequiredI {
	/**
	 * set a new frequency for a given core on this processor; exceptions are raised
	 * if the required frequency is not admissible for this processor or not
	 * currently possible for the given core.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	this.isValidCoreNo(coreNo)
	 * post	true // no postcondition.
	 * </pre>
	 *
	 * @param coreNo number of the core to be modified.
	 * @param frequency new frequency for the given core.
	 */
	void setCoreFrequency(final int coreNo, final int frequency)
			throws UnavailableFrequencyException, UnacceptableFrequencyException, Exception;
}
