package fr.sorbonne_u.sylalexcenter.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
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
public class TestAdmissionControllerMultiJVM extends AbstractDistributedCVM {
	
	// Multi JVM
	// -----------------------------------------------------------------
	private static final ArrayList<String> jvmURI = new ArrayList<>(Arrays.asList("jvm1", "jvm2"));
	
	
	// Setup
	// -----------------------------------------------------------------
	private static final Integer numberOfComputers = 4;
	private static final Integer numberOfProcessors = 2;
	private static final Integer numberOfCores = 2;
	private static final Integer numberOfApplications = 4;
	
	private static final Integer coresPerAVM = 2;
	
	private static final Integer[] coresNeeded = new Integer[] {4, 2, 4, 2};

	private static final long applicationTime = 50000L;

	// Port URIs
	// -----------------------------------------------------------------
	private static final String applicationManagementInboundPortURI = "appmip";
	private static final String applicationServicesInboundPortURI = "appsvip";
	private static final String applicationSubmissionInboundPortURI = "appsip";
	private static final String applicationNotificationInboundPortURI = "appnip";
	
	private ArrayList<String> computerServicesInboundPortURIList;

	// Component URIs
	// -----------------------------------------------------------------	
	private ArrayList<String> computerURIsList;
	private ArrayList<Application> applicationList;


	public TestAdmissionControllerMultiJVM(String[] args) throws Exception {
		super(args);
	}
	
	@Override
	public void	initialise() throws Exception {
		super.initialise();
	}

	// Deploy
	// -----------------------------------------------------------------	
	@Override
 	public void instantiateAndPublish() throws Exception {
		
		if (thisJVMURI.equals(jvmURI.get(0))) {
			
			// Deploy Computers 
			// -----------------------------------------------------------------
			computerURIsList = new ArrayList<>();
			computerServicesInboundPortURIList = new ArrayList<>();
			
			for(int i = 0 ; i < numberOfComputers; i++) {
				String computerServicesInboundPortURI = "csip_" +i;
				String computerStaticStateDataInboundPortURI = "cssdip_" + i;
				String computerDynamicStateDataInboundPortURI = "cdsdip_" + i;
				String computerURI = "computer" + i;
				
				Set<Integer> possibleFrequencies = new HashSet<>();
				possibleFrequencies.add(1500); 
				possibleFrequencies.add(3000); 
				
				Map<Integer, Integer> processingPower = new HashMap<>();
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

				ComputerMonitor computerMonitor = new ComputerMonitor (
						computerURI, 
						true,
						computerStaticStateDataInboundPortURI, 
						computerDynamicStateDataInboundPortURI
				);
				this.addDeployedComponent(computerMonitor);
				
				computerURIsList.add(computerURI);
				computerServicesInboundPortURIList.add(computerServicesInboundPortURI);
				System.out.println(computerURI + " deployed.");
			}
		} else if (thisJVMURI.equals(jvmURI.get(1))) {

			// Deploy Applications
			// --------------------------------------------------------------------
			Double meanInterArrivalTime = 500.0;
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
		} else {
			System.out.println("I don't know this JVM");
		}
		
		super.instantiateAndPublish();
	}

	@Override
	public void	interconnect() throws Exception {

		if (thisJVMURI.equals(jvmURI.get(0))) {
			System.out.println("Connecting " + thisJVMURI);
			//TODO

		} else if (thisJVMURI.equals(jvmURI.get(1))) {
			System.out.println("Connecting " + thisJVMURI);
			//TODO

		} else {
			System.out.println("I don't know this JVM");
		}
		super.interconnect();
	}

	private void scenario() throws RuntimeException {
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

		try {
			final TestAdmissionControllerMultiJVM testAdmissionController = new TestAdmissionControllerMultiJVM(args);
			
			testAdmissionController.deploy();
			
			System.out.println("starting...");
			testAdmissionController.start();
			
			new Thread(() -> {
				try {
					testAdmissionController.scenario();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}).start();
			
			Thread.sleep(90000L);
			
			System.out.println("shutting down...");
			testAdmissionController.shutdown();
			
			System.out.println("ending...");
			System.exit(0);
			
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

}
