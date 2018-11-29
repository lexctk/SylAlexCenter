package fr.sorbonne_u.sylalexcenter.bcm.overrides;

import fr.sorbonne_u.components.connectors.AbstractConnector;

//-----------------------------------------------------------------------------
/**
 * The class <code>DynamicComponentCreationConnector</code> replaces 
 * DynamicComponentCreationConnector from BCM to delay component start
 * 
 * 
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class DynamicComponentCreationConnector extends AbstractConnector implements DynamicComponentCreationI {
	/**
	 * @see fr.sorbonne_u.components.pre.dcc.interfaces.DynamicComponentCreationI#createComponent(java.lang.String,
	 *      java.lang.Object[])
	 */
	@Override
	public void createComponent(String classname, Object[] constructorParams) throws Exception {
		((DynamicComponentCreationI) this.offering).createComponent(classname, constructorParams);
	}

	@Override
	public void startComponents() throws Exception {
		((DynamicComponentCreationI) this.offering).startComponents();
	}

	@Override
	public void executeComponents() throws Exception {
		((DynamicComponentCreationI) this.offering).executeComponents();

	}
}
//-----------------------------------------------------------------------------