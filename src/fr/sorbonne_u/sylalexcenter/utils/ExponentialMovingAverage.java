package fr.sorbonne_u.sylalexcenter.utils;

/**
 * Exponential moving Average
 * EMA [today] = (Price [today] x K) + (EMA [yesterday] x (1 – K))
 *
 * where
 *
 * K = 2 ÷(N + 1)
 * N = number of values
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public class ExponentialMovingAverage {

	private long n;
	private double k;
	private double previousEMA;

	public ExponentialMovingAverage() {
		this.n = 0;
		this.k = 0;
		this.previousEMA = 0;
	}

	public double getNextAverage(Long nextValue) {
		this.n++;
		this.k = 2/(double)(n + 1);

		this.previousEMA = nextValue * this.k + this.previousEMA * (1 - k);

		return this.previousEMA;
	}
}
