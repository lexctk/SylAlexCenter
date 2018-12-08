package fr.sorbonne_u.sylalexcenter.application;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.pre.dcc.connectors.DynamicComponentCreationConnector;
import fr.sorbonne_u.components.pre.dcc.interfaces.DynamicComponentCreationI;
import fr.sorbonne_u.components.pre.dcc.ports.DynamicComponentCreationOutboundPort;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import fr.sorbonne_u.datacenter.software.connectors.RequestNotificationConnector;
import fr.sorbonne_u.datacenter.software.connectors.RequestSubmissionConnector;
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;
import fr.sorbonne_u.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.sorbonne_u.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.connectors.ApplicationServicesConnector;
import fr.sorbonne_u.sylalexcenter.application.connectors.ApplicationSubmissionConnector;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationManagementI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationNotificationHandlerI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationNotificationI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationServicesI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationSubmissionI;
import fr.sorbonne_u.sylalexcenter.application.ports.ApplicationManagementInboundPort;
import fr.sorbonne_u.sylalexcenter.application.ports.ApplicationNotificationInboundPort;
import fr.sorbonne_u.sylalexcenter.application.ports.ApplicationServicesInboundPort;
import fr.sorbonne_u.sylalexcenter.application.ports.ApplicationServicesOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.ports.ApplicationSubmissionOutboundPort;

/**
 * The class <code>Application</code> implements a an application
 * component
 * 
 * <p>
 * <strong>Description</strong>
 * </p>
 * An application will send requests to the AdmissionController 
 * containing the application URI, number of cores the application needs, and two port 
 * URIs for its Request Generator. 
 * 
 * The application deploys a RequestGenerator before each admission request, 
 * and if application is accepted, starts generating requests
 * 
 */

public class Application extends AbstractComponent implements ApplicationManagementI, ApplicationServicesI, ApplicationNotificationHandlerI {
	
	private final String appURI;
	private final Double meanInterArrivalTime; //for the request generator
	private final Long meanNumberOfInstructions; //for the request generator

	private final Long applicationTime;

	private String rgURI;
	private String requestGeneratorManagementInboundPortURI;
	private String requestGeneratorSubmissionInboundPortURI;
	private String requestGeneratorSubmissionOutboundPortURI;
	private String requestGeneratorNotificationInboundPortURI;

	private String applicationSubmissionInboundPortURI;
	private String applicationServicesInboundPortURI;

	private int coresNeeded;

	private ApplicationServicesOutboundPort asvop;

	private ApplicationSubmissionOutboundPort asop;
	private ApplicationNotificationInboundPort anip;

	// Dynamic Component Creation
	private DynamicComponentCreationOutboundPort dccop;
	private ReflectionOutboundPort rop;

	/**
	 * @param appURI: application uri
	 * @param coresNeeded: minimum number of cores application needs to be able to run
	 * @param meanInterArrivalTime: the mean inter-arrival time of requests in ms, for Request Generator
	 * @param meanNumberOfInstructions: mean number of instructions of the requests in ms, for Request Generator
	 * @param applicationServicesInboundPortURI: services handle admission communication
	 * @param applicationSubmissionInboundPortURI: submissions handle requests to admission controller
	 * @param applicationNotificationInboundPortURI: notifications handle termination requests
	 * @throws Exception: throws Exception
	 */
	public Application (
			String appURI, 
			Integer coresNeeded, 
			Double meanInterArrivalTime, 
			Long meanNumberOfInstructions,
			Long applicationTime,
			String applicationManagementInboundPortURI,
			String applicationServicesInboundPortURI,
			String applicationSubmissionInboundPortURI,
			String applicationNotificationInboundPortURI
		) throws Exception {
		
		super(appURI, 1, 1);
		
		assert meanInterArrivalTime > 0.0;
		assert meanNumberOfInstructions > 0;
		assert applicationManagementInboundPortURI != null;
		assert applicationServicesInboundPortURI != null;
		assert applicationSubmissionInboundPortURI != null;
		assert applicationNotificationInboundPortURI != null;
		
		this.appURI = appURI;
		this.applicationSubmissionInboundPortURI = applicationSubmissionInboundPortURI;
		this.applicationServicesInboundPortURI = applicationServicesInboundPortURI;
		this.coresNeeded = coresNeeded;
		
		// Request Generator
		this.meanInterArrivalTime = meanInterArrivalTime;
		this.meanNumberOfInstructions = meanNumberOfInstructions;

		this.applicationTime = applicationTime;
		
		this.rgURI = appURI + "-rg";
		this.requestGeneratorManagementInboundPortURI = appURI + "-rgmip";
		this.requestGeneratorSubmissionInboundPortURI = appURI + "-rgsip";
		this.requestGeneratorSubmissionOutboundPortURI = appURI + "-rgsop";
		this.requestGeneratorNotificationInboundPortURI = appURI + "-rgnip";
		
		this.addOfferedInterface(ApplicationManagementI.class);
		ApplicationManagementInboundPort amip = new ApplicationManagementInboundPort(applicationManagementInboundPortURI, this);
		this.addPort(amip);
		amip.publishPort();

		this.addOfferedInterface(ApplicationServicesI.class);
		ApplicationServicesInboundPort asvip = new ApplicationServicesInboundPort(applicationServicesInboundPortURI, this);
		this.addPort(asvip);
		asvip.publishPort();
		
		this.asvop = new ApplicationServicesOutboundPort(this);
		this.addPort(this.asvop);
		this.asvop.publishPort();
		
		this.addRequiredInterface(ApplicationSubmissionI.class);
		this.asop = new ApplicationSubmissionOutboundPort(this);
		this.addPort(this.asop);
		this.asop.publishPort();
		
		this.addOfferedInterface(ApplicationNotificationI.class);
		this.anip = new ApplicationNotificationInboundPort(applicationNotificationInboundPortURI, this);
		this.addPort(this.anip);
		this.anip.publishPort();
		
		this.rop = new ReflectionOutboundPort(this);
		
		this.tracer.setRelativePosition(0, 1);
		
		assert this.appURI != null && this.appURI.length() > 0;
	}
	
