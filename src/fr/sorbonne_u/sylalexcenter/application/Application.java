package fr.sorbonne_u.sylalexcenter.application;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;
import fr.sorbonne_u.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;
import fr.sorbonne_u.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.connectors.ApplicationServicesConnector;
import fr.sorbonne_u.sylalexcenter.application.connectors.ApplicationSubmissionConnector;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationNotificationHandlerI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationNotificationI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationServicesI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationSubmissionI;
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
 * containing the application URI and number of cores the application needs
 * 
 * It dynamically deploys the RequestGenerator, and if application is accepted, 
 * starts generating requests
 * 
 */

public class Application extends AbstractComponent implements ApplicationServicesI, ApplicationNotificationHandlerI {
	
	protected final String appURI;
	protected final Double meanInterArrivalTime; //for the request generator
	protected final Long meanNumberOfInstructions; //for the request generator

	protected String rgURI;
	protected String requestGeneratorManagementInboundPortURI;
	protected String requestGeneratorSubmissionInboundPortURI;
	protected String requestGeneratorNotificationInboundPortURI;
	
	protected String applicationSubmissionInboundPortURI;
	protected String applicationServicesInboundPortURI;
	
	protected int coresNeeded;
	
	protected ApplicationServicesInboundPort asvip;
	protected ApplicationServicesOutboundPort asvop;
	
	protected ApplicationSubmissionOutboundPort asop;
	protected ApplicationNotificationInboundPort anip;
	
	protected RequestGeneratorManagementOutboundPort rgmop;
	protected RequestGenerator requestGenerator;

	public Application (
			String appURI,
			Integer coresNeeded,
			Double meanInterArrivalTime,
			Long meanNumberOfInstructions,
			String applicationServicesInboundPortURI,
			String applicationSubmissionInboundPortURI,
			String applicationNotificationInboundPortURI
		) throws Exception {
		
		super(appURI, 1, 1);
		
		assert meanInterArrivalTime > 0.0;
		assert meanNumberOfInstructions > 0;
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
		this.rgURI = "rg-" + appURI; 		
		this.requestGeneratorManagementInboundPortURI = appURI + "-rgmip";
		this.requestGeneratorSubmissionInboundPortURI = appURI + "-rgsip";
		this.requestGeneratorNotificationInboundPortURI = appURI + "-rgnip";

		this.addOfferedInterface(ApplicationServicesI.class);
		this.asvip = new ApplicationServicesInboundPort(applicationServicesInboundPortURI, this);
		this.addPort(this.asvip);
		this.asvip.publishPort();
		
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
		
		assert this.appURI != null && this.appURI.length() > 0;
		assert this.asvip != null && this.asvip instanceof ApplicationServicesI;
		assert this.asop != null && this.asop instanceof ApplicationSubmissionI;
		assert this.anip != null && this.anip instanceof ApplicationNotificationI;		
		assert this.rgmop != null && this.rgmop instanceof RequestGeneratorManagementI;	
	}
	
	
	/**
	 * (see Javadoc fr.sorbonne_u.components.pre.dcc)
	 * 
	 * A component that wants to create another component on a remote JVM has to 
	 * create an outbound port DynamicComponentCreationOutboundPort and connect it 
	 * to the inbound port of the dynamic component creator running on the 
	 * remote virtual machine. Create port on start()
	 */
	@Override
	public void start() throws ComponentStartException {
		
		try {
			this.doPortConnection(this.asop.getPortURI(), this.applicationSubmissionInboundPortURI,
					ApplicationSubmissionConnector.class.getCanonicalName());
			
			this.doPortConnection(this.asvop.getPortURI(), this.applicationServicesInboundPortURI,
					ApplicationServicesConnector.class.getCanonicalName());
			
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
		
		super.start();
	}
	
	@Override
	public void execute() throws Exception {
		
		this.asvop.sendRequestForApplicationExecution(coresNeeded);
		
		super.execute();
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

	/**
	 * (see Javadoc fr.sorbonne_u.components.pre.dcc)
	 * 
	 * Application will create a Request Generator: 
	 * call the service createComponent with the appropriate parameters on the 
	 * DynamicComponentCreationOutboundPort
	 */
	public void deployGenerator () throws Exception {

		this.requestGenerator = new RequestGenerator (
				this.rgURI,
				this.meanInterArrivalTime,
				this.meanNumberOfInstructions,
				this.requestGeneratorManagementInboundPortURI,
				this.requestGeneratorSubmissionInboundPortURI,
				this.requestGeneratorNotificationInboundPortURI);
		
		AbstractCVM.getCVM().addDeployedComponent(requestGenerator);
		this.requestGenerator.toggleLogging();
		this.requestGenerator.toggleTracing();
		
		this.rgmop = new RequestGeneratorManagementOutboundPort(this);
		this.addPort(this.rgmop);
		
		this.rgmop.doConnection(
				this.requestGeneratorManagementInboundPortURI,
				RequestGeneratorManagementConnector.class.getCanonicalName());
		
		this.logMessage("Application " + this.appURI + " deployed request generator " + this.rgURI + ".");
	}
	
	@Override
	public void	sendRequestForApplicationExecution(int coresToReserve) throws Exception {
		this.logMessage("Application " + this.appURI + " asking for execution permission.");
		
		deployGenerator();

		this.asop.submitApplicationAndNotify (
				this.appURI, 
				this.requestGeneratorSubmissionInboundPortURI, 
				this.requestGeneratorNotificationInboundPortURI, 
				coresToReserve
		);
	}

	@Override
	public void acceptApplicationAdmissionNotification(boolean isAccepted) throws Exception {
			
		this.logMessage("Application " + this.appURI + " is notified that admission request "
					+ ((isAccepted)? "has" : "hasn't")
					+ " been accepted.");
		
		if (isAccepted) {				
			launch();					
		}
	}

	public void	launch() throws Exception {
		// start generation
		this.rgmop.startGeneration();
		
		// wait 20 seconds
		Thread.sleep(2000L);
		
		// then stop the generation.
		this.rgmop.stopGeneration();		
	}	
}