package fr.sorbonne_u.datacenter.software.interfaces;

/**
 * The interface <code>RequestSubmissionHandlerI</code> defines the component
 * internal services to receive and execute requests.
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
 * invariant		true
 * </pre>
 * 
 * <p>
 * Created on : May 4, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface RequestSubmissionHandlerI {
	/**
	 * accept the request <code>r</code> and execute it.
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
	 * @param r the request to be executed.
	 */
	void acceptRequestSubmission(final RequestI r) throws Exception;

	/**
	 * accept the request <code>r</code>, execute it and notify its termination.
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
	 * @param r the request to be executed.
	 */
	void acceptRequestSubmissionAndNotify(final RequestI r) throws Exception;
}
