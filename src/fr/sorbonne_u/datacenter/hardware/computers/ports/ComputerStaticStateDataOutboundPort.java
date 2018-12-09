package fr.sorbonne_u.datacenter.hardware.computers.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.helpers.CVMDebugModes;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.components.ports.AbstractDataOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerStateDataConsumerI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerStaticStateDataI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerStaticStateI;

/**
 * The class <code>ComputerStaticStateDataOutboundPort</code> implements a data
 * outbound port requiring the <code>ComputerStaticStateDataI</code> interface.
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
 * Created on : April 14, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ComputerStaticStateDataOutboundPort extends AbstractDataOutboundPort implements ComputerStaticStateDataI {
	// ------------------------------------------------------------------------
	// Constants and instance variables
	// ------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	protected final String computerURI;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public ComputerStaticStateDataOutboundPort(ComponentI owner, String computerURI) throws Exception {
		super(DataRequiredI.PullI.class, DataRequiredI.PushI.class, owner);
		this.computerURI = computerURI;

		assert owner instanceof ComputerStateDataConsumerI;
	}

	public ComputerStaticStateDataOutboundPort(String uri, ComponentI owner, String computerURI) throws Exception {
		super(uri, DataRequiredI.PullI.class, DataRequiredI.PushI.class, owner);
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
		if (AbstractCVM.DEBUG_MODE.contains(CVMDebugModes.CALLING)) {
			System.out.println("ComputerStaticStateDataOutboundPort>>receive " + computerURI + " " + d);
		}

		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((ComputerStateDataConsumerI) this.getOwner()).acceptComputerStaticData(computerURI,
						(ComputerStaticStateI) d);
				return null;
			}
		});
	}
}
