package fr.sorbonne_u.datacenter.hardware.processors.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.helpers.CVMDebugModes;
import fr.sorbonne_u.datacenter.hardware.processors.UnacceptableFrequencyException;
import fr.sorbonne_u.datacenter.hardware.processors.UnavailableFrequencyException;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorManagementI;

/**
 * The class <code>ProcessorManagementConnector</code> implements a connector
 * for ports exchanging through the interface <code>ProcessorManagementI</code>.
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
 * Created on : January 30, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ProcessorManagementConnector extends AbstractConnector implements ProcessorManagementI {
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorManagementI#setCoreFrequency(int,
	 *      int)
	 */
	@Override
	public void setCoreFrequency(final int coreNo, final int frequency)
			throws UnavailableFrequencyException, UnacceptableFrequencyException, Exception {
		if (AbstractCVM.DEBUG_MODE.contains(CVMDebugModes.CALLING)) {
			System.out.println("ProcessorManagementConnector>>setCoreFrequency(" + coreNo + ", " + frequency + ")");
		}

		((ProcessorManagementI) this.offering).setCoreFrequency(coreNo, frequency);
	}
}
