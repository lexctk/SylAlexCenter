package fr.sorbonne_u.datacenterclient.requestgenerator.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;

/**
 * The class <code>RequestGeneratorManagementOutboundPort</code> implements the
 * outbound port through which one calls the component management methods.
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
 * Created on : May 5, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class RequestGeneratorManagementOutboundPort extends AbstractOutboundPort
		implements RequestGeneratorManagementI {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public RequestGeneratorManagementOutboundPort(ComponentI owner) throws Exception {
		super(RequestGeneratorManagementI.class, owner);

		assert owner != null;
	}

	public RequestGeneratorManagementOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestGeneratorManagementI.class, owner);

		assert uri != null && owner != null;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#startGeneration()
	 */
	@Override
	public void startGeneration() throws Exception {
		((RequestGeneratorManagementI) this.connector).startGeneration();
	}

	/**
	 * @see fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#stopGeneration()
	 */
	@Override
	public void stopGeneration() throws Exception {
		((RequestGeneratorManagementI) this.connector).stopGeneration();
	}

	/**
	 * @see fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#getMeanInterArrivalTime()
	 */
	@Override
	public double getMeanInterArrivalTime() throws Exception {
		return ((RequestGeneratorManagementI) this.connector).getMeanInterArrivalTime();
	}

	/**
	 * @see fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#setMeanInterArrivalTime(double)
	 */
	@Override
	public void setMeanInterArrivalTime(double meanInterArrivalTime) throws Exception {
		((RequestGeneratorManagementI) this.connector).setMeanInterArrivalTime(meanInterArrivalTime);
	}
}
