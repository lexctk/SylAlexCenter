package fr.sorbonne_u.datacenter.hardware.computers;

import fr.sorbonne_u.datacenter.data.AbstractTimeStampedData;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;

/**
 * The class <code>ComputerDynamicState</code> implements objects representing a
 * snapshot of the dynamic state of a computer component to be pulled or pushed
 * through the dynamic state data interface.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * <strong>Invariant</strong>
 * </p>
 * 
 * TODO: complete!
 * 
 * <pre>
 * invariant		timestamp &gt;= 0 and timestamperIP != null
 * invariant		computerURI != null and reservedCores != null
 * </pre>
 * 
 * <p>
 * Created on : April 23, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ComputerDynamicState extends AbstractTimeStampedData implements ComputerDynamicStateI {
	// ------------------------------------------------------------------------
	// Constants and instance variables
	// ------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI of the computer to which this dynamic state relates. */
	protected final String computerURI;
	/** reservation status of the cores of all computer's processors. */
	private final boolean[][] reservedCores;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * create a dynamic state object.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 *
	 * <pre>
	 * pre	computerURI != null
	 * pre	reservedCores != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param computerURI   URI of the computer to which this dynamic state relates.
	 * @param reservedCores reservation status of the cores of all computer's
	 *                      processors.
	 * @throws Exception exception
	 */
	public ComputerDynamicState(String computerURI, boolean[][] reservedCores) throws Exception {
		super();

		this.computerURI = computerURI;
		this.reservedCores = new boolean[reservedCores.length][reservedCores[0].length];
		for (int p = 0; p < reservedCores.length; p++) {
			System.arraycopy(reservedCores[p], 0, this.reservedCores[p], 0, reservedCores[0].length);
		}
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerDynamicStateI#getComputerURI()
	 */
	@Override
	public String getComputerURI() {
		return this.computerURI;
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerDynamicStateI#getCurrentCoreReservations()
	 */
	@Override
	public boolean[][] getCurrentCoreReservations() {
		// copy not to provide direct access to internal data structures.
		boolean[][] ret = new boolean[this.reservedCores.length][this.reservedCores[0].length];
		for (int i = 0; i < this.reservedCores.length; i++) {
			if (this.reservedCores[i].length >= 0)
				System.arraycopy(this.reservedCores[i], 0, ret[i], 0, this.reservedCores[i].length);
		}
		return ret;
	}
}
