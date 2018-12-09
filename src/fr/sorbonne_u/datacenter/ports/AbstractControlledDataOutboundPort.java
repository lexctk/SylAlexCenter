package fr.sorbonne_u.datacenter.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.components.ports.AbstractDataOutboundPort;
import fr.sorbonne_u.datacenter.interfaces.ControlledDataRequiredI;
import fr.sorbonne_u.datacenter.interfaces.PushModeControllingI;

/**
 * The class <code>AbstractControlledDataOutboundPort</code> defines an abstract
 * controlled outbound port where the push interface adds control method to
 * start and stop the pushing.
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
 * Created on : October 1, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class AbstractControlledDataOutboundPort extends AbstractDataOutboundPort
		implements ControlledDataRequiredI.ControlledPullI {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public AbstractControlledDataOutboundPort(ComponentI owner) throws Exception {
		super(ControlledDataRequiredI.ControlledPullI.class, DataRequiredI.PushI.class, owner);
	}

	public AbstractControlledDataOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ControlledDataRequiredI.ControlledPullI.class, DataRequiredI.PushI.class, owner);
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.interfaces.PushModeControllingI#startUnlimitedPushing(int)
	 */
	@Override
	public void startUnlimitedPushing(int interval) throws Exception {
		((PushModeControllingI) this.connector).startUnlimitedPushing(interval);
	}

	/**
	 * @see fr.sorbonne_u.datacenter.interfaces.PushModeControllingI#startLimitedPushing(int,
	 *      int)
	 */
	@Override
	public void startLimitedPushing(int interval, int n) throws Exception {
		((PushModeControllingI) this.connector).startLimitedPushing(interval, n);
	}

	/**
	 * @see fr.sorbonne_u.datacenter.interfaces.PushModeControllingI#stopPushing()
	 */
	@Override
	public void stopPushing() throws Exception {
		((PushModeControllingI) this.connector).stopPushing();
	}
}
