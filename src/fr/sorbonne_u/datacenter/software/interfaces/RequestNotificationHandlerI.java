package fr.sorbonne_u.datacenter.software.interfaces;

/**
 * The interface <code>RequestNotificationHandlerI</code> defines the methods
 * that must be implemented by a component to handle request notifications
 * received through an inbound port <code>RequestNotificationInboundPort</code>.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * Created on : May 4, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface RequestNotificationHandlerI {
	/**
	 * process the termination notification of a request.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	r != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param r terminated request.
	 */
	void acceptRequestTerminationNotification(RequestI r) throws Exception;
}
