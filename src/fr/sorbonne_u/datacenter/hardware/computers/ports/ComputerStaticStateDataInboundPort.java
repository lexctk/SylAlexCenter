package fr.sorbonne_u.datacenter.hardware.computers.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.ports.AbstractDataInboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerStaticStateDataI;

/**
 * The class <code>ComputerStaticStateDataInboundPort</code> implements a data
 * inbound port offering the <code>ComputerStaticStateDataI</code> interface.
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
public class ComputerStaticStateDataInboundPort extends AbstractDataInboundPort implements ComputerStaticStateDataI {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public ComputerStaticStateDataInboundPort(ComponentI owner) throws Exception {
		super(DataOfferedI.PullI.class, DataOfferedI.PushI.class, owner);

		assert owner instanceof Computer;
	}

	public ComputerStaticStateDataInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, DataOfferedI.PullI.class, DataOfferedI.PushI.class, owner);

		assert owner instanceof Computer;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.interfaces.DataOfferedI.PullI#get()
	 */
	@Override
	public DataOfferedI.DataI get() throws Exception {
		return this.owner.handleRequestSync(new AbstractComponent.AbstractService<>() {
			@Override
			public DataOfferedI.DataI call() throws Exception {
				return ((Computer) this.getOwner()).getStaticState();
			}
		});
	}
}
