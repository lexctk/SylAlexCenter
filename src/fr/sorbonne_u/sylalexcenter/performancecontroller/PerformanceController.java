package fr.sorbonne_u.sylalexcenter.performancecontroller;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.datacenter.connectors.ControlledDataConnector;
import fr.sorbonne_u.datacenter.interfaces.ControlledDataRequiredI;
import fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces.PerformanceControllerManagementI;
import fr.sorbonne_u.sylalexcenter.performancecontroller.ports.PerformanceControllerManagementInboundPort;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherStateDataConsumerI;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.ports.RequestDispatcherDynamicStateDataOutboundPort;

public class PerformanceController extends AbstractComponent implements PerformanceControllerManagementI, RequestDispatcherStateDataConsumerI {

	private static final int timer = 2000;

	private String performanceController;
	private String requestDispatcherURI;
	private String appURI;

	private String requestDispatcherDynamicStateDataInboundPortURI;

	private PerformanceControllerManagementInboundPort pcmip;

	private RequestDispatcherDynamicStateDataOutboundPort rddsdop;

	public PerformanceController(
			String performanceController,
			String performanceControllerManagementInboundPortURI,
			String appURI,
			String requestDispatcherURI,
			String requestDispatcherDynamicStateDataInboundPortURI) throws Exception {
		super(performanceController, 1, 1);

		this.performanceController = performanceController;
		this.appURI = appURI;
		this.requestDispatcherURI = requestDispatcherURI;
		this.requestDispatcherDynamicStateDataInboundPortURI = requestDispatcherDynamicStateDataInboundPortURI;

		this.addOfferedInterface(PerformanceControllerManagementI.class);
		this.pcmip = new PerformanceControllerManagementInboundPort(performanceControllerManagementInboundPortURI, this);
		this.addPort(this.pcmip);
		this.pcmip.publishPort();

		this.addRequiredInterface(DataRequiredI.PullI.class);
		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class);

		this.rddsdop = new RequestDispatcherDynamicStateDataOutboundPort(this, requestDispatcherURI);
		this.addPort(this.rddsdop);
		this.rddsdop.publishPort();

		this.tracer.setTitle("pc-" + requestDispatcherURI);
		this.tracer.setRelativePosition(3, 0);
	}

	@Override
	public void start() throws ComponentStartException {
		this.toggleTracing();
		this.toggleLogging();

		super.start();

		checkUsage();
	}

	private void checkUsage() {

	}

	@Override
	public void doConnectionWithRequestDispatcherForDynamicState (String requestDispatcherDynamicStateInboundPortUri) throws Exception {
		this.doPortConnection(
				this.rddsdop.getPortURI(),
				this.requestDispatcherDynamicStateDataInboundPortURI,
				ControlledDataConnector.class.getCanonicalName());
		try {
			this.rddsdop.startUnlimitedPushing(timer);

		} catch (Exception e) {
			throw new ComponentStartException("Unable to start pushing dynamic data from the request dispatcher " + e);
		}
	}

	@Override
	public synchronized void acceptRequestDispatcherDynamicData (String requestDispatcherURI, RequestDispatcherDynamicStateI currentDynamicState) throws Exception {
		if (!requestDispatcherURI.equals(this.requestDispatcherURI)) return;

		this.logMessage("Average execution time for " + this.appURI + " is " + currentDynamicState.getExponentialAverageExecutionTime());
	}

}
