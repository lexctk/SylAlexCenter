package fr.sorbonne_u.datacenter.interfaces;

import fr.sorbonne_u.components.interfaces.DataRequiredI;

/**
 * The interface <code>ControlledDataRequiredI</code> defines the data exchange
 * services required by a client component which can explicitly start and stop
 * the pushing of data.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * The interface extends the standard <code>DataRequiredI</code> with its
 * methods to pull and push data. Its pull interface also extends
 * <code>PushModeControllingI</code> to start and stop pushing of data in the
 * server to be used by the client to manage its notification reception periods.
 * 
 * <p>
 * Created on : October 1, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface ControlledDataRequiredI extends DataRequiredI {
	interface ControlledDataI extends DataRequiredI.DataI {

	}

	interface ControlledPullI extends DataRequiredI.PullI, PushModeControllingI {

	}
}
