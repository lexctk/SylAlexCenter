package fr.sorbonne_u.datacenter.hardware.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.connectors.DataConnector;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.connectors.ControlledDataConnector;
import fr.sorbonne_u.datacenter.hardware.processors.Processor;
import fr.sorbonne_u.datacenter.hardware.processors.connectors.ProcessorIntrospectionConnector;
import fr.sorbonne_u.datacenter.hardware.processors.connectors.ProcessorManagementConnector;
import fr.sorbonne_u.datacenter.hardware.processors.connectors.ProcessorServicesConnector;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorDynamicStateDataOutboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorIntrospectionOutboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorServicesOutboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorStaticStateDataOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.TaskI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;

/**
 * The class <code>TestsProcessor</code> deploys a <code>Processor</code>
 * component connected to a <code>ProcessorMonitor</code> component and then
 * execute one of two test scenarios on the simulated processor.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * The two scenarios create a processor with two cores having two levels of
 * admissible frequencies. They then execute two tasks, one on each core and
 * respectively raise or lower the frequency of the first core to test the
 * dynamic adaptation of the task duration. In parallel, the processor monitor
 * starts the notification of the dynamic state of the processor by requesting
 * 25 pushes at the rate of one each second.
 * 
 * One scenario is activated by uncommenting its lines and commenting the
 * other's ones.
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
 * Created on : January 19, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class TestsProcessor extends AbstractCVM {
	private static final String ProcessorServicesInboundPortURI = "ps-ibp";
	private static final String ProcessorServicesOutboundPortURI = "ps-obp";
	private static final String ProcessorServicesNotificationInboundPortURI = "psn-ibp";
	private static final String ProcessorIntrospectionInboundPortURI = "pi-ibp";
	private static final String ProcessorIntrospectionOutboundPortURI = "pi-obp";
	private static final String ProcessorManagementInboundPortURI = "pm-ibp";
	private static final String ProcessorManagementOutboundPortURI = "pm-obp";
	private static final String ProcessorStaticStateDataInboundPortURI = "pss-dip";
	private static final String ProcessorStaticStateDataOutboundPortURI = "pss-dop";
	private static final String ProcessorDynamicStateDataInboundPortURI = "pds-dip";
	private static final String ProcessorDynamicStateDataOutboundPortURI = "pds-dop";

	private ProcessorServicesOutboundPort psPort;
	private ProcessorIntrospectionOutboundPort piPort;
	private ProcessorManagementOutboundPort pmPort;
	protected ProcessorStaticStateDataOutboundPort pssPort;
	protected ProcessorDynamicStateDataOutboundPort pdsPort;

	private TestsProcessor() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
		Processor.DEBUG = true;

		String processorURI = "processor0";

		Set<Integer> admissibleFrequencies = new HashSet<>();
		admissibleFrequencies.add(1500);
		admissibleFrequencies.add(3000);

		Map<Integer, Integer> processingPower = new HashMap<>();
		processingPower.put(1500, 1500000);
		processingPower.put(3000, 3000000);

		Processor processor = new Processor(
				processorURI,
				admissibleFrequencies,
				processingPower,
				1500,
				1500,
				2,
				ProcessorServicesInboundPortURI,
				ProcessorIntrospectionInboundPortURI,
				ProcessorManagementInboundPortURI,
				ProcessorStaticStateDataInboundPortURI,
				ProcessorDynamicStateDataInboundPortURI
		);
		processor.toggleTracing();
		processor.toggleLogging();
		this.addDeployedComponent(processor);

		ComponentI nullComponent = new AbstractComponent(0, 0) {};
		this.psPort = new ProcessorServicesOutboundPort(ProcessorServicesOutboundPortURI, nullComponent);
		this.psPort.publishPort();

		nullComponent.doPortConnection(this.psPort.getPortURI(), ProcessorServicesInboundPortURI,
				ProcessorServicesConnector.class.getCanonicalName());

		this.piPort = new ProcessorIntrospectionOutboundPort(ProcessorIntrospectionOutboundPortURI, nullComponent);
		this.piPort.publishPort();

		nullComponent.doPortConnection(this.piPort.getPortURI(), ProcessorIntrospectionInboundPortURI,
				ProcessorIntrospectionConnector.class.getCanonicalName());

		this.pmPort = new ProcessorManagementOutboundPort(ProcessorManagementOutboundPortURI, nullComponent);
		this.pmPort.publishPort();

		nullComponent.doPortConnection(this.pmPort.getPortURI(), ProcessorManagementInboundPortURI,
				ProcessorManagementConnector.class.getCanonicalName());

		ProcessorMonitor pm = new ProcessorMonitor(processorURI, false, ProcessorServicesNotificationInboundPortURI,
				ProcessorStaticStateDataOutboundPortURI, ProcessorDynamicStateDataOutboundPortURI);
		this.addDeployedComponent(pm);

		pm.toggleLogging();
		pm.toggleTracing();

		pm.doPortConnection(ProcessorStaticStateDataOutboundPortURI, ProcessorStaticStateDataInboundPortURI,
				DataConnector.class.getCanonicalName());

		pm.doPortConnection(ProcessorDynamicStateDataOutboundPortURI, ProcessorDynamicStateDataInboundPortURI,
				ControlledDataConnector.class.getCanonicalName());

		super.deploy();
	}

	@Override
	public void start() throws Exception {
		super.start();

		System.out.println("0 isValidCoreNo: " + this.piPort.isValidCoreNo(0));
		System.out.println("3000 isAdmissibleFrequency: " + this.piPort.isAdmissibleFrequency(3000));
		System.out.println("3000 is CurrentlyPossibleFrequencyForCore 0: "
				+ this.piPort.isCurrentlyPossibleFrequencyForCore(0, 3000));

		this.psPort.executeTaskOnCoreAndNotify(new TaskI() {
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
						return "r0";
					}
				};
			}

			@Override
			public String getTaskURI() {
				return "task-001";
			}
		}, 0, ProcessorServicesNotificationInboundPortURI);

		this.psPort.executeTaskOnCoreAndNotify(new TaskI() {
			private static final long serialVersionUID = 1L;

			@Override
			public RequestI getRequest() {
				return new RequestI() {
					private static final long serialVersionUID = 1L;

					@Override
					public long getPredictedNumberOfInstructions() {
						return 45000000000L;
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
		}, 1, ProcessorServicesNotificationInboundPortURI);

		// Test scenario 1
		Thread.sleep(10000L);
		this.pmPort.setCoreFrequency(0, 3000);
		// Test scenario 2
		// Thread.sleep(5000L) ;
		// this.pmPort.setCoreFrequency(0, 1500) ;
	}

	@Override
	public void shutdown() throws Exception {
		this.psPort.doDisconnection();
		this.piPort.doDisconnection();
		this.pmPort.doDisconnection();

		super.shutdown();
	}

	public static void main(String[] args) {
		// AbstractCVM.toggleDebugMode() ;
		try {
			AbstractCVM c = new TestsProcessor();
			c.deploy();
			System.out.println("starting...");
			c.start();
			Thread.sleep(30000L);
			System.out.println("shutting down...");
			c.shutdown();
			System.out.println("ending...");
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
