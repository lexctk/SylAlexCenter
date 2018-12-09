package fr.sorbonne_u.datacenter.hardware.processors.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorServicesI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.TaskI;

/**
 * The class <code>ProcessorServicesConnector</code> implements a connector for
 * ports exchanging through the interface <code>ProcessorServicesI</code>.
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
 * invariant true
 * </pre>
 * 
 * <p>
 * Created on : January 19, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ProcessorServicesConnector extends AbstractConnector implements ProcessorServicesI {
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorServicesI#executeTaskOnCore(fr.sorbonne_u.datacenter.software.applicationvm.interfaces.TaskI,
	 *      int)
	 */
	@Override
	public void executeTaskOnCore(final TaskI t, final int coreNo) throws Exception {
		((ProcessorServicesI) this.offering).executeTaskOnCore(t, coreNo);
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorServicesI#executeTaskOnCoreAndNotify(fr.sorbonne_u.datacenter.software.applicationvm.interfaces.TaskI,
	 *      int, java.lang.String)
	 */
	@Override
	public void executeTaskOnCoreAndNotify(TaskI t, int coreNo, String notificationPortURI) throws Exception {
		((ProcessorServicesI) this.offering).executeTaskOnCoreAndNotify(t, coreNo, notificationPortURI);
	}
}
