package fr.sorbonne_u.datacenter.software.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;

/**
 * The class <code>RequestNotificationConnector</code> implements a connector
 * for ports exchanging through the interface <code>RequestNotificationI</code>.
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
 * Created on : April 9, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class RequestNotificationConnector extends AbstractConnector implements RequestNotificationI {
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI#notifyRequestTermination(fr.sorbonne_u.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void notifyRequestTermination(RequestI r) throws Exception {
		((RequestNotificationI) this.offering).notifyRequestTermination(r);
	}
}
