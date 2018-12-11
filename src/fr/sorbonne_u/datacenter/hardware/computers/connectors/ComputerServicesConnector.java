package fr.sorbonne_u.datacenter.hardware.computers.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI;

/**
 * The class <code>ComputerServiceConnector</code> implements a connector for
 * ports exchanging through the interface <code>ComputerServicesI</code>.
 *
 * <p>
 * Created on : April 9, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ComputerServicesConnector extends AbstractConnector implements ComputerServicesI {
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI#allocateCore()
	 */
	@Override
	public AllocatedCore allocateCore() throws Exception {
		return ((ComputerServicesI) this.offering).allocateCore();
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI#allocateCores(int)
	 */
	@Override
	public AllocatedCore[] allocateCores(int numberRequested) throws Exception {
		return ((ComputerServicesI) this.offering).allocateCores(numberRequested);
	}

	@Override
	public boolean increaseFrequency(int coreNo, String processorURI) throws Exception {
		return ((ComputerServicesI) this.offering).increaseFrequency(coreNo, processorURI);
	}

	@Override
	public boolean decreaseFrequency(int coreNo, String processorURI) throws Exception {
		return ((ComputerServicesI) this.offering).decreaseFrequency(coreNo, processorURI);
	}

	@Override
	public void releaseCores(AllocatedCore[] allocateCores) throws Exception {
		((ComputerServicesI) this.offering).releaseCores(allocateCores);
	}

}
