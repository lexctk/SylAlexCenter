package fr.sorbonne_u.datacenter.connectors;

import fr.sorbonne_u.components.connectors.DataConnector;
import fr.sorbonne_u.datacenter.interfaces.ControlledDataRequiredI;
import fr.sorbonne_u.datacenter.interfaces.PushModeControllingI;

/**
 * The class <code>ControlledDataConnector</code> defines a connector associated
 * with the interface <code>ControlledDataRequiredI.ControlledPullI</code>.
 *
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ControlledDataConnector extends DataConnector implements ControlledDataRequiredI.ControlledPullI {
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.interfaces.PushModeControllingI#startUnlimitedPushing(int)
	 */
	@Override
	public void startUnlimitedPushing(int interval) throws Exception {
		((PushModeControllingI) this.offering).startUnlimitedPushing(interval);
	}

	/**
	 * @see fr.sorbonne_u.datacenter.interfaces.PushModeControllingI#startLimitedPushing(int,
	 *      int)
	 */
	@Override
	public void startLimitedPushing(int interval, int n) throws Exception {
		((PushModeControllingI) this.offering).startLimitedPushing(interval, n);
	}

	/**
	 * @see fr.sorbonne_u.datacenter.interfaces.PushModeControllingI#stopPushing()
	 */
	@Override
	public void stopPushing() throws Exception {
		((PushModeControllingI) this.offering).stopPushing();
	}
}
