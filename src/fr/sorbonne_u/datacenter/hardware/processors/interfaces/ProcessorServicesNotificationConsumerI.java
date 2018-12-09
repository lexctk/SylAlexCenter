package fr.sorbonne_u.datacenter.hardware.processors.interfaces;

import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.TaskI;

/**
 * The interface <code>ProcessorServicesNotificationConsumerI</code> is
 * implemented by consumers of the processor notifications.
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
 * invariant		true
 * </pre>
 * 
 * <p>
 * Created on : April 24, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface ProcessorServicesNotificationConsumerI {
	void acceptNotifyEndOfTask(final TaskI t) throws Exception;
}
