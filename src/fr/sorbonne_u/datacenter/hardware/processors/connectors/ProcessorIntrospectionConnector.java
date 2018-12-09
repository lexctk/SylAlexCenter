package fr.sorbonne_u.datacenter.hardware.processors.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorStaticStateI;

/**
 * The class <code>ProcessorIntrospectionConnector</code> implements the
 * connector between outbound and inbound ports implementing the interface
 * <code>ProcessorIntrospectionI</code>.
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
 * invariant true
 * </pre>
 * 
 * <p>
 * Created on : January 29, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ProcessorIntrospectionConnector extends AbstractConnector implements ProcessorIntrospectionI {
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getNumberOfCores()
	 */
	@Override
	public int getNumberOfCores() throws Exception {
		return ((ProcessorIntrospectionI) this.offering).getNumberOfCores();
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getDefaultFrequency()
	 */
	@Override
	public int getDefaultFrequency() throws Exception {
		return ((ProcessorIntrospectionI) this.offering).getDefaultFrequency();
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getMaxFrequencyGap()
	 */
	@Override
	public int getMaxFrequencyGap() throws Exception {
		return ((ProcessorIntrospectionI) this.offering).getMaxFrequencyGap();
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#isValidCoreNo(int)
	 */
	@Override
	public boolean isValidCoreNo(final int coreNo) throws Exception {
		return ((ProcessorIntrospectionI) this.offering).isValidCoreNo(coreNo);
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#isAdmissibleFrequency(int)
	 */
	@Override
	public boolean isAdmissibleFrequency(final int frequency) throws Exception {
		return ((ProcessorIntrospectionI) this.offering).isAdmissibleFrequency(frequency);
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#isCurrentlyPossibleFrequencyForCore(int,
	 *      int)
	 */
	@Override
	public boolean isCurrentlyPossibleFrequencyForCore(final int coreNo, final int frequency) throws Exception {
		return ((ProcessorIntrospectionI) this.offering).isCurrentlyPossibleFrequencyForCore(coreNo, frequency);
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getStaticState()
	 */
	@Override
	public ProcessorStaticStateI getStaticState() throws Exception {
		return ((ProcessorIntrospectionI) this.offering).getStaticState();
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getDynamicState()
	 */
	@Override
	public ProcessorDynamicStateI getDynamicState() throws Exception {
		return ((ProcessorIntrospectionI) this.offering).getDynamicState();
	}
}
