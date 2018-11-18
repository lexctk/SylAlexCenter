package fr.sorbonne_u.sylalexcenter.admissioncontroller.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.AdmissionController;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.interfaces.AdmissionControlerManagementI;

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class AdmissionControlerManagementInboundPort extends AbstractInboundPort implements	AdmissionControlerManagementI {
	
	private static final long serialVersionUID = 1L;

	public AdmissionControlerManagementInboundPort (ComponentI owner) throws Exception {
		super(AdmissionControlerManagementI.class, owner) ;
		assert	owner != null && owner instanceof AdmissionController ;
	}

	public AdmissionControlerManagementInboundPort (String uri, ComponentI owner) throws Exception {
		super(uri, AdmissionControlerManagementI.class, owner);

		assert	owner != null && owner instanceof AdmissionController ;
	}
}
