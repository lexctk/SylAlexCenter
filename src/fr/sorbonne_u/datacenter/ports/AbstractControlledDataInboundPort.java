package fr.sorbonne_u.datacenter.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.ports.AbstractDataInboundPort;
import fr.sorbonne_u.datacenter.interfaces.ControlledDataOfferedI;
import fr.sorbonne_u.datacenter.interfaces.PushModeControllingI;

/**
 * The class <code>AbstractControlledDataInboundPort</code> defines an abstract
 * controlled inbound port where the push interface adds control method to start
 * and stop the pushing.
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
public abstract class AbstractControlledDataInboundPort extends AbstractDataInboundPort
		implements ControlledDataOfferedI.ControlledPullI {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public AbstractControlledDataInboundPort(ComponentI owner) throws Exception {
		super(ControlledDataOfferedI.ControlledPullI.class, DataOfferedI.PushI.class, owner);

		assert owner instanceof PushModeControllingI;
	}

	public AbstractControlledDataInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ControlledDataOfferedI.ControlledPullI.class, DataOfferedI.PushI.class, owner);

		assert owner instanceof PushModeControllingI;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.interfaces.PushModeControllingI#startUnlimitedPushing(int)
	 */
	@Override
	public void startUnlimitedPushing(final int interval) throws Exception {
		this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((PushModeControllingI) this.getOwner()).startUnlimitedPushing(interval);
				return null;
			}
		});
	}

	/**
	 * @see fr.sorbonne_u.datacenter.interfaces.PushModeControllingI#startLimitedPushing(int,
	 *      int)
	 */
	@Override
	public void startLimitedPushing(final int interval, final int n) throws Exception {
		this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((PushModeControllingI) this.getOwner()).startLimitedPushing(interval, n);
				return null;
			}
		});
	}

	/**
	 * @see fr.sorbonne_u.datacenter.interfaces.PushModeControllingI#stopPushing()
	 */
	@Override
	public void stopPushing() throws Exception {
		this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((PushModeControllingI) this.getOwner()).stopPushing();
				return null;
			}
		});
	}
}
