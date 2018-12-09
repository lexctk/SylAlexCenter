package fr.sorbonne_u.datacenter.software.applicationvm.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;

/**
 * The class <code>ApplicationVMManagementConnector</code> implements a
 * connector for ports exchanging through the interface
 * <code>ApplicationVMManagementI</code>.
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
 * invariant	true
 * </pre>
 * 
 * <p>
 * Created on : August 25, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ApplicationVMManagementConnector extends AbstractConnector implements ApplicationVMManagementI {
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI#allocateCores(fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore[])
	 */
	@Override
	public void allocateCores(final AllocatedCore[] allocatedCores) throws Exception {
		((ApplicationVMManagementI) this.offering).allocateCores(allocatedCores);
	}
}
