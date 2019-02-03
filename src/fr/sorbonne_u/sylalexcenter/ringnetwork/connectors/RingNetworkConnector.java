package fr.sorbonne_u.sylalexcenter.ringnetwork.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.sylalexcenter.ringnetwork.interfaces.RingNetworkI;
import fr.sorbonne_u.sylalexcenter.ringnetwork.utils.AvmInformation;

import java.util.ArrayList;

public class RingNetworkConnector extends AbstractConnector implements RingNetworkI {

	@Override
	public void receiveAVMBuffer(ArrayList<AvmInformation> avmBufferArrayList) throws Exception {
		((RingNetworkI)this.offering).receiveAVMBuffer(avmBufferArrayList);
	}
}
