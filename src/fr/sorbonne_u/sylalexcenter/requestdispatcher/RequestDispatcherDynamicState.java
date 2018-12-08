package fr.sorbonne_u.sylalexcenter.requestdispatcher;

import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;

public class RequestDispatcherDynamicState implements RequestDispatcherDynamicStateI {

	private final String rdURI;
	private final double exponentialAverageExecutionTime;
	private final int availableAVMsCount;

	RequestDispatcherDynamicState(
			String rdURI,
			double exponentialAverageExecutionTime,
			int availableAVMsCount) {

		super();
		this.rdURI = rdURI;
		this.exponentialAverageExecutionTime = exponentialAverageExecutionTime;
		this.availableAVMsCount = availableAVMsCount;
	}

	@Override
	public String getRequestDispatcherURI() {
		return this.rdURI;
	}

	@Override
	public double getExponentialAverageExecutionTime() {
		return this.exponentialAverageExecutionTime;
	}

	@Override
	public int getAvailableAVMsCount() {
		return this.availableAVMsCount;
	}
}
