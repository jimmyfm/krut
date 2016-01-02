package krut.memory.gui;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

class ComponentInterrogator implements ComponentListener {
	
	private String componentID;  
	
	/**
	 * 
	 */
	private final GUIMemory guiMemory;

	private void process(ComponentEvent e) {
		this.guiMemory.update(componentID, e.getComponent()); 
	}

	public ComponentInterrogator(String componentID, GUIMemory guiMemory) {
		super();
		this.componentID = componentID;
		this.guiMemory = guiMemory;
	}

	@Override
	public void componentResized(ComponentEvent e) {
		process(e);
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		process(e);
	}

	@Override
	public void componentShown(ComponentEvent e) {
		process(e);
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		process(e);
	}

	public GUIMemory getGuiMemory() {
		return guiMemory;
	}
}