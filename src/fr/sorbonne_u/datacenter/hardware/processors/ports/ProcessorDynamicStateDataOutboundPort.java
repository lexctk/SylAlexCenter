package fr.sorbonne_u.datacenter.hardware.processors.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorStateDataConsumerI;
import fr.sorbonne_u.datacenter.ports.AbstractControlledDataOutboundPort;

/**
 * The class <code>ProcessorDynamicStateDataOutboundPort</code> is the
 * client-side port to exchange dynamic state data with a processor component.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * Outbound port used to pull or push dynamic state data from a processor or to
 * a processor client. These ports must be connected by a
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
 * Created on : April 8, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ProcessorDynamicStateDataOutboundPort extends AbstractControlledDataOutboundPort {
	// ------------------------------------------------------------------------
	// Constants and instance variables
	// ------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	protected String processorURI;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public ProcessorDynamicStateDataOutboundPort(ComponentI owner, String processorURI) throws Exception {
		super(owner);
		this.processorURI = processorURI;

		assert owner instanceof ProcessorStateDataConsumerI;
	}

	public ProcessorDynamicStateDataOutboundPort(String uri, ComponentI owner, String processorURI) throws Exception {
		super(uri, owner);
		this.processorURI = processorURI;

		assert owner instanceof ProcessorStateDataConsumerI;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.interfaces.DataRequiredI.PushI#receive(fr.sorbonne_u.components.interfaces.DataRequiredI.DataI)
	 */
	@Override
	public void receive(final DataRequiredI.DataI d) throws Exception {
		final String uri = this.processorURI;
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((ProcessorStateDataConsumerI) this.getOwner()).acceptProcessorDynamicData(uri,
						((ProcessorDynamicStateI) d));
				return null;
			}
		});
	}
}
