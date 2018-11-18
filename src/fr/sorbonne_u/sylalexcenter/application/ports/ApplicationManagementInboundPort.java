package fr.sorbonne_u.sylalexcenter.application.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.sylalexcenter.application.Application;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationManagementI;

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class ApplicationManagementInboundPort extends AbstractInboundPort implements ApplicationManagementI {

	private static final long serialVersionUID = 1L;
	
	public ApplicationManagementInboundPort(ComponentI owner) throws Exception {
		super(ApplicationManagementI.class, owner);

		assert owner != null && owner instanceof Application;
	}

	public ApplicationManagementInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ApplicationManagementI.class, owner);

		assert	owner != null && owner instanceof Application;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#startGeneration()
	 */
	@Override
	public void startGeneration() throws Exception {
		this.getOwner().handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((Application) this.getOwner()).startGeneration();
				return null;
			}
		});
	}

	/**
	 * @see fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#stopGeneration()
	 */
	@Override
	public void stopGeneration() throws Exception {
		this.getOwner().handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((Application) this.getOwner()).stopGeneration();
				return null;
			}
		});
	}

	/**
	 * @see fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#getMeanInterArrivalTime()
	 */
	@Override
	public double getMeanInterArrivalTime() throws Exception {
		return this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<Double>() {
			@Override
			public Double call() throws Exception {
				return ((Application) this.getOwner()).getMeanInterArrivalTime();
			}
		});
	}

	/**
	 * @see fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#setMeanInterArrivalTime(double)
	 */
	@Override
	public void setMeanInterArrivalTime(final double miat) throws Exception {
		this.getOwner().handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((Application) this.getOwner()).setMeanInterArrivalTime(miat);
				return null;
			}
		});
	}

	@Override
	public boolean sendAdmissionRequest() throws Exception {

		this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return ((Application) this.getOwner()).sendAdmissionRequest();

			}
		});
		return false;

	}

	@Override
	public void freeAdmissionControlerRessources() throws Exception {
		this.getOwner().handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((Application) this.getOwner()).freeAdmissionControlerRessources();
				return null;
			}
		});
	}
}