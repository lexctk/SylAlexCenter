package fr.sorbonne_u.datacenter.interfaces;

/**
 * The interface <code>PushModeControllingI</code> defines the services to be
 * implemented to control the data push services.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * Created on : September 30, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface PushModeControllingI {
	/**
	 * start the pushing of data and force the pushing to be done each
	 * <code>interval</code> period of time.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	interval &gt; 0
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param interval delay between pushes (in milliseconds).
	 */
	void startUnlimitedPushing(final int interval) throws Exception;

	/**
	 * start <code>n</code> pushing of data and force the pushing to be done each
	 * <code>interval</code> period of time.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	interval &gt; 0 and n &gt; 0
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param interval delay between pushes (in milliseconds).
	 * @param n        total number of pushes to be done, unless stopped.
	 */
	void startLimitedPushing(final int interval, final int n) throws Exception;

	/**
	 * stop the pushing of data.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 */
	void stopPushing() throws Exception;
}
