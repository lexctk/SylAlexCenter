package fr.sorbonne_u.datacenter.hardware.computers.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.ports.AbstractControlledDataInboundPort;

/**
 * The class <code>ComputerDynamicStateDataInboundPort</code> implements a data
 * inbound port offering the <code>ComputerDynamicStateDataI</code> interface.
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
 * Created on : April 15, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ComputerDynamicStateDataInboundPort extends AbstractControlledDataInboundPort {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public ComputerDynamicStateDataInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);

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
		return this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<>() {
			@Override
			public DataOfferedI.DataI call() throws Exception {
				return ((Computer) this.getOwner()).getDynamicState();
			}
		});
	}
}
