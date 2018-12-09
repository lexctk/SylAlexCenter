package fr.sorbonne_u.datacenterclient.requestgenerator;

import fr.sorbonne_u.datacenter.software.interfaces.RequestI;

/**
 * The class <code>Request</code> implements the interface <code>RequestI</code>
 * to provide request objects for the test.
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
 * Created on : May 5, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class Request implements RequestI {
	// ------------------------------------------------------------------------
	// Constants and instance variables
	// ------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	private final long numberOfInstructions;
	private final String requestURI;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public Request(long numberOfInstructions) {
		super();
		this.numberOfInstructions = numberOfInstructions;
		this.requestURI = java.util.UUID.randomUUID().toString();
	}

	Request(String uri, long numberOfInstructions) {
		super();
		this.numberOfInstructions = numberOfInstructions;
		this.requestURI = uri;
	}

	/**
	 * @see fr.sorbonne_u.datacenter.software.interfaces.RequestI#getRequestURI()
	 */
	@Override
	public String getRequestURI() {
		return this.requestURI;
	}

	/**
	 * @see fr.sorbonne_u.datacenter.software.interfaces.RequestI#getPredictedNumberOfInstructions()
	 */
	@Override
	public long getPredictedNumberOfInstructions() {
		return this.numberOfInstructions;
	}
}
