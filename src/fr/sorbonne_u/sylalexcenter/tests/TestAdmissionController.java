package fr.sorbonne_u.sylalexcenter.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.AdmissionController;
import fr.sorbonne_u.sylalexcenter.application.Application;
import fr.sorbonne_u.sylalexcenter.bcm.overrides.DynamicComponentCreator;

/**
 * The class <code>TestAdmissionController</code> deploys all the components
 * and runs a test.
 * 
 * <p><strong>Description</strong></p>
 * TestAdmissionController deploys: computers, applications, admission controller
 * and a dynamic component creator that overrides BCM, to be used by 
 * admission controller when deploying components. 
 * 
 * The dynamic component creator delays start() until everything is deployed. 
 *
 */
public class TestAdmissionController extends AbstractCVM {
	
	private static final String dynamicComponentCreationInboundPortURI = "dynamicComponentCreationInboundPortURI";
	
	// Setup
	// -----------------------------------------------------------------
	protected static final Integer numberOfComputers = 2;
	protected static final Integer numberOfProcessors = 2;
	protected static final Integer numberOfCores = 2;
	
	protected static final Integer numberOfApplications = 3;
	protected static final Integer avmsPerApplication = 2;
	protected static final Integer coresPerAVM = 2;
	
	protected static final Integer coresNeeded = 4;
	

	// Port URIs
	// -----------------------------------------------------------------
	protected static final String applicationServicesInboundPortURI = "appsvip";
	protected static final String applicationSubmissionInboundPortURI = "appsip";
	protected static final String applicationNotificationInboundPortURI = "appnip";
	
	protected ArrayList<String> computerServicesInboundPortURIList;
	protected ArrayList<String> computerStaticStateDataInboundPortURIList;
	protected ArrayList<String> computerDynamicStateDataInboundPortURIList;
	
	protected ArrayList<String> applicationSubmissionInboundPortURIList;
	protected ArrayList<String> applicationNotificationInboundPortURIList;	
	
	// Component URIs
	// -----------------------------------------------------------------	
	protected ArrayList<String> computerURIsList;
	protected ArrayList<String> applicationURIsList;

	// Components
	// -----------------------------------------------------------------
	private AdmissionController admissionController;
	protected DynamicComponentCreator dynamicComponentCreator;
	
	
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
		computerURIsList = new ArrayList<String> ();
		computerServicesInboundPortURIList = new ArrayList<String> ();
		computerStaticStateDataInboundPortURIList = new ArrayList<String> ();
		computerDynamicStateDataInboundPortURIList = new ArrayList<String> ();
		
		for(int i = 0 ; i < numberOfComputers; i++) {
			String computerServicesInboundPortURI = "csip_" +i;
			String computerStaticStateDataInboundPortURI = "cssdip_" + i;
			String computerDynamicStateDataInboundPortURI = "cdsdip_" + i;
			String computerURI = "computer" + i;
			
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
			
			ComputerMonitor computerMonitor = new ComputerMonitor (
					computerURI, 
					active, 
					computerStaticStateDataInboundPortURI, 
					computerDynamicStateDataInboundPortURI
			);
			this.addDeployedComponent(computerMonitor);
			
			computerURIsList.add(computerURI);
			computerServicesInboundPortURIList.add(computerServicesInboundPortURI);
			computerStaticStateDataInboundPortURIList.add(computerStaticStateDataInboundPortURI);
			computerDynamicStateDataInboundPortURIList.add(computerDynamicStateDataInboundPortURI);
			System.out.println(computerURI + " deployed.");
		}
		
		// Dynamic Component Creator
		// --------------------------------------------------------------------	
		dynamicComponentCreator = new DynamicComponentCreator(dynamicComponentCreationInboundPortURI);
		
		
		// Deploy Applications
		// --------------------------------------------------------------------
		Double meanInterArrivalTime = 500.0;
		Long meanNumberOfInstructions = 6000000000L;
		
		applicationURIsList = new ArrayList<String> ();
		applicationSubmissionInboundPortURIList = new ArrayList<String> ();
		applicationNotificationInboundPortURIList = new ArrayList<String> ();
		
		for(int i = 0 ; i < numberOfApplications; i++) {
			String appURI = "app" + i;
			
			Application application = new Application (
					appURI,
					coresNeeded,
					meanInterArrivalTime,
					meanNumberOfInstructions,
					applicationServicesInboundPortURI + "_" + i,
					applicationSubmissionInboundPortURI + "_" + i,
					applicationNotificationInboundPortURI + "_" + i
			);
			this.addDeployedComponent(application);
			application.toggleLogging();
			application.toggleTracing();
			applicationURIsList.add(appURI);
			applicationSubmissionInboundPortURIList.add(applicationSubmissionInboundPortURI + "_" + i);
			applicationNotificationInboundPortURIList.add(applicationNotificationInboundPortURI + "_" + i);
			
			System.out.println("application " + appURI + " deployed.");
		}
		
		
		// Deploy an Admission Controller
		// --------------------------------------------------------------------		
		this.admissionController = new AdmissionController (
			computerURIsList,
			computerServicesInboundPortURIList,
			computerStaticStateDataInboundPortURIList,
			computerDynamicStateDataInboundPortURIList,
			applicationURIsList,
			applicationSubmissionInboundPortURIList,
			applicationNotificationInboundPortURIList,
			dynamicComponentCreationInboundPortURI,
			avmsPerApplication,
			coresPerAVM
		);
		this.addDeployedComponent(this.admissionController);
		this.admissionController.toggleTracing();
		this.admissionController.toggleLogging();
		
		System.out.println("admission controller deployed.");
		
		super.deploy();
		
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
