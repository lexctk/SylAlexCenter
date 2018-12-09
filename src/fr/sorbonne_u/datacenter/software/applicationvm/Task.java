package fr.sorbonne_u.datacenter.software.applicationvm;

import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.TaskI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;

/**
 * The class <code>Task</code> represents a task to be run on a processor core
 * for an Application VM.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * The task has an URI and embeds the request submitted to the simulated
 * application.
 * 
 * <p>
 * <strong>Invariant</strong>
 * </p>
 * 
 * <pre>
 * invariant taskURI != null and request != null
 * </pre>
 * 
 * <p>
 * Created on : April 9, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class Task implements TaskI {
	private static final long serialVersionUID = 1L;
	protected RequestI request;
	String taskURI;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * create a task object.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	request != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param request request to be processed as this task.
	 */
	Task(RequestI request) {
		super();

		assert request != null;

		this.request = request;
		this.taskURI = java.util.UUID.randomUUID().toString();
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.software.applicationvm.interfaces.TaskI#getRequest()
	 */
	@Override
	public RequestI getRequest() {
		return this.request;
	}

	/**
	 * @see fr.sorbonne_u.datacenter.software.applicationvm.interfaces.TaskI#getTaskURI()
	 */
	@Override
	public String getTaskURI() {
		return this.taskURI;
	}
}
