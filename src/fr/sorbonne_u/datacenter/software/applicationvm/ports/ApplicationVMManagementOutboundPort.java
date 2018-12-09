package fr.sorbonne_u.datacenter.software.applicationvm.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;

/**
 * The class <code>ApplicationVMManagementOutboundPort</code> implements the
 * inbound port requiring the interface <code>ApplicationVMManagementI</code>.
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
 * Created on : August 25, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ApplicationVMManagementOutboundPort extends AbstractOutboundPort implements ApplicationVMManagementI {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public ApplicationVMManagementOutboundPort(ComponentI owner) throws Exception {
		super(ApplicationVMManagementI.class, owner);
	}

	public ApplicationVMManagementOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ApplicationVMManagementI.class, owner);
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI#allocateCores(fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore[])
	 */
	@Override
	public void allocateCores(final AllocatedCore[] allocatedCores) throws Exception {
		((ApplicationVMManagementI) this.connector).allocateCores(allocatedCores);
	}
}
