package fr.sorbonne_u.sylalexcenter.admissioncontroller.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.sylalexcenter.admissioncontroller.interfaces.AdmissionControlerManagementI;

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class AdmissionControlerManagementOutboundPort extends AbstractOutboundPort implements AdmissionControlerManagementI {

	private static final long serialVersionUID = 1L;

	public AdmissionControlerManagementOutboundPort (String uri, ComponentI owner) throws Exception{
		super (uri, AdmissionControlerManagementI.class, owner);
		assert uri != null && owner != null ;
	}
	
	public AdmissionControlerManagementOutboundPort(ComponentI owner) throws Exception {
		super (AdmissionControlerManagementI.class, owner);
		assert owner!=null;
	}
}
