package fr.sorbonne_u.datacenter.hardware.computers.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI;

/**
 * The class <code>ComputerServiceOutboundPort</code> implements an outbound
 * port requiring the <code>ComputerServicesI</code> interface.
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
public class ComputerServicesOutboundPort extends AbstractOutboundPort implements ComputerServicesI {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public ComputerServicesOutboundPort(ComponentI owner) throws Exception {
		super(ComputerServicesI.class, owner);
	}

	public ComputerServicesOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ComputerServicesI.class, owner);
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI#allocateCore()
	 */
	@Override
	public AllocatedCore allocateCore() throws Exception {
		return ((ComputerServicesI) this.connector).allocateCore();
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI#allocateCores(int)
	 */
	@Override
	public AllocatedCore[] allocateCores(final int numberRequested) throws Exception {
		return ((ComputerServicesI) this.connector).allocateCores(numberRequested);
	}

	@Override
	public boolean increaseFrequency(int coreNo, String processorURI) throws Exception{
		return ((ComputerServicesI) this.connector).increaseFrequency(coreNo, processorURI);
	}

	@Override
	public boolean decreaseFrequency(int coreNo, String processorURI) throws Exception {
		return ((ComputerServicesI) this.connector).decreaseFrequency(coreNo, processorURI);
	}
}
