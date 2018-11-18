package fr.sorbonne_u.sylalexcenter.admissioncontroller.interfaces;

import java.io.Serializable;

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public interface RequestAdmissionI extends Serializable {
	
	public String getRequestNotificationPortURI();

	public String getRequestSubmissionPortURI();
	
	public void setRequestSubmissionPortURI(String uri);
	
	public String getApplicationManagementInboundPortURI();
	
	public void setApplicationManagementInboundPortURI(String uri);
	
	public String getRequestDispatcherURI();
	
	public void setRequestDispatcherURI(String uri);	
	
}
