package fr.sorbonne_u.datacenter.software.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;

/**
 * The class <code>RequestSubmissionConnector</code> implements a connector for
 * ports exchanging through the interface <code>RequestSubmissionI</code>.
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
public class RequestSubmissionConnector extends AbstractConnector implements RequestSubmissionI {
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI#submitRequest(fr.sorbonne_u.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void submitRequest(RequestI r) throws Exception {
		((RequestSubmissionI) this.offering).submitRequest(r);
	}

	/**
	 * @see fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI#submitRequestAndNotify(fr.sorbonne_u.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void submitRequestAndNotify(RequestI r) throws Exception {
		((RequestSubmissionI) this.offering).submitRequestAndNotify(r);
	}
}
