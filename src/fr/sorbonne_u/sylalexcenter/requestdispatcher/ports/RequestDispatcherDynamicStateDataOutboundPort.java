package fr.sorbonne_u.sylalexcenter.requestdispatcher.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.datacenter.ports.AbstractControlledDataOutboundPort;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherStateDataConsumerI;

public class RequestDispatcherDynamicStateDataOutboundPort extends AbstractControlledDataOutboundPort {

	private String rdURI;

	public RequestDispatcherDynamicStateDataOutboundPort(ComponentI owner, String rdURI) throws Exception {

		super(owner);
		this.rdURI = rdURI;

		assert owner instanceof RequestDispatcherStateDataConsumerI;
	}

	public RequestDispatcherDynamicStateDataOutboundPort(String uri, ComponentI owner, String rdURI)
			throws Exception {

		super(uri, owner);
		this.rdURI = rdURI;

		assert owner instanceof RequestDispatcherStateDataConsumerI;
	}

	@Override
	public void receive(DataRequiredI.DataI d) throws Exception {
		((RequestDispatcherStateDataConsumerI)this.owner).
				acceptRequestDispatcherDynamicData (this.rdURI, (RequestDispatcherDynamicStateI) d);
	}
}
