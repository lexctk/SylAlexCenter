package fr.sorbonne_u.sylalexcenter.application.interfaces;

/**
 * The interface <code>ApplicationNotificationHandlerI</code> defines
 * the notification service used by the application component to accept notifications.
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
public interface ApplicationNotificationHandlerI {
	
	void acceptApplicationAdmissionNotification(boolean isAccepted) throws Exception;

}
