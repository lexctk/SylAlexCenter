package fr.sorbonne_u.datacenter.software.applicationvm.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;

/**
 * The class <code>ApplicationVMManagementInboundPort</code> implements the
 * inbound port offering the interface <code>ApplicationVMManagementI</code>.
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
 * invariant		owner instanceof ApplicationVMManagementI
 * </pre>
 * 
 * <p>
 * Created on : August 25, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class ApplicationVMManagementInboundPort extends AbstractInboundPort implements ApplicationVMManagementI {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * @param owner owner component.
	 */
	public ApplicationVMManagementInboundPort(ComponentI owner) throws Exception {
		super(ApplicationVMManagementI.class, owner);

		assert owner instanceof ApplicationVMManagementI;
	}

	/**
	 * @param uri   uri of the port.
	 * @param owner owner component.
	 */
	public ApplicationVMManagementInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ApplicationVMManagementI.class, owner);

		assert uri != null && owner instanceof ApplicationVMManagementI;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI#allocateCores(fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore[])
	 */
	@Override
	public void allocateCores(final AllocatedCore[] allocatedCores) throws Exception {
		this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((ApplicationVMManagementI) this.getOwner()).allocateCores(allocatedCores);
				return null;
			}
		});
	}

	@Override
	public void destroyComponent() throws Exception {
		this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((ApplicationVMManagementI) this.getOwner()).destroyComponent();
				return null;
			}
		});
	}
}
