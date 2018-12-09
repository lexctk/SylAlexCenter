package fr.sorbonne_u.datacenter.hardware.processors;

/**
 * The class <code>UnacceptableFrequencyException</code>.
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
 * Created on : January 15, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class UnacceptableFrequencyException extends Exception {
	// ------------------------------------------------------------------------
	// Constants and instance variables
	// ------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	protected int frequency;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	UnacceptableFrequencyException(int frequency) {
		super();
		this.frequency = frequency;
	}

	public UnacceptableFrequencyException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnacceptableFrequencyException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnacceptableFrequencyException(String message) {
		super(message);
	}

	public UnacceptableFrequencyException(Throwable cause) {
		super(cause);
	}
}
