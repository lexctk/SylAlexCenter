package fr.sorbonne_u.sylalexcenter.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.AdmissionController;
import fr.sorbonne_u.sylalexcenter.application.Application;


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

	// Setup
	// -----------------------------------------------------------------
	private static final Integer numberOfComputers = 1;
	private static final Integer numberOfProcessors = 2;
	private static final Integer numberOfCores = 6;
	private static final Integer numberOfApplications = 1;
	
	private static final Integer coresPerAVM = 2;
	private static final Integer[] coresNeeded = new Integer[] {2};

	private static final long applicationTime = 500000L;

	// Port URIs
	// -----------------------------------------------------------------
	private static final String applicationManagementInboundPortURI = "appmip";
	private static final String applicationServicesInboundPortURI = "appsvip";
	private static final String applicationSubmissionInboundPortURI = "appsip";
	private static final String applicationNotificationInboundPortURI = "appnip";

	private ArrayList<Application> applicationList;

	private TestAdmissionController() throws Exception {
		super();
	}

	// Deploy
	// -----------------------------------------------------------------	
	@Override
 	public void deploy() throws Exception { 		

		// Deploy Computers 
		// -----------------------------------------------------------------
		// Component URIs
		// -----------------------------------------------------------------
		ArrayList<String> computerURIsList = new ArrayList<>();
		ArrayList<String> computerServicesInboundPortURIList = new ArrayList<>();
		ArrayList<String> computerStaticStateDataInboundPortURIList = new ArrayList<>();
		ArrayList<String> computerDynamicStateDataInboundPortURIList = new ArrayList<>();
		
		for(int i = 0 ; i < numberOfComputers; i++) {
			String computerServicesInboundPortURI = "csip_" +i;
			String computerStaticStateDataInboundPortURI = "cssdip_" + i;
			String computerDynamicStateDataInboundPortURI = "cdsdip_" + i;
			String computerURI = "computer" + i;
			
			Set<Integer> possibleFrequencies = new HashSet<>();
			possibleFrequencies.add(1500);
			possibleFrequencies.add(2000);
			possibleFrequencies.add(2500);
			possibleFrequencies.add(3000); 
			
			Map<Integer, Integer> processingPower = new HashMap<>();
			processingPower.put(1500, 1500000);
			processingPower.put(2000, 2000000);
			processingPower.put(2500, 2500000);
			processingPower.put(3000, 3000000); 
			
			int defaultFrequency = 2000;
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
			
			ComputerMonitor computerMonitor = new ComputerMonitor (
					computerURI, 
					true,
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
		
		// Deploy Applications
		// --------------------------------------------------------------------
		Double meanInterArrivalTime = 1000.0;
		Long meanNumberOfInstructions = 6000000000L;

		ArrayList<String> applicationURIsList = new ArrayList<>();
		applicationList = new ArrayList<>();
		ArrayList<String> applicationManagementInboundPortURIList = new ArrayList<>();
		ArrayList<String> applicationSubmissionInboundPortURIList = new ArrayList<>();
		ArrayList<String> applicationNotificationInboundPortURIList = new ArrayList<>();
		
		for(int i = 0 ; i < numberOfApplications; i++) {
			String appURI = "app" + i;
			
			Application application = new Application (
					appURI,
					coresNeeded[i],
					meanInterArrivalTime,
					meanNumberOfInstructions,
					applicationTime,
					applicationManagementInboundPortURI + "_" + i,
					applicationServicesInboundPortURI + "_" + i,
					applicationSubmissionInboundPortURI + "_" + i,
					applicationNotificationInboundPortURI + "_" + i
			);
			this.addDeployedComponent(application);
			application.toggleLogging();
			application.toggleTracing();
			applicationURIsList.add(appURI);
			applicationList.add(application);
			applicationManagementInboundPortURIList.add(applicationManagementInboundPortURI + "_" + i);
			applicationSubmissionInboundPortURIList.add(applicationSubmissionInboundPortURI + "_" + i);
			applicationNotificationInboundPortURIList.add(applicationNotificationInboundPortURI + "_" + i);
			
			System.out.println("application " + appURI + " deployed.");
		}
		
		
		// Deploy an Admission Controller
		// --------------------------------------------------------------------		
		// Components
		// -----------------------------------------------------------------
		AdmissionController admissionController = new AdmissionController(
				computerURIsList,
				computerServicesInboundPortURIList,
				computerStaticStateDataInboundPortURIList,
				computerDynamicStateDataInboundPortURIList,
				applicationURIsList,
				applicationManagementInboundPortURIList,
				applicationSubmissionInboundPortURIList,
				applicationNotificationInboundPortURIList,
				coresPerAVM
		);
		this.addDeployedComponent(admissionController);
		admissionController.toggleTracing();
		admissionController.toggleLogging();
		
		System.out.println("admission controller deployed.");
		
		super.deploy();
	}
	
	
	
	public void start() throws Exception {
		super.start();
		
		for(int i = 0 ; i < numberOfApplications; i++) {
			
			this.applicationList.get(i).runTask(new AbstractComponent.AbstractTask() {
				public void run() {
					try {
						((Application) this.getOwner()).sendRequest();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});
		}
		
	}
	
	public static void main(String[] args) {
		
		TestAdmissionController testAdmissionController;
		
		try {
			testAdmissionController = new TestAdmissionController();
			
			testAdmissionController.startStandardLifeCycle(applicationTime*10);
			
			Thread.sleep(5000L);
//			System.exit(0);
			
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

}
