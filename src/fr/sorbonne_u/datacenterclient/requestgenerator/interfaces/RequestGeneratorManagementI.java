package fr.sorbonne_u.datacenterclient.requestgenerator.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * The interface <code>RequestGeneratorManagementI</code> defines the management
 * actions provided by the request generator component.
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
public interface RequestGeneratorManagementI extends OfferedI, RequiredI {
	/**
	 * start the generation of the requests.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true // no precondition.
	 * post	true // no postcondition.
	 * </pre>
	 *
	 */
	void startGeneration() throws Exception;

	/**
	 * stop the generation of the requests.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 */
	void stopGeneration() throws Exception;

	/**
	 * get the current value of the mean inter-arrival time used to generate
	 * requests.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return the current value of the mean inter-arrival time.
	 */
	double getMeanInterArrivalTime() throws Exception;

	/**
	 * set the value of the mean inter-arrival time used to generate requests.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	meanInterArrivalTime &gt; 0.0
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param meanInterArrivalTime new value for the mean inter-arrival time.
	 */
	void setMeanInterArrivalTime(double meanInterArrivalTime) throws Exception;
}
