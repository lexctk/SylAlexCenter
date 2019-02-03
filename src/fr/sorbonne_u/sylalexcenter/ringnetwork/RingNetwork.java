package fr.sorbonne_u.sylalexcenter.ringnetwork;

/**
 * The class <code>RingNetwork</code> implements a ring network for the admission
 * controller and performance controllers.
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class RingNetwork {
	private String ringInboundPortURI;
	private String ringOutboundPortURI;

	private String performanceControllerURI;

	public String getRingInboundPortURI() {
		return ringInboundPortURI;
	}

	public void setRingInboundPortURI(String ringInboundPortURI) {
		this.ringInboundPortURI = ringInboundPortURI;
	}

	public String getRingOutboundPortURI() {
		return ringOutboundPortURI;
	}

	public void setRingOutboundPortURI(String ringOutboundPortURI) {
		this.ringOutboundPortURI = ringOutboundPortURI;
	}

	public String getPerformanceControllerURI() {
		return performanceControllerURI;
	}

	public void setPerformanceControllerURI(String performanceControllerURI) {
		this.performanceControllerURI = performanceControllerURI;
	}
}
