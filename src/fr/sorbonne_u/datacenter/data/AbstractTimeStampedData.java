package fr.sorbonne_u.datacenter.data;

import java.net.InetAddress;
import fr.sorbonne_u.datacenter.TimeManagement;
import fr.sorbonne_u.datacenter.interfaces.TimeStampingI;

/**
 * The abstract class <code>AbstractTimeStampedData</code> implements the basics
 * of data time stamped by a computer clock and its identifier (IP address).
 *
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class AbstractTimeStampedData implements TimeStampingI {
	// ------------------------------------------------------------------------
	// Constants and instance variables
	// ------------------------------------------------------------------------

	/** timestamp in Unix time format, local time of the timestamper. */
	protected final long timestamp;
	/** IP of the node that did the time stamping. */
	protected final String timestamperIP;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public AbstractTimeStampedData() {
		super();

		this.timestamp = TimeManagement.timeStamp();
		String tid;
		try {
			tid = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			tid = "localhost";
		}
		this.timestamperIP = tid;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.interfaces.TimeStampingI#getTimeStamp()
	 */
	@Override
	public long getTimeStamp() {
		return this.timestamp;
	}

	/**
	 * @see fr.sorbonne_u.datacenter.interfaces.TimeStampingI#getTimeStamperId()
	 */
	@Override
	public String getTimeStamperId() {
		return this.timestamperIP;
	}
}
