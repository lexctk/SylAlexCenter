package fr.sorbonne_u.datacenter.hardware.processors;

import fr.sorbonne_u.datacenter.data.AbstractTimeStampedData;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI;

/**
 * The class <code>ProcessorDynamicState</code> implements objects representing
 * a snapshot of the dynamic state of a processor component to be pulled or
 * pushed through the dynamic state data interface.
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
 * invariant coresIdleStatus != null and currentCoreFrequencies != null
 * </pre>
 * 
 * <p>
 * Created on : April 7, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ProcessorDynamicState extends AbstractTimeStampedData implements ProcessorDynamicStateI {
	// ------------------------------------------------------------------------
	// Instance variables and constants
	// ------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** execution status of the processor cores. */
	private final boolean[] coresIdleStatus;
	/** current frequencies of the processor cores. */
	private final int[] currentCoreFrequencies;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * create a snapshot of the dynamic state of a processor component.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	coresIdleStatus != null and currentCoreFrequencies != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param coresIdleStatus        execution status of the processor cores.
	 * @param currentCoreFrequencies current frequencies of the processor cores.
	 */
	ProcessorDynamicState(boolean[] coresIdleStatus, int[] currentCoreFrequencies) throws Exception {
		super();

		assert coresIdleStatus != null && currentCoreFrequencies != null;

		this.coresIdleStatus = new boolean[currentCoreFrequencies.length];
		this.currentCoreFrequencies = new int[currentCoreFrequencies.length];
		for (int i = 0; i < currentCoreFrequencies.length; i++) {
			this.coresIdleStatus[i] = coresIdleStatus[i];
			this.currentCoreFrequencies[i] = currentCoreFrequencies[i];
		}
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI#getCoresIdleStatus()
	 */
	@Override
	public boolean[] getCoresIdleStatus() {
		return this.coresIdleStatus;
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI#getCoreIdleStatus(int)
	 */
	@Override
	public boolean getCoreIdleStatus(int coreNo) {
		return this.coresIdleStatus[coreNo];
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI#getCurrentCoreFrequencies()
	 */
	@Override
	public int[] getCurrentCoreFrequencies() {
		return this.currentCoreFrequencies;
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI#getCurrentCoreFrequency(int)
	 */
	@Override
	public int getCurrentCoreFrequency(int coreNo) {
		return this.currentCoreFrequencies[coreNo];
	}
}
