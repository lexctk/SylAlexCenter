package fr.sorbonne_u.sylalexcenter.bcm.overrides;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

//-----------------------------------------------------------------------------
/**
 * The class <code>DynamicComponentCreationInboundPort</code> replaces 
 * DynamicComponentCreationInboundPort from BCM to delay component start
 * 
 * 
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class DynamicComponentCreationInboundPort extends AbstractInboundPort implements DynamicComponentCreationI {
	private static final long serialVersionUID = 1L;

	public DynamicComponentCreationInboundPort(ComponentI owner) throws Exception {
		super(DynamicComponentCreationI.class, owner);
	}

	public DynamicComponentCreationInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, DynamicComponentCreationI.class, owner);
	}

	/**
	 * @see fr.sorbonne_u.components.pre.dcc.interfaces.DynamicComponentCreationI#createComponent(java.lang.String,
	 *      java.lang.Object[])
	 */
	@Override
	public void createComponent(String classname, Object[] constructorParams) throws Exception {
		final String fClassname = classname;
		final Object[] fConstructorParams = constructorParams;
		this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((DynamicComponentCreator) this.getOwner()).createComponent(fClassname, fConstructorParams);
				return null;
			}
		});
	}

	@Override
	public void startComponents() throws Exception {
		this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((DynamicComponentCreator) this.getOwner()).startComponents();
				return null;
			}
		});

	}

	@Override
	public void executeComponents() throws Exception {
		this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((DynamicComponentCreator) this.getOwner()).executeComponents();
				return null;
			}
		});

	}
}
//-----------------------------------------------------------------------------