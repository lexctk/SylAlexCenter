package fr.sorbonne_u.datacenter.hardware.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.connectors.DataConnector;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.Processor;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.sorbonne_u.datacenter.software.connectors.RequestNotificationConnector;
import fr.sorbonne_u.datacenter.software.connectors.RequestSubmissionConnector;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;

/**
 * The class <code>TestApplicationVM</code> deploys an
 * <code>ApplicationVM</code> running on a <code>Computer</code> component
 * connected to a <code>ComputerMonitor</code> component and then execute a test
 * scenario.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * The test scenario submits ten requests to the application virtual machine and
 * the waits for the completion of these requests. In parallel, the computer
 * monitor starts the notification of the dynamic state of the computer by
 * requesting 25 pushes at the rate of one each second.
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class TestApplicationVM extends AbstractCVM {
	// ------------------------------------------------------------------------
	// Static inner classes
	// ------------------------------------------------------------------------

	public static class Request implements RequestI {
		private static final long serialVersionUID = 1L;
		final long numberOfInstructions;
		final String requestURI;

		Request(String uri, long numberOfInstructions) {
			super();
			this.numberOfInstructions = numberOfInstructions;
			this.requestURI = uri;
		}

		@Override
		public long getPredictedNumberOfInstructions() {
			return this.numberOfInstructions;
		}

		@Override
		public String getRequestURI() {
			return this.requestURI;
		}
	}

	public static class RequestNotificationConsumer extends AbstractComponent
			implements RequestNotificationHandlerI {
		static boolean ACTIVE = true;

		RequestNotificationConsumer() {
			super(1, 0);
		}

		@Override
		public void acceptRequestTerminationNotification(RequestI r) {
			if (RequestNotificationConsumer.ACTIVE) {
				this.logMessage(" Request " + r.getRequestURI() + " has ended.");
			}
		}
	}

	// ------------------------------------------------------------------------
	// Constants and instance variables
	// ------------------------------------------------------------------------

	private static final String ComputerServicesInboundPortURI = "csip";
	private static final String ComputerServicesOutboundPortURI = "csop";
	private static final String ComputerStaticStateDataInboundPortURI = "cssdip";
	private static final String ComputerStaticStateDataOutboundPortURI = "cssdop";
	private static final String ComputerDynamicStateDataInboundPortURI = "cdsdip";
	private static final String ComputerDynamicStateDataOutboundPortURI = "cdsdop";
	private static final String ApplicationVMManagementInboundPortURI = "avmip";
	private static final String ApplicationVMManagementOutboundPortURI = "avmop";
	private static final String RequestSubmissionInboundPortURI = "rsip";
	private static final String RequestSubmissionOutboundPortURI = "rsop";
	private static final String RequestNotificationInboundPortURI = "rnip";
	private static final String RequestNotificationOutboundPortURI = "rnop";

	private ComputerServicesOutboundPort csop;
	private ComputerMonitor cm;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	private TestApplicationVM() throws Exception {
		super();
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	@Override
	public void deploy() throws Exception {
		Processor.DEBUG = true;

		String computerURI = "computer0";
		int numberOfProcessors = 2;
		int numberOfCores = 2;

		Set<Integer> admissibleFrequencies = new HashSet<>();
		admissibleFrequencies.add(1500);
		admissibleFrequencies.add(3000);

		Map<Integer, Integer> processingPower = new HashMap<>();
		processingPower.put(1500, 1500000);
		processingPower.put(3000, 3000000);

		Computer c = new Computer(
				computerURI,
				admissibleFrequencies,
				processingPower,
				1500,
				1500,
				numberOfProcessors,
				numberOfCores,
				ComputerServicesInboundPortURI,
				ComputerStaticStateDataInboundPortURI,
				ComputerDynamicStateDataInboundPortURI
		);

		c.toggleTracing();
		c.toggleLogging();
		this.addDeployedComponent(c);

		ComponentI fake = new AbstractComponent(0, 0) {};

		fake.addRequiredInterface(ComputerServicesI.class);

		this.csop = new ComputerServicesOutboundPort(ComputerServicesOutboundPortURI, fake);
		this.csop.publishPort();
		this.csop.doConnection(ComputerServicesInboundPortURI, ComputerServicesConnector.class.getCanonicalName());

		this.cm = new ComputerMonitor(computerURI, true, ComputerStaticStateDataOutboundPortURI,
				ComputerDynamicStateDataOutboundPortURI);
		this.cm.toggleLogging();
		this.cm.toggleTracing();
		this.addDeployedComponent(this.cm);

		ComputerStaticStateDataOutboundPort cssdop = new ComputerStaticStateDataOutboundPort(ComputerStaticStateDataOutboundPortURI, fake, computerURI);
		cssdop.publishPort();
		cssdop.doConnection(ComputerStaticStateDataInboundPortURI, DataConnector.class.getCanonicalName());

		ComputerDynamicStateDataOutboundPort cdsdop = new ComputerDynamicStateDataOutboundPort(ComputerDynamicStateDataOutboundPortURI, fake, computerURI);
		cdsdop.publishPort();
		cdsdop.doConnection(ComputerDynamicStateDataInboundPortURI, DataConnector.class.getCanonicalName());

		super.deploy();
	}

	@Override
	public void start() throws Exception {
		super.start();
	}

	@Override
	public void shutdown() throws Exception {
		this.csop.doDisconnection();

		super.shutdown();
	}

	private void testScenario() throws Exception {
		AllocatedCore[] ac = this.csop.allocateCores(4);

		ApplicationVM vm = new ApplicationVM(
				"vm0",
				ApplicationVMManagementInboundPortURI,
				RequestSubmissionInboundPortURI,
				RequestNotificationInboundPortURI,
				RequestNotificationOutboundPortURI
		);

		this.addDeployedComponent(vm);
		vm.start();

		ApplicationVMManagementOutboundPort avmPort = new ApplicationVMManagementOutboundPort(
				ApplicationVMManagementOutboundPortURI,
				new AbstractComponent(0, 0) {});

		avmPort.publishPort();

		avmPort.doConnection(ApplicationVMManagementInboundPortURI,
				ApplicationVMManagementConnector.class.getCanonicalName());

		avmPort.allocateCores(ac);

		ComponentI fake = new AbstractComponent(0, 0) {};

		fake.addRequiredInterface(RequestSubmissionI.class);

		RequestSubmissionOutboundPort rsop = new RequestSubmissionOutboundPort(RequestSubmissionOutboundPortURI, fake);
		rsop.publishPort();
		rsop.doConnection(RequestSubmissionInboundPortURI, RequestSubmissionConnector.class.getCanonicalName());

		RequestNotificationConsumer rnc = new RequestNotificationConsumer();
		rnc.toggleLogging();
		rnc.toggleTracing();

		this.addDeployedComponent(rnc);
		rnc.start();
		RequestNotificationInboundPort rnip = new RequestNotificationInboundPort(RequestNotificationInboundPortURI, rnc);
		rnip.publishPort();

		vm.doPortConnection(RequestNotificationOutboundPortURI, RequestNotificationInboundPortURI,
				RequestNotificationConnector.class.getCanonicalName());

		for (int i = 0; i < 10; i++) {
			rsop.submitRequestAndNotify(new Request("r" + i, 6000000000L));
			Thread.sleep(500L);
		}

		Thread.sleep(40000L);
		rsop.doDisconnection();
		rsop.unpublishPort();
	}

	public static void main(String[] args) {
		// AbstractCVM.toggleDebugMode() ;
		try {
			final TestApplicationVM testApplicationVM = new TestApplicationVM();
			testApplicationVM.deploy();
			System.out.println("starting...");
			testApplicationVM.start();
			new Thread(() -> {
				try {
					testApplicationVM.testScenario();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}).start();
			Thread.sleep(60000L);
			System.out.println("shutting down...");
			testApplicationVM.shutdown();
			System.out.println("ending...");
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
