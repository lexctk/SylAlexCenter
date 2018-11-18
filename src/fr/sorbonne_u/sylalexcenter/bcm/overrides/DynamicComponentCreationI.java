package fr.sorbonne_u.sylalexcenter.bcm.overrides;

/**
 * The class <code>DynamicComponentCreationI</code> replaces 
 * DynamicComponentCreationI from BCM to delay component start
 * 
 * 
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public interface DynamicComponentCreationI extends fr.sorbonne_u.components.pre.dcc.interfaces.DynamicComponentCreationI {
	
	public void startComponents() throws Exception;

	public void executeComponents() throws Exception;

}
