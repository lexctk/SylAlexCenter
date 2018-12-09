package fr.sorbonne_u.datacenter;

/**
 * The class <code>TimeManagement</code> manages the relationship between the
 * simulated time and the real time of the underlying operating system.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * It allow to perform simulations in accelerated time compared to the real
 * time.
 * 
 * <p>
 * <strong>Invariant</strong>
 * </p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>
 * Created on : October 18, 2016
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class TimeManagement {
	// ------------------------------------------------------------------------
	// Constants and instance variables
	// ------------------------------------------------------------------------

	private static double ACCELERATION_FACTOR = 1.0;
	private static long START_TIME;

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	public static void setACCELERATION_FACTOR(double accelerationFactor) {
		ACCELERATION_FACTOR = accelerationFactor;
	}

	public static long getSTART_TIME() {
		return START_TIME;
	}

	public static void setSTART_TIME(long startTime) {
		START_TIME = startTime;
	}

	public static long acceleratedDelay(long realDelay) {
		return (long) (realDelay / ACCELERATION_FACTOR);
	}

	public static long realDelay(long acceleratedDelay) {
		return (long) (acceleratedDelay * ACCELERATION_FACTOR);
	}

	public static long acceleratedTime(long realTime) {
		return START_TIME + (long) ((realTime - START_TIME) / ACCELERATION_FACTOR);
	}

	public static long currentTime() {
		return TimeManagement.realTime(System.currentTimeMillis());
	}

	private static long realTime(long acceleratedTime) {
		return START_TIME + (long) ((acceleratedTime - START_TIME) * ACCELERATION_FACTOR);
	}

	public static long timeStamp() {
		return TimeManagement.realTime(System.currentTimeMillis());
	}
}
