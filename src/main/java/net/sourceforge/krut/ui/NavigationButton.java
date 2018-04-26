package net.sourceforge.krut.ui;

import java.awt.event.ActionListener;
import java.net.URL;
import java.util.function.Supplier;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import net.sourceforge.krut.events.BaseEvent;
import net.sourceforge.krut.events.EventBus;

public class NavigationButton extends JButton {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a navigation button of the specified appearance, and returns it.
	 *
	 * @param imageName
	 *            A String representation of the URL to an image that should be
	 *            displayed on this button.
	 * @param actionCommand
	 *            The action command for this button. This command is listened for
	 *            in the actionPerformed(ActionEvent e) method, to determine which
	 *            button was pressed.
	 * @param toolTipText
	 *            The tooltip text for this button.
	 * @param altText
	 *            The text that should be typed on this button, if any.
	 * @param listener
	 *
	 * @return A JButton according to the specifications given in the parameters.
	 */
	public NavigationButton(String imageName, String actionCommand, String toolTipText, String altText) {
		/** Attempt to locate the image */
		URL imageURL = getClass().getResource(imageName);

		/** Create and initialize the button. */
		setActionCommand(actionCommand);
		setToolTipText(toolTipText);

		/** Add the image if it was succesfully located. */
		if (imageURL != null) {
			setIcon(new ImageIcon(imageURL, altText));
		} else {
			System.err.println("Resource not found: " + imageName);
		}

		/**
		 * Set a text on the button. If there is no String or an empty String in the
		 * parameter altText no text will appear on the button.
		 */
		setText(altText);
	}

	public NavigationButton(String imageName, String actionCommand, String toolTipText, String altText, Supplier<Object> event) {
		this(imageName, actionCommand, toolTipText, altText);
		addActionListener(evt -> {
			EventBus.get().fire(event.get());
		});
	}

	public NavigationButton(String imageName, String actionCommand, String toolTipText, String altText, ActionListener listener) {
		this(imageName, actionCommand, toolTipText, altText);
		addActionListener(evt -> EventBus.get().fire(new BaseEvent(NavigationButton.this, actionCommand)));
		addActionListener(listener);
	}
}