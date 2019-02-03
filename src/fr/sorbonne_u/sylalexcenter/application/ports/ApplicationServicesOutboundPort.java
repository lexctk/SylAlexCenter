package fr.sorbonne_u.sylalexcenter.application.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationServicesI;
/**
 * The class <code>ApplicationServicesOutboundPort</code> defines
 * an outbound port that allows the application component to send admission requests to
 * the admission controller.
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public class ApplicationServicesOutboundPort extends AbstractOutboundPort implements ApplicationServicesI {

	private static final long serialVersionUID = 1L;

	public ApplicationServicesOutboundPort(ComponentI owner) throws Exception {
		super(ApplicationServicesI.class, owner);

		assert owner != null;
	}
	
	@Override
	public void sendRequestForApplicationExecution(int coresToReserve) throws Exception {
		
		((ApplicationServicesI)this.connector).sendRequestForApplicationExecution(coresToReserve);		
	}	
}