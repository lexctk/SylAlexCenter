package fr.sorbonne_u.sylalexcenter.performancecontroller.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.sylalexcenter.performancecontroller.interfaces.PerformanceControllerManagementI;

public class PerformanceControllerManagementConnector extends AbstractConnector implements PerformanceControllerManagementI {

	@Override
	public void doConnectionWithRequestDispatcherForDynamicState(String requestDispatcherDynamicStateInboundPortUri) throws Exception {

		((PerformanceControllerManagementI)this.offering).
				doConnectionWithRequestDispatcherForDynamicState(requestDispatcherDynamicStateInboundPortUri);
	}
}
