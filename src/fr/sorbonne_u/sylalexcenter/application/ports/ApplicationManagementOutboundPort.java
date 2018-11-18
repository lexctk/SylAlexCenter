package fr.sorbonne_u.sylalexcenter.application.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationManagementI;

/**
 *
 * Sorbonne University 2018-2019
 * 
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class ApplicationManagementOutboundPort extends AbstractOutboundPort implements ApplicationManagementI {

	private static final long serialVersionUID = 1L;

	public ApplicationManagementOutboundPort(ComponentI owner) throws Exception {
		super(ApplicationManagementI.class, owner);

		assert owner != null;
	}

	public ApplicationManagementOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ApplicationManagementI.class, owner);

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
		((ApplicationManagementI) this.connector).startGeneration();
	}

	/**
	 * @see fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#stopGeneration()
	 */
	@Override
	public void stopGeneration() throws Exception {
		((ApplicationManagementI) this.connector).stopGeneration();
	}

	/**
	 * @see fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#getMeanInterArrivalTime()
	 */
	@Override
	public double getMeanInterArrivalTime() throws Exception {
		return ((ApplicationManagementI) this.connector).getMeanInterArrivalTime();
	}

	/**
	 * @see fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#setMeanInterArrivalTime(double)
	 */
	@Override
	public void setMeanInterArrivalTime(double miat) throws Exception {
		((ApplicationManagementI) this.connector).setMeanInterArrivalTime(miat);
	}

	@Override
	public boolean sendAdmissionRequest() throws Exception {
		return ((ApplicationManagementI)this.connector).sendAdmissionRequest();
	}

	@Override
	public void freeAdmissionControlerRessources() throws Exception {
		((ApplicationManagementI)this.connector).freeAdmissionControlerRessources();
	}
}