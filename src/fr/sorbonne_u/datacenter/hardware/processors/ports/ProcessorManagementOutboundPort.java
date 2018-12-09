package fr.sorbonne_u.datacenter.hardware.processors.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.helpers.CVMDebugModes;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.UnacceptableFrequencyException;
import fr.sorbonne_u.datacenter.hardware.processors.UnavailableFrequencyException;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorManagementI;

/**
 * The class <code>ProcessorManagementOutboundPort</code> defines an outbound
 * port associated with the interface <code>ProcessorManagementI</code>.
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
 * Created on : January 30, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ProcessorManagementOutboundPort extends AbstractOutboundPort implements ProcessorManagementI {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public ProcessorManagementOutboundPort(ComponentI owner) throws Exception {
		super(ProcessorManagementI.class, owner);
	}

	public ProcessorManagementOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ProcessorManagementI.class, owner);
	}

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
			System.out.println("ProcessorManagementOutboundPort>>setCoreFrequency(" + coreNo + ", " + frequency + ")");
		}

		((ProcessorManagementI) this.connector).setCoreFrequency(coreNo, frequency);
	}
}
