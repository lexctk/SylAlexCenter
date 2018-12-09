package fr.sorbonne_u.datacenterclient.requestgenerator.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;

/**
 * The class <code>RequestGeneratorManagementConnector</code> implements a
 * standard client/server connector for the management request generator
 * management interface.
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
public class RequestGeneratorManagementConnector extends AbstractConnector implements RequestGeneratorManagementI {
	/**
	 * @see fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#startGeneration()
	 */
	@Override
	public void startGeneration() throws Exception {
		((RequestGeneratorManagementI) this.offering).startGeneration();
	}

	/**
	 * @see fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#stopGeneration()
	 */
	@Override
	public void stopGeneration() throws Exception {
		((RequestGeneratorManagementI) this.offering).stopGeneration();
	}

	/**
	 * @see fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#getMeanInterArrivalTime()
	 */
	@Override
	public double getMeanInterArrivalTime() throws Exception {
		return ((RequestGeneratorManagementI) this.offering).getMeanInterArrivalTime();
	}

	/**
	 * @see fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#setMeanInterArrivalTime(double)
	 */
	@Override
	public void setMeanInterArrivalTime(double meanInterArrivalTime) throws Exception {
		((RequestGeneratorManagementI) this.offering).setMeanInterArrivalTime(meanInterArrivalTime);
	}
}
