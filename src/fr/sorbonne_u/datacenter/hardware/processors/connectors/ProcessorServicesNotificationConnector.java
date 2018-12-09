package fr.sorbonne_u.datacenter.hardware.processors.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.helpers.CVMDebugModes;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorServicesNotificationI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.TaskI;

/**
 * The class <code>ProcessorServicesNotificationConnector</code> implements a
 * connector for ports exchanging through the interface
 * <code>ProcessorServicesNotificationI</code>.
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
 * Created on : April 24, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ProcessorServicesNotificationConnector extends AbstractConnector
		implements ProcessorServicesNotificationI {
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorServicesNotificationI#notifyEndOfTask(fr.sorbonne_u.datacenter.software.applicationvm.interfaces.TaskI)
	 */
	@Override
	public void notifyEndOfTask(TaskI t) throws Exception {
		if (AbstractCVM.DEBUG_MODE.contains(CVMDebugModes.CALLING)) {
			System.out.println("ProcessorServicesNotificationConnector>>notifyEndOfTask(" + t.getTaskURI() + ")");
		}

		((ProcessorServicesNotificationI) this.offering).notifyEndOfTask(t);
	}
}
