package fr.sorbonne_u.sylalexcenter.application;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.TimeManagement;
import fr.sorbonne_u.datacenter.software.connectors.RequestSubmissionConnector;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.sorbonne_u.datacenterclient.requestgenerator.Request;
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;
import fr.sorbonne_u.datacenterclient.utils.TimeProcessing;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.ApplicationAdmission;
import fr.sorbonne_u.sylalexcenter.application.connectors.ApplicationAdmissionNotificationConnector;
import fr.sorbonne_u.sylalexcenter.application.connectors.ApplicationAdmissionSubmissionConnector;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationAdmissionI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationAdmissionNotificationI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationAdmissionSubmissionI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationManagementI;
import fr.sorbonne_u.sylalexcenter.application.ports.ApplicationAdmissionNotificationOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.ports.ApplicationAdmissionSubmissionOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.ports.ApplicationManagementInboundPort;

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
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class Application extends AbstractComponent implements RequestNotificationHandlerI {
	
	protected int counter;
	
	protected final String appURI;
	protected final Integer numCores;
	protected Double meanInterArrivalTime; 
	protected final Long meanNumberOfInstructions; 

	protected String applicationSubmissionInboundPortURI;
	protected String applicationNotificationInboundPortURI;
	protected String applicationAdmissionSubmissionInboundPortURI;
	protected String applicationAdmissionNotificationInboundPortURI;
		
	protected ApplicationManagementInboundPort amip;
	protected RequestSubmissionOutboundPort asop;
	protected RequestNotificationInboundPort anip;
	
	protected ApplicationAdmissionSubmissionOutboundPort aasop;
	protected ApplicationAdmissionNotificationOutboundPort aanop;
	
	protected RandomDataGenerator rng;
	protected Future<?> nextRequestTaskFuture;
	
	protected ApplicationAdmissionI applicationAdmission;
	
	protected boolean isRsopPortConnected = false;

	public Application (
			String appURI, 
			int numCores, 
			double meanInterArrivalTime,
			long meanNumberOfInstructions,
			String applicationManagementInboundPortURI,
			String applicationSubmissionInboundPortURI,			
			String applicationNotificationInboundPortURI,
			String applicationAdmissionSubmissionInboundPortURI,
			String applicationAdmissionNotificationInboundPortURI
		) throws Exception {

		super(1, 1) ;

		// preconditions check
		assert meanInterArrivalTime > 0.0 && meanNumberOfInstructions > 0 ;
		assert applicationManagementInboundPortURI != null ;
		assert applicationSubmissionInboundPortURI != null ;
		assert applicationNotificationInboundPortURI != null ;
		assert applicationAdmissionSubmissionInboundPortURI != null;
		assert applicationAdmissionNotificationInboundPortURI != null;

		// initialization
		this.appURI = appURI ;
		this.counter = 0 ;
		this.numCores = numCores;
		this.meanInterArrivalTime = meanInterArrivalTime ;
		this.meanNumberOfInstructions = meanNumberOfInstructions ;
		this.rng = new RandomDataGenerator() ;
		this.rng.reSeed() ;
		this.nextRequestTaskFuture = null ;
		this.applicationSubmissionInboundPortURI = applicationSubmissionInboundPortURI;
		this.applicationAdmissionSubmissionInboundPortURI = applicationAdmissionSubmissionInboundPortURI;
		
		this.applicationNotificationInboundPortURI = applicationNotificationInboundPortURI;

		this.addOfferedInterface(ApplicationManagementI.class) ;
		this.amip = new ApplicationManagementInboundPort(applicationManagementInboundPortURI, this) ;
		this.addPort(this.amip) ;
		this.amip.publishPort() ;

		this.addRequiredInterface(RequestSubmissionI.class) ;
		this.asop = new RequestSubmissionOutboundPort(this) ;
		this.addPort(this.asop) ;
		this.asop.publishPort() ;

		this.addOfferedInterface(RequestNotificationI.class) ;
		this.anip = new RequestNotificationInboundPort(applicationNotificationInboundPortURI, this) ;
		this.addPort(this.anip) ;
		this.anip.publishPort() ;

		applicationAdmission = new ApplicationAdmission (applicationNotificationInboundPortURI);
		
		addRequiredInterface(ApplicationAdmissionSubmissionI.class);
		aasop = new ApplicationAdmissionSubmissionOutboundPort(this);
		addPort(aasop);
		aasop.publishPort();
		
		this.applicationAdmissionNotificationInboundPortURI = applicationAdmissionNotificationInboundPortURI;
		
		addRequiredInterface(ApplicationAdmissionNotificationI.class);
		aanop = new ApplicationAdmissionNotificationOutboundPort(this);
		addPort(aanop);
		aanop.publishPort();
		
		// post-conditions check
		assert this.rng != null && this.counter >= 0 ;
		assert this.meanInterArrivalTime > 0.0 ;
		assert this.meanNumberOfInstructions > 0 ;
		assert this.asop != null && this.asop instanceof RequestSubmissionI ;
		assert this.aasop!=null && aasop instanceof ApplicationAdmissionSubmissionI;
		assert this.aanop != null && aanop instanceof ApplicationAdmissionNotificationI;		
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
			this.doPortConnection(this.aasop.getPortURI(), applicationAdmissionSubmissionInboundPortURI,
					ApplicationAdmissionSubmissionConnector.class.getCanonicalName());
			this.doPortConnection(this.aanop.getPortURI(), applicationAdmissionNotificationInboundPortURI,
					ApplicationAdmissionNotificationConnector.class.getCanonicalName());

		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
		super.start();
	}
	

	@Override
	public void finalise() throws Exception {

		if (this.nextRequestTaskFuture != null && !(this.nextRequestTaskFuture.isCancelled() ||
				this.nextRequestTaskFuture.isDone())) {
			this.nextRequestTaskFuture.cancel(true) ;
		}
		
		if(isRsopPortConnected) this.doPortDisconnection(this.asop.getPortURI());
		
		super.finalise() ;
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {
		
		try {
			this.asop.unpublishPort() ;
			this.anip.unpublishPort() ;
			this.amip.unpublishPort() ;
			this.aasop.unpublishPort();
			this.aanop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		super.shutdown();
	}	
	
	// -------------------------------------------------------------------------
	// Component internal services
	// -------------------------------------------------------------------------

	/**
	 * start the generation and submission of requests.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception <i>todo.</i>
	 */
	public void startGeneration() throws Exception {
		if (RequestGenerator.DEBUG_LEVEL == 1) {
			this.logMessage("Application " + this.appURI + " starting.");
		}
		this.generateNextRequest();
	}

	/**
	 * stop the generation and submission of requests.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception <i>todo.</i>
	 */
	public void stopGeneration() throws Exception {

		if (RequestGenerator.DEBUG_LEVEL == 1) {
			this.logMessage("Application " + this.appURI + " stopping.");
		}
		if (this.nextRequestTaskFuture != null
				&& !(this.nextRequestTaskFuture.isCancelled() || this.nextRequestTaskFuture.isDone())) {
			this.nextRequestTaskFuture.cancel(true);
		}
	}

	/**
	 * return the current value of the mean inter-arrival time used to generate
	 * requests.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return the current value of the mean inter-arrival time.
	 */
	public double getMeanInterArrivalTime() {
		return this.meanInterArrivalTime;
	}

	/**
	 * set the value of the mean inter-arrival time used to generate requests.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param miat new value for the mean inter-arrival time.
	 */
	public void setMeanInterArrivalTime(double miat) {
		assert miat > 0.0;
		this.meanInterArrivalTime = miat;
	}

	/**
	 * generate a new request with some processing time following an exponential
	 * distribution and then schedule the next request generation in a delay also
	 * following an exponential distribution.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception <i>todo.</i>
	 */
	public void generateNextRequest() throws Exception {
		// generate a random number of instructions for the request.
		long noi = (long) this.rng.nextExponential(this.meanNumberOfInstructions);
		Request r = new Request(this.appURI + "-" + this.counter++, noi);
		
		// generate a random delay until the next request generation.
		long interArrivalDelay = (long) this.rng.nextExponential(this.meanInterArrivalTime);

		if (RequestGenerator.DEBUG_LEVEL == 2) {
			this.logMessage("Request generator " + this.appURI + " submitting request " + r.getRequestURI() + " at "
					+ TimeProcessing.toString(System.currentTimeMillis() + interArrivalDelay)
					+ " with number of instructions " + noi);
		}

		// submit the current request.
		this.asop.submitRequestAndNotify(r);
		// schedule the next request generation.

		this.nextRequestTaskFuture = this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					((RequestGenerator) this.getOwner()).generateNextRequest();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, TimeManagement.acceleratedDelay(interArrivalDelay), TimeUnit.MILLISECONDS);

	}

	/**
	 * process an end of execution notification for a request r previously
	 * submitted.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	r != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param r request that just terminated.
	 * @throws Exception <i>todo.</i>
	 */
	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		assert r != null;

		if (RequestGenerator.DEBUG_LEVEL == 2) {
			this.logMessage("Application " + this.appURI + " is notified that request " + r.getRequestURI()
					+ " has ended.");
		}
	}

	public boolean sendAdmissionRequest() throws Exception {

		applicationAdmission.setApplicationManagementInboundPortURI(amip.getPortURI());

		String reqSubInboundPortURI = null;
		try {
			aasop.setSubmissionInboundPortURI(applicationAdmission);
			reqSubInboundPortURI = applicationAdmission.getRequestSubmissionPortURI();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			aasop.unpublishPort();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (applicationAdmission.getRequestSubmissionPortURI() == null) {
			this.logMessage("Application " + this.appURI + ": was rejected");
			return false;
		}

		this.logMessage("Application " + this.appURI + ": was accepted");

		try {
			this.doPortConnection(this.asop.getPortURI(), reqSubInboundPortURI,
					RequestSubmissionConnector.class.getCanonicalName());
			isRsopPortConnected = true;

		} catch (Exception e) {
			throw new ComponentStartException(e);
		}

		return true;
	}

	public void freeAdmissionControlerRessources() {
		try {
			aanop.acceptRequestTerminationNotification(applicationAdmission);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
