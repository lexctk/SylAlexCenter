package fr.sorbonne_u.datacenter.interfaces;

import fr.sorbonne_u.components.interfaces.DataOfferedI;

/**
 * The interface <code>ControlledDataOfferedI</code> defines the data exchange
 * services offered by a server component where the pushing of data can be
 * explicitly started and stopped by the client component.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * The interface extends the standard <code>DataOfferedI</code> with its methods
 * to pull and push data. Its pull interface also extends
 * <code>PushModeControllingI</code> to start and stop pushing of data in the
 * server to be used by the client to manage its notification reception periods.
 * 
 * <p>
 * Created on : October 1, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface ControlledDataOfferedI extends DataOfferedI {

	interface ControlledDataI extends DataOfferedI.DataI {
	}

	interface ControlledPullI extends DataOfferedI.PullI, PushModeControllingI {
	}
}
