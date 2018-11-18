package fr.sorbonne_u.sylalexcenter.admissioncontroller;

import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationAdmissionI;

public class ApplicationAdmission implements ApplicationAdmissionI {
	private static final long serialVersionUID = 1L;
	
	private String requestNotificationPortURI;
	private String requestSubmissionPortURI;
	private String requestDispatcherURI;
	private String requestGeneratorManagementInboundPortURI;
	
	
	public ApplicationAdmission (String requestNotificationPortURI) {
		this.requestNotificationPortURI = requestNotificationPortURI;
	}
	
	@Override
	public String getRequestNotificationPortURI() { //AdmissionController -> RequestDispatcherNotificationInboundPort
		return requestNotificationPortURI;
	}
	
	@Override
	public void setRequestSubmissionPortURI(String uri) { //AdmissionController <- RequestDispatcherSubmissionInboundPort
		requestSubmissionPortURI = uri;
	}
	
	@Override
	public String getRequestSubmissionPortURI() {
		return requestSubmissionPortURI;
	}

	@Override
	public String getApplicationManagementInboundPortURI() {
		return requestGeneratorManagementInboundPortURI;
	}

	@Override
	public void setApplicationManagementInboundPortURI(String uri) {
		requestGeneratorManagementInboundPortURI = uri;
	}

	@Override
	public String getRequestDispatcherURI() { //AdmissionController
		return requestDispatcherURI;
	}

	@Override
	public void setRequestDispatcherURI(String uri) { //AdmissionController
		requestDispatcherURI = uri;
		
	}
	

}
