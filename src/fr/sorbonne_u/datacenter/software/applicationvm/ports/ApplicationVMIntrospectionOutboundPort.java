package fr.sorbonne_u.datacenter.software.applicationvm.ports;

import java.util.Map;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM.ApplicationVMPortTypes;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMIntrospectionI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateI;

/**
 * The class <code>ApplicationVMIntrospectionOutboundPort</code> implements an
 * outbound port for interface <code>ApplicationVMIntrospectionI</code>.
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
 * Created on : October 5, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ApplicationVMIntrospectionOutboundPort extends AbstractOutboundPort
		implements ApplicationVMIntrospectionI {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public ApplicationVMIntrospectionOutboundPort(ComponentI owner) throws Exception {
		super(ApplicationVMIntrospectionI.class, owner);
	}

	public ApplicationVMIntrospectionOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ApplicationVMIntrospectionI.class, owner);
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMIntrospectionI#getAVMPortsURI()
	 */
	@Override
	public Map<ApplicationVMPortTypes, String> getAVMPortsURI() throws Exception {
		return ((ApplicationVMIntrospectionI) this.connector).getAVMPortsURI();
	}

	/**
	 * @see fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMIntrospectionI#getStaticState()
	 */
	@Override
	public ApplicationVMStaticStateI getStaticState() throws Exception {
		return ((ApplicationVMIntrospectionI) this.connector).getStaticState();
	}

	/**
	 * @see fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMIntrospectionI#getDynamicState()
	 */
	@Override
	public ApplicationVMDynamicStateI getDynamicState() throws Exception {
		return ((ApplicationVMIntrospectionI) this.connector).getDynamicState();
	}
}
