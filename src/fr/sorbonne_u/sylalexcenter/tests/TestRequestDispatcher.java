package fr.sorbonne_u.sylalexcenter.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenter.software.connectors.RequestNotificationConnector;
import fr.sorbonne_u.datacenter.software.connectors.RequestSubmissionConnector;
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.RequestDispatcher;

/**
 * The class <code>TestRequestDispatcher</code> deploys a single AVM, with a 
 * single application and tests the request dispatcher.
 * 
 * <p><strong>Description</strong></p>
 * 
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class TestRequestDispatcher extends AbstractCVM {
	
	// Port URIs
	// -----------------------------------------------------------------
	private static final String computerServicesInboundPortURI = "csip";
	private static final String computerStaticStateDataInboundPortURI = "cssdip";
	private static final String computerDynamicStateDataInboundPortURI = "cdsdip";
	
	private static final ArrayList<String> applicationVMManagementInboundPortURIList = new ArrayList<>();
	private static final ArrayList<String> applicationVMRequestSubmissionInboundPortURIList = new ArrayList<>();
	private static final ArrayList<String> applicationVMRequestNotificationInboundPortURIList = new ArrayList<>();
	private static final ArrayList<String> applicationVMRequestNotificationOutboundPortURIList = new ArrayList<>();

	private static final String requestGeneratorManagementInboundPortURI = "rgmip";
	private static final String requestGeneratorSubmissionInboundPortURI = "rgsip";
	private static final String requestGeneratorSubmissionOutboundPortURI = "rgsop";
	private static final String requestGeneratorNotificationInboundPortURI = "rgnip";
	
	private static final String requestDispatcherManagementInboundPortURI = "rdmip";
	private static final String requestDispatcherServicesInboundPortURI = "rdsvip";
	private static final String requestDispatcherSubmissionInboundPortURI = "rdsip";
	private static final String requestDispatcherNotificationOutboundPortURI = "rdnop";
	private static final String requestDispatcherDynamicStateDataInboundPortURI = "rddsdip";

	private static final ArrayList<String> requestDispatcherSubmissionOutboundPortURIList = new ArrayList<>();
	private static final ArrayList<String> requestDispatcherNotificationInboundPortURIList = new ArrayList<>();

	// Constructors
	// -----------------------------------------------------------------
	private TestRequestDispatcher() throws Exception {
		super();
	}

	// Deploy
	// -----------------------------------------------------------------	
	@Override
 	public void deploy() throws Exception { 
		// Deploy a Computer with 2 Processors and 2 Cores each
		// -----------------------------------------------------------------
		String computerURI = "computer0";
		int numberOfProcessors = 4;
		int numberOfCores = 2;
		
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
		computer.toggleTracing();
		computer.toggleLogging();
		
		
		// Deploy a computer monitor 
		// --------------------------------------------------------------------
		ComputerMonitor computerMonitor = new ComputerMonitor(
				computerURI,
				true,
				computerStaticStateDataInboundPortURI,
				computerDynamicStateDataInboundPortURI
		);
		
		this.addDeployedComponent(computerMonitor);
		

		// Deploy a Request Generator
		// --------------------------------------------------------------------
		String rgURI = "rg0";
		double meanInterArrivalTime = 500.0;
		long meanNumberOfInstructions = 6000000000L;

		RequestGenerator requestGenerator = new RequestGenerator(
				rgURI,
				meanInterArrivalTime,
				meanNumberOfInstructions,
				requestGeneratorManagementInboundPortURI,
				requestGeneratorSubmissionInboundPortURI,
				requestGeneratorSubmissionOutboundPortURI,
				requestGeneratorNotificationInboundPortURI
		);
		
		this.addDeployedComponent(requestGenerator);
		
		// AVM URIs
		// --------------------------------------------------------------------
		ArrayList<String> vmURIList = new ArrayList<>();
		int numAvm = 2;
		
		for (int i = 0; i < numAvm; i++) {
			vmURIList.add("avm" + i);
			applicationVMManagementInboundPortURIList.add("avmip" + i);
			applicationVMRequestSubmissionInboundPortURIList.add("avmrsip" + i);
			applicationVMRequestNotificationInboundPortURIList.add("avmrnip" + i);
			applicationVMRequestNotificationOutboundPortURIList.add("avmrnop" + i);
		}
		

		// Deploy the request dispatcher
		// --------------------------------------------------------------------
		String rdURI = "rd0";
		
		for (int i = 0; i < numAvm; i++) {
			requestDispatcherSubmissionOutboundPortURIList.add("rdsop" + i);
			requestDispatcherNotificationInboundPortURIList.add("rdnip" + i);
		}

		RequestDispatcher requestDispatcher = new RequestDispatcher(
				rdURI,
				vmURIList,
				requestDispatcherManagementInboundPortURI,
				requestDispatcherServicesInboundPortURI,
				requestDispatcherSubmissionInboundPortURI,
				requestDispatcherSubmissionOutboundPortURIList,
				requestDispatcherNotificationInboundPortURIList,
				requestDispatcherNotificationOutboundPortURI,
				requestDispatcherDynamicStateDataInboundPortURI
		);
		
		this.addDeployedComponent(requestDispatcher);
	
		// Deploy numAvm AVM
		// --------------------------------------------------------------------
		// Components
		// -----------------------------------------------------------------
		ArrayList<ApplicationVM> applicationVM = new ArrayList<>();

		for (int i = 0; i < numAvm; i++) {
			try {
				applicationVM.add(new ApplicationVM (
						vmURIList.get(i), 
						applicationVMManagementInboundPortURIList.get(i), 
						applicationVMRequestSubmissionInboundPortURIList.get(i), 
						applicationVMRequestNotificationInboundPortURIList.get(i),
						applicationVMRequestNotificationOutboundPortURIList.get(i)
				));
			} catch (Exception e) {
				throw new Exception (e);
			}
			this.addDeployedComponent(applicationVM.get(i));
		}

		for (int i = 0; i < numAvm; i++ ) {
			try {
				requestDispatcher.doPortConnection(
						requestDispatcherSubmissionOutboundPortURIList.get(i),
						applicationVMRequestSubmissionInboundPortURIList.get(i),
						RequestSubmissionConnector.class.getCanonicalName());
			} catch (Exception e) {
				throw new ComponentStartException("Exception connecting Dispatcher with AVM for submission " + e);
			}

			try {
				applicationVM.get(i).doPortConnection(
						applicationVMRequestNotificationOutboundPortURIList.get(i),
						requestDispatcherNotificationInboundPortURIList.get(i),
						RequestNotificationConnector.class.getCanonicalName());
			} catch (Exception e) {
				throw new ComponentStartException("Exception connecting AVM with Dispatcher for notification " + e);
			}
		}
		
		try {
			requestGenerator.doPortConnection(
				requestGeneratorSubmissionOutboundPortURI,
				requestDispatcherSubmissionInboundPortURI,
				RequestSubmissionConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new Exception("Exception connecting Request Generator with Dispatcher for submission " + e);
		}

		try {
			requestDispatcher.doPortConnection(
					requestDispatcherNotificationOutboundPortURI,
					requestGeneratorNotificationInboundPortURI,
					RequestNotificationConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new Exception("Exception connecting Request Generator with Dispatcher for notification " + e);
		}

		// Deploy an integrator.
		// --------------------------------------------------------------------
		RequestDispatcherIntegrator requestDispatcherIntegrator = new RequestDispatcherIntegrator(
				computerServicesInboundPortURI,
				applicationVMManagementInboundPortURIList,
				requestGeneratorManagementInboundPortURI,
				requestDispatcherManagementInboundPortURI
		);
		this.addDeployedComponent(requestDispatcherIntegrator);

		super.deploy();
	}
	
	public static void main(String[] args) {
		
		TestRequestDispatcher testRequestDispatcher;
		
		try {
			testRequestDispatcher = new TestRequestDispatcher();
			
			testRequestDispatcher.startStandardLifeCycle(500000L);
			
			Thread.sleep(500L);
			System.exit(0);
			
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

}
