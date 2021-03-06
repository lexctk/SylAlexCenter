package fr.sorbonne_u.sylalexcenter.requestdispatcher;

import fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;

public class RequestDispatcherDynamicState implements RequestDispatcherDynamicStateI {

	private final String rdURI;

	private final int availableAVMsCount;
	private final double exponentialAverageExecutionTime;
	private final int totalRequestSubmitted;
	private final int totalRequestTerminated;

	RequestDispatcherDynamicState(
			String rdURI,
			double exponentialAverageExecutionTime,
			int availableAVMsCount,
			int totalRequestSubmitted,
			int totalRequestTerminated) {

		super();
		this.rdURI = rdURI;
		this.exponentialAverageExecutionTime = exponentialAverageExecutionTime;
		this.availableAVMsCount = availableAVMsCount;
		this.totalRequestSubmitted = totalRequestSubmitted;
		this.totalRequestTerminated = totalRequestTerminated;
	}

	@Override
	public double getExponentialAverageExecutionTime() {
		return this.exponentialAverageExecutionTime;
	}

	@Override
	public int getAvailableAVMsCount() {
		return this.availableAVMsCount;
	}

	@Override
	public int getTotalRequestSubmitted() {
		return this.totalRequestSubmitted;
	}

	@Override
	public int getTotalRequestTerminated() {
		return this.totalRequestTerminated;
	}
}
