package fr.sorbonne_u.sylalexcenter.requestdispatcher.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.datacenter.ports.AbstractControlledDataInboundPort;
import fr.sorbonne_u.sylalexcenter.requestdispatcher.RequestDispatcher;

public class RequestDispatcherDynamicStateDataInboundPort extends AbstractControlledDataInboundPort {

	public RequestDispatcherDynamicStateDataInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);

		assert owner instanceof RequestDispatcher;
	}

	@Override
	public DataOfferedI.DataI get() throws Exception {
		final RequestDispatcher requestDispatcher = (RequestDispatcher) this.owner;
		return requestDispatcher.handleRequestSync(new AbstractComponent.AbstractService<>() {
			@Override
			public DataOfferedI.DataI call() {
				return requestDispatcher.getDynamicState();
			}
		});
	}
}
