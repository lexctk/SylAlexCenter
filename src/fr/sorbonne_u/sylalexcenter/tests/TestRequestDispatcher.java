package fr.sorbonne_u.sylalexcenter.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.RequestDispatcherIntegrator;

/**
 * The class <code>TestRequestDispatcher</code> deploys a single AVM, with a 
 * single application and tests the request dispatcher.
 * 
 * <p><strong>Description</strong></p>
 * 
 * 
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class TestRequestDispatcher extends AbstractCVM {
	
	// Port URIs
	// -----------------------------------------------------------------
	public static final String computerServicesInboundPortURI = "csip";
	public static final String computerStaticStateDataInboundPortURI = "cssdip";
	public static final String computerDynamicStateDataInboundPortURI = "cdsdip";
	
	public static final ArrayList<String> applicationVMManagementInboundPortURIList = new ArrayList<String>();
	public static final ArrayList<String> applicationVMRequestSubmissionInboundPortURIList = new ArrayList<String>();
	public static final ArrayList<String> applicationVMRequestNotificationInboundPortURIList = new ArrayList<String>();
	
	public static final String requestGeneratorManagementInboundPortURI = "rgmip";
	public static final String requestGeneratorSubmissionInboundPortURI = "rgsip";
	public static final String requestGeneratorNotificationInboundPortURI = "rgnip";
	
	public static final String requestDispatcherManagementInboundPortURI = "rdmip";

	
	// Components
	// -----------------------------------------------------------------
	private ApplicationVM applicationVM;
	private RequestGenerator requestGenerator;
	private ComputerMonitor computerMonitor;
	private RequestDispatcherIntegrator requestDispatcherIntegrator;
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
		
		this.computerMonitor = new ComputerMonitor (
				computerURI, 
				active, 
				computerStaticStateDataInboundPortURI, 
				computerDynamicStateDataInboundPortURI
		);
		
		this.addDeployedComponent(this.computerMonitor);


		// Deploy a Request Generator
		// --------------------------------------------------------------------
		String rgURI = "rg0";
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

		
		// Deploy numAvm AVM
		// --------------------------------------------------------------------
		ArrayList<String> vmURIList = new ArrayList<String>();
		int numAvm = 2;
		
		for (int i = 0; i < numAvm; i++) {
			vmURIList.add("avm" + i);
			applicationVMManagementInboundPortURIList.add("avmip" + i);
			applicationVMRequestSubmissionInboundPortURIList.add("avmrsip" + i);
			applicationVMRequestNotificationInboundPortURIList.add("avmrnip" + i);
		}

		for (int i = 0; i < numAvm; i++) {
			try {
				this.applicationVM = new ApplicationVM (
						vmURIList.get(i), 
						applicationVMManagementInboundPortURIList.get(i), 
						applicationVMRequestSubmissionInboundPortURIList.get(i), 
						applicationVMRequestNotificationInboundPortURIList.get(i)
				);
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.addDeployedComponent(this.applicationVM);
	
			this.applicationVM.toggleTracing();
			this.applicationVM.toggleLogging();
		}
		
		
		// Deploy the request dispatcher
		// --------------------------------------------------------------------
		String rdURI = "rd0";
		
		this.requestDispatcher = new RequestDispatcher (
				rdURI, 
				requestDispatcherManagementInboundPortURI,
				requestGeneratorSubmissionInboundPortURI,
				requestGeneratorNotificationInboundPortURI,
				vmURIList,
				applicationVMRequestSubmissionInboundPortURIList,
				applicationVMRequestNotificationInboundPortURIList
		);
		
		this.addDeployedComponent(this.requestDispatcher);
		this.requestDispatcher.toggleTracing();
		this.requestDispatcher.toggleLogging();
	

		// Deploy an integrator.
		// --------------------------------------------------------------------
		this.requestDispatcherIntegrator = new RequestDispatcherIntegrator (
				computerServicesInboundPortURI, 
				applicationVMManagementInboundPortURIList,
				requestGeneratorManagementInboundPortURI,
				requestDispatcherManagementInboundPortURI
		);
		this.addDeployedComponent(this.requestDispatcherIntegrator);

		
		super.deploy();
	}
	
	public static void main(String[] args) {
		
		TestRequestDispatcher testRequestDispatcher;
		
		try {
			testRequestDispatcher = new TestRequestDispatcher();
			
			testRequestDispatcher.startStandardLifeCycle(10000L);
			
			Thread.sleep(10000L);
			System.exit(0);
			
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

}
