package fr.sorbonne_u.sylalexcenter.bcm.overrides;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

//-----------------------------------------------------------------------------
/**
 * The class <code>DynamicComponentCreator</code> replaces 
 * DynamicComponentCreator from BCM to delay component start
 * 
 * 
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
@OfferedInterfaces(offered = { DynamicComponentCreationI.class })
public class DynamicComponentCreator extends AbstractComponent {
	protected DynamicComponentCreationInboundPort p;
	protected List<AbstractComponent> componentsToStart = new ArrayList<>();

	/**
	 * create the component, publish its offered interface and its inbound port.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	dynamicComponentCreationInboundPortURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param dynamicComponentCreationInboundPortURI URI of the port offering the
	 *                                               service
	 * @throws Exception <i>todo.</i>
	 */
	public DynamicComponentCreator(String dynamicComponentCreationInboundPortURI) throws Exception {
		super(1, 0);

		assert dynamicComponentCreationInboundPortURI != null;

		this.p = new DynamicComponentCreationInboundPort(dynamicComponentCreationInboundPortURI, this);
		this.addPort(this.p);
		if (AbstractCVM.isDistributed) {
			this.p.publishPort();
		} else {
			this.p.localPublishPort();
		}
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			this.p.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component internal services
	// -------------------------------------------------------------------------

	/**
	 * create and start a component instantiated from the class of the given class
	 * name and initialised by the constructor which parameters are given.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	reflInboundPortURI != null and classname != null
	 * post	true // no postcondition.
	 * </pre>
	 *
	 * @param classname         name of the class from which the component is
	 *                          created.
	 * @param constructorParams parameters to be passed to the constructor.
	 * @throws Exception if the creation did not succeed.
	 */
	public void createComponent(String classname, Object[] constructorParams) throws Exception {
		assert classname != null;

		Class<?> cl = Class.forName(classname);
		assert cl != null;
		Class<?>[] parameterTypes = new Class[constructorParams.length];
		for (int i = 0; i < constructorParams.length; i++) {
			parameterTypes[i] = constructorParams[i].getClass();
		}
		Constructor<?> cons = cl.getConstructor(parameterTypes);
		assert cons != null;
		AbstractComponent component = (AbstractComponent) cons.newInstance(constructorParams);
		AbstractCVM.getCVM().addDeployedComponent(component);
		component.toggleLogging();
		component.toggleTracing();
		componentsToStart.add(component);
	}

	public void startComponents() throws Exception {
		for (AbstractComponent c : componentsToStart) {

			try {
				c.start();

			} catch (ComponentStartException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void executeComponents() throws Exception {

		for (AbstractComponent c : componentsToStart) {
			c.execute();
		}
		componentsToStart.clear();

	}
}
//-----------------------------------------------------------------------------
