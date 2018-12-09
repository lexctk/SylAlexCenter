package fr.sorbonne_u.datacenter.software.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;

/**
 * The class <code>RequestSubmissionInboundPort</code> implements the inbound
 * port offering the interface <code>RequestSubmissionI</code>.
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
 * invariant uri != null and owner instanceof RequestSubmissionHandlerI
 * </pre>
 * 
 * <p>
 * Created on : April 9, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class RequestSubmissionInboundPort extends AbstractInboundPort implements RequestSubmissionI {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	owner instanceof RequestSubmissionHandlerI
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param owner owner component.
	 */
	public RequestSubmissionInboundPort(ComponentI owner) throws Exception {
		super(RequestSubmissionI.class, owner);

		assert owner instanceof RequestSubmissionHandlerI;
	}

	/**
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	uri != null and owner instanceof RequestSubmissionHandlerI
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri   uri of the port.
	 * @param owner owner component.
	 */
	public RequestSubmissionInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestSubmissionI.class, owner);

		assert uri != null && owner instanceof RequestSubmissionHandlerI;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI#submitRequest(fr.sorbonne_u.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void submitRequest(final RequestI r) throws Exception {
		this.getOwner().handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((RequestSubmissionHandlerI) this.getOwner()).acceptRequestSubmission(r);
				return null;
			}
		});
	}

	/**
	 * @see fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI#submitRequestAndNotify(fr.sorbonne_u.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void submitRequestAndNotify(final RequestI r) throws Exception {
		this.getOwner().handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((RequestSubmissionHandlerI) this.getOwner()).acceptRequestSubmissionAndNotify(r);
				return null;
			}
		});
	}
}
