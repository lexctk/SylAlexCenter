package fr.sorbonne_u.datacenterclient.requestgenerator.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;
import fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;

/**
 * The class <code>RequestGeneratorManagementInboundPort</code> implements the
 * inbound port through which the component management methods are called.
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
 * invariant	true
 * </pre>
 * 
 * <p>
 * Created on : May 5, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class RequestGeneratorManagementInboundPort extends AbstractInboundPort implements RequestGeneratorManagementI {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public RequestGeneratorManagementInboundPort(ComponentI owner) throws Exception {
		super(RequestGeneratorManagementI.class, owner);

		assert owner instanceof RequestGenerator;
	}

	public RequestGeneratorManagementInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestGeneratorManagementI.class, owner);

		assert owner instanceof RequestGenerator;
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
				((RequestGenerator) this.getOwner()).startGeneration();
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
				((RequestGenerator) this.getOwner()).stopGeneration();
				return null;
			}
		});
	}

	/**
	 * @see fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#getMeanInterArrivalTime()
	 */
	@Override
	public double getMeanInterArrivalTime() throws Exception {
		return this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<>() {
			@Override
			public Double call() {
				return ((RequestGenerator) this.getOwner()).getMeanInterArrivalTime();
			}
		});
	}

	/**
	 * @see fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#setMeanInterArrivalTime(double)
	 */
	@Override
	public void setMeanInterArrivalTime(final double meanInterArrivalTime) throws Exception {
		try {
			this.getOwner().handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
				@Override
				public Void call() {
					((RequestGenerator) this.getOwner()).setMeanInterArrivalTime(meanInterArrivalTime);
					return null;
				}
			});
		} catch (Exception e) {
			throw new Exception("Set mean interval time " + e);
		}

	}
}
