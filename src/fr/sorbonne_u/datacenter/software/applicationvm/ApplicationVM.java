package fr.sorbonne_u.datacenter.software.applicationvm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.TimeManagement;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.processors.Processor.ProcessorPortTypes;
import fr.sorbonne_u.datacenter.hardware.processors.connectors.ProcessorServicesConnector;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorServicesI;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorServicesNotificationConsumerI;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorServicesNotificationI;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorServicesNotificationInboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorServicesOutboundPort;
import fr.sorbonne_u.datacenter.interfaces.PushModeControllingI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.TaskI;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMDynamicStateDataInboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMIntrospectionInboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementInboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMStaticStateDataInboundPort;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionInboundPort;

/**
 * The class <code>ApplicationVM</code> implements the component representing an
 * application VM in the data center.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * The Application VM (AVM) component simulates the execution of web
 * applications by receiving requests, executing them and notifying the emitter
 * of the end of execution of its request (as a way to simulate the return of
 * the result).
 * 
 * The AVM is allocated cores on processors of a single computer and uses them
 * to execute the submitted requests. It maintain a queue for requests waiting a
 * core to become idle before beginning their execution.
 * 
 * As a component, the AVM offers a request submission service through the
 * interface <code>RequestSubmissionI</code> implemented by
 * <code>RequestSubmissionInboundPort</code> inbound port. To notify the end of
 * the execution of requests, the AVM requires the interface
 * <code>RequestNotificationI</code> through the
 * <code>RequestNotificationOutboundPort</code> outbound port.
 * 
 * The AVM can be managed (essentially allocated cores) and it offers the
 * interface <code>ApplicationVMManagementI</code> through the inbound port
 * <code>ApplicationVMManagementInboundPort</code> for this.
 * 
 * AVM uses cores on processors to execute requests. To pass the request to the
 * cores, it requires the interface <code>ProcessorServicesI</code> through
 * <code>ProcessorServicesOutboundPort</code>. It receives the notifications of
 * the end of execution of the requests by offering the interface
 * <code>ProcessorServicesNotificationI</code> through the inbound port
 * <code>ProcessorServicesNotificationInboundPort</code>.
 * 
 * <p>
 * <strong>Invariant</strong>
 * </p>
 *
 * TODO: complete!
 * 
 * <pre>
 * invariant		vmURI != null
 * invariant		applicationVMManagementInboundPortURI != null
 * invariant		requestSubmissionInboundPortURI != null
 * invariant		requestNotificationOutboundPortURI != null
 * </pre>
 * 
 * <p>
 * Created on : April 9, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * 
 * @author Alexandra Tudor
 * @author Sylia Righi
 * 
 */
