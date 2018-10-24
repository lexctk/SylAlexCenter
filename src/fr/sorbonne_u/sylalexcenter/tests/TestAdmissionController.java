package fr.sorbonne_u.sylalexcenter.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.AdmissionController;

public class TestAdmissionController extends AbstractCVM {

	// Port URIs
	// -----------------------------------------------------------------
	public static final String computerServicesInboundPortURI = "csip";
	public static final String computerStaticStateDataInboundPortURI = "cssdip";
	public static final String computerDynamicStateDataInboundPortURI = "cdsdip";
	
	public static final String requestGeneratorManagementInboundPortURI = "appmip";
	public static final String requestGeneratorSubmissionInboundPortURI = "appsip";
	public static final String requestGeneratorNotificationInboundPortURI = "appnip";	
	

	// Components
	// -----------------------------------------------------------------
	private RequestGenerator requestGenerator;
	private ComputerMonitor computerMonitor;
	private AdmissionController admissionController;
	
	
	public TestAdmissionController() throws Exception {
		super();
	}

	public TestAdmissionController(boolean isDistributed) throws Exception {
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
		
		this.computerMonitor = new ComputerMonitor (
				computerURI, 
				active, 
				computerStaticStateDataInboundPortURI, 
				computerDynamicStateDataInboundPortURI
		);
		
		this.addDeployedComponent(this.computerMonitor);
		

		// Deploy a Request Generator - playing the role of application
		// --------------------------------------------------------------------
		String rgURI = "app0";
		double meanInterArrivalTime = 500.0;
		long meanNumberOfInstructions = 6000000000L;
		
		this.requestGenerator = new RequestGenerator (
				rgURI, 
				meanInterArrivalTime, 
				meanNumberOfInstructions, 
				requestGeneratorManagementInboundPortURI, 
				requestGeneratorSubmissionInboundPortURI, 
				requestGeneratorNotificationInboundPortURI
		);
		
		this.addDeployedComponent(requestGenerator);
		this.requestGenerator.toggleTracing();
		this.requestGenerator.toggleLogging();
		
		
		// Deploy an Admission Controller
		// --------------------------------------------------------------------		
		this.admissionController = new AdmissionController (
				"acURI", 
				computerURI, //single computer for now
				computerServicesInboundPortURI,
				computerStaticStateDataInboundPortURI,
				computerDynamicStateDataInboundPortURI,
				requestGeneratorManagementInboundPortURI,
				requestGeneratorSubmissionInboundPortURI,
				requestGeneratorNotificationInboundPortURI
		);
		
		this.addDeployedComponent(admissionController);
		this.admissionController.toggleTracing();
		this.admissionController.toggleLogging();
	}

	public static void main(String[] args) {
		
		TestAdmissionController testAdmissionController;
		
		try {
			testAdmissionController = new TestAdmissionController();
			
			testAdmissionController.startStandardLifeCycle(10000L);
			
			Thread.sleep(10000L);
			//System.exit(0);
			
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

}
