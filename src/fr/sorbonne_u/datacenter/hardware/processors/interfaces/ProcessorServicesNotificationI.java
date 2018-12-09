package fr.sorbonne_u.datacenter.hardware.processors.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.TaskI;

/**
 * The interface <code>ProcessorServicesNotificationI</code> defines the
 * notification services, namely the end of task notification.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * <strong>Invariant</strong>
 * </p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>
 * Created on : April 24, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface ProcessorServicesNotificationI extends OfferedI, RequiredI {
	/**
	 * notify the end of a task.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t task which execution has ended.
	 */
	void notifyEndOfTask(final TaskI t) throws Exception;
}
