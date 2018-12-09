package fr.sorbonne_u.datacenter.hardware.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.connectors.DataConnector;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.Processor;
import fr.sorbonne_u.datacenter.hardware.processors.Processor.ProcessorPortTypes;
import fr.sorbonne_u.datacenter.hardware.processors.connectors.ProcessorManagementConnector;
import fr.sorbonne_u.datacenter.hardware.processors.connectors.ProcessorServicesConnector;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorServicesOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.TaskI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;

/**
 * The class <code>TestsComputer</code> deploys a <code>Computer</code>
 * component connected to a <code>ComputerMonitor</code> component and then
 * execute one of two test scenarios on the simulated computer.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * The two scenarios create a computer with one processor having two cores with
 * two levels of admissible frequencies. They then execute two tasks, one on
 * each core and respectively raise or lower the frequency of the first core to
 * test the dynamic adaptation of the task duration. In parallel, the computer
 * monitor starts the notification of the dynamic state of the computer by
 * requesting 25 pushes at the rate of one each second.
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
 * Created on : April 15, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class TestsComputer extends AbstractCVM {

	private static final String ComputerServicesInboundPortURI = "cs-ibp";
	private static final String ComputerServicesOutboundPortURI = "cs-obp";
	private static final String ComputerStaticStateDataInboundPortURI = "css-dip";
	private static final String ComputerStaticStateDataOutboundPortURI = "css-dop";
	private static final String ComputerDynamicStateDataInboundPortURI = "cds-dip";
	private static final String ComputerDynamicStateDataOutboundPortURI = "cds-dop";

	private ComputerServicesOutboundPort csPort;
	private ComputerMonitor cm;

	private TestsComputer() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
		Processor.DEBUG = true;

		String computerURI = "computer0";
		int numberOfProcessors = 1;
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

		this.csPort = new ComputerServicesOutboundPort(ComputerServicesOutboundPortURI, new AbstractComponent(0, 0) {});
		this.csPort.publishPort();
		this.csPort.doConnection(ComputerServicesInboundPortURI, ComputerServicesConnector.class.getCanonicalName());

		this.cm = new ComputerMonitor(computerURI, true, ComputerStaticStateDataOutboundPortURI,
				ComputerDynamicStateDataOutboundPortURI);
		cm.toggleTracing();
		cm.toggleLogging();

		this.addDeployedComponent(cm);

		ComputerStaticStateDataOutboundPort cssdop = new ComputerStaticStateDataOutboundPort(ComputerStaticStateDataOutboundPortURI, c, computerURI);
		cssdop.publishPort();
		cssdop.doConnection(ComputerStaticStateDataInboundPortURI, DataConnector.class.getCanonicalName());

		ComputerDynamicStateDataOutboundPort cdsdop = new ComputerDynamicStateDataOutboundPort(ComputerDynamicStateDataOutboundPortURI, c, computerURI);
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
		this.csPort.doDisconnection();

		super.shutdown();
	}

	private void testScenario() throws Exception {
		AllocatedCore[] ac = this.csPort.allocateCores(2);

		final String processorServicesInboundPortURI = ac[0].processorInboundPortURI.get(ProcessorPortTypes.SERVICES);
		final String processorManagementInboundPortURI = ac[0].processorInboundPortURI
				.get(ProcessorPortTypes.MANAGEMENT);

		ProcessorServicesOutboundPort psPort = new ProcessorServicesOutboundPort(new AbstractComponent(0, 0) {});
		psPort.publishPort();
		psPort.doConnection(processorServicesInboundPortURI, ProcessorServicesConnector.class.getCanonicalName());

		ProcessorManagementOutboundPort pmPort = new ProcessorManagementOutboundPort(new AbstractComponent(0, 0) {});
		pmPort.publishPort();
		pmPort.doConnection(processorManagementInboundPortURI, ProcessorManagementConnector.class.getCanonicalName());

		System.out.println("starting task-001 on core 0");
		psPort.executeTaskOnCore(new TaskI() {
			private static final long serialVersionUID = 1L;

			@Override
			public RequestI getRequest() {
				return new RequestI() {
					private static final long serialVersionUID = 1L;

					@Override
					public long getPredictedNumberOfInstructions() {
						return 15000000000L;
					}

					@Override
					public String getRequestURI() {
						return "r0";
					}
				};
			}

			@Override
			public String getTaskURI() {
				return "task-001";
			}
		}, ac[0].coreNo);

		System.out.println("starting task-002 on core 1");
		psPort.executeTaskOnCore(new TaskI() {
			private static final long serialVersionUID = 1L;

			@Override
			public RequestI getRequest() {
				return new RequestI() {
					private static final long serialVersionUID = 1L;

					@Override
					public long getPredictedNumberOfInstructions() {
						return 30000000000L;
					}

					@Override
					public String getRequestURI() {
						return "r1";
					}
				};
			}

			@Override
			public String getTaskURI() {
				return "task-002";
			}
		}, ac[1].coreNo);

		// Test scenario 1
		Thread.sleep(5000L);
		pmPort.setCoreFrequency(0, 3000);
		// Test scenario 2
		// Thread.sleep(3000L) ;
		// pmPort.setCoreFrequency(0, 1500) ;

		psPort.doDisconnection();
		pmPort.doDisconnection();
		psPort.unpublishPort();
		pmPort.unpublishPort();
	}

	public static void main(String[] args) {
		// AbstractCVM.toggleDebugMode() ;
		try {
			final TestsComputer c = new TestsComputer();
			c.deploy();
			System.out.println("starting...");
			c.start();
			new Thread(() -> {
				try {
					c.testScenario();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}).start();
			Thread.sleep(25000L);
			System.out.println("shutting down...");
			c.shutdown();
			System.out.println("ending...");
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
