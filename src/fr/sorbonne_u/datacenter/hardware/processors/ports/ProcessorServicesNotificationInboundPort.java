package fr.sorbonne_u.datacenter.hardware.processors.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.helpers.CVMDebugModes;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorServicesNotificationConsumerI;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorServicesNotificationI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.TaskI;

/**
 * The class <code>ProcessorServicesNotificationInboundPort</code> defines an
 * inbound port associated with the interface
 * <code>ProcessorServicesNotificationI</code>.
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
 * Created on : April 24 , 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ProcessorServicesNotificationInboundPort extends AbstractInboundPort
		implements ProcessorServicesNotificationI {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public ProcessorServicesNotificationInboundPort(ComponentI owner) throws Exception {
		super(ProcessorServicesNotificationI.class, owner);
		assert owner instanceof ProcessorServicesNotificationConsumerI;
	}

	public ProcessorServicesNotificationInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ProcessorServicesNotificationI.class, owner);
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorServicesNotificationI#notifyEndOfTask(fr.sorbonne_u.datacenter.software.applicationvm.interfaces.TaskI)
	 */
	@Override
	public void notifyEndOfTask(final TaskI t) throws Exception {
		if (AbstractCVM.DEBUG_MODE.contains(CVMDebugModes.CALLING)) {
			System.out.println("ProcessorServicesNotificationInboundPort>>notifyEndOfTask(" + t.getTaskURI() + ")");
		}

		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((ProcessorServicesNotificationConsumerI) this.getOwner()).acceptNotifyEndOfTask(t);
				return null;
			}
		});
	}
}
