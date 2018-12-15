package fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces;

import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;

public interface RequestDispatcherDynamicStateI extends DataOfferedI.DataI, DataRequiredI.DataI {

	double getExponentialAverageExecutionTime();

	int getAvailableAVMsCount();

	int getTotalRequestSubmitted();

	int getTotalRequestTerminated();
}
