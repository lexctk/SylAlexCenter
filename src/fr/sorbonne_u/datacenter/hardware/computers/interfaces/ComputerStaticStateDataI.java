package fr.sorbonne_u.datacenter.hardware.computers.interfaces;

import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;

/**
 * The interface <code>ComputerStaticStateDataI</code> defines the static state
 * notification services for <code>Computer</code> components.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * The interface extends the standard <code>DataOfferedI</code> and
 * <code>DataRequiredI</code> with their methods to pull and push data.
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
 * Created on : April 7, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface ComputerStaticStateDataI extends DataOfferedI, DataRequiredI {
	// The data interface is defined as an external interface
	// ComputerStaticStateI
}
