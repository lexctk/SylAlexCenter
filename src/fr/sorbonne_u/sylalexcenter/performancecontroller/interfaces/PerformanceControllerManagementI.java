package fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces;

import java.util.ArrayList;

public interface PerformanceControllerManagementI {

	void doConnectionWithRequestDispatcherForDynamicState(String requestDispatcherDynamicStateInboundPortUri) throws Exception;

	void doConnectionWithComputerForDynamicState(ArrayList<String> computerDynamicStateInboundPortUri) throws Exception;
}
