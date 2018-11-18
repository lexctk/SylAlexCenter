package fr.sorbonne_u.sylalexcenter.application.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationManagementI;

/**
 *
 * Sorbonne University 2018-2019
 * 
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class ApplicationManagementConnector extends AbstractConnector implements ApplicationManagementI {

	/**
	 * @see fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#startGeneration()
	 */
	@Override
	public void startGeneration() throws Exception {
		((ApplicationManagementI) this.offering).startGeneration();
	}

	/**
	 * @see fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#stopGeneration()
	 */
	@Override
	public void stopGeneration() throws Exception {
		((ApplicationManagementI) this.offering).stopGeneration();
	}

	/**
	 * @see fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#getMeanInterArrivalTime()
	 */
	@Override
	public double getMeanInterArrivalTime() throws Exception {
		return ((ApplicationManagementI) this.offering).getMeanInterArrivalTime();
	}

	/**
	 * @see fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#setMeanInterArrivalTime(double)
	 */
	@Override
	public void setMeanInterArrivalTime(double miat) throws Exception {
		((ApplicationManagementI) this.offering).setMeanInterArrivalTime(miat);
	}

	@Override
	public boolean sendAdmissionRequest() throws Exception {
		return ((ApplicationManagementI) this.offering).sendAdmissionRequest();

	}

	@Override
	public void freeAdmissionControlerRessources() throws Exception {
		((ApplicationManagementI) this.offering).freeAdmissionControlerRessources();

	}
}
