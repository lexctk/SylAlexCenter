package fr.sorbonne_u.datacenter.software.applicationvm.interfaces;

import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.datacenter.interfaces.TimeStampingI;

/**
 * The interface <code>ApplicationVMStaticStateI</code> types the data objects
 * exchanged to get static information from application virtual machines.
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
 * Created on : October 2, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface ApplicationVMStaticStateI extends DataOfferedI.DataI, DataRequiredI.DataI, TimeStampingI {

}
