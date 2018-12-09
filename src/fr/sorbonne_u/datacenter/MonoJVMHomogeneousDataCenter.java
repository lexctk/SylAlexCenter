package fr.sorbonne_u.datacenter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;

/**
 * The class <code>MonoJVMDataCenter</code> defines the basis of a simulated
 * data center deployed as a component-based simulation in a single JVM.
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
 * Created on : August 26, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class MonoJVMHomogeneousDataCenter extends AbstractCVM {
	// ------------------------------------------------------------------------
	// Constants and instance variables
	// ------------------------------------------------------------------------

	private static final int NUMBER_OF_COMPUTERS = 10;
	private static final int NUMBER_OF_PROCESSORS_PER_COMPUTER = 2;
	private static final int NUMBER_OF_CORES_PER_PROCESSOR = 4;

	// Predefined URI of the different ports visible at the component assembly
	// level.
	private static final String ComputerServicesInboundPortURIPrefix = "cs-ibp-";
	public static final String ComputerServicesOutboundPortURIPrefix = "cs-obp";
	private static final String ComputerStaticStateDataInboundPortURIPrefix = "css-dip-";
	public static final String ComputerStaticStateDataOutboundPortURIPrefix = "css-dop-";
	private static final String ComputerDynamicStateDataInboundPortURIPrefix = "cds-dip-";
	public static final String ComputerDynamicStateDataOutboundPortURIPrefix = "cds-dop";

	/** default frequency of processors cores. */
	private int defaultFrequency;
	/**
	 * maximum difference in frequencies among cores of the same processor.
	 */
	private int maxFrequencyGap;
	/** set of admissible frequencies for processors cores. */
	private Set<Integer> admissibleFrequencies;
	/**
	 * map defining the relationship between the frequency of a core and the number
	 * of instructions per second the core can process.
	 */
	protected Map<Integer, Integer> processingPower;
	/** components simulating computers. */
	protected Computer[] computers;

	// ------------------------------------------------------------------------
	// Component virtual constructor
	// ------------------------------------------------------------------------

	/**
	 * create a set of computers in a simulated data center.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param admissibleFrequencies set of admissible frequencies for processors
	 *                              cores.
	 * @param defaultFrequency       default frequency of processors cores.
	 * @param maxFrequencyGap       maximum difference in frequencies among cores of
	 *                              the same processor.
	 * @param processingPower       map defining the relationship between the
	 *                              frequency of a core and the number of
	 *                              instructions per second the core can process.
	 */
	public MonoJVMHomogeneousDataCenter(Set<Integer> admissibleFrequencies, int defaultFrequency, int maxFrequencyGap,
			Map<Integer, Integer> processingPower) throws Exception {
		super();
		this.admissibleFrequencies = new HashSet<>();
		this.admissibleFrequencies.addAll(admissibleFrequencies);
		this.defaultFrequency = defaultFrequency;
		this.maxFrequencyGap = maxFrequencyGap;
		this.processingPower = new HashMap<>();
		for (Integer fr : processingPower.keySet()) {
			this.processingPower.put(fr, processingPower.get(fr));
		}

		this.computers = new Computer[NUMBER_OF_COMPUTERS];
	}

	// ------------------------------------------------------------------------
	// Component virtual machine methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void deploy() throws Exception {
		for (int c = 0; c < NUMBER_OF_COMPUTERS; c++) {
			// ----------------------------------------------------------------
			// Create and deploy a computer component with its processors.
			// ----------------------------------------------------------------
			this.computers[c] = new Computer(
					"computer-" + c,
					this.admissibleFrequencies,
					this.processingPower,
					this.defaultFrequency,
					this.maxFrequencyGap, // max frequency gap within a processor
					NUMBER_OF_PROCESSORS_PER_COMPUTER, NUMBER_OF_CORES_PER_PROCESSOR,
					ComputerServicesInboundPortURIPrefix + c,
					ComputerStaticStateDataInboundPortURIPrefix + c,
					ComputerDynamicStateDataInboundPortURIPrefix + c
			);
			this.addDeployedComponent(this.computers[c]);
		}

		// allow to complete the deployment in a subclass
		this.completeDeployment();
		// close the deployment at the component virtual machine level.
		super.deploy();

	}

	private void completeDeployment() {
		// To be redefined in subclasses.
	}
}
