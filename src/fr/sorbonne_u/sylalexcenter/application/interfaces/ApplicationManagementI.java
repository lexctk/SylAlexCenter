package fr.sorbonne_u.sylalexcenter.application.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public interface ApplicationManagementI extends OfferedI, RequiredI {

	/**
	 * start the generation of the requests.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception <i>todo.</i>
	 */
	public void	startGeneration() throws Exception;

	/**
	 * stop the generation of the requests.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception <i>todo.</i>
	 */
	public void stopGeneration() throws Exception;

	/**
	 * get the current value of the mean inter-arrival time used to generate
	 * requests.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return the current value of the mean inter-arrival time.
	 * @throws Exception <i>todo.</i>
	 */
	public double getMeanInterArrivalTime() throws Exception ;

	/**
	 * set the value of the mean inter-arrival time used to generate requests.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param miat			new value for the mean inter-arrival time.
	 * @throws Exception <i>todo.</i>
	 */
	public void	setMeanInterArrivalTime(double miat) throws Exception ;

	public boolean sendAdmissionRequest() throws Exception;

	public void freeAdmissionControlerRessources() throws Exception;
}