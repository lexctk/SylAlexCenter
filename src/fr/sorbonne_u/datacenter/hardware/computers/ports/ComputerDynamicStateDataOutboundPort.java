package fr.sorbonne_u.datacenter.hardware.computers.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerStateDataConsumerI;
import fr.sorbonne_u.datacenter.ports.AbstractControlledDataOutboundPort;

/**
 * The class <code>ComputerDynamicDataOutboundPort</code> implements a data
 * outbound port requiring the <code>ComputerDynamicStateDataI</code> interface.
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
 * Created on : April 15, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ComputerDynamicStateDataOutboundPort extends AbstractControlledDataOutboundPort {
	// ------------------------------------------------------------------------
	// Constants and instance variables
	// ------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	protected String computerURI;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public ComputerDynamicStateDataOutboundPort(ComponentI owner, String computerURI) throws Exception {
		super(owner);
		this.computerURI = computerURI;

		assert owner instanceof ComputerStateDataConsumerI;
	}

	public ComputerDynamicStateDataOutboundPort(String uri, ComponentI owner, String computerURI) throws Exception {
		super(uri, owner);
		this.computerURI = computerURI;

		assert owner instanceof ComputerStateDataConsumerI;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.interfaces.DataRequiredI.PushI#receive(fr.sorbonne_u.components.interfaces.DataRequiredI.DataI)
	 */
	@Override
	public void receive(DataRequiredI.DataI d) throws Exception {
		((ComputerStateDataConsumerI) this.owner).acceptComputerDynamicData(this.computerURI,
				(ComputerDynamicStateI) d);
	}
}
