package net.sourceforge.krut.events;

public class BaseEvent {

	private final Object source;
	private final String actionCommand;

	public BaseEvent(Object source, String actionCommand) {
		this.source = source;
		this.actionCommand = actionCommand;
	}

	public Object getSource() {
		return source;
	}

	public String getActionCommand() {
		return actionCommand;
	}

	@Override
	public String toString() {
		return String.format("(%s) %s", source, actionCommand);
	}

}
