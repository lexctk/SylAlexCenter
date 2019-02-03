package fr.sorbonne_u.sylalexcenter.ringnetwork.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.sylalexcenter.ringnetwork.interfaces.RingNetworkI;
import fr.sorbonne_u.sylalexcenter.ringnetwork.utils.AvmInformation;

import java.util.ArrayList;

public class RingNetworkInboundPort extends AbstractInboundPort implements RingNetworkI {

	public RingNetworkInboundPort(ComponentI owner) throws Exception {
		super(RingNetworkI.class, owner);

		assert owner instanceof RingNetworkI;
	}

	public RingNetworkInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RingNetworkI.class, owner);

		assert uri != null && owner instanceof RingNetworkI;
	}

	@Override
	public void receiveAVMBuffer(ArrayList<AvmInformation> avmBufferArrayList) throws Exception {
		((RingNetworkI) this.getOwner()).receiveAVMBuffer(avmBufferArrayList);
	}
}