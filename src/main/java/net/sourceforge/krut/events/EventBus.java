package net.sourceforge.krut.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventBus {

	private static final Logger logger = Logger.getLogger(EventBus.class.getName());
	private static final EventBus instance = new EventBus();

	private final Collection<Object> listeners;

	public EventBus() {
		listeners = new ArrayList<>();
	}

	public void register(Object listener) {
		listeners.add(listener);
	}

	public void fire(Object event) {
		for (Object listener : listeners) {
			for (Method m : listener.getClass().getMethods()) {
				if (!m.getName().startsWith("listen")) {
					continue;
				}

				Class<?> param = m.getParameterTypes()[0];
				if (param.isAssignableFrom(event.getClass())) {
					try {
						m.invoke(listener, event);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						logger.log(Level.SEVERE, "Event bus problems", e);
					}
				}
			}
		}
	}

	public static final EventBus get() {
		return EventBus.instance;
	}

	public static void main(String[] args) {
		EventBus.instance.register(new net.sourceforge.krut.events.LogSubscriber());
		EventBus.instance.fire(new BaseEvent("asd", "asdasd"));
	}
}
