package fr.sorbonne_u.sylalexcenter.requestdispatcher;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.software.connectors.RequestNotificationConnector;
import fr.sorbonne_u.datacenter.software.connectors.RequestSubmissionConnector;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.ports.RequestDispatcherManagementInboundPort;

/**
 * The class <code>RequestDispatcher</code> implements a request dispatcher.
 * 
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * The request dispatcher component will receive requests from the request
 * generator and forward them to an application's dedicated virtual machines.
 * 
 * TODO:
 * When the request dispatcher receives a request, it goes through the list of
 * available application virtual machines (AVMs) and submits the request to the 
 * least recently used AVM.
 *
 * @author lexa
 *
 */
public class RequestDispatcher extends AbstractComponent implements RequestDispatcherManagementI, RequestSubmissionHandlerI, RequestNotificationHandlerI {

	public static int DEBUG_LEVEL = 2;
	
	protected String rdURI;
	
	// RequestGenerator Ports
	// -------------------------------------------------------------------------
	protected RequestDispatcherManagementInboundPort rdmip;

	protected String requestSubmissionInboundAVMPortURI;
	protected String requestNotificationInboundGeneratorPortURI;
	
	protected RequestSubmissionInboundPort rsip;
	protected RequestSubmissionOutboundPort rsop;
	protected RequestNotificationInboundPort rnip;
	protected RequestNotificationOutboundPort rnop;	
	
	// Constructor
	// -------------------------------------------------------------------------
	public RequestDispatcher (
			String rdURI, 
			String requestDispatcherManagementInboundPortURI,
			String requestSubmissionInboundGeneratorPortURI, // connected to Request Generator
			String requestNotificationInboundGeneratorPortURI, // connected to Request Generator
			String requestSubmissionInboundAVMPortURI, // connected to AVM
			String requestNotificationInboundAVMPortURI // connected to AVM
		) throws Exception {
		
		super(1, 1);
		
		// preconditions check
		assert requestDispatcherManagementInboundPortURI != null;
		assert requestSubmissionInboundGeneratorPortURI != null;
		assert requestNotificationInboundGeneratorPortURI != null;
		assert requestSubmissionInboundAVMPortURI != null;
		assert requestNotificationInboundAVMPortURI != null;

		// initialization
		this.rdURI = rdURI;	
		this.requestNotificationInboundGeneratorPortURI = requestNotificationInboundGeneratorPortURI;
		this.requestSubmissionInboundAVMPortURI = requestSubmissionInboundAVMPortURI;
		
		this.rdmip = new RequestDispatcherManagementInboundPort(requestDispatcherManagementInboundPortURI, this);
		this.addPort(rdmip);
		this.rdmip.publishPort();
		
		this.addOfferedInterface(RequestSubmissionI.class);
		this.rsip = new RequestSubmissionInboundPort(requestSubmissionInboundGeneratorPortURI, this);
		this.addPort(this.rsip);
		this.rsip.publishPort();
		
		this.addRequiredInterface(RequestNotificationI.class);
		this.rnop = new RequestNotificationOutboundPort(this);
		this.addPort(this.rnop);
		this.rnop.publishPort();

		this.addOfferedInterface(RequestNotificationI.class);
		this.rnip = new RequestNotificationInboundPort(requestNotificationInboundAVMPortURI, this);
		this.addPort(this.rnip);
		this.rnip.publishPort();		
		
		this.addRequiredInterface(RequestSubmissionI.class);
		this.rsop = new RequestSubmissionOutboundPort(this);
		this.addPort(this.rsop);
		this.rsop.publishPort();

		// post-conditions check
		assert this.rsip != null && this.rsip instanceof RequestSubmissionI;
		assert this.rsop != null && this.rsop instanceof RequestSubmissionI;
		assert this.rnip != null && this.rnip instanceof RequestNotificationI;
		assert this.rnop != null && this.rnop instanceof RequestNotificationI;
		
	}

	// Component life-cycle
	// -------------------------------------------------------------------------
	@Override
	public void start() throws ComponentStartException {
		super.start();
		try {
			this.doPortConnection(this.rsop.getPortURI(), requestSubmissionInboundAVMPortURI,
					RequestSubmissionConnector.class.getCanonicalName());
			this.doPortConnection(this.rnop.getPortURI(), requestNotificationInboundGeneratorPortURI,
					RequestNotificationConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}

	@Override
	public void finalise() throws Exception {
		
		if (this.rsop.connected()) this.doPortDisconnection(this.rsop.getPortURI());
		if (this.rnop.connected()) this.doPortDisconnection(this.rnop.getPortURI());

		super.finalise();
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {

		try {
			if (rdmip.isPublished()) this.rdmip.unpublishPort();
			if (rsip.isPublished()) this.rsip.unpublishPort();
			if (rsop.isPublished()) this.rsop.unpublishPort();
			if (rnip.isPublished()) this.rnip.unpublishPort();
			if (rnop.isPublished()) this.rnop.unpublishPort();
			
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}

		super.shutdown();
	}	
	
	// Component internal services
	// -------------------------------------------------------------------------	

	/**
	 * accept a request submission from request generator and send it to
	 * least recently used AVM
	 *
	 * @param r request that just terminated.
	 * @throws Exception <i>todo.</i>
	 */
	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		assert r != null;
		
		this.rsop.submitRequest(r);
		
		if (RequestDispatcher.DEBUG_LEVEL == 2) {
			this.logMessage ("Request dispatcher " + this.rdURI + " accepted submission request " + r.getRequestURI());
		}
	}

	/**
	 * accept a request submission from request generator, send it to the
	 * least recently used AVM and and require notifications of request execution progress.
	 *
	 * @param r request that just terminated.
	 * @throws Exception <i>todo.</i>
	 */
	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		assert r != null;
		this.rsop.submitRequestAndNotify(r);
		
		if (RequestDispatcher.DEBUG_LEVEL == 2) {
			this.logMessage ("Request dispatcher " + this.rdURI + " accepted submission request " + r.getRequestURI() +
					"and required notification of request execution progress");
		}		
	}

	/**
	 * notify request generator that a request was terminated
	 *
	 * @param r request that just terminated.
	 * @throws Exception <i>todo.</i>
	 */
	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		assert r != null;
		
		this.rnop.notifyRequestTermination(r);
		
		if (RequestDispatcher.DEBUG_LEVEL == 2) {
			this.logMessage ("Request dispatcher " + this.rdURI + " notified request generator that request " + 
					r.getRequestURI() + " has terminated");
		}
	}	

}
