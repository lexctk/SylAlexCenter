package fr.sorbonne_u.datacenter.software.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;

/**
 * The class <code>RequestSubmissionOutboundPort</code> implements the inbound
 * port requiring the interface <code>RequestSubmissionI</code>.
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
public class RequestSubmissionOutboundPort extends AbstractOutboundPort implements RequestSubmissionI {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public RequestSubmissionOutboundPort(ComponentI owner) throws Exception {
		super(RequestSubmissionI.class, owner);
	}

	public RequestSubmissionOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestSubmissionI.class, owner);

		assert uri != null;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI#submitRequest(fr.sorbonne_u.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void submitRequest(final RequestI r) throws Exception {
		((RequestSubmissionI) this.connector).submitRequest(r);
	}

	/**
	 * @see fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI#submitRequestAndNotify(fr.sorbonne_u.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void submitRequestAndNotify(RequestI r) throws Exception {
		((RequestSubmissionI) this.connector).submitRequestAndNotify(r);
	}
}
