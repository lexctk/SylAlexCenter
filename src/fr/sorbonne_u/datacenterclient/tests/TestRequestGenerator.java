package fr.sorbonne_u.datacenterclient.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.processors.Processor;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenter.software.connectors.RequestSubmissionConnector;
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;

/**
 * The class <code>TestRequestGenerator</code> deploys a test application for
 * request generation in a single JVM (no remote execution provided) for a data
 * center simulation.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * A data center has a set of computers, each with several multi-core
 * processors. Application virtual machines (AVM) are created to run requests of
 * an application. Each AVM is allocated cores of different processors of a
 * computer. AVM then receive requests for their application. See the data
 * center simulator documentation for more details about the implementation of
 * this simulation.
 * </p>
 * <p>
 * This test creates one computer component with two processors, each having two
 * cores. It then creates an AVM and allocates it all four cores of the two
 * processors of this unique computer. A request generator component is then
 * created and linked to the application virtual machine. The test scenario
 * starts the request generation, wait for a specified time and then stops the
 * generation. The overall test allots sufficient time to the execution of the
 * application so that it completes the execution of all the generated requests.
 * </p>
 * <p>
 * The waiting time in the scenario and in the main method must be manually set
 * by the tester.
 * </p>
 * 
 * <p>
 * <strong>Invariant</strong>
 * </p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>
 * Created on : May 5, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * 
 * @author Alexandra Tudor
 * @author Sylia Righi
 * 
 */
// Modified: update to work with new RequestGenerator: connect submission in/out ports and remove logging
public class TestRequestGenerator extends AbstractCVM {
	// ------------------------------------------------------------------------
	// Constants and instance variables
	// ------------------------------------------------------------------------

	// Predefined URI of the different ports visible at the component assembly
	// level.
	private static final String ComputerServicesInboundPortURI = "csip";
	private static final String ComputerStaticStateDataInboundPortURI = "cssdip";
	private static final String ComputerDynamicStateDataInboundPortURI = "cdsdip";
	private static final String ApplicationVMManagementInboundPortURI = "avmip";
	private static final String RequestSubmissionInboundPortURI = "rsip";
	private static final String RequestSubmissionOutboundPortURI = "rsop";
	private static final String RequestNotificationInboundPortURI = "rnip";
	private static final String RequestNotificationOutboundPortURI = "rnop";
	private static final String RequestGeneratorManagementInboundPortURI = "rgmip";

	/** Application virtual machine component. */
	protected ApplicationVM vm;

	// ------------------------------------------------------------------------
	// Component virtual machine constructors
	// ------------------------------------------------------------------------

	private TestRequestGenerator() throws Exception {
		super();
	}

	// ------------------------------------------------------------------------
	// Component virtual machine methods
	// ------------------------------------------------------------------------

	@Override
	public void deploy() throws Exception {
		Processor.DEBUG = true;

		// --------------------------------------------------------------------
		// Create and deploy a computer component with its 2 processors and
		// each with 2 cores.
		// --------------------------------------------------------------------
		String computerURI = "computer0";
		int numberOfProcessors = 2;
		int numberOfCores = 2;
		Set<Integer> admissibleFrequencies = new HashSet<>();
		admissibleFrequencies.add(1500); // Cores can run at 1,5 GHz
		admissibleFrequencies.add(3000); // and at 3 GHz
		Map<Integer, Integer> processingPower = new HashMap<>();
		processingPower.put(1500, 1500000); // 1,5 GHz executes 1,5 Mips
		processingPower.put(3000, 3000000); // 3 GHz executes 3 Mips
		Computer c = new Computer(computerURI, admissibleFrequencies, processingPower, 
				1500,		// Test scenario 1, frequency = 1,5 GHz
//				3000,		// Test scenario 2, frequency = 3 GHz
				1500,		// max frequency gap within a processor
				numberOfProcessors, numberOfCores, ComputerServicesInboundPortURI,
				ComputerStaticStateDataInboundPortURI, ComputerDynamicStateDataInboundPortURI);
		this.addDeployedComponent(c);
		c.toggleLogging();
		c.toggleTracing();
		// --------------------------------------------------------------------

		// --------------------------------------------------------------------
		// Create the computer monitor component and connect its to ports
		// with the computer component.
		// --------------------------------------------------------------------
		ComputerMonitor cm = new ComputerMonitor(computerURI, true, ComputerStaticStateDataInboundPortURI,
				ComputerDynamicStateDataInboundPortURI);
		this.addDeployedComponent(cm);
		// --------------------------------------------------------------------

		// --------------------------------------------------------------------
		// Create an Application VM component
		// --------------------------------------------------------------------
		this.vm = new ApplicationVM("vm0",
				ApplicationVMManagementInboundPortURI,
				RequestSubmissionInboundPortURI,
				RequestNotificationInboundPortURI,
				RequestNotificationOutboundPortURI);
		this.addDeployedComponent(this.vm);
		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.
		this.vm.toggleTracing();
		this.vm.toggleLogging();
		// --------------------------------------------------------------------

		// --------------------------------------------------------------------
		// Creating the request generator component.
		// --------------------------------------------------------------------
		RequestGenerator rg = new RequestGenerator("rg", // generator component URI
				500.0, // mean time between two requests
				6000000000L, // mean number of instructions in requests
				RequestGeneratorManagementInboundPortURI, RequestSubmissionInboundPortURI, RequestSubmissionOutboundPortURI,
				RequestNotificationInboundPortURI);
		this.addDeployedComponent(rg);

		// --------------------------------------------------------------------
		// Creating the integrator component.
		// --------------------------------------------------------------------
		Integrator integrator = new Integrator(ComputerServicesInboundPortURI, ApplicationVMManagementInboundPortURI,
				RequestGeneratorManagementInboundPortURI);
		this.addDeployedComponent(integrator);
		// --------------------------------------------------------------------
		
		try {
 			rg.doPortConnection(RequestSubmissionOutboundPortURI, RequestSubmissionInboundPortURI,
 					RequestSubmissionConnector.class.getCanonicalName());
 		} catch (Exception e) {
 			throw new Exception(e);
 		}

		// complete the deployment at the component virtual machine level.
		super.deploy();
	}

	// ------------------------------------------------------------------------
	// Test scenarios and main execution.
	// ------------------------------------------------------------------------

	/**
	 * execute the test application.
	 * 
	 * @param args command line arguments, disregarded here.
	 */
	public static void main(String[] args) {
		// Uncomment next line to execute components in debug mode.
		// AbstractCVM.toggleDebugMode() ;
		try {
			final TestRequestGenerator trg = new TestRequestGenerator();
			trg.startStandardLifeCycle(10000L);
			// Augment the time if you want to examine the traces after
			// the execution of the program.
			Thread.sleep(10000L);
			// Exit from Java (closes all trace windows...).
			// System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
