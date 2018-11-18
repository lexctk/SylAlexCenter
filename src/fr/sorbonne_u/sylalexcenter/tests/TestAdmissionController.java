package fr.sorbonne_u.sylalexcenter.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.sorbonne_u.sylalexcenter.utils.ComputerURI;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.AdmissionController;
import fr.sorbonne_u.sylalexcenter.application.Application;
import fr.sorbonne_u.sylalexcenter.application.ApplicationIntegrator;
import fr.sorbonne_u.sylalexcenter.bcm.overrides.DynamicComponentCreator;

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
	public static final int	numberOfComputers = 2 ;

	// Port URIs
	// -----------------------------------------------------------------
	protected static final String admissionControlerManagementInboundURI = "acmip";
	
	public static final String applicationManagementInboundPortURI = "appmip";
	protected static final String applicationSubmissionInboundPortURI = "appsip";
	protected static final String applicationNotificationInboundPortURI = "appnip";

	private static final String dynamicComponentCreationInboundPortURI = "dynamicComponentCreationInboundPortURI";
	
	// Component URIs
	// -----------------------------------------------------------------	
	protected List<String> computersURIs = new ArrayList<>();
	protected static final String admissionControlerURI = "admissionControler";

	// Components
	// -----------------------------------------------------------------
	protected List<Computer> computers = new ArrayList<>();
	protected ComputerMonitor computerMonitor;
	protected List<ComputerMonitor> computerMonitors = new ArrayList<>();
	
	protected DynamicComponentCreator dynamicComponentCreator;
	
	private Application application;
	protected List<Application> applications = new ArrayList<>();
	private ApplicationIntegrator applicationIntegrator;
	protected List<ApplicationIntegrator> applicationIntegrators = new ArrayList<>();
	
	private AdmissionController admissionController;
	
	public TestAdmissionController(boolean isDistributed) throws Exception {
		super(isDistributed);
	}
	
	public TestAdmissionController() throws Exception {
		super();
	}

	// Deploy
	// -----------------------------------------------------------------	
	@Override
 	public void deploy() throws Exception { 		

		// Deploy Computers 
		// -----------------------------------------------------------------		
		List<ComputerURI> computerURIsAll = new ArrayList<>();
		
		for(int i = 0 ; i < numberOfComputers; i++) {
			String computerServicesInboundPortURI = "csip_" +i;
			String computerStaticStateDataInboundPortURI = "cssdip_" + i;
			String computerDynamicStateDataInboundPortURI = "cdsdip_" + i;
			String computerURI = "computer_" + i;
			
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
			
			computers.add(computer);
			computerMonitors.add(computerMonitor);
			computersURIs.add(computerServicesInboundPortURI);
			computerURIsAll.add(new ComputerURI(computerURI, computerServicesInboundPortURI, computerStaticStateDataInboundPortURI, computerDynamicStateDataInboundPortURI));
			
			System.out.println("computer " + computerURI + " deployed.");
		}
		
		// Dynamic Component Creator
		// --------------------------------------------------------------------	
		dynamicComponentCreator = new DynamicComponentCreator(dynamicComponentCreationInboundPortURI);

		
		// Create an Admission Controller
		// --------------------------------------------------------------------		
		this.admissionController = new AdmissionController (
				numberOfComputers,
				computers,
				computerURIsAll,
				computerMonitors,
				admissionControlerURI,
				admissionControlerManagementInboundURI, 
				dynamicComponentCreationInboundPortURI,
				applicationSubmissionInboundPortURI,
				applicationNotificationInboundPortURI
		);
		
		this.admissionController.toggleTracing();
		this.admissionController.toggleLogging();
		
		System.out.println("admission controller created.");
		
		
		// Create Applications
		// --------------------------------------------------------------------
		for(int i = 0 ; i < numberOfComputers; i++) {
			String appURI = "app_" + 1;
			
			Integer numCores = 2;
			Double meanInterArrivalTime = 500.0;
			Long meanNumberOfInstructions = 6000000000L;
			
			this.application = new Application (
					appURI, 
					numCores, 
					meanInterArrivalTime, 
					meanNumberOfInstructions,
					applicationManagementInboundPortURI + "_" +i ,
					applicationSubmissionInboundPortURI + "_" + i,
					applicationNotificationInboundPortURI + "_" + i
			);
			applications.add(application);
			this.application.toggleLogging();
			this.application.toggleTracing();
			
			// Deploy Application Integrator
			// --------------------------------------------------------------------	
			applicationIntegrator = new ApplicationIntegrator(applicationManagementInboundPortURI + "_" +i);
			applicationIntegrators.add(applicationIntegrator);		
		}
		System.out.println("applications created.");
		
		
		addDeployedComponent(dynamicComponentCreator);
		
		addDeployedComponent(admissionController);
		
		for(Application application : applications)
			addDeployedComponent(application);
		
		
		for(ApplicationIntegrator applicationIntegrator : applicationIntegrators)
			addDeployedComponent(applicationIntegrator) ;
		
		super.deploy();
		
		assert this.deploymentDone();
		
		System.out.println("deployment done.");
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
