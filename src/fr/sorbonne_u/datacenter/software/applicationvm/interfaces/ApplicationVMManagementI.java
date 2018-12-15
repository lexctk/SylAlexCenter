package fr.sorbonne_u.datacenter.software.applicationvm.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;

/**
 * The interface <code>ApplicationVMManagementI</code> defines the methods to
 * manage an application virtual machine component.

 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface ApplicationVMManagementI extends OfferedI, RequiredI {
	/**
	 * allocate cores to the application virtual machine.
	 * @param allocatedCores array of cores already reserved provided to the VM.
	 */
	void allocateCores(AllocatedCore[] allocatedCores) throws Exception;

	void destroyComponent () throws Exception;
}
