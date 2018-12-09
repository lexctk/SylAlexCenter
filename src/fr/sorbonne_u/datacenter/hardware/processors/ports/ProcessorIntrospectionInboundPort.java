package fr.sorbonne_u.datacenter.hardware.processors.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.Processor;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorStaticStateI;

/**
 * The class <code>ProcessorIntrospectionInboundPort</code> defines an inbound
 * port associated with the interface <code>ProcessorIntrospectionI</code>.
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
 * Created on : January 28, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ProcessorIntrospectionInboundPort extends AbstractInboundPort implements ProcessorIntrospectionI {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public ProcessorIntrospectionInboundPort(ComponentI owner) throws Exception {
		super(ProcessorIntrospectionI.class, owner);
	}

	public ProcessorIntrospectionInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ProcessorIntrospectionI.class, owner);
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getNumberOfCores()
	 */
	@Override
	public int getNumberOfCores() throws Exception {
		return this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<>() {
			@Override
			public Integer call() throws Exception {
				return ((Processor) this.getOwner()).getNumberOfCores();
			}
		});
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getDefaultFrequency()
	 */
	@Override
	public int getDefaultFrequency() throws Exception {
		return this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<>() {
			@Override
			public Integer call() throws Exception {
				return ((Processor) this.getOwner()).getDefaultFrequency();
			}
		});
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getMaxFrequencyGap()
	 */
	@Override
	public int getMaxFrequencyGap() throws Exception {
		return this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<>() {
			@Override
			public Integer call() throws Exception {
				return ((Processor) this.getOwner()).getMaxFrequencyGap();
			}
		});
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#isValidCoreNo(int)
	 */
	@Override
	public boolean isValidCoreNo(final int coreNo) throws Exception {
		return this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<>() {
			@Override
			public Boolean call() throws Exception {
				return ((Processor) this.getOwner()).isValidCoreNo(coreNo);
			}
		});
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#isAdmissibleFrequency(int)
	 */
	@Override
	public boolean isAdmissibleFrequency(final int frequency) throws Exception {
		return this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<>() {
			@Override
			public Boolean call() throws Exception {
				return ((Processor) this.getOwner()).isAdmissibleFrequency(frequency);
			}
		});
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#isCurrentlyPossibleFrequencyForCore(int,
	 *      int)
	 */
	@Override
	public boolean isCurrentlyPossibleFrequencyForCore(final int coreNo, final int frequency) throws Exception {
		return this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<>() {
			@Override
			public Boolean call() throws Exception {
				return ((Processor) this.getOwner()).isCurrentlyPossibleFrequencyForCore(coreNo, frequency);
			}
		});
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getStaticState()
	 */
	@Override
	public ProcessorStaticStateI getStaticState() throws Exception {
		return this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<>() {
			@Override
			public ProcessorStaticStateI call() throws Exception {
				return ((Processor) this.getOwner()).getStaticState();
			}
		});
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getDynamicState()
	 */
	@Override
	public ProcessorDynamicStateI getDynamicState() throws Exception {
		return this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<>() {
			@Override
			public ProcessorDynamicStateI call() throws Exception {
				return ((Processor) this.getOwner()).getDynamicState();
			}
		});
	}
}
