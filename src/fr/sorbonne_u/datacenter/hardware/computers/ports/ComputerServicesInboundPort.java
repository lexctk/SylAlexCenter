package fr.sorbonne_u.datacenter.hardware.computers.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI;

/**
 * The class <code>ComputerServicesInboundPort</code> implements an inbound port
 * offering the <code>ComputerServicesI</code> interface.
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
 * Created on : April 9, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ComputerServicesInboundPort extends AbstractInboundPort implements ComputerServicesI {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public ComputerServicesInboundPort(ComponentI owner) throws Exception {
		super(ComputerServicesI.class, owner);

		assert owner instanceof Computer;
	}

	public ComputerServicesInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ComputerServicesI.class, owner);

		assert owner instanceof Computer;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI#allocateCore()
	 */
	@Override
	public AllocatedCore allocateCore() throws Exception {
		return this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<>() {
			@Override
			public AllocatedCore call() throws Exception {
				return ((Computer) this.getOwner()).allocateCore();
			}
		});
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI#allocateCores(int)
	 */
	@Override
	public AllocatedCore[] allocateCores(final int numberRequested) throws Exception {
		return this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<>() {
			@Override
			public AllocatedCore[] call() throws Exception {
				return ((Computer) this.getOwner()).allocateCores(numberRequested);
			}
		});
	}
}
