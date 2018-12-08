package fr.sorbonne_u.sylalexcenter.requestdispatcher.interfaces;

public interface RequestDispatcherStateDataConsumerI {
	void acceptRequestDispatcherDynamicData (String rdURI, RequestDispatcherDynamicStateI currentDynamicState) throws Exception;
}
