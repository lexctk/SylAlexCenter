package fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces;

import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;

public interface RequestDispatcherDynamicStateI extends DataOfferedI.DataI, DataRequiredI.DataI {

	public String getRequestDispatcherURI();

	public double getExponentialAverageExecutionTime();

	public int getAvailableAVMsCount();
}
