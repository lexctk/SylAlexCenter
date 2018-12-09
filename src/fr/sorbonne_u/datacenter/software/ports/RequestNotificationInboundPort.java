package fr.sorbonne_u.datacenter.software.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;

/**
 * The class <code>RequestNotificationInboundPort</code> implements the inbound
 * port offering the interface <code>RequestNotificationI</code>.
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
 * invariant	uri != null and owner instanceof RequestNotificationHandlerI
 * </pre>
 * 
 * <p>
 * Created on : April 9, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class RequestNotificationInboundPort extends AbstractInboundPort implements RequestNotificationI {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	owner instanceof RequestNotificationHandlerI
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param owner owner component.
	 */
	public RequestNotificationInboundPort(ComponentI owner) throws Exception {
		super(RequestNotificationI.class, owner);

		assert owner instanceof RequestNotificationHandlerI;
		assert uri != null;
	}

	/**
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	uri != null and owner instanceof RequestNotificationHandlerI
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri   uri of the port.
	 * @param owner owner component.
	 */
	public RequestNotificationInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestNotificationI.class, owner);

		assert uri != null && owner instanceof RequestNotificationHandlerI;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI#notifyRequestTermination(fr.sorbonne_u.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void notifyRequestTermination(final RequestI r) throws Exception {
		this.getOwner().handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((RequestNotificationHandlerI) this.getOwner()).acceptRequestTerminationNotification(r);
				return null;
			}
		});
	}
}
