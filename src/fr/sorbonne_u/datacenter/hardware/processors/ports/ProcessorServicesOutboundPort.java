package fr.sorbonne_u.datacenter.hardware.processors.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorServicesI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.TaskI;

/**
 * The class <code>ProcessorServicesOutboundPort</code> defines an outbound port
 * associated with the interface <code>ProcessorServicesI</code>.
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
 * Created on : January 19, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public class ProcessorServicesOutboundPort extends AbstractOutboundPort implements ProcessorServicesI {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public ProcessorServicesOutboundPort(ComponentI owner) throws Exception {
		super(ProcessorServicesI.class, owner);

		assert uri != null;
	}

	public ProcessorServicesOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ProcessorServicesI.class, owner);

		assert uri != null;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorServicesI#executeTaskOnCore(fr.sorbonne_u.datacenter.software.applicationvm.interfaces.TaskI,
	 *      int)
	 */
	@Override
	public void executeTaskOnCore(final TaskI t, final int coreNo) throws Exception {
		assert t != null && coreNo >= 0;

		((ProcessorServicesI) this.connector).executeTaskOnCore(t, coreNo);
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorServicesI#executeTaskOnCoreAndNotify(fr.sorbonne_u.datacenter.software.applicationvm.interfaces.TaskI,
	 *      int, java.lang.String)
	 */
	@Override
	public void executeTaskOnCoreAndNotify(final TaskI t, final int coreNo, final String notificationPortURI)
			throws Exception {
		((ProcessorServicesI) this.connector).executeTaskOnCoreAndNotify(t, coreNo, notificationPortURI);
	}
}
