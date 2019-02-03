package fr.sorbonne_u.sylalexcenter.ringnetwork.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.sylalexcenter.ringnetwork.interfaces.RingNetworkI;
import fr.sorbonne_u.sylalexcenter.ringnetwork.utils.AvmInformation;

import java.util.ArrayList;

public class RingNetworkOutboundPort extends AbstractOutboundPort implements RingNetworkI {

	public RingNetworkOutboundPort(ComponentI owner) throws Exception {
		super(RingNetworkI.class, owner);

		assert owner instanceof RingNetworkI;
	}

	public RingNetworkOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RingNetworkI.class, owner);

		assert uri != null && owner instanceof RingNetworkI;
	}

	@Override
	public void receiveAVMBuffer(ArrayList<AvmInformation> avmBufferArrayList) throws Exception {
		((RingNetworkI) this.connector).receiveAVMBuffer(avmBufferArrayList);
	}
}