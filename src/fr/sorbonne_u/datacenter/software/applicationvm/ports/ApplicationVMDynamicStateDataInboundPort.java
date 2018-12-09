package fr.sorbonne_u.datacenter.software.applicationvm.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.datacenter.interfaces.ControlledDataOfferedI;
import fr.sorbonne_u.datacenter.ports.AbstractControlledDataInboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;

/**
 * The class <code>ApplicationVMDynamicStateDataInboundPort</code> implements an
 * inbound port for interface
 *
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ApplicationVMDynamicStateDataInboundPort extends AbstractControlledDataInboundPort
		implements ControlledDataOfferedI.ControlledPullI {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public ApplicationVMDynamicStateDataInboundPort(ComponentI owner) throws Exception {
		super(owner);

		assert owner instanceof ApplicationVM;
	}

	public ApplicationVMDynamicStateDataInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);

		assert owner instanceof ApplicationVM;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.interfaces.DataOfferedI.PullI#get()
	 */
	@Override
	public DataOfferedI.DataI get() throws Exception {
		return null;
	}
}
