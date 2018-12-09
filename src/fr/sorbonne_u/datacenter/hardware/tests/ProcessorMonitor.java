package fr.sorbonne_u.datacenter.hardware.tests;

import java.util.Map.Entry;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorServicesNotificationConsumerI;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorServicesNotificationI;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorStateDataConsumerI;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorStaticStateI;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorDynamicStateDataOutboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorServicesNotificationInboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorStaticStateDataOutboundPort;
import fr.sorbonne_u.datacenter.interfaces.ControlledDataRequiredI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.TaskI;

/**
 * The class <code>ProcessorMonitor</code> implements a monitor component
 * receiving data from processor components.
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
 * invariant	true
 * </pre>
 * 
 * <p>
 * Created on : April 24, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ProcessorMonitor extends AbstractComponent
		implements ProcessorStateDataConsumerI, ProcessorServicesNotificationConsumerI {
	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	private boolean active;
	protected String processorURI;
	private ProcessorStaticStateDataOutboundPort pssPort;
	private ProcessorDynamicStateDataOutboundPort pdsPort;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	ProcessorMonitor(String processorURI, boolean active, String processorServicesNotificationInboundPortURI,
	                 String processorStaticStateDataOutboundPortURI, String processorDynamicStateDataOutboundPortURI)
			throws Exception {
		super(1, 0);
		this.processorURI = processorURI;
		this.active = active;

		this.addOfferedInterface(ProcessorServicesNotificationI.class);
		ProcessorServicesNotificationInboundPort pnPort = new ProcessorServicesNotificationInboundPort(
				processorServicesNotificationInboundPortURI, this);
		this.addPort(pnPort);
		pnPort.publishPort();

		this.addOfferedInterface(DataRequiredI.PushI.class);
		this.addRequiredInterface(DataRequiredI.PullI.class);
		this.pssPort = new ProcessorStaticStateDataOutboundPort(processorStaticStateDataOutboundPortURI, this,
				processorURI);
		this.addPort(this.pssPort);
		this.pssPort.publishPort();

		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class);
		this.pdsPort = new ProcessorDynamicStateDataOutboundPort(processorDynamicStateDataOutboundPortURI, this,
				processorURI);
		this.addPort(this.pdsPort);
		this.pdsPort.publishPort();
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	@Override
	public void shutdown() throws ComponentShutdownException {
		super.shutdown();
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();

		// start the pushing of dynamic state information from the processor;
		// here only one push of information is planned after one second.
		try {
			this.pdsPort.startLimitedPushing(1000, 25);
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}

	// -------------------------------------------------------------------------
	// Component internal services
	// -------------------------------------------------------------------------

	@Override
	public void acceptProcessorStaticData(String processorURI, ProcessorStaticStateI ss) throws Exception {
		if (this.active) {
			StringBuilder sb = new StringBuilder();
			sb.append("Accepting static data from ").append(processorURI).append("\n");
			sb.append("  timestamp              : ").append(ss.getTimeStamp()).append("\n");
			sb.append("  timestamper id         : ").append(ss.getTimeStamperId()).append("\n");
			sb.append("  number of cores        : ").append(ss.getNumberOfCores()).append("\n");
			sb.append("  default frequency      : ").append(ss.getDefaultFrequency()).append("\n");
			sb.append("  max. frequency gap     : ").append(ss.getMaxFrequencyGap()).append("\n");
			sb.append("  admissible frequencies : [");
			int count = ss.getAdmissibleFrequencies().size();
			for (Integer f : ss.getAdmissibleFrequencies()) {
				sb.append(f);
				count--;
				if (count > 0) {
					sb.append(", ");
				}
			}
			sb.append("]\n");
			sb.append("  processing power       : [");
			count = ss.getProcessingPower().entrySet().size();
			for (Entry<Integer, Integer> e : ss.getProcessingPower().entrySet()) {
				sb.append("(").append(e.getKey()).append(" => ").append(e.getValue()).append(")");
				count--;
				if (count > 0) {
					sb.append(", ");
				}
			}
			sb.append("]\n");
			this.logMessage(sb.toString());
		}
	}

	@Override
	public void acceptProcessorDynamicData(String processorURI, ProcessorDynamicStateI cds) throws Exception {
		if (this.active) {
			StringBuilder sb = new StringBuilder();
			sb.append("Accepting dynamic data from ").append(processorURI).append("\n");
			sb.append("  timestamp                : ").append(cds.getTimeStamp()).append("\n");
			sb.append("  timestamper id           : ").append(cds.getTimeStamperId()).append("\n");
			sb.append("  current idle status      : [");
			for (int i = 0; i < cds.getCoresIdleStatus().length; i++) {
				sb.append(cds.getCoreIdleStatus(i));
				if (i < cds.getCoresIdleStatus().length - 1) {
					sb.append(", ");
				}
			}
			sb.append("]\n");
			sb.append("  current core frequencies : [");
			for (int i = 0; i < cds.getCurrentCoreFrequencies().length; i++) {
				sb.append(cds.getCurrentCoreFrequency(i));
				if (i < cds.getCurrentCoreFrequencies().length - 1) {
					sb.append(", ");
				}
			}
			sb.append("]\n");
			this.logMessage(sb.toString());
		}
	}

	@Override
	public void acceptNotifyEndOfTask(TaskI t) throws Exception {
		if (this.active) {
			this.logMessage(this.processorURI + " notifies end of task " + t.getTaskURI() + ".");
		}
	}

}
