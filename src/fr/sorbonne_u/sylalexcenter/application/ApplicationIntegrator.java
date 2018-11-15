package fr.sorbonne_u.sylalexcenter.application;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.sylalexcenter.application.connectors.ApplicationManagementConnector;
import fr.sorbonne_u.sylalexcenter.application.ports.ApplicationManagementOutboundPort;

public class ApplicationIntegrator extends AbstractComponent {

	protected ApplicationManagementOutboundPort amop;

	private String applicationManagementInboundPort;

	
	public ApplicationIntegrator (String applicationManagementInboundPort) throws Exception {
		super(1, 0);

		assert applicationManagementInboundPort != null;

		this.applicationManagementInboundPort = applicationManagementInboundPort;

		amop = new ApplicationManagementOutboundPort(this);
		addPort(amop);
		amop.publishPort();
	}

	@Override
	public void start() throws ComponentStartException {

		super.start();

		try {
			doPortConnection(amop.getPortURI(), applicationManagementInboundPort,
					ApplicationManagementConnector.class.getCanonicalName());

		} catch (Exception e) {
			throw new ComponentStartException(e);
		}

	}

	@Override
	public void execute() throws Exception {
		super.execute();
		amop.sendAdmissionRequest();
	}

	@Override
	public void finalise() throws Exception {

		this.doPortDisconnection(this.amop.getPortURI());

		super.finalise();
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			this.amop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	@Override
	public void shutdownNow() throws ComponentShutdownException {
		try {
			this.amop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdownNow();
	}

}