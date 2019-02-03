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
 * least used AVM (min number of requests in queue)
 *
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

	private String appURIRemoval;
	private String performanceControllerURIRemoval;
	private String markedForRemoval;

	// Statistics
	private double currentAverage;
	private ExponentialMovingAverage exponentialMovingAverage;
	private int totalRequestSubmitted;
	private int totalRequestTerminated;

	// Data Pushing
	private ScheduledFuture<?> pushingFuture;

	/**
	 *
	 * @param rdURI request dispatcher URI
	 * @param vmURIList list of avm URIs available for the request dispatcher
	 * @param requestDispatcherManagementInboundPortURI request dispatcher management inbound port URI
	 * @param requestDispatcherServicesInboundPortURI request dispatcher services inbound port URI
	 * @param requestDispatcherSubmissionInboundPortURI request dispatcher submission inbound port URI
	 * @param requestDispatcherSubmissionOutboundPortURIList list of request dispatcher submission outbound port URIs
	 * @param requestDispatcherNotificationInboundPortURIList list of request dispatcher notification inbound port URIs
	 * @param requestDispatcherNotificationOutboundPortURI request dispatcher notification outbound port URI
	 * @param requestDispatcherDynamicStateDataInboundPortURI request dispatcher dynamic state data inbound port URI
	 */
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

		this.markedForRemoval = null;

		this.tracer.setRelativePosition(1, 0);
		
		// post-conditions check
		assert this.rsopList != null && this.rsopList.size() > 0;
		assert this.rnipList != null && this.rnipList.size() > 0;
		assert this.rddsdip !=null;
	}

	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * Connect service in/out ports
	 * @throws ComponentStartException error connecting ports
	 */
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

	/**
	 * Disconnect service in/out ports, notification in/out ports and dynamic state in/out ports
	 * @throws Exception
	 */
	@Override
	public void finalise() throws Exception {
		for (String vmURI : this.vmURIList) {
			if (this.rsopList.get(vmURI).connected()) this.doPortDisconnection(this.rsopList.get(vmURI).getPortURI());
		}
		if (this.rnop.connected()) this.doPortDisconnection(this.rnop.getPortURI());
		if (this.rddsdip.connected()) this.doPortDisconnection(this.rddsdip.getPortURI());

		super.finalise();
	}

	/**
	 * Unpublish management, services, submission, notification and dynamic state ports
	 * @throws ComponentShutdownException error unpublishing ports
	 */
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
	 * Accept a request submission from request generator and send it to
	 * least recently used AVM
	 *
	 * @param r request that just terminated.
	 */
	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		acceptRequestSubmissionAndNotify(r);
	}

	/**
	 * Accept a request submission from request generator, send it to the
	 * least recently used AVM and and require notifications of request execution progress.
	 *
	 * @param r request that just terminated.
	 */
	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		assert r != null;

		HashMap<String, Integer> vmPriorityCopy = new HashMap<>(this.vmPriority);
		vmPriorityCopy.remove(this.markedForRemoval);
		String leastUsedVM = Collections.min(vmPriorityCopy.entrySet(), comparingInt(Map.Entry::getValue)).getKey();

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
	 * Notify request generator that a request was terminated
	 *
	 * @param r request that just terminated.
	 */
	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		assert r != null;

		Long executionTime = System.nanoTime() - this.vmStartTime.get(r.getRequestURI());

		synchronized (this) {
			currentAverage = exponentialMovingAverage.getNextAverage(executionTime);
		}
		String vmURI = this.vmAllocation.get(r.getRequestURI());

		if (this.vmPriority.containsKey(vmURI)) {
			int requests = this.vmPriority.get(vmURI) - 1;
			this.vmPriority.replace(vmURI, requests);
		}

		if (this.markedForRemoval != null && this.vmPriority.get(this.markedForRemoval) <= 0) {
			// this avm was marked for removal and completed all its requests
			this.rdsvop.notifyAVMRemovalComplete (this.markedForRemoval, this.appURIRemoval, this.performanceControllerURIRemoval);
			this.vmURIList.remove(this.markedForRemoval);
			this.vmPriority.remove(this.markedForRemoval);
			this.markedForRemoval = null;
		}

		this.vmStartTime.remove(r.getRequestURI());
		this.vmAllocation.remove(r.getRequestURI());

		this.totalRequestTerminated++;
		this.logMessage ("Request dispatcher " + this.rdURI + " notified that request "
				+ r.getRequestURI() + " has terminated");
	}


	// Component dynamic state services
	// -------------------------------------------------------------------------

	/**
	 * Request dispatcher dynamic data contains request dispatcher URI, current exponential moving average
	 * total requests submitted, total requests terminated, and number of AVMs available
	 * @return dynamic data
	 */
	public RequestDispatcherDynamicStateI getDynamicState() {
		return new RequestDispatcherDynamicState(
				this.rdURI,
				this.currentAverage,
				this.vmURIList.size(),
				this.totalRequestSubmitted,
				this.totalRequestTerminated);
	}

	/**
	 * Send dynamic state via dynamic state inbound port.
	 */
	private void sendDynamicState() throws Exception {
		if (this.rddsdip.connected()) {
			this.rddsdip.send(this.getDynamicState());
		}
	}

	/**
	 * Send dynamic state information
	 */
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

	/**
	 * Start unlimited pushing of dynamic state data
	 * @param interval delay between pushes (in milliseconds).
	 */
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

	/**
	 * Start limited pushing of dynamic state data
	 * @param interval delay between pushes (in milliseconds).
	 * @param n        total number of pushes to be done, unless stopped.
	 */
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

	/**
	 * Stop pushing dynamic state data
	 */
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

	/**
	 * Receive notification that resources have been allocated for a new AVM.
	 *
	 * Create ports for the new AVM and notify admission controller that ports are ready.
	 *
	 * @param appURI application URI
	 * @param performanceControllerURI performance controller URI
	 * @param allocatedMap allocation map for new AVM
	 * @param avmURI new AVM URI
	 * @param requestDispatcherSubmissionOutboundPortURI request dispatcher submission outbound port URI to use for the new AVM
	 * @param requestDispatcherNotificationInboundPortURI request dispatcher notification inbound port URI to use for the new AVM
	 */
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

	/**
	 * Receive notification that a new AVM was deployed
	 * @param avmURI new AVM URI
	 */
	@Override
	public void notifyDispatcherNewAVMDeployed(String avmURI) {
		this.vmURIList.add(avmURI);
		this.vmPriority.put(avmURI, 0);
	}

	/**
	 * Receive notification that an AVM should be removed.
	 *
	 * The request dispatcher decides which AVM to remove, by choosing the least used one
	 * (min request queue size)
	 *
	 * Mark the AVM for removal
	 * @param appURI application URI
	 */
	@Override
	public void notifyDispatcherToRemoveAVM(String appURI, String performanceControllerURI) throws Exception {
		if (this.markedForRemoval == null) {
			this.markedForRemoval = Collections.min(this.vmPriority.entrySet(), comparingInt(Map.Entry::getValue)).getKey();
			this.appURIRemoval = appURI;
			this.performanceControllerURIRemoval = performanceControllerURI;

			this.logMessage("---> AVM " + markedForRemoval + " will be removed.");
		} else {
			this.logMessage("---> AVM removal already in progress " + this.markedForRemoval);
			// refuse new removal:
			this.rdsvop.notifyAVMRemovalRefused (appURI, performanceControllerURI);
		}

	}
}
