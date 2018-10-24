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
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;
import fr.sorbonne_u.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationNotificationI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationSubmissionI;
import fr.sorbonne_u.sylalexcenter.application.ports.ApplicationNotificationInboundPort;
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

public class Application extends AbstractComponent {
	
	protected final String appURI;
	protected final int numCores;
	protected final double meanInterArrivalTime; //for the request generator
	protected final long meanNumberOfInstructions; //for the request generator

	protected String rgURI;
	protected String requestGeneratorManagementInboundPortURI;
	protected String requestGeneratorSubmissionInboundPortURI;
	protected String requestGeneratorNotificationInboundPortURI;
	
	protected ApplicationSubmissionOutboundPort asop;
	protected ApplicationNotificationInboundPort anip;
	
	protected String applicationSubmissionInboundPortURI;
	
	protected DynamicComponentCreationOutboundPort rgport;
	protected ReflectionOutboundPort rop;
	
	protected RequestGeneratorManagementOutboundPort rgmop;

	public Application (
			String appURI, 
			int numCores, 
			double meanInterArrivalTime,
			long meanNumberOfInstructions,
			String applicationSubmissionInboundPortURI,			
			String applicationNotificationInboundPortURI
		) throws Exception {
		
		super(1, 1);
		assert appURI != null;
		assert numCores > 0;
		
		this.appURI = appURI;
		this.numCores = numCores;
		this.meanInterArrivalTime = meanInterArrivalTime;
		this.meanNumberOfInstructions = meanNumberOfInstructions;
		this.applicationSubmissionInboundPortURI = applicationSubmissionInboundPortURI;
		
		// create request generator port URIs for this application
		this.rgURI = appURI + "_rg";
		this.requestGeneratorManagementInboundPortURI = appURI + "_rgmip";
		this.requestGeneratorSubmissionInboundPortURI = appURI + "_rgsip";
		this.requestGeneratorNotificationInboundPortURI = appURI + "_rgnip";
		
		this.addRequiredInterface(ApplicationSubmissionI.class);
		this.asop = new ApplicationSubmissionOutboundPort(this);
		this.addPort(this.asop);
		this.asop.publishPort();
		
		this.addOfferedInterface(ApplicationNotificationI.class);
		this.anip = new ApplicationNotificationInboundPort(applicationNotificationInboundPortURI, this);
		this.addPort(this.anip);
		this.anip.publishPort();
		
		this.addRequiredInterface(DynamicComponentCreationI.class);
		
		assert this.appURI != null && this.appURI.length() > 0;
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
			// connect internal port outbound to inbound URI
			this.doPortConnection(this.asop.getPortURI(), applicationSubmissionInboundPortURI,
					RequestNotificationConnector.class.getCanonicalName());
			
			// create outbound port for the Request Generator
			this.rgport = new DynamicComponentCreationOutboundPort(this);
			this.rgport.localPublishPort();
			this.addPort(rgport);
			this.rgport.doConnection(AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX, DynamicComponentCreationConnector.class.getCanonicalName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.start();
	}
	

	@Override
	public void finalise() throws Exception {

		if (this.asop.connected()) this.asop.doDisconnection();
		if (this.rgport.connected()) this.rgport.doDisconnection();

		super.finalise();
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {
		
		try {
			this.asop.unpublishPort();
			this.anip.unpublishPort();
			
			if (this.rgport.isPublished()) this.rgport.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendAdmissionRequest() throws Exception {
		this.logMessage("Application " + this.appURI + " asking for admission with " + this.numCores + " cores.");
		
		// deploy generator - this is done here to have the ports available if Controller accepts
		deployGenerator ();
		
		this.asop.submitApplicationAndNotify(this.appURI, this.numCores);
	}
	
	public void acceptApplicationAdmissionNotification (boolean accepted) throws Exception {
		this.logMessage("Application " + this.appURI + " is notified that admission is " + accepted);
		
		if (accepted) {
			startRequestGeneration();
		}
	}
	
	
	/**
	 * (see Javadoc fr.sorbonne_u.components.pre.dcc)
	 * 
	 * Application will create a Request Generator: 
	 * call the service createComponent with the appropriate parameters on the 
	 * DynamicComponentCreationOutboundPort
	 */
	public void deployGenerator () throws Exception {
		
		Object [] requestGenerator = new Object[] {
				this.rgURI,
				this.meanInterArrivalTime,
				this.meanNumberOfInstructions,
				this.requestGeneratorManagementInboundPortURI,
				this.requestGeneratorSubmissionInboundPortURI,
				this.requestGeneratorNotificationInboundPortURI
		};
		
		this.rgport.createComponent (RequestGenerator.class.getCanonicalName(), requestGenerator);
		
		rop = new ReflectionOutboundPort(this);
		rop.localPublishPort();
		this.addPort(rop);
		rop.doConnection(this.rgURI, ReflectionConnector.class.getCanonicalName());
		
		rop.toggleLogging();
		rop.toggleTracing();
	}
	
	public void startRequestGeneration() throws Exception {
		
		this.doPortConnection(this.rgmop.getPortURI(), requestGeneratorManagementInboundPortURI,
				RequestNotificationConnector.class.getCanonicalName());
		
		// start generation
		this.rgmop.startGeneration();
		
		// wait 20 seconds
		Thread.sleep(2000L);
		
		// then stop the generation.
		this.rgmop.stopGeneration();
	}
}
