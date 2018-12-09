package fr.sorbonne_u.datacenter.software.applicationvm.connectors;

import java.util.Map;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM.ApplicationVMPortTypes;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMIntrospectionI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateI;

/**
 * The class <code>ApplicationVMIntrospectionConnector</code> defines a
 * connector associated with the interface
 * <code>ApplicationVMIntrospectionI</code>.
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
public class ApplicationVMIntrospectionConnector extends AbstractConnector implements ApplicationVMIntrospectionI {
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMIntrospectionI#getAVMPortsURI()
	 */
	@Override
	public Map<ApplicationVMPortTypes, String> getAVMPortsURI() throws Exception {
		return ((ApplicationVMIntrospectionI) this.offering).getAVMPortsURI();
	}

	/**
	 * @see fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMIntrospectionI#getStaticState()
	 */
	@Override
	public ApplicationVMStaticStateI getStaticState() throws Exception {
		return ((ApplicationVMIntrospectionI) this.offering).getStaticState();
	}

	/**
	 * @see fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMIntrospectionI#getDynamicState()
	 */
	@Override
	public ApplicationVMDynamicStateI getDynamicState() throws Exception {
		return ((ApplicationVMIntrospectionI) this.offering).getDynamicState();
	}
}
