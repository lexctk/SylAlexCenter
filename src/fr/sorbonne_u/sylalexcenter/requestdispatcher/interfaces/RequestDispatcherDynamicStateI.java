package fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces;

import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;

/**
 * The interface <code>RequestDispatcherDynamicStateI</code> defines a dynamic state for the request
 * dispatcher, containing exponential average execution time, number of available AVMs, total requests
 * submitted and total requests terminated
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public interface RequestDispatcherDynamicStateI extends DataOfferedI.DataI, DataRequiredI.DataI {

	double getExponentialAverageExecutionTime();

	int getAvailableAVMsCount();

	int getTotalRequestSubmitted();

	int getTotalRequestTerminated();
}
