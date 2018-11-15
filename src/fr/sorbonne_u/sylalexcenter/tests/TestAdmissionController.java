package fr.sorbonne_u.sylalexcenter.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.AdmissionController;
import fr.sorbonne_u.sylalexcenter.application.Application;
import fr.sorbonne_u.sylalexcenter.application.ApplicationIntegrator;

/**
 * The class <code>TestAdmissionController</code> deploys all the components
 * and runs a test.
 * 
 * <p><strong>Description</strong></p>
 *
 */
public class TestAdmissionController extends AbstractCVM {
	
	/** Number of processors per computer and number of cores per processor **/ 
	public static final int	numberOfProcessors = 2 ;
	public static final int	numberOfCores = 2 ;

	// Port URIs
	// -----------------------------------------------------------------
	public static final String computerServicesInboundPortURI = "csip";
	public static final String computerStaticStateDataInboundPortURI = "cssdip";
	public static final String computerDynamicStateDataInboundPortURI = "cdsdip";
	
	public static final String applicationServicesInboundPortURI = "assip";
	public static final String applicationManagementInboundPortURI = "amip";
	public static final String applicationSubmissionInboundPortURI = "asip";
	public static final String applicationNotificationInboundPortURI = "anip";
	

	// Components
	// -----------------------------------------------------------------
	private Application application;
	private ComputerMonitor computerMonitor;
	private AdmissionController admissionController;
	private ApplicationIntegrator applicationIntegrator;
	
	
	public TestAdmissionController() throws Exception {
		super();
	}

	// Deploy
	// -----------------------------------------------------------------	
	@Override
 	public void deploy() throws Exception { 		

		
		// Deploy a Computer 
		// -----------------------------------------------------------------
		String computerURI = "computer0";
		
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
		
		this.computerMonitor = new ComputerMonitor (
				computerURI, 
				active, 
				computerStaticStateDataInboundPortURI, 
				computerDynamicStateDataInboundPortURI
		);
		
		this.addDeployedComponent(this.computerMonitor);
		
		System.out.println("computer deployed.");

		
		// Deploy an Admission Controller
		// --------------------------------------------------------------------		
		this.admissionController = new AdmissionController (
				"acURI", 
				computerURI, //single computer for now
				computerServicesInboundPortURI,
				computerStaticStateDataInboundPortURI,
				computerDynamicStateDataInboundPortURI,
				applicationManagementInboundPortURI,
				applicationSubmissionInboundPortURI,
				applicationNotificationInboundPortURI
		);
		
		this.addDeployedComponent(this.admissionController);
		this.admissionController.toggleTracing();
		this.admissionController.toggleLogging();
		
		System.out.println("admission controller deployed.");
		
		
		// Deploy an Application
		// --------------------------------------------------------------------
		String appURI = "app0";
		Integer numCores = 2;
		Double meanInterArrivalTime = 500.0;
		Long meanNumberOfInstructions = 6000000000L;
		
		this.application = new Application (
				appURI, 
				numCores, 
				meanInterArrivalTime, 
				meanNumberOfInstructions,
				applicationManagementInboundPortURI,
				applicationSubmissionInboundPortURI,
				applicationNotificationInboundPortURI
		);
		
		this.addDeployedComponent(this.application);
		this.application.toggleLogging();
		this.application.toggleTracing();
		
		System.out.println("application deployed.");
		
		
		// Deploy Application Integrator
		// --------------------------------------------------------------------	
		applicationIntegrator = new ApplicationIntegrator(applicationManagementInboundPortURI);
		this.addDeployedComponent(applicationIntegrator);
		
		System.out.println("application integrator deployed.");
	}
	
	
	public static void main(String[] args) {
		
		TestAdmissionController testAdmissionController;
		
		try {
			testAdmissionController = new TestAdmissionController();
			
			testAdmissionController.startStandardLifeCycle(10000L);
			
			Thread.sleep(10000L);
			//System.exit(0);
			
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

}
