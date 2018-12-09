package fr.sorbonne_u.datacenter.hardware.tests;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.connectors.DataConnector;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.datacenter.connectors.ControlledDataConnector;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerStateDataConsumerI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
import fr.sorbonne_u.datacenter.interfaces.ControlledDataRequiredI;

/**
 * The class <code>ComputerMonitor</code> is a component used in the test to act
 * as a receiver for state data notifications coming from a computer.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * The component class simply implements the necessary methods to process the
 * notifications without paying attention to do that in a really safe component
 * programming way. More or less quick and dirty...
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
public class ComputerMonitor extends AbstractComponent implements ComputerStateDataConsumerI {
	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	private boolean active;
	private String computerStaticStateDataInboundPortURI;
	private String computerDynamicStateDataInboundPortURI;
	private ComputerStaticStateDataOutboundPort cssPort;
	private ComputerDynamicStateDataOutboundPort cdsPort;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public ComputerMonitor(String computerURI, boolean active, String computerStaticStateDataInboundPortURI,
			String computerDynamicStateDataInboundPortURI) throws Exception {
		super(1, 0);

		assert computerStaticStateDataInboundPortURI != null;
		assert computerDynamicStateDataInboundPortURI != null;

		this.active = active;
		this.computerDynamicStateDataInboundPortURI = computerDynamicStateDataInboundPortURI;
		this.computerStaticStateDataInboundPortURI = computerStaticStateDataInboundPortURI;

		this.addOfferedInterface(DataRequiredI.PushI.class);
		this.addRequiredInterface(DataRequiredI.PullI.class);
		this.cssPort = new ComputerStaticStateDataOutboundPort(this, computerURI);
		this.addPort(cssPort);
		this.cssPort.publishPort();

		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class);
		this.cdsPort = new ComputerDynamicStateDataOutboundPort(this, computerURI);
		this.addPort(cdsPort);
		this.cdsPort.publishPort();
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	@Override
	public void start() throws ComponentStartException {
		super.start();

		// start the pushing of dynamic state information from the computer;
		// here only one push of information is planned after one second.
		try {
			this.doPortConnection(this.cssPort.getPortURI(), this.computerStaticStateDataInboundPortURI,
					DataConnector.class.getCanonicalName());
			this.doPortConnection(this.cdsPort.getPortURI(), this.computerDynamicStateDataInboundPortURI,
					ControlledDataConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(
					"Unable to start the pushing of dynamic data from" + " the computer component.", e);
		}
	}

	@Override
	public void execute() throws Exception {
		super.execute();

		this.cdsPort.startLimitedPushing(1000, 25);
	}

	@Override
	public void finalise() throws Exception {
		try {
			if (this.cssPort.connected()) {
				this.cssPort.doDisconnection();
			}
			if (this.cdsPort.connected()) {
				this.cdsPort.doDisconnection();
			}
		} catch (Exception e) {
			throw new ComponentShutdownException("port disconnection error", e);
		}
		super.finalise();
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			this.cssPort.unpublishPort();
			this.cdsPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException("port unpublishing error", e);
		}

		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component internal services
	// -------------------------------------------------------------------------

	@Override
	public void acceptComputerStaticData(String computerURI, ComputerStaticStateI ss) throws Exception {
		if (this.active) {
			StringBuilder sb = new StringBuilder();
			sb.append("Accepting static data from ").append(computerURI).append("\n");
			sb.append("  timestamp                      : ").append(ss.getTimeStamp()).append("\n");
			sb.append("  timestamper id                 : ").append(ss.getTimeStamperId()).append("\n");
			sb.append("  number of processors           : ").append(ss.getNumberOfProcessors()).append("\n");
			sb.append("  number of cores per processors : ").append(ss.getNumberOfCoresPerProcessor()).append("\n");
			for (int p = 0; p < ss.getNumberOfProcessors(); p++) {
				if (p == 0) {
					sb.append("  processor URIs                 : ");
				} else {
					sb.append("                                 : ");
				}
				sb.append(p).append("  ").append(ss.getProcessorURIs().get(p)).append("\n");
			}
			sb.append("  processor port URIs            : " + "\n");
			sb.append(Computer.printProcessorsInboundPortURI(10, ss.getNumberOfProcessors(), ss.getProcessorURIs(),
					ss.getProcessorPortMap()));
			this.logMessage(sb.toString());
		}
	}

	@Override
	public void acceptComputerDynamicData(String computerURI, ComputerDynamicStateI cds) throws Exception {
		if (this.active) {
			StringBuilder sb = new StringBuilder();
			sb.append("Accepting dynamic data from ").append(computerURI).append("\n");
			sb.append("  timestamp                : ").append(cds.getTimeStamp()).append("\n");
			sb.append("  timestamper id           : ").append(cds.getTimeStamperId()).append("\n");

			boolean[][] reservedCores = cds.getCurrentCoreReservations();
			for (int p = 0; p < reservedCores.length; p++) {
				if (p == 0) {
					sb.append("  reserved cores           : ");
				} else {
					sb.append("                             ");
				}
				for (int c = 0; c < reservedCores[p].length; c++) {
					if (reservedCores[p][c]) {
						sb.append("t ");
					} else {
						sb.append("f ");
					}
				}
			}
			this.logMessage(sb.toString());
		}
	}
}
