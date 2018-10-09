/**
 * 
 */
package fr.sorbonne_u.sylalexcenter.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;
import fr.sorbonne_u.datacenterclient.tests.Integrator;
import fr.sorbonne_u.sylalexcenter.software.RequestDispatcher;

/**
 * The class <code>TestRequestDispatcher</code> deploys a single AVM, with a 
 * single application and tests the request dispatcher.
 * 
 * <p><strong>Description</strong></p>
 * 
 * @author lexa
 *
 */
public class TestRequestDispatcher extends AbstractCVM {
	
	// Ports
	// -----------------------------------------------------------------
	
	
	public static final String computerServicesInboundPortURI = "csip";
	public static final String computerStaticStateDataInboundPortURI = "cssdip";
	public static final String computerDynamicStateDataInboundPortURI = "cdsdip";
	
	public static final String applicationVMManagementInboundPortURI = "avmip";
	
	public static final String requestSubmissionInboundPortURI = "rsip";
	public static final String requestNotificationInboundPortURI = "rnip";
	
	public static final String requestSubmissionOutboundPortURI = "rsop";
	public static final String requestNotificationOutboundPortURI = "rnop";
	
	public static final String requestGeneratorManagementInboundPortURI = "rgmip";
	
	
	// Components
	// -----------------------------------------------------------------
	private ApplicationVM applicationVM;
	private RequestGenerator requestGenerator;
	private ComputerMonitor computerMonitor;
	private Integrator integrator;
	private RequestDispatcher requestDispatcher;
	

	// Constructors
	// -----------------------------------------------------------------
	public TestRequestDispatcher() throws Exception {
		super();
	}
	
	public TestRequestDispatcher(boolean isDistributed) throws Exception {
		super(isDistributed);
	}
	
	// Deploy
	// -----------------------------------------------------------------	
	@Override
 	public void deploy() throws Exception { 
		
		// Deploy a Computer with 2 Processors and 2 Cores each
		// -----------------------------------------------------------------
		String computerURI = "computer0";
		int numberOfProcessors = 2;
		int numberOfCores = 2;
		
		Set<Integer> possibleFrequencies = new HashSet<Integer>();
		possibleFrequencies.add(1500); 
		possibleFrequencies.add(3000); 
		
		Map<Integer, Integer> processingPower = new HashMap<Integer, Integer>();
		processingPower.put(1500, 1500000); 
		processingPower.put(3000, 3000000); 
		
		int defaultFrequency = 1500;
		int maxFrequencyGap = 500;		
	
		Computer computer = new Computer (
				computerURI, 
				possibleFrequencies, 
				processingPower, 
				defaultFrequency, 
				maxFrequencyGap, 
				numberOfProcessors, 
				numberOfCores, 
				computerServicesInboundPortURI, 
				computerStaticStateDataInboundPortURI, 
				computerDynamicStateDataInboundPortURI
		);
		
		this.addDeployedComponent(computer);
		computer.toggleLogging();
		computer.toggleTracing();
		
		
		// Deploy a computer monitor 
		// --------------------------------------------------------------------
		boolean active = true;

		this.computerMonitor = new ComputerMonitor(computerURI, active, computerStaticStateDataInboundPortURI, computerDynamicStateDataInboundPortURI);
		
		this.addDeployedComponent(this.computerMonitor);	
		
		
		// Deploy an AVM
		// -----------------------------------------------------------------
 		String vmURI = "avm0";
 		
		try {
			this.applicationVM = new ApplicationVM (
					vmURI, 
					applicationVMManagementInboundPortURI, 
					requestSubmissionInboundPortURI, 
					requestNotificationInboundPortURI
			);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.addDeployedComponent(this.applicationVM);
		
		this.applicationVM.toggleTracing();
		this.applicationVM.toggleLogging();

		
		// Deploy a Request Generator
		// -----------------------------------------------------------------
		
		String rgURI = "rg0";
		double meanInterArrivalTime = 500.0;
		long meanNumberOfInstructions = 6000000000L;
		
		this.requestGenerator = new RequestGenerator (
				rgURI, 
				meanInterArrivalTime, 
				meanNumberOfInstructions, 
				requestGeneratorManagementInboundPortURI, 
				requestSubmissionInboundPortURI, 
				requestNotificationInboundPortURI
		);
		
		this.addDeployedComponent(requestGenerator);
		this.requestGenerator.toggleTracing();
		this.requestGenerator.toggleLogging();
		
		
		// Deploy an integrator.
		// --------------------------------------------------------------------
		this.integrator = new Integrator(computerServicesInboundPortURI, applicationVMManagementInboundPortURI,
				requestGeneratorManagementInboundPortURI);
		this.addDeployedComponent(this.integrator);		
		
		
		// Deploy the request dispatcher
		// --------------------------------------------------------------------
		String rdURI = "rd0";
		
		this.requestDispatcher = new RequestDispatcher (
				rdURI, 
				requestSubmissionOutboundPortURI, 
				requestNotificationInboundPortURI, 
				requestSubmissionInboundPortURI, 
				requestNotificationOutboundPortURI );
		
		this.addDeployedComponent(requestDispatcher);
		this.requestDispatcher.toggleTracing();
		this.requestDispatcher.toggleLogging();		

		super.deploy();
	}
	
	public static void main(String[] args) {
		
		TestRequestDispatcher testRequestDispatcher;
		
		try {
			testRequestDispatcher = new TestRequestDispatcher();
			
			testRequestDispatcher.startStandardLifeCycle(10000L);
			
			Thread.sleep(10000L);
			//System.exit(0);
			
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

}
