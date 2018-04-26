package net.sourceforge.krut.events;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogSubscriber {

	private static final Logger logger = Logger.getLogger(LogSubscriber.class.getName());

	@EventListener
	public void listenAndLogBaseEvents(Object event) {
		logger.log(Level.INFO, event.toString());
	}
}
