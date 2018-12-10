package fr.sorbonne_u.datacenter.hardware.computers.interfaces;

import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.datacenter.interfaces.TimeStampingI;

/**
 * The interface <code>ComputerDynamicStateI</code> implements objects
 * representing the dynamic state information of computers transmitted through
 * the <code>ComputerDynamicStateDataI</code> interface of <code>Computer</code>
 * components.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * The interface is used to type objects pulled from or pushed by a computer
 * using a data interface in pull or push mode. It gives access to dynamic
 * information, that is information subject to changes during the existence of
 * the computer.
 * 
 * Data objects are timestamped in standard Unix local time format, with the IP
 * of the computer doing this timestamp.
 *
 * <p>
 * Created on : April 14, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface ComputerDynamicStateI extends DataOfferedI.DataI, DataRequiredI.DataI, TimeStampingI {
	/**
	 * return the computer URI.
	 *
	 * @return the computer URI.
	 */
	String getComputerURI();

	/**
	 * return a boolean 2D array containing <code>true</code> in each cell
	 * <code>(p,c)</code> if the core <code>c</code> of processor <code>p</code> is
	 * currently reserved and false otherwise.
	 *
	 * @return a boolean 2D array where true cells indicate reserved cores.
	 */
	boolean[][] getCurrentCoreReservations();

	int[][] getCurrentCoreFrequencies();
}
