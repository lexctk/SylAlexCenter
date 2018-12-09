package fr.sorbonne_u.datacenter.hardware.processors.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.components.ports.AbstractDataOutboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorStateDataConsumerI;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorStaticStateDataI;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorStaticStateI;

/**
 * The class <code>ProcessorStaticStateDataOutboundPort</code> is the
 * client-side port to exchange static state data with a processor component.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * Outbound port used to pull or push static state data from a processor or to a
 * processor client. These ports can be connected by a simple
 * <code>DataConnector</code>.
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
public class ProcessorStaticStateDataOutboundPort extends AbstractDataOutboundPort
		implements ProcessorStaticStateDataI {
	// ------------------------------------------------------------------------
	// Constants and instance variables
	// ------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	protected final String processorURI;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public ProcessorStaticStateDataOutboundPort(ComponentI owner, String processorURI) throws Exception {
		super(DataRequiredI.PullI.class, DataRequiredI.PushI.class, owner);
		this.processorURI = processorURI;

		assert owner instanceof ProcessorStateDataConsumerI;
	}

	public ProcessorStaticStateDataOutboundPort(String uri, ComponentI owner, String processorURI) throws Exception {
		super(uri, DataRequiredI.PullI.class, DataRequiredI.PushI.class, owner);
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
	public void receive(DataRequiredI.DataI d) throws Exception {
		((ProcessorStateDataConsumerI) this.owner).acceptProcessorStaticData(this.processorURI,
				((ProcessorStaticStateI) d));
	}
}
