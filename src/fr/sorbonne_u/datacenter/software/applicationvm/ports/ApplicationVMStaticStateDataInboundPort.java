package fr.sorbonne_u.datacenter.software.applicationvm.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI.DataI;
import fr.sorbonne_u.datacenter.interfaces.ControlledDataOfferedI;
import fr.sorbonne_u.datacenter.ports.AbstractControlledDataInboundPort;

/**
 * The class <code>ApplicationVMStaticStateDataInboundPort</code> implements an
 * inbound port for interface
 * <code>ControlledDataOfferedI.ControlledPullI</code>.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * TODO: TO BE COMPLETED
 * 
 * <p>
 * <strong>Invariant</strong>
 * </p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>
 * Created on : October 2, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ApplicationVMStaticStateDataInboundPort extends AbstractControlledDataInboundPort
		implements ControlledDataOfferedI.ControlledPullI {
	private static final long serialVersionUID = 1L;

	public ApplicationVMStaticStateDataInboundPort(ComponentI owner) throws Exception {
		super(owner);
	}

	public ApplicationVMStaticStateDataInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);
	}

	/**
	 * @see fr.sorbonne_u.components.interfaces.DataOfferedI.PullI#get()
	 */
	@Override
	public DataI get() throws Exception {
		return null;
	}
}
