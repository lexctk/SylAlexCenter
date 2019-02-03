package fr.sorbonne_u.sylalexcenter.ringnetwork.utils;

public class AvmInformation {
	private String avmURI;
	private boolean free;
	private AvmInformation avmInformation;

	private String requestDispatcherSubmissionOutboundPortURI;
	private String requestDispatcherNotificationInboundPortURI;

	public String getAvmURI() {
		return avmURI;
	}

	public void setAvmURI(String avmURI) {
		this.avmURI = avmURI;
	}

	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}

	public AvmInformation getAvmInformation() {
		return avmInformation;
	}

	public void setAvmInformation(AvmInformation avmInformation) {
		this.avmInformation = avmInformation;
	}

	public String getRequestDispatcherSubmissionOutboundPortURI() {
		return requestDispatcherSubmissionOutboundPortURI;
	}

	public void setRequestDispatcherSubmissionOutboundPortURI(String requestDispatcherSubmissionOutboundPortURI) {
		this.requestDispatcherSubmissionOutboundPortURI = requestDispatcherSubmissionOutboundPortURI;
	}

	public String getRequestDispatcherNotificationInboundPortURI() {
		return requestDispatcherNotificationInboundPortURI;
	}

	public void setRequestDispatcherNotificationInboundPortURI(String requestDispatcherNotificationInboundPortURI) {
		this.requestDispatcherNotificationInboundPortURI = requestDispatcherNotificationInboundPortURI;
	}
}
