package fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces;

/**
 * The interface <code>RequestDispatcherStateDataConsumerI</code> defines methods
 * for component subscribed to request dispatcher dynamic data to accept data
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public interface RequestDispatcherStateDataConsumerI {
	void acceptRequestDispatcherDynamicData (String rdURI, RequestDispatcherDynamicStateI currentDynamicState) throws Exception;
}