public class ApplicationVM extends AbstractComponent implements ProcessorServicesNotificationConsumerI,
		RequestSubmissionHandlerI, ApplicationVMManagementI, PushModeControllingI {
	private static boolean DEBUG = false;

	public enum ApplicationVMPortTypes {
		REQUEST_SUBMISSION, MANAGEMENT, INTROSPECTION, STATIC_STATE, DYNAMIC_STATE
	}

	// ------------------------------------------------------------------------
	// Component internal state
	// ------------------------------------------------------------------------

	/** URI of this application VM. */
	private String vmURI;
	
	/** Status, idle or in use, of each core allocated to this VM. */
	private Map<AllocatedCore, Boolean> allocatedCoresIdleStatus;
	
	/** Map between processor URIs and the outbound ports to call them. */
	private Map<String, ProcessorServicesOutboundPort> processorServicesPorts;
	
	/**
	 * Map between processor URIs and the inbound ports through which task
	 * termination notifications are received from each processor.
	 */
	private Map<String, ProcessorServicesNotificationInboundPort> processorNotificationInboundPorts;
	
	/**
	 * Map between running task URIs and the processor cores running them.
	 */
	private Map<String, AllocatedCore> runningTasks;
	
	/** Queue of tasks waiting to be started. */
	private Queue<TaskI> taskQueue;
	
	/* Set of task URIs which termination will need to be notified. */
	private HashSet<String> tasksToNotify;
	
	/** Inbound port offering the management interface. */
	private ApplicationVMManagementInboundPort applicationVMManagementInboundPort;
	
	/** Inbound port offering the request submission service of the VM. */
	private RequestSubmissionInboundPort requestSubmissionInboundPort;
	
	/** Outbound port used by the VM to notify tasks' termination. */
	private RequestNotificationOutboundPort requestNotificationOutboundPort;

	private ApplicationVMIntrospectionInboundPort avmIntrospectionInboundPort;
	
	/** data inbound port through which it pushes the static state data. */
	private ApplicationVMStaticStateDataInboundPort avmStaticStateDataInboundPort;
	
	/** data inbound port through which it pushes the dynamic state data. */
	private ApplicationVMDynamicStateDataInboundPort avmdsdip;
	
	/** future of the task scheduled to push dynamic data. */
	private ScheduledFuture<?> pushingFuture;

	// ------------------------------------------------------------------------
	// Component constructor
	// ------------------------------------------------------------------------

	/**
	 * create a new application VM with the given URI, the given processor cores,
	 * and the URIs to be used to create and publish its inbound and outbound ports.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 *
	 * 
	 * <pre>
	 * pre	vmURI != null
	 * pre	applicationVMManagementInboundPortURI != null
	 * pre	requestSubmissionInboundPortURI != null
	 * pre	requestNotificationOutboundPortURI != null
	 * post	true			// no post condition.
	 * </pre>
	 *
	 * @param vmURI                                 URI of the newly created VM.
	 * @param applicationVMManagementInboundPortURI URI of the VM management inbound
	 *                                              port.
	 * @param requestSubmissionInboundPortURI       URI of the request submission
	 *                                              inbound port.
	 * @param requestNotificationInboundPortURI     URI of the request notification
	 *                                              inbound port.
	 */
	public ApplicationVM (
			String vmURI, 
			String applicationVMManagementInboundPortURI,
			String requestSubmissionInboundPortURI, 
			String requestNotificationInboundPortURI,
			String requestNotificationOutboundPortURI) throws Exception {
		
		// The normal thread pool is used to process component services, while
		// the scheduled one is used to schedule the pushes of dynamic state
		// when requested.
		
		super(vmURI, 1, 1);
		// Preconditions
		assert vmURI != null;
		assert applicationVMManagementInboundPortURI != null;
		assert requestSubmissionInboundPortURI != null;
		assert requestNotificationInboundPortURI != null;

		this.vmURI = vmURI;
		// hash map keeping track of the idle status of cores
		this.allocatedCoresIdleStatus = new HashMap<>();
		// queue of awaiting tasks
		this.taskQueue = new LinkedList<>();
		// tasks needing a end of execution notification
		this.tasksToNotify = new HashSet<>();
		// tasks currently running on the cores
		this.runningTasks = new HashMap<>();

		// Interfaces and ports
		this.addOfferedInterface(ApplicationVMManagementI.class);
		this.applicationVMManagementInboundPort = new ApplicationVMManagementInboundPort(
				applicationVMManagementInboundPortURI, this);
		this.addPort(this.applicationVMManagementInboundPort);
		this.applicationVMManagementInboundPort.publishPort();

		this.addRequiredInterface(ProcessorServicesI.class);
		this.addOfferedInterface(ProcessorServicesNotificationI.class);
		this.processorServicesPorts = new HashMap<>();
		this.processorNotificationInboundPorts = new HashMap<>();

		this.addOfferedInterface(RequestSubmissionI.class);
		this.requestSubmissionInboundPort = new RequestSubmissionInboundPort(requestSubmissionInboundPortURI, this);
		this.addPort(this.requestSubmissionInboundPort);
		this.requestSubmissionInboundPort.publishPort();

		this.addRequiredInterface(RequestNotificationI.class);
		this.requestNotificationOutboundPort = new RequestNotificationOutboundPort(requestNotificationOutboundPortURI,this);
		this.addPort(this.requestNotificationOutboundPort);
		this.requestNotificationOutboundPort.publishPort();

        this.tracer.setRelativePosition(0, 2);
	}

	// ------------------------------------------------------------------------
	// Component life-cycle
	// ------------------------------------------------------------------------

	@Override
	public void start() throws ComponentStartException {
		this.toggleTracing();
		this.toggleLogging();
		
		super.start();
	}

	@Override
	public void finalise() throws Exception {
		if (this.requestNotificationOutboundPort.connected()) this.doPortDisconnection(this.requestNotificationOutboundPort.getPortURI());
		for (ProcessorServicesOutboundPort p : this.processorServicesPorts.values()) {
			p.doDisconnection();
		}
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void shutdown() throws ComponentShutdownException {
		// Disconnect ports to the request emitter and to the processors owning
		// the allocated cores.
		try {
			if (this.requestNotificationOutboundPort.isPublished()) this.requestNotificationOutboundPort.unpublishPort();
			for (ProcessorServicesOutboundPort p : this.processorServicesPorts.values()) {
				p.unpublishPort();
			}
			if (this.requestSubmissionInboundPort.isPublished()) this.requestSubmissionInboundPort.unpublishPort();
			for (String uri : this.processorNotificationInboundPorts.keySet()) {
				this.processorNotificationInboundPorts.get(uri).unpublishPort();
			}
			if (this.applicationVMManagementInboundPort.isPublished()) this.applicationVMManagementInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException("processor services outbound port disconnection" + " error", e);
		}

		super.shutdown();
	}

	// ------------------------------------------------------------------------
	// Component services
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionHandlerI#acceptRequestSubmission(fr.sorbonne_u.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void acceptRequestSubmission(final RequestI r) throws Exception {
		this.taskQueue.add(new Task(r));
		this.startTask();
	}

	/**
	 * @see fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionHandlerI#acceptRequestSubmissionAndNotify(fr.sorbonne_u.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void acceptRequestSubmissionAndNotify(final RequestI r) throws Exception {
		if (ApplicationVM.DEBUG) {
			this.logMessage("ApplicationVM>>acceptRequestSubmissionAndNotify");
		}
		this.logMessage(this.vmURI + " queues request " + r.getRequestURI());
		Task t = new Task(r);
		this.taskQueue.add(t);
		this.tasksToNotify.add(t.taskURI);
		this.startTask();
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorServicesNotificationConsumerI#acceptNotifyEndOfTask(fr.sorbonne_u.datacenter.software.applicationvm.interfaces.TaskI)
	 */
	@Override
	public void acceptNotifyEndOfTask(TaskI t) throws Exception {
		this.endTask(t);
	}

	// ------------------------------------------------------------------------
	// Component internal services
	// ------------------------------------------------------------------------

	/**
	 * start a task if there is at least one in the queue, and an idle core on which
	 * it can be executed.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no post condition.
	 * </pre>
	 *
	 */
	private void startTask() throws Exception {
		assert !this.taskQueue.isEmpty();

		AllocatedCore ac = this.findIdleCore();
		if (ac != null) {
			this.allocatedCoresIdleStatus.remove(ac);
			this.allocatedCoresIdleStatus.put(ac, false);
			TaskI t = this.taskQueue.remove();
			this.logMessage(this.vmURI + " starts request " + t.getRequest().getRequestURI());
			this.runningTasks.put(t.getTaskURI(), ac);
			ProcessorServicesOutboundPort p = this.processorServicesPorts.get(ac.processorURI);
			ProcessorServicesNotificationInboundPort np = this.processorNotificationInboundPorts.get(ac.processorURI);
			p.executeTaskOnCoreAndNotify(t, ac.coreNo, np.getPortURI());
		}
	}

	/**
	 * update the data structures after the end of a task <code>t</code>, and if the
	 * task queue is not empty, start another one.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	t != null and this.isRunningTask(t)
	 * post	true			// no post condition.
	 * </pre>
	 *
	 * @param t ending task.
	 */
	private void endTask(TaskI t) throws Exception {
		assert t != null && this.isRunningTask(t);

		this.logMessage(this.vmURI + " terminates request " + t.getRequest().getRequestURI());
		AllocatedCore ac = this.runningTasks.remove(t.getTaskURI());
		this.allocatedCoresIdleStatus.remove(ac);
		this.allocatedCoresIdleStatus.put(ac, true);
		if (this.tasksToNotify.contains(t.getTaskURI())) {
			this.tasksToNotify.remove(t.getTaskURI());
			this.requestNotificationOutboundPort.notifyRequestTermination(t.getRequest());
		}
		if (!this.taskQueue.isEmpty()) {
			this.startTask();
		}
	}

	/**
	 * return true if the internal data structures reflects the fact that the task
	 * <code>t</code> is currently in running state.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	t != null
	 * post	true			// no post condition.
	 * </pre>
	 *
	 * @param t the task to be tested.
	 * @return true if the t is currently considered as running on a core.
	 */
	private boolean isRunningTask(TaskI t) {
		assert t != null;

		return this.runningTasks.containsKey(t.getTaskURI())
				&& !this.allocatedCoresIdleStatus.get(this.runningTasks.get(t.getTaskURI()));
	}

	/**
	 * find an idle core and return its reference or null if none is.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no post condition.
	 * </pre>
	 *
	 * @return an idle core reference or null if none is.
	 */
	private AllocatedCore findIdleCore() {
		AllocatedCore ret = null;
		for (AllocatedCore ac : this.allocatedCoresIdleStatus.keySet()) {
			if (this.allocatedCoresIdleStatus.get(ac)) {
				ret = ac;
				break;
			}
		}
		return ret;
	}

	protected void printIdleStatus() {
		System.out.println("----------");
		for (AllocatedCore ac : this.allocatedCoresIdleStatus.keySet()) {
			System.out
					.println("*** " + ac.processorURI + " " + ac.coreNo + " " + this.allocatedCoresIdleStatus.get(ac));
		}
		System.out.println("----------");
	}

	// ------------------------------------------------------------------------
	// Component introspection services
	// ------------------------------------------------------------------------

	/**
	 * return a map of the application VM port URI by their types.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return != null
	 * </pre>
	 *
	 * @return a map from application VM port types to their URI.
	 */
	public Map<ApplicationVMPortTypes, String> getAVMPortsURI() throws Exception {
		HashMap<ApplicationVMPortTypes, String> ret = new HashMap<>();
		ret.put(ApplicationVMPortTypes.REQUEST_SUBMISSION, this.requestSubmissionInboundPort.getClientPortURI());
		ret.put(ApplicationVMPortTypes.MANAGEMENT, this.applicationVMManagementInboundPort.getPortURI());
		ret.put(ApplicationVMPortTypes.INTROSPECTION, this.avmIntrospectionInboundPort.getPortURI());
		ret.put(ApplicationVMPortTypes.STATIC_STATE, this.avmStaticStateDataInboundPort.getPortURI());
		ret.put(ApplicationVMPortTypes.DYNAMIC_STATE, this.avmdsdip.getPortURI());
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.datacenter.interfaces.PushModeControllingI#startUnlimitedPushing(int)
	 */
	@Override
	public void startUnlimitedPushing(int interval) throws Exception {
		// first, send the static state if the corresponding port is connected
		this.sendStaticState();
		this.pushingFuture = this.scheduleTaskAtFixedRate(
			new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						((ApplicationVM) this.getOwner()).sendDynamicState();
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
	 * @see fr.sorbonne_u.datacenter.interfaces.PushModeControllingI#startLimitedPushing(int,
	 *      int)
	 */
	@Override
	public void startLimitedPushing(final int interval, final int n) throws Exception {
		assert n > 0;

		this.logMessage(this.vmURI + " startLimitedPushing with interval " + interval + " ms for " + n + " times.");

		// first, send the static state if the corresponding port is connected
		this.sendStaticState();

		this.pushingFuture = this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					((ApplicationVM) this.getOwner()).sendDynamicState(interval, n);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, TimeManagement.acceleratedDelay(interval), TimeUnit.MILLISECONDS);
	}

	/**
	 * @see fr.sorbonne_u.datacenter.interfaces.PushModeControllingI#stopPushing()
	 */
	@Override
	public void stopPushing() {
		if (this.pushingFuture != null && !(this.pushingFuture.isCancelled() || this.pushingFuture.isDone())) {
			this.pushingFuture.cancel(false);
		}
	}

	private void sendStaticState() throws Exception {
		if (this.avmStaticStateDataInboundPort.connected()) {
			this.avmStaticStateDataInboundPort.send(this.getStaticState());
		}
	}

	private void sendDynamicState() throws Exception {
		if (this.avmdsdip.connected()) {
			this.avmdsdip.send(this.getDynamicState());
		}
	}

	private void sendDynamicState(final int interval, final int n) throws Exception {
		this.sendDynamicState();
		final int fNumberOfRemainingPushes = n - 1;
		if (fNumberOfRemainingPushes > 0) {
			this.pushingFuture = this.scheduleTask(new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						((ApplicationVM) this.getOwner()).sendDynamicState(interval, fNumberOfRemainingPushes);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}, TimeManagement.acceleratedDelay(interval), TimeUnit.MILLISECONDS);

		}
	}

	public ApplicationVMStaticStateI getStaticState() {
		return null;
	}

	public ApplicationVMDynamicStateI getDynamicState() {
		return null;
	}

	// ------------------------------------------------------------------------
	// Component management services
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI#allocateCores(fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore[])
	 */
	@Override
	public void allocateCores(AllocatedCore[] allocatedCores) throws Exception {
		assert allocatedCores != null && allocatedCores.length != 0;

		for (AllocatedCore allocatedCore : allocatedCores) {
			this.allocatedCoresIdleStatus.put(allocatedCore, true);
		}

		// Link the VM with the newly allocated cores' processors if they are
		// not yet.
		for (AllocatedCore allocatedCore : allocatedCores) {
			if (!this.processorServicesPorts.containsKey(allocatedCore.processorURI)) {
				ProcessorServicesOutboundPort p = new ProcessorServicesOutboundPort(this);
				this.addPort(p);
				p.publishPort();
				p.doConnection(allocatedCore.processorInboundPortURI.get(ProcessorPortTypes.SERVICES),
						ProcessorServicesConnector.class.getCanonicalName());
				this.processorServicesPorts.put(allocatedCore.processorURI, p);

				ProcessorServicesNotificationInboundPort np = new ProcessorServicesNotificationInboundPort(this);
				this.addPort(np);
				np.publishPort();
				this.processorNotificationInboundPorts.put(allocatedCore.processorURI, np);
			}
		}
		this.logMessage("---> Received " + allocatedCores.length + " new cores");
	}

	@Override
	public void destroyComponent() throws Exception {
		finalise();
		shutdown();
	}
}
