package fr.sorbonne_u.datacenter.software.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * The interface <code>RequestSubmissionI</code> defines the component services
 * to receive and execute requests.
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
public interface RequestSubmissionI extends OfferedI, RequiredI {
	/**
	 * submit a request to a request handler.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	r != null and r.getPredictedNumberOfInstructions() &gt;= 0
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param r request to be submitted.
	 */
	void submitRequest(final RequestI r) throws Exception;

	/**
	 * submit a request to a request handler and require notifications of request
	 * execution progress.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	r != null and r.getPredictedNumberOfInstructions() &gt;= 0
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param r request to be submitted.
	 */
	void submitRequestAndNotify(final RequestI r) throws Exception;
}
