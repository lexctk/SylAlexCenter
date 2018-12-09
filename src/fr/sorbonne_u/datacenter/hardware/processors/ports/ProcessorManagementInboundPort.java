package fr.sorbonne_u.datacenter.hardware.processors.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.helpers.CVMDebugModes;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.Processor;
import fr.sorbonne_u.datacenter.hardware.processors.UnacceptableFrequencyException;
import fr.sorbonne_u.datacenter.hardware.processors.UnavailableFrequencyException;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorManagementI;

/**
 * The class <code>ProcessorManagementInboundPort</code> defines an inbound port
 * associated with the interface <code>ProcessorManagementI</code>.
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
public class ProcessorManagementInboundPort extends AbstractInboundPort implements ProcessorManagementI {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public ProcessorManagementInboundPort(ComponentI owner) throws Exception {
		super(ProcessorManagementI.class, owner);
	}

	public ProcessorManagementInboundPort(String uri, ComponentI owner) throws Exception {
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
			System.out.println("ProcessorManagementInboundPort>>setCoreFrequency(" + coreNo + ", " + frequency + ")");
		}

		this.getOwner().handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((Processor) this.getOwner()).setCoreFrequency(coreNo, frequency);
				return null;
			}
		});
	}
}
