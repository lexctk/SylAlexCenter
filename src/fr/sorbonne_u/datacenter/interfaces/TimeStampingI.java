package fr.sorbonne_u.datacenter.interfaces;

/**
 * The interface <code>TimeStampingI</code>
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
 * Created on : 30 sept. 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface TimeStampingI {
	/**
	 * return the time at which the state has been gathered in local system time
	 * (currentTimeMillis). in milliseconds.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return &gt;= 0
	 * </pre>
	 *
	 * @return the time at which the state has been gathered.
	 */
	long getTimeStamp();

	/**
	 * return the string representation of the IP address of the host on which the
	 * timestamp has been taken.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return != null
	 * </pre>
	 *
	 * @return the IP address of the host.
	 */
	String getTimeStamperId();
}
