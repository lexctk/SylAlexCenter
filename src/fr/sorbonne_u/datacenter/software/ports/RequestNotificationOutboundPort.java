package fr.sorbonne_u.datacenter.software.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;

/**
 * The class <code>RequestNotificationOutboundPort</code> implements the inbound
 * port requiring the interface <code>RequestNotificationI</code>.
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
 * Created on : April 9, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class RequestNotificationOutboundPort extends AbstractOutboundPort implements RequestNotificationI {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public RequestNotificationOutboundPort(ComponentI owner) throws Exception {
		super(RequestNotificationI.class, owner);
	}

	public RequestNotificationOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestNotificationI.class, owner);

		assert uri != null;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI#notifyRequestTermination(fr.sorbonne_u.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void notifyRequestTermination(RequestI r) throws Exception {
		((RequestNotificationI) this.connector).notifyRequestTermination(r);
	}
}
