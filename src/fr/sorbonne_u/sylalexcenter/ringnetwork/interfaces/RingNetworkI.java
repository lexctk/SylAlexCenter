package fr.sorbonne_u.sylalexcenter.ringnetwork.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import fr.sorbonne_u.sylalexcenter.ringnetwork.utils.AvmInformation;

import java.util.ArrayList;

public interface RingNetworkI extends OfferedI, RequiredI {

	void receiveAVMBuffer (ArrayList<AvmInformation> avmBufferArrayList) throws Exception;
}
