package fr.sorbonne_u.datacenter.software.applicationvm.interfaces;

import java.util.Map;
import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM.ApplicationVMPortTypes;

/**
 * The class <code>ApplicationVMIntrospectionI</code> defines the component
 * services to introspect application virtual machines.
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
public interface ApplicationVMIntrospectionI extends OfferedI, RequiredI {
	/**
	 * return a map of the application VM port URI by their types.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return != null
	 * </pre>
	 *
	 * @return a map from application VM port types to their URI.
	 */
	Map<ApplicationVMPortTypes, String> getAVMPortsURI() throws Exception;

	/**
	 * return the static state of the application VM as an instance of
	 * <code>ApplicationVMStaticStateI</code>.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return != null
	 * </pre>
	 *
	 * @return the current static state of the application VM.
	 */
	ApplicationVMStaticStateI getStaticState() throws Exception;

	/**
	 * return the dynamic state of the application VM as an instance of
	 * <code>ApplicationVMDynamicStateI</code>.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return != null
	 * </pre>
	 *
	 * @return the dynamic state of the application VM.
	 */
	ApplicationVMDynamicStateI getDynamicState() throws Exception;
}
