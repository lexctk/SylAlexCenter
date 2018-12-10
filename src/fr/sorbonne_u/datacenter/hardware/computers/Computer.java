package fr.sorbonne_u.datacenter.hardware.computers;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.connectors.DataConnector;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.datacenter.TimeManagement;
import fr.sorbonne_u.datacenter.connectors.ControlledDataConnector;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerStaticStateDataI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerDynamicStateDataInboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesInboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerStaticStateDataInboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.Processor;
import fr.sorbonne_u.datacenter.hardware.processors.Processor.ProcessorPortTypes;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorStateDataConsumerI;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorStaticStateI;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorDynamicStateDataOutboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorStaticStateDataOutboundPort;
import fr.sorbonne_u.datacenter.interfaces.ControlledDataOfferedI;
import fr.sorbonne_u.datacenter.interfaces.ControlledDataRequiredI;
import fr.sorbonne_u.datacenter.interfaces.PushModeControllingI;

/**
 * The class <code>Computer</code> implements a component that represents a
 * computer in a data center.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * Computer components use static and dynamic data structures. The static data
 * structure represents the hardware itself modeled as components and objects:
 * processor components, their URI, their ports URI, ... Instead of creating
 * processors aside and then providing the components to the computer, the
 * computer component is in charge of creating its processors. Hence, several
 * parameters passed to the constructor are in fact used for the creation of the
 * processors. The dynamic data structure includes essentially the state of
 * reservation of the cores to be able to allocate them on request.
 * </p>
 * <p>
 * The computer component offers its baseline services through the interface
 * <code>ComputerServicesI</code>. It allows to obtain information about its
 * static state by offering the interface <code>ComputerStaticStateDataI</code>,
 * which is a simple sub interface of the standard component interface
 * <code>DataOfferedI</code>, and thus offers its pull interface and requires
 * its push one. Similarly, it allows to obtain information about its dynamic
 * state by offering the interface <code>ComputerDynamicStateDataI</code>, which
 * is also a subinterface of the standard component interface
 * <code>DataOfferedI</code>, thus also offering its pull interface and
 * requiring its push one, but adds a few methods to start and stop the pushing
 * of dynamic data towards a monitoring component.
 * </p>
 * 
 * <p>
 * <strong>Invariant</strong>
 * </p>
 * 
 * TODO: complete!
 * 
 * <pre>
 * invariant		computerURI != null
 * invariant		numberOfProcessors &gt; 0
 * invariant		numberOfCores &gt; 0
 * invariant		processors != null and processors.length == numberOfProcessors
 * invariant		processorStaticDataOutboundPorts != null and
 *				    processorStaticDataOutboundPorts.length == numberOfProcessors
 * invariant		processorDynamicDataOutboundPorts != null and
 *				    processorDynamicDataOutboundPorts.length == numberOfProcessors
 * invariant		processorsURI != null and
 *				    processorsURI.size() == numberOfProcessors
 * invariant		computerServicesInboundPortURI != null
 * invariant		computerStaticStateDataInboundPortURI != null
 * invariant		computerDynamicStateDataInboundPortURI != null
 * </pre>
 * 
 * <p>
 * Created on : January 15, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class Computer extends AbstractComponent implements ProcessorStateDataConsumerI, PushModeControllingI {
	/** The three types of interfaces offered by Computer. */
	public static enum ComputerPortTypes {
		SERVICES, // basic services: allocating and releasing cores
		STATIC_STATE, // notification (data interface) for the static state
		DYNAMIC_STATE // notification (data interface) for the dynamic state
	}

	// ------------------------------------------------------------------------
	// Component public inner classes
	// ------------------------------------------------------------------------

	/**
	 * The class <code>AllocatedCore</code> implements object collecting information
	 * about cores allocated to an Application VM.
	 *
	 * <p>
	 * <strong>Description</strong>
	 * </p>
	 * 
	 * <p>
	 * <strong>Invariant</strong>
	 * </p>
	 * 
	 * <pre>
	 * invariant		processorURI != null
	 * invariant		processorNo &gt;= 0 and coreNo &gt;= 0
	 * invariant		processorInboundPortURI != null
	 * invariant		for all uri in processorInboundPortURI.values() {
	 * 				    uri != null
	 * 				    }
	 * </pre>
	 * 
	 * <p>
	 * Created on : August 26, 2015
	 * </p>
	 * 
	 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static class AllocatedCore implements Serializable {
		private static final long serialVersionUID = 1L;
		/** the number of the owning processor within its computer. */
		final int processorNo;
		/** the URI of the owning processor within its computer. */
		public final String processorURI;
		/** the number of the core within its owning processor. */
		public final int coreNo;
		/**
		 * a map from processor port types and their URI for the owning processor.
		 */
		public final Map<Processor.ProcessorPortTypes, String> processorInboundPortURI;

		/**
		 * Creating a structure representing an allocated core.
		 * 
		 * <p>
		 * <strong>Contract</strong>
		 * </p>
		 * 
		 * <pre>
		 * pre	processorURI != null
		 * pre	processorNo &gt;= 0 and coreNo &gt;= 0
		 * pre	processorInboundPortURI != null
		 * pre	for all uri in processorInboundPortURI.values() {
		 * 		    uri != null
		 * 		}
		 * post	true			// no post condition.
		 * </pre>
		 *
		 * @param processorNo             processor number within the computer.
		 * @param processorURI            URI of the processor.
		 * @param coreNo                  core number within the processor.
		 * @param processorInboundPortURI map giving the URIs of the inbound ports of
		 *                                the processors.
		 */
		public AllocatedCore(int processorNo, String processorURI, int coreNo,
				Map<ProcessorPortTypes, String> processorInboundPortURI) {
			super();

			assert processorURI != null;
			assert processorNo >= 0 && coreNo >= 0;
			assert processorInboundPortURI != null;
			boolean allNonNull = true;
			for (String uri : processorInboundPortURI.values()) {
				allNonNull = allNonNull && uri != null;
			}
			assert allNonNull;

			this.processorNo = processorNo;
			this.processorURI = processorURI;
			this.coreNo = coreNo;
			this.processorInboundPortURI = processorInboundPortURI;
		}
	}

	// ------------------------------------------------------------------------
	// Component internal state
	// ------------------------------------------------------------------------

	/** URI of the computer component. */
	protected final String computerURI;
	/** the number of processor owned by the computer. */
	private final int numberOfProcessors;
	/** references to the owned processor components for internal usage. */
	protected final Processor[] processors;
	/**
	 * ports of the computer receiving the static data from its processor
	 * components.
	 */
	private final ProcessorStaticStateDataOutboundPort[] processorStaticDataOutboundPorts;
	/**
	 * ports of the computer receiving the dynamic data from its processor
	 * components.
	 */
	private final ProcessorDynamicStateDataOutboundPort[] processorDynamicDataOutboundPorts;
	/** number of cores of each processor (processor are core homogeneous). */
	private final int numberOfCores;
	/** a map from processor numbers to processors URI. */
	private final Map<Integer, String> processorsURI;
	/** a map from processor URI to their different inbound ports URI. */
	private final Map<String, Map<Processor.ProcessorPortTypes, String>> processorsInboundPortURI;
	/** array collecting the reservation status of the cores. */
	private boolean[][] reservedCores;
	/** computer inbound port through which management methods are called. */
	private ComputerServicesInboundPort computerServicesInboundPort;
	/** computer data inbound port through which it pushes its static data. */
	private ComputerStaticStateDataInboundPort computerStaticStateDataInboundPort;
	/** computer data inbound port through which it pushes its dynamic data. */
	private ComputerDynamicStateDataInboundPort computerDynamicStateDataInboundPort;
	/** future of the task scheduled to push dynamic data. */
	private ScheduledFuture<?> pushingFuture;

	private ArrayList<Integer> possibleFrequencies;

	private int[][] coreFrequencies;

	// ------------------------------------------------------------------------
	// Component constructor
	// ------------------------------------------------------------------------

	/**
	 * create a computer component.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	computerURI != null
	 * pre	possibleFrequencies != null and for all i in possibleFrequencies, i &gt; 0
	 * pre	processingPower != null and for all i in processingPower.values(), i &gt; 0
	 * pre	processingPower.keySet().containsAll(possibleFrequencies)
	 * pre	possibleFrequencies.contains(defaultFrequency)
	 * pre	maxFrequencyGap &gt;= 0 and for all i in possibleFrequencies, maxFrequencyGap &lt;= i
	 * pre	numberOfProcessors &gt; 0
	 * pre	numberOfCores &gt; 0
	 * pre	computerServicesInboundPortURI != null
	 * pre	computerStaticStateDataInboundPortURI != null
	 * pre	computerDynamicStateDataInboundPortURI != null
	 * post	true			// no post condition.
	 * </pre>
	 *
	 * @param computerURI                            URI of the computer.
	 * @param possibleFrequencies                    possible frequencies for cores.
	 * @param processingPower                        Mips for the different possible
	 *                                               frequencies.
	 * @param defaultFrequency                       default frequency at which the
	 *                                               cores run.
	 * @param maxFrequencyGap                        max frequency gap among cores
	 *                                               of the same processor.
	 * @param numberOfProcessors                     number of processors in the
	 *                                               computer.
	 * @param numberOfCores                          number of cores per processor
	 *                                               (homogeneous).
	 * @param computerServicesInboundPortURI         URI of the computer service
	 *                                               inbound port.
	 * @param computerStaticStateDataInboundPortURI  URI of the computer static data
	 *                                               notification inbound port.
	 * @param computerDynamicStateDataInboundPortURI URI of the computer dynamic
	 *                                               data notification inbound port.
	 * @throws Exception exception
	 */
	public Computer(String computerURI, Set<Integer> possibleFrequencies, Map<Integer, Integer> processingPower,
			int defaultFrequency, int maxFrequencyGap, int numberOfProcessors, int numberOfCores,
			String computerServicesInboundPortURI, String computerStaticStateDataInboundPortURI,
			String computerDynamicStateDataInboundPortURI) throws Exception {
		// The normal thread pool is used to process component services, while
		// the scheduled one is used to schedule the pushes of dynamic state
		// when requested.
		super(1, 1);

		// Verifying the preconditions
		assert computerURI != null;
		assert possibleFrequencies != null;
		boolean allPositive = true;
		for (int f : possibleFrequencies) {
			allPositive = allPositive && (f > 0);
		}
		assert allPositive;
		assert processingPower != null;
		allPositive = true;
		for (int ips : processingPower.values()) {
			allPositive = allPositive && ips > 0;
		}
		assert allPositive;
		assert processingPower.keySet().containsAll(possibleFrequencies);
		assert possibleFrequencies.contains(defaultFrequency);
		int max = -1;
		for (int f : possibleFrequencies) {
			if (max < f) {
				max = f;
			}
		}
		assert maxFrequencyGap >= 0 && maxFrequencyGap <= max;
		assert numberOfProcessors > 0;
		assert numberOfCores > 0;
		assert computerServicesInboundPortURI != null;
		assert computerStaticStateDataInboundPortURI != null;
		assert computerDynamicStateDataInboundPortURI != null;

		// For processor static data
		this.addRequiredInterface(DataRequiredI.PullI.class);
		this.addOfferedInterface(DataRequiredI.PushI.class);
		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class);

		this.computerURI = computerURI;
		this.numberOfProcessors = numberOfProcessors;
		this.numberOfCores = numberOfCores;
		this.processors = new Processor[numberOfProcessors];
		this.processorStaticDataOutboundPorts = new ProcessorStaticStateDataOutboundPort[numberOfProcessors];
		this.processorDynamicDataOutboundPorts = new ProcessorDynamicStateDataOutboundPort[numberOfProcessors];
		this.processorsURI = new HashMap<>();
		this.processorsInboundPortURI = new HashMap<>();

		this.possibleFrequencies = new ArrayList<>();
		this.possibleFrequencies.addAll(possibleFrequencies);
		Collections.sort(this.possibleFrequencies);

		// Create the different processors
		for (int i = 0; i < numberOfProcessors; i++) {
			// generate URI for the processor and its different ports
			String processorURI = this.computerURI + "-processor-" + i;
			String psipURI = processorURI + "-psip";
			String piipURI = processorURI + "-piip";
			String pmipURI = processorURI + "-pmip";
			String pssdipURI = processorURI + "-pssdip";
			String pdsdipURI = processorURI + "-pdsdip";

			// record the mapping between the processor number and its generated
			// URI
			this.processorsURI.put(i, processorURI);

			// create the processor component
			this.processors[i] = new Processor(processorURI, possibleFrequencies, processingPower, defaultFrequency,
					maxFrequencyGap, numberOfCores, psipURI, piipURI, pmipURI, pssdipURI, pdsdipURI);
			// add it to the deployed components in the CVM
			AbstractCVM.getCVM().addDeployedComponent(this.processors[i]);

			// create a map between the port types and the ports URI
			EnumMap<Processor.ProcessorPortTypes, String> map = new EnumMap<>(
					Processor.ProcessorPortTypes.class);
			map.put(Processor.ProcessorPortTypes.SERVICES, psipURI);
			map.put(Processor.ProcessorPortTypes.INTROSPECTION, piipURI);
			map.put(Processor.ProcessorPortTypes.MANAGEMENT, pmipURI);
			map.put(Processor.ProcessorPortTypes.STATIC_STATE, pssdipURI);
			map.put(Processor.ProcessorPortTypes.DYNAMIC_STATE, pdsdipURI);
			// record this map for the processor in the computer data
			this.processorsInboundPortURI.put(processorURI, map);

			// create the computer ports to receive the static and dynamic data
			// from the processor
			this.processorStaticDataOutboundPorts[i] = new ProcessorStaticStateDataOutboundPort(this, processorURI);
			this.addPort(this.processorStaticDataOutboundPorts[i]);
			this.processorStaticDataOutboundPorts[i].publishPort();
			this.doPortConnection(this.processorStaticDataOutboundPorts[i].getPortURI(), pssdipURI,
					DataConnector.class.getCanonicalName());

			this.processorDynamicDataOutboundPorts[i] = new ProcessorDynamicStateDataOutboundPort(this, processorURI);
			this.addPort(this.processorDynamicDataOutboundPorts[i]);
			this.processorDynamicDataOutboundPorts[i].publishPort();
			this.doPortConnection(this.processorDynamicDataOutboundPorts[i].getPortURI(), pdsdipURI,
					ControlledDataConnector.class.getCanonicalName());
		}

		// Initialize the reservation status of the cores.
		this.reservedCores = new boolean[this.numberOfProcessors][this.numberOfCores];
		for (int np = 0; np < this.numberOfProcessors; np++) {
			for (int nc = 0; nc < this.numberOfCores; nc++) {
				this.reservedCores[np][nc] = false;
			}
		}

		this.coreFrequencies = new int[this.numberOfProcessors][this.numberOfCores];
		for (int np = 0; np < this.numberOfProcessors; np++) {
			for (int nc = 0; nc < this.numberOfCores; nc++) {
				this.coreFrequencies[np][nc] = defaultFrequency;
			}
		}

		// Adding computer interfaces, creating and publishing the related ports
		this.addOfferedInterface(ComputerServicesI.class);
		this.computerServicesInboundPort = new ComputerServicesInboundPort(computerServicesInboundPortURI, this);
		this.addPort(this.computerServicesInboundPort);
		this.computerServicesInboundPort.publishPort();

		this.addOfferedInterface(DataOfferedI.PullI.class);
		this.addRequiredInterface(DataOfferedI.PushI.class);
		this.addOfferedInterface(ComputerStaticStateDataI.class);
		this.computerStaticStateDataInboundPort = new ComputerStaticStateDataInboundPort(
				computerStaticStateDataInboundPortURI, this);
		this.addPort(this.computerStaticStateDataInboundPort);
		this.computerStaticStateDataInboundPort.publishPort();

		this.addOfferedInterface(ControlledDataOfferedI.ControlledPullI.class);
		this.computerDynamicStateDataInboundPort = new ComputerDynamicStateDataInboundPort(
				computerDynamicStateDataInboundPortURI, this);
		this.addPort(computerDynamicStateDataInboundPort);
		this.computerDynamicStateDataInboundPort.publishPort();

		this.tracer.setTitle(this.computerURI);
	}

	// ------------------------------------------------------------------------
	// Component life-cycle
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void finalise() throws Exception {
		for (int i = 0; i < this.numberOfProcessors; i++) {
			try {
				// disconnect the ports between the computer and its processors
				if (this.processorStaticDataOutboundPorts[i].connected()) {
					this.processorStaticDataOutboundPorts[i].doDisconnection();
				}
				if (this.processorDynamicDataOutboundPorts[i].connected()) {
					this.processorDynamicDataOutboundPorts[i].doDisconnection();
				}
				// disconnect the ports between the computer and its clients
				if (this.computerStaticStateDataInboundPort.connected()) {
					this.computerStaticStateDataInboundPort.doDisconnection();
				}
				if (this.computerDynamicStateDataInboundPort.connected()) {
					this.computerDynamicStateDataInboundPort.doDisconnection();
				}
			} catch (Exception e) {
				throw new ComponentShutdownException(e);
			}
		}
		super.finalise();
	}

	/**
	 * shutdown the computer, first disconnecting all processor components' outbound
	 * ports.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more post conditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			for (int i = 0; i < this.numberOfProcessors; i++) {
				this.processorStaticDataOutboundPorts[i].unpublishPort();
				this.processorDynamicDataOutboundPorts[i].unpublishPort();
			}
			this.computerServicesInboundPort.unpublishPort();
			this.computerStaticStateDataInboundPort.unpublishPort();
			this.computerDynamicStateDataInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	/**
	 * toggle logging for the computer component and its processor components.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more post conditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.components.AbstractComponent#toggleLogging()
	 */
	@Override
	public void toggleLogging() {
		for (int p = 0; p < this.numberOfProcessors; p++) {
			this.processors[p].toggleLogging();
		}
		super.toggleLogging();
	}

	/**
	 * toggle tracing for the computer component and its processor components.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more post conditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.components.AbstractComponent#toggleTracing()
	 */
	@Override
	public void toggleTracing() {
		for (int p = 0; p < this.numberOfProcessors; p++) {
			this.processors[p].toggleTracing();
		}
		super.toggleTracing();
	}

	// ------------------------------------------------------------------------
	// Component introspection services (ComputerStaticStateDataI)
	// ------------------------------------------------------------------------

	/**
	 * collect and return the static state of the computer.
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
	 * @return the static state of the computer.
	 * @throws Exception exception
	 */
	public ComputerStaticStateI getStaticState() throws Exception {
		Map<Integer, String> pURIs = new HashMap<>(this.processorsURI.size());
		Map<String, Map<Processor.ProcessorPortTypes, String>> pPortsURI = new HashMap<>(
				this.processorsURI.size());
		for (Integer n : this.processorsURI.keySet()) {
			pURIs.put(n, this.processorsURI.get(n));
			Map<Processor.ProcessorPortTypes, String> pIbpURIs = new HashMap<>();
			for (Processor.ProcessorPortTypes ppt : this.processorsInboundPortURI.get(this.processorsURI.get(n))
					.keySet()) {
				pIbpURIs.put(ppt, this.processorsInboundPortURI.get(this.processorsURI.get(n)).get(ppt));
			}
			pPortsURI.put(this.processorsURI.get(n), pIbpURIs);
		}
		return new ComputerStaticState(this.computerURI, this.numberOfProcessors, this.numberOfCores, pURIs, pPortsURI);
	}

	/**
	 * push the static state of the computer through its notification data inbound
	 * port.
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
	 * @throws Exception exception
	 */
	private void sendStaticState() throws Exception {
		if (this.computerStaticStateDataInboundPort.connected()) {
			ComputerStaticStateI css = this.getStaticState();
			this.computerStaticStateDataInboundPort.send(css);
		}
	}

	// ------------------------------------------------------------------------
	// Component introspection services
	// ------------------------------------------------------------------------

	/**
	 * collect and return the dynamic state of the computer.
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
	 * @return the dynamic state of the computer.
	 * @throws Exception exception
	 */
	public ComputerDynamicStateI getDynamicState() throws Exception {
		return new ComputerDynamicState(this.computerURI, this.reservedCores, this.coreFrequencies);
	}

	/**
	 * push the dynamic state of the computer through its notification data inbound
	 * port.
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
	 * @throws Exception exception
	 */
	private void sendDynamicState() throws Exception {
		if (this.computerDynamicStateDataInboundPort.connected()) {
			ComputerDynamicStateI cds = this.getDynamicState();
			this.computerDynamicStateDataInboundPort.send(cds);
		}
	}

	/**
	 * push the dynamic state of the computer through its notification data inbound
	 * port at a specified time interval in ms and for a specified number of times.
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
	 * @param interval                time interval between data pushes.
	 * @param numberOfRemainingPushes number of data pushes yet to be done.
	 * @throws Exception exception
	 */
	private void sendDynamicState(final int interval, int numberOfRemainingPushes) throws Exception {
		this.sendDynamicState();
		final int fNumberOfRemainingPushes = numberOfRemainingPushes - 1;
		if (fNumberOfRemainingPushes > 0) {
			this.pushingFuture = this.scheduleTask(new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						((Computer) this.getOwner()).sendDynamicState(interval, fNumberOfRemainingPushes);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}, TimeManagement.acceleratedDelay(interval), TimeUnit.MILLISECONDS);
		}
	}

	/**
	 * @see fr.sorbonne_u.datacenter.interfaces.PushModeControllingI#startUnlimitedPushing(int)
	 */
	@Override
	public void startUnlimitedPushing(int interval) throws Exception {
		// first, send the static state if the corresponding port is connected
		this.sendStaticState();

		this.pushingFuture = this.scheduleTaskAtFixedRate(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					((Computer) this.getOwner()).sendDynamicState();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, TimeManagement.acceleratedDelay(interval), TimeManagement.acceleratedDelay(interval), TimeUnit.MILLISECONDS);
	}

	/**
	 * @see fr.sorbonne_u.datacenter.interfaces.PushModeControllingI#startLimitedPushing(int,
	 *      int)
	 */
	@Override
	public void startLimitedPushing(final int interval, final int n) throws Exception {
		assert n > 0;

		this.logMessage(
				this.computerURI + " startLimitedPushing with interval " + interval + " ms for " + n + " times.");

		// first, send the static state if the corresponding port is connected
		this.sendStaticState();

		this.pushingFuture = this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					((Computer) this.getOwner()).sendDynamicState(interval, n);
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
	public void stopPushing() throws Exception {
		if (this.pushingFuture != null && !(this.pushingFuture.isCancelled() || this.pushingFuture.isDone())) {
			this.pushingFuture.cancel(false);
		}
	}

	// ------------------------------------------------------------------------
	// Component self-monitoring (ProcessorStateDataConsumerI)
	// ------------------------------------------------------------------------

	/**
	 * process the static state data received from a processor.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	processorURI != null and processorsURI.containsValue(processorURI)
	 * pre	ss != null
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorStateDataConsumerI#acceptProcessorStaticData(java.lang.String,
	 *      fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorStaticStateI)
	 */
	@Override
	public void acceptProcessorStaticData(String processorURI, ProcessorStaticStateI ss) throws Exception {
		assert processorURI != null && processorsURI.containsValue(processorURI);
		assert ss != null;

		this.logMessage("Computer " + this.computerURI + " accepting static data from " + processorURI);
		this.logMessage("  timestamp              : " + ss.getTimeStamp());
		this.logMessage("  timestamper id         : " + ss.getTimeStamperId());
		this.logMessage("  number of cores        : " + ss.getNumberOfCores());
		this.logMessage("  default frequency      : " + ss.getDefaultFrequency());
		this.logMessage("  max. frequency gap     : " + ss.getMaxFrequencyGap());
		StringBuilder admissibleFrequencies = new StringBuilder("  admissible frequencies : [");
		int count = ss.getAdmissibleFrequencies().size();
		for (Integer f : ss.getAdmissibleFrequencies()) {
			admissibleFrequencies.append(f);
			count--;
			if (count > 0) {
				admissibleFrequencies.append(", ");
			}
		}
		admissibleFrequencies.append("]");
		this.logMessage(admissibleFrequencies.toString());
		StringBuilder pp = new StringBuilder("  processing power       : [");
		count = ss.getProcessingPower().entrySet().size();
		for (Entry<Integer, Integer> e : ss.getProcessingPower().entrySet()) {
			pp.append("(").append(e.getKey()).append(" => ").append(e.getValue()).append(")");
			count--;
			if (count > 0) {
				pp.append(", ");
			}
		}
		this.logMessage(pp + "]");
	}

	/**
	 * process the dynamic state data received from a processor.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	processorURI != null and processorsURI.containsValue(processorURI)
	 * pre	cds != null
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorStateDataConsumerI#acceptProcessorDynamicData(java.lang.String,
	 *      fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI)
	 */
	@Override
	public void acceptProcessorDynamicData(String processorURI, ProcessorDynamicStateI cds) throws Exception {
		assert processorURI != null && processorsURI.containsValue(processorURI);
		assert cds != null;

		this.logMessage("Computer " + this.computerURI + " accepting dynamic data from " + processorURI);
		this.logMessage("  timestamp                : " + cds.getTimeStamp());
		this.logMessage("  timestamper id           : " + cds.getTimeStamperId());
		StringBuilder idleStatus = new StringBuilder("  current idle status      : [");
		for (int i = 0; i < cds.getCoresIdleStatus().length; i++) {
			idleStatus.append(cds.getCoreIdleStatus(i));
			if (i < cds.getCoresIdleStatus().length - 1) {
				idleStatus.append(", ");
			}
		}
		this.logMessage(idleStatus + "]");
		StringBuilder coreFreq = new StringBuilder("  current core frequencies : [");
		for (int i = 0; i < cds.getCurrentCoreFrequencies().length; i++) {
			coreFreq.append(cds.getCurrentCoreFrequency(i));
			if (i < cds.getCurrentCoreFrequencies().length - 1) {
				coreFreq.append(", ");
			}
		}
		this.logMessage(coreFreq + "]");
	}

	// ------------------------------------------------------------------------
	// Component services
	// ------------------------------------------------------------------------

	/**
	 * allocate one core on this computer and return an instance of
	 * <code>AllocatedCore</code> containing the processor number, the core number
	 * and a map giving the URI of the processor inbound ports; return null if no
	 * core is available.
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
	 * @return an instance of <code>AllocatedCore</code> with the data about the
	 *         allocated core.
	 * @throws Exception exception
	 */
	public AllocatedCore allocateCore() throws Exception {
		AllocatedCore ret = null;
		int processorNo = -1;
		int coreNo = -1;
		boolean notFound = true;
		for (int p = 0; notFound && p < this.numberOfProcessors; p++) {
			for (int c = 0; notFound && c < this.numberOfCores; c++) {
				if (!this.reservedCores[p][c]) {
					notFound = false;
					this.reservedCores[p][c] = true;
					this.coreFrequencies[p][c] = this.processors[p].getCoreFrequency(c);
					processorNo = p;
					coreNo = c;
				}
			}
		}
		if (!notFound) {
			ret = new AllocatedCore(processorNo, this.processorsURI.get(processorNo), coreNo,
					this.processorsInboundPortURI.get(this.processorsURI.get(processorNo)));
		}
		return ret;
	}

	/**
	 * allocate up to <code>numberRequested</code> cores on this computer and return
	 * and array of <code>AllocatedCore</code> containing the data for each
	 * requested core; return an empty array if no core is available.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	numberRequested &gt; 0
	 * post	return.length &gt;= 0 and return.length &lt;= numberRequested
	 * </pre>
	 *
	 * @param numberRequested number of cores to be allocated.
	 * @return an array of instances of <code>AllocatedCore</code> with the data
	 *         about the allocated cores.
	 * @throws Exception exception
	 */
	public AllocatedCore[] allocateCores(int numberRequested) throws Exception {
		printCurrentOccupancy();
		Vector<AllocatedCore> allocated = new Vector<>(numberRequested);
		boolean notExhausted = true;
		for (int i = 0; notExhausted && i < numberRequested; i++) {
			AllocatedCore c = this.allocateCore();
			if (c != null) {
				allocated.add(c);
			} else {
				notExhausted = false;
			}
		}
		printCurrentOccupancy();
		return allocated.toArray(new AllocatedCore[0]);
	}

	/**
	 * releases a previously reserved core.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	this.isReserved(ac.processorNo, ac.coreNo) ;
	 * post	!this.isReserved(ac.processorNo, ac.coreNo) ;
	 * </pre>
	 *
	 * @param ac previously allocated core data.
	 * @throws Exception exception
	 */
	public void releaseCore(AllocatedCore ac) throws Exception {
		assert this.isReserved(ac.processorNo, ac.coreNo);

		this.reservedCores[ac.processorNo][ac.coreNo] = false;
		assert !this.isReserved(ac.processorNo, ac.coreNo);
	}

	/**
	 * release an array of previously reserved cores.
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
	 * @param acs array of previously allocated cores data.
	 * @throws Exception exception
	 */
	public void releaseCores(AllocatedCore[] acs) throws Exception {
		for (AllocatedCore ac : acs) {
			this.releaseCore(ac);
		}
	}

	// ------------------------------------------------------------------------
	// Component internal services
	// ------------------------------------------------------------------------

	/**
	 * reserve the core <code>coreNo</code> of processor <code>processorNo</code>.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	!this.isReserved(processorNo, coreNo)
	 * post	this.isReserved(processorNo, coreNo)
	 * </pre>
	 *
	 * @param processorNo number of the processor.
	 * @param coreNo      number of the core.
	 * @throws Exception when the core is already reserved.
	 */
	public void reserveCore(int processorNo, int coreNo) throws Exception {
		assert !this.isReserved(processorNo, coreNo);

		this.reservedCores[processorNo][coreNo] = true;

		assert this.isReserved(processorNo, coreNo);
	}

	/**
	 * return true if the core <code>coreNo</code> of processor
	 * <code>processorNo</code> is reserved and false otherwise.
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
	 * @param processorNo number of the processor on which is the core to be tested.
	 * @param coreNo      number of the core to be tested.
	 * @return true if the core is reserved, false otherwise.
	 * @throws Exception exception
	 */
	private boolean isReserved(int processorNo, int coreNo) throws Exception {
		return this.reservedCores[processorNo][coreNo];
	}

	/**
	 * utility to format processor information in a string for later logging or
	 * printing.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	leadingBlanks &gt;= 0
	 * pre	numberOfProcessors &gt; 0
	 * pre	processorsURI != null
	 * pre	processorsInboundPortURI != null
	 * pre	processorsURI.size() == processorsInboundPortURI.size()
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param leadingBlanks            number of blank characters leading each line.
	 * @param numberOfProcessors       number of processors in the computer.
	 * @param processorsURI            URI of the processors.
	 * @param processorsInboundPortURI map form processors' URI to URI of the
	 *                                 processors' inbound ports.
	 * @return a string with preformatted information.
	 */
	public static String printProcessorsInboundPortURI(int leadingBlanks, int numberOfProcessors,
			Map<Integer, String> processorsURI,
			Map<String, Map<Processor.ProcessorPortTypes, String>> processorsInboundPortURI) {
		assert leadingBlanks >= 0;
		assert numberOfProcessors > 0;
		assert processorsURI != null;
		assert processorsInboundPortURI != null;
		assert processorsURI.size() == processorsInboundPortURI.size();

		StringBuilder sb = new StringBuilder();
		StringBuilder leading = new StringBuilder();
		for (int i = 0; i < leadingBlanks; i++) {
			leading.append(" ");
		}
		for (int p = 0; p < processorsURI.size(); p++) {
			sb.append(leading).append(processorsURI.get(p)).append("\n");
			Map<Processor.ProcessorPortTypes, String> pURIs = processorsInboundPortURI.get(processorsURI.get(p));
			for (Processor.ProcessorPortTypes pt : pURIs.keySet()) {
				sb.append(leading).append("    ").append(pt).append("  ").append(pURIs.get(pt)).append("\n");
			}
		}
		return sb.toString();
	}

	public boolean increaseFrequency(int coreNo, String processorURI) throws Exception {
		printCurrentFrequencies ();
		this.logMessage("Increasing processor " + processorURI + " and core " + coreNo);

		int currentFrequency = getCurrentFrequency(coreNo, processorURI);
		if (currentFrequency == -1) return false;

		for (Integer possibleFrequency : this.possibleFrequencies) {
			if (possibleFrequency > currentFrequency) { //stop at the first one
				boolean res = setCurrentFrequency(possibleFrequency, coreNo, processorURI);
				printCurrentFrequencies();
				return res;
			}
		}
		this.logMessage("Increase not possible");
		return false;
	}

	public boolean decreaseFrequency(int coreNo, String processorURI) throws Exception {
		printCurrentFrequencies ();
		this.logMessage("Decreasing processor " + processorURI + " and core " + coreNo);

		int currentFrequency = getCurrentFrequency(coreNo, processorURI);
		if (currentFrequency == -1) return false;

		for (int i = this.possibleFrequencies.size()-1; i >= 0; i--) {
			if (this.possibleFrequencies.get(i) < currentFrequency) { //stop at the first one
				boolean res =  setCurrentFrequency(possibleFrequencies.get(i), coreNo, processorURI);
				printCurrentFrequencies ();
				return res;
			}
		}
		this.logMessage("Decrease not possible");
		return false;
	}

	private boolean setCurrentFrequency(int possibleFrequency, int coreNo, String processorURI) throws Exception {

		int processorNo = -1;
		for (Entry<Integer, String> entry : this.processorsURI.entrySet()) {
			if (Objects.equals(entry.getValue(), processorURI)) {
				processorNo = entry.getKey();
				break;
			}
		}

		if (this.processors[processorNo].isValidCoreNo(coreNo)) {
			if (this.processors[processorNo].isAdmissibleFrequency(possibleFrequency) &&
					this.processors[processorNo].isCurrentlyPossibleFrequencyForCore(coreNo, possibleFrequency)) {

				this.processors[processorNo].setCoreFrequency(coreNo, possibleFrequency);
				this.coreFrequencies[processorNo][coreNo] = possibleFrequency;
				return true;
			}

		}

		return false;
	}

	private int getCurrentFrequency(int coreNo, String processorURI) {
		for (Entry<Integer, String> entry : this.processorsURI.entrySet()) {
			if (Objects.equals(entry.getValue(), processorURI)) {
				int processorNo = entry.getKey();
				return this.processors[processorNo].getCoreFrequency(coreNo);
			}
		}
		return -1;
	}


	private void printCurrentFrequencies () {
		this.logMessage("--> Current frequencies");

		for (int np = 0; np < this.numberOfProcessors; np++) {
			StringBuilder sb = new StringBuilder();
			for (int nc = 0; nc < this.numberOfCores; nc++) {
				sb.append(this.processors[np].getCoreFrequency(nc)).append(" ");
			}
			this.logMessage("-----> processor " + np + ": " + sb);
		}
	}

	private void printCurrentOccupancy () {
		this.logMessage("--> Current occupancy");

		for (int np = 0; np < this.numberOfProcessors; np++) {
			StringBuilder sb = new StringBuilder();
			for (int nc = 0; nc < this.numberOfCores; nc++) {
				sb.append(this.reservedCores[np][nc]).append(" ");
			}
			this.logMessage("-----> processor " + np + ": " + sb);
		}
	}
}
