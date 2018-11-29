package fr.sorbonne_u.sylalexcenter.bcm.overrides;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

//-----------------------------------------------------------------------------
/**
 * The class <code>DynamicComponentCreationOutboundPort</code> replaces 
 * DynamicComponentCreationOutboundPort from BCM to delay component start
 * 
 * 
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class DynamicComponentCreationOutboundPort extends AbstractOutboundPort implements DynamicComponentCreationI {
	private static final long serialVersionUID = 1L;

	public DynamicComponentCreationOutboundPort(ComponentI owner) throws Exception {
		super(DynamicComponentCreationI.class, owner);
	}

	public DynamicComponentCreationOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, DynamicComponentCreationI.class, owner);
	}

	/**
	 * @see fr.sorbonne_u.components.pre.dcc.interfaces.DynamicComponentCreationI#createComponent(java.lang.String,
	 *      java.lang.Object[])
	 */
	@Override
	public void createComponent(String classname, Object[] constructorParams) throws Exception {
		((DynamicComponentCreationI) this.connector).createComponent(classname, constructorParams);
	}

	@Override
	public void startComponents() throws Exception {
		((DynamicComponentCreationI) this.connector).startComponents();

	}

	public void executeComponents() throws Exception {
		((DynamicComponentCreationI) this.connector).executeComponents();

	}
}