	@Override
	public void start() throws ComponentStartException {
		
		try {
			this.doPortConnection(this.asop.getPortURI(), this.applicationSubmissionInboundPortURI,
					ApplicationSubmissionConnector.class.getCanonicalName());
			
			this.doPortConnection(this.asvop.getPortURI(), this.applicationServicesInboundPortURI,
					ApplicationServicesConnector.class.getCanonicalName());
			
			// create outbound port for the Dynamic Component Creator
			this.addRequiredInterface(DynamicComponentCreationI.class);
			this.dccop = new DynamicComponentCreationOutboundPort(this.appURI + "dccop", this);
			this.addPort(this.dccop);
			this.dccop.localPublishPort();

			// Needed to avoid class cast exception
			String dynamicComponentCreationInboundPortURI = "";
			this.dccop.doConnection (dynamicComponentCreationInboundPortURI + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
					DynamicComponentCreationConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
		
		super.start();
	}

	@Override
	public void finalise() throws Exception {

		if (this.asop.connected()) this.asop.doDisconnection();

		super.finalise();
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			this.asop.unpublishPort();
			this.anip.unpublishPort();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
	}	

	
	public void sendRequest() throws Exception {
		this.asvop.sendRequestForApplicationExecution(coresNeeded);
		
		super.execute();
	}	
	
	@Override
	public void	sendRequestForApplicationExecution(int coresToReserve) throws Exception {
		this.logMessage("Application " + this.appURI + " asking for execution permission.");
		
		deployGenerator();

		this.asop.submitApplicationAndNotify (this.appURI, coresToReserve);
	}

	@Override
	public void acceptApplicationAdmissionNotification(boolean isAccepted) throws Exception {
		this.logMessage("Application " + this.appURI + " is notified that admission request "
					+ ((isAccepted)? "has been accepted" : "has been rejected"));
		
		if (isAccepted) {
			launch();					
		} else {
			this.rop.toggleLogging();
			this.rop.toggleTracing();
		}
	}

	/**
	 * (see Javadoc fr.sorbonne_u.components.pre.dcc)
	 * 
	 * Application will create a Request Generator: 
	 * call the service createComponent with the appropriate parameters on the 
	 * DynamicComponentCreationOutboundPort
	 */
	private void deployGenerator () throws Exception {

		try {
			this.dccop.createComponent(RequestGenerator.class.getCanonicalName(), new Object[] {
				this.rgURI,
				this.meanInterArrivalTime,
				this.meanNumberOfInstructions,
				this.requestGeneratorManagementInboundPortURI,
				this.requestGeneratorSubmissionInboundPortURI,
				this.requestGeneratorSubmissionOutboundPortURI,
				this.requestGeneratorNotificationInboundPortURI
			});
		} catch (Exception e) {
			throw new Exception("Error dynamically creating Request Generator " + e);
		}

		this.addPort(this.rop);
		this.rop.publishPort();

		try {
			this.rop.doConnection(this.rgURI, ReflectionConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new Exception("Error connecting Reflection Outbound Port for Generator " + e);
		}
		
		this.logMessage("Application " + this.appURI + " deployed request generator " + this.rgURI + ".");
	}	
	
	private void launch() throws Exception {

		RequestGeneratorManagementOutboundPort rgmop = new RequestGeneratorManagementOutboundPort(this);
		this.addPort(rgmop);
		
		this.doPortConnection(rgmop.getPortURI(),
			this.requestGeneratorManagementInboundPortURI,
			RequestGeneratorManagementConnector.class.getCanonicalName());
		// start generation
		rgmop.startGeneration();
		
		// wait
		Thread.sleep(applicationTime);
		
		// then stop the generation.
		 rgmop.stopGeneration();
	}
	
	// Connect to Dispatcher
	@Override
	public void doConnectionWithDispatcherForSubmission (String requestDispatcherSubmissionInboundPortUri) throws Exception {
		
		try {
			rop.doPortConnection(
				this.requestGeneratorSubmissionOutboundPortURI,
				requestDispatcherSubmissionInboundPortUri,
				RequestSubmissionConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new Exception("Exception connecting Request Generator with Dispatcher for Submission " + e);
		}
	}
	
	@Override
	public void doConnectionWithDispatcherForNotification (ReflectionOutboundPort ropDispatcher, String requestDispatcherNotificationInboundPortURI) throws Exception {			
		try {
			ropDispatcher.doPortConnection(
					requestDispatcherNotificationInboundPortURI,
				this.requestGeneratorNotificationInboundPortURI,
				RequestNotificationConnector.class.getCanonicalName());			
		} catch (Exception e) {
			throw new Exception("Exception connecting Request Generator with Dispatcher for Notification " + e);
		}
	}
}