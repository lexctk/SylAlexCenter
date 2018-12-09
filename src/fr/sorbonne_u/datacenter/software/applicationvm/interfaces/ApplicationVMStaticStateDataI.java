package fr.sorbonne_u.datacenter.software.applicationvm.interfaces;

import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;

/**
 * The interface <code>ApplicationVMStaticStateDataI</code> defines the data
 * exchanges to get static information from application virtual machines.
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
 * Created on : October 2, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface ApplicationVMStaticStateDataI extends DataOfferedI, DataRequiredI {
	// The data interface is defined as an external interface
	// ApplicationVMStaticStateI
}
