package fr.sorbonne_u.sylalexcenter.requestdispatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.datacenter.TimeManagement;
import fr.sorbonne_u.datacenter.interfaces.ControlledDataOfferedI;
import fr.sorbonne_u.datacenter.interfaces.ControlledDataRequiredI;
import fr.sorbonne_u.datacenter.interfaces.PushModeControllingI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.utils.AllocationMap;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.connectors.RequestDispatcherServicesConnector;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherServicesI;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.ports.RequestDispatcherDynamicStateDataInboundPort;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.ports.RequestDispatcherManagementInboundPort;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.ports.RequestDispatcherServicesOutboundPort;
import fr.sorbonne_u.sylalexcenter.utils.ExponentialMovingAverage;

import static java.util.Comparator.comparingInt;

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
 * When the request dispatcher receives a request, it goes through the list of
 * available application virtual machines (AVMs) and submits the request to the 
 * least recently used AVM.
 *
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class RequestDispatcher extends AbstractComponent implements RequestDispatcherManagementI,
		RequestSubmissionHandlerI, RequestNotificationHandlerI, PushModeControllingI {

	private String rdURI;
	
	// RequestDispatcher Ports
	// -------------------------------------------------------------------------
	private RequestDispatcherManagementInboundPort rdmip;
	private RequestDispatcherServicesOutboundPort rdsvop;
	private String requestDispatcherServicesInboundPortURI;

	private RequestSubmissionInboundPort rsip;
	private HashMap<String, RequestSubmissionOutboundPort> rsopList = new HashMap<>();
	private HashMap<String, RequestNotificationInboundPort> rnipList = new HashMap<>();
	private RequestNotificationOutboundPort rnop;

	private RequestDispatcherDynamicStateDataInboundPort rddsdip;
	
	// List of available avm
	private ArrayList<String> vmURIList;

	private HashMap<String, Integer> vmPriority = new HashMap<>(); //vmURI -> number of requests in queue
	private HashMap<String, String> vmAllocation = new HashMap<>(); //request URI -> vmURI
	private HashMap<String, Long> vmStartTime = new HashMap<>(); //request URI -> request start time

	// Statistics
	private double currentAverage;
	private ExponentialMovingAverage exponentialMovingAverage;
	private int totalRequestSubmitted;
	private int totalRequestTerminated;

	// Data Pushing
	private ScheduledFuture<?> pushingFuture;

	// Constructor
	// -------------------------------------------------------------------------
	public RequestDispatcher (
			String rdURI,
			ArrayList<String> vmURIList,
			String requestDispatcherManagementInboundPortURI,
			String requestDispatcherServicesInboundPortURI,
			String requestDispatcherSubmissionInboundPortURI,
			ArrayList<String> requestDispatcherSubmissionOutboundPortURIList,
			ArrayList<String> requestDispatcherNotificationInboundPortURIList,
			String requestDispatcherNotificationOutboundPortURI,
			String requestDispatcherDynamicStateDataInboundPortURI
		) throws Exception {
		
		super(rdURI,1, 1);
		
		// preconditions check
		assert rdURI != null;
		assert requestDispatcherManagementInboundPortURI != null;
		assert requestDispatcherSubmissionInboundPortURI != null;
		assert vmURIList != null && vmURIList.size() > 0;
		assert requestDispatcherSubmissionOutboundPortURIList != null && requestDispatcherSubmissionOutboundPortURIList.size() > 0;
		assert requestDispatcherNotificationInboundPortURIList != null && requestDispatcherNotificationInboundPortURIList.size() > 0;
		assert requestDispatcherNotificationOutboundPortURI != null;
		assert requestDispatcherDynamicStateDataInboundPortURI != null;

		// initialization
		this.rdURI = rdURI;
		this.vmURIList = new ArrayList<>(vmURIList);
		this.totalRequestSubmitted = 0;
		this.totalRequestTerminated = 0;

		this.exponentialMovingAverage = new ExponentialMovingAverage();

		this.rdmip = new RequestDispatcherManagementInboundPort(requestDispatcherManagementInboundPortURI, this);
		this.addPort(rdmip);
		this.rdmip.publishPort();

		this.requestDispatcherServicesInboundPortURI = requestDispatcherServicesInboundPortURI;
		this.addRequiredInterface(RequestDispatcherServicesI.class);
		this.rdsvop = new RequestDispatcherServicesOutboundPort(this);
		this.addPort(rdsvop);
		this.rdsvop.publishPort();
		
		this.addOfferedInterface(RequestSubmissionI.class);
		this.rsip = new RequestSubmissionInboundPort(requestDispatcherSubmissionInboundPortURI, this);
		this.addPort(this.rsip);
		this.rsip.publishPort();
		
		this.addRequiredInterface(RequestNotificationI.class);
		this.rnop = new RequestNotificationOutboundPort(requestDispatcherNotificationOutboundPortURI, this);
		this.addPort(this.rnop);
		this.rnop.publishPort();

		this.addRequiredInterface(DataRequiredI.PullI.class);
		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class);

		for (int i = 0; i < vmURIList.size(); i++ ) {
			this.vmPriority.put(vmURIList.get(i), 0);

			this.addOfferedInterface(RequestNotificationI.class);
			RequestNotificationInboundPort rnip = new RequestNotificationInboundPort(requestDispatcherNotificationInboundPortURIList.get(i), this);
			this.rnipList.put(vmURIList.get(i), rnip);
			this.addPort(rnip);
			this.rnipList.get(vmURIList.get(i)).publishPort();
			
			this.addRequiredInterface(RequestSubmissionI.class);
			RequestSubmissionOutboundPort rsop = new RequestSubmissionOutboundPort(requestDispatcherSubmissionOutboundPortURIList.get(i), this);
			this.rsopList.put(vmURIList.get(i), rsop);
			this.addPort(rsop);
			this.rsopList.get(vmURIList.get(i)).publishPort();
		}

		this.addOfferedInterface(ControlledDataOfferedI.ControlledPullI.class);
		this.rddsdip = new RequestDispatcherDynamicStateDataInboundPort(requestDispatcherDynamicStateDataInboundPortURI, this);
		this.addPort(rddsdip);
		this.rddsdip.publishPort();

		this.tracer.setRelativePosition(1, 0);
		
		// post-conditions check
		assert this.rsopList != null && this.rsopList.size() > 0;
		assert this.rnipList != null && this.rnipList.size() > 0;
		assert this.rddsdip !=null;
	}

	// Component life-cycle
	// -------------------------------------------------------------------------
	@Override
	public void start() throws ComponentStartException {
		this.toggleTracing();
		this.toggleLogging();

		try {
			this.doPortConnection(this.rdsvop.getPortURI(),
					this.requestDispatcherServicesInboundPortURI,
					RequestDispatcherServicesConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException ("Error connecting request dispatcher service ports " + e);
		}
		super.start();
	}

	@Override
	public void finalise() throws Exception {
		for (String vmURI : this.vmURIList) {
			if (this.rsopList.get(vmURI).connected()) this.doPortDisconnection(this.rsopList.get(vmURI).getPortURI());
		}
		if (this.rnop.connected()) this.doPortDisconnection(this.rnop.getPortURI());
		if (this.rddsdip.connected()) this.doPortDisconnection(this.rddsdip.getPortURI());

		super.finalise();
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {

		try {
			if (this.rdmip.isPublished()) this.rdmip.unpublishPort();
			if (this.rsip.isPublished()) this.rsip.unpublishPort();
			for (String vmURI : this.vmURIList) {
				if (this.rsopList.get(vmURI).isPublished()) this.rsopList.get(vmURI).unpublishPort();
				if (this.rnipList.get(vmURI).isPublished()) this.rnipList.get(vmURI).unpublishPort();
			}
			if (this.rnop.isPublished()) this.rnop.unpublishPort();
			if (this.rddsdip.isPublished()) this.rddsdip.unpublishPort();
			
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
	 */
	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		acceptRequestSubmissionAndNotify(r);
	}

	/**
	 * accept a request submission from request generator, send it to the
	 * least recently used AVM and and require notifications of request execution progress.
	 *
	 * @param r request that just terminated.
	 */
	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		assert r != null;

		String leastUsedVM = Collections.min(this.vmPriority.entrySet(), comparingInt(Map.Entry::getValue)).getKey();

		if (leastUsedVM.length() > 0) {
			int requests = this.vmPriority.get(leastUsedVM) + 1;
			this.vmPriority.replace(leastUsedVM, requests);
			this.vmAllocation.put(r.getRequestURI(), leastUsedVM);
			this.vmStartTime.put(r.getRequestURI(), System.nanoTime());

			this.logMessage ("Request dispatcher " + this.rdURI + " accepted request " + r.getRequestURI());
			this.totalRequestSubmitted++;
			this.rsopList.get(leastUsedVM).submitRequestAndNotify(r);
			
		} else {
			this.logMessage ("Request dispatcher " + this.rdURI + " refused request " + r.getRequestURI());
		}
	}

	/**
	 * notify request generator that a request was terminated
	 *
	 * @param r request that just terminated.
	 */
	@Override
	public void acceptRequestTerminationNotification(RequestI r) {
		assert r != null;

		Long executionTime = System.nanoTime() - this.vmStartTime.get(r.getRequestURI());

		synchronized (this) {
			currentAverage = exponentialMovingAverage.getNextAverage(executionTime);
		}
		int requests = this.vmPriority.get(this.vmAllocation.get(r.getRequestURI())) - 1;
		this.vmPriority.replace(this.vmAllocation.get(r.getRequestURI()), requests);

		this.vmStartTime.remove(r.getRequestURI());
		this.vmAllocation.remove(r.getRequestURI());

		this.totalRequestTerminated++;
		this.logMessage ("Request dispatcher " + this.rdURI + " notified that request "
				+ r.getRequestURI() + " has terminated");
	}


	// Component dynamic state services
	// -------------------------------------------------------------------------

	public RequestDispatcherDynamicStateI getDynamicState() {
		return new RequestDispatcherDynamicState(
				this.rdURI,
				this.currentAverage,
				this.vmURIList.size(),
				this.totalRequestSubmitted,
				this.totalRequestTerminated);
	}

	private void sendDynamicState() throws Exception {
		if (this.rddsdip.connected()) {
			this.rddsdip.send(this.getDynamicState());
		}
	}

	private void sendDynamicState(final int interval, int numberOfRemainingPushes) throws Exception {
		this.sendDynamicState();
		final int fNumberOfRemainingPushes = numberOfRemainingPushes - 1;
		if (fNumberOfRemainingPushes > 0) {
			final RequestDispatcher rd = this;
			this.pushingFuture = this.scheduleTask(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							rd.sendDynamicState(interval, fNumberOfRemainingPushes);
						} catch (Exception e) {
							throw new RuntimeException("Error sending dynamic state " + e);
						}
					}
				},
				TimeManagement.acceleratedDelay(interval),
				TimeUnit.MILLISECONDS);
		}
	}

	@Override
	public void startUnlimitedPushing(int interval) throws RuntimeException {
		final RequestDispatcher rd = this;

		this.pushingFuture = this.scheduleTaskAtFixedRate(
			new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						rd.sendDynamicState();
					} catch (Exception e) {
						throw new RuntimeException("Error unlimited pushing dynamic state " + e);
					}
				}
			},
			TimeManagement.acceleratedDelay(interval),
			TimeManagement.acceleratedDelay(interval),
			TimeUnit.MILLISECONDS);
	}

	@Override
	public void startLimitedPushing(int interval, int n) throws RuntimeException {
		assert n > 0;
		final RequestDispatcher rd = this;

		this.logMessage(this.rdURI + " startLimitedPushing with interval " + interval + " ms for " + n + " times.");

		this.pushingFuture = this.scheduleTask(
			new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						rd.sendDynamicState(interval, n);
					} catch (Exception e) {
						throw new RuntimeException("Error limited pushing dynamic state " + e);
					}
				}
			},
			TimeManagement.acceleratedDelay(interval),
			TimeUnit.MILLISECONDS);
	}

	@Override
	public void stopPushing() throws Exception {
		try {
			if (this.pushingFuture != null && !(this.pushingFuture.isCancelled() || this.pushingFuture.isDone())) {
				this.pushingFuture.cancel(false);
			}
		} catch (Exception e) {
			throw new Exception("Error stop pushing " + e);
		}
	}

	@Override
	public void notifyDispatcherOfNewAVM (
			String appURI,
			String performanceControllerURI,
			ArrayList<AllocationMap> allocatedMap,
			String avmURI,
			String requestDispatcherSubmissionOutboundPortURI,
			String requestDispatcherNotificationInboundPortURI) throws Exception {

		this.addOfferedInterface(RequestNotificationI.class);
		RequestNotificationInboundPort rnip = new RequestNotificationInboundPort(requestDispatcherNotificationInboundPortURI, this);
		this.rnipList.put(avmURI, rnip);
		this.addPort(rnip);
		this.rnipList.get(avmURI).publishPort();

		this.addRequiredInterface(RequestSubmissionI.class);
		RequestSubmissionOutboundPort rsop = new RequestSubmissionOutboundPort(requestDispatcherSubmissionOutboundPortURI, this);
		this.rsopList.put(avmURI, rsop);
		this.addPort(rsop);
		this.rsopList.get(avmURI).publishPort();

		// Notify Admission Controller that new ports are ready!
		this.rdsvop.notifyNewAVMPortsReady(appURI,
				performanceControllerURI,
				allocatedMap,
				avmURI,
				requestDispatcherSubmissionOutboundPortURI,
				requestDispatcherNotificationInboundPortURI);

		this.logMessage("---> Created ports for new AVM");
	}

	@Override
	public void notifyDispatcherNewAVMDeployed(String avmURI) throws Exception {
		this.vmURIList.add(avmURI);
		this.vmPriority.put(avmURI, 0);
	}
}
