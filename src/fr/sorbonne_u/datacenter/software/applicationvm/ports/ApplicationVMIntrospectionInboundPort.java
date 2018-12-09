package fr.sorbonne_u.datacenter.software.applicationvm.ports;

import java.util.Map;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM.ApplicationVMPortTypes;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMIntrospectionI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateI;

/**
 * The class <code>ApplicationVMIntrospectionInboundPort</code> implements an
 * inbound port for interface <code>ApplicationVMIntrospectionI</code>.
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
 * invariant true
 * </pre>
 * 
 * <p>
 * Created on : October 5, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ApplicationVMIntrospectionInboundPort extends AbstractInboundPort implements ApplicationVMIntrospectionI {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public ApplicationVMIntrospectionInboundPort(ComponentI owner) throws Exception {
		super(ApplicationVMIntrospectionI.class, owner);

		assert owner instanceof ApplicationVM;
	}

	public ApplicationVMIntrospectionInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ApplicationVMIntrospectionI.class, owner);

		assert owner instanceof ApplicationVM;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMIntrospectionI#getAVMPortsURI()
	 */
	@Override
	public Map<ApplicationVMPortTypes, String> getAVMPortsURI() throws Exception {
		return this.getOwner()
				.handleRequestSync(new AbstractComponent.AbstractService<>() {
					@Override
					public Map<ApplicationVMPortTypes, String> call() throws Exception {
						return ((ApplicationVM) this.getOwner()).getAVMPortsURI();
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMIntrospectionI#getStaticState()
	 */
	@Override
	public ApplicationVMStaticStateI getStaticState() throws Exception {
		return this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<>() {
			@Override
			public ApplicationVMStaticStateI call() throws Exception {
				return ((ApplicationVM) this.getOwner()).getStaticState();
			}
		});
	}

	/**
	 * @see fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMIntrospectionI#getDynamicState()
	 */
	@Override
	public ApplicationVMDynamicStateI getDynamicState() throws Exception {
		return this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<>() {
			@Override
			public ApplicationVMDynamicStateI call() throws Exception {
				return ((ApplicationVM) this.getOwner()).getDynamicState();
			}
		});
	}
}
