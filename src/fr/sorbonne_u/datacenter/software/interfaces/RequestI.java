package fr.sorbonne_u.datacenter.software.interfaces;

import java.io.Serializable;

/**
 * The interface <code>RequestI</code> must be implemented by requests submitted
 * to a <code>TaskDispatcher</code> for execution as a job for an application
 * running on the data center.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * Requests are simulated by a fixed predicted number of instructions that will
 * have to be executed to complete the request.
 * 
 * <p>
 * Created on : April 9, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface RequestI extends Serializable {
	/**
	 * return the URI of the request.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true // no precondition.
	 * post	return != null
	 * </pre>
	 *
	 * @return the URI of the request.
	 */
	String getRequestURI();

	/**
	 * return the predicted number of instructions to be executed to complete the
	 * request.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true // no precondition.
	 * post	return &gt;= 0
	 * </pre>
	 *
	 * @return predicted number of instructions to be executed to complete the
	 *         request.
	 */
	long getPredictedNumberOfInstructions();
}
