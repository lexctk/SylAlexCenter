package fr.sorbonne_u.datacenter.hardware.processors.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.datacenter.hardware.processors.Processor;
import fr.sorbonne_u.datacenter.ports.AbstractControlledDataInboundPort;

/**
 * The class <code>ProcessorDynamicStateDataInboundPort</code> is the
 * server-side port to exchange dynamic state data with a processor component.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * Inbound port used to pull or push dynamic state data from a processor or to a
 * processor client. These ports must be connected by a
 * <code>ProcessorDynamicStateDataConnector</code>.
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
 * Created on : April 7, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ProcessorDynamicStateDataInboundPort extends AbstractControlledDataInboundPort {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public ProcessorDynamicStateDataInboundPort(ComponentI owner) throws Exception {
		super(owner);

		assert owner instanceof Processor;
	}

	public ProcessorDynamicStateDataInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);

		assert owner instanceof Processor;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.interfaces.DataOfferedI.PullI#get()
	 */
	@Override
	public DataOfferedI.DataI get() throws Exception {
		return this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<>() {
			@Override
			public DataOfferedI.DataI call() throws Exception {
				return ((Processor) this.getOwner()).getDynamicState();
			}
		});
	}
}
