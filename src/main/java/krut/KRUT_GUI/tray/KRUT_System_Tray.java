/**
 * 
 */
package krut.KRUT_GUI.tray;

import java.awt.AWTException;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

public class KRUT_System_Tray {

	private static final int _TITLE = 13;
	private static final double scale = 1.3;

	private static final String FONT_FACE = "Verdana"; //$NON-NLS-1$
	public static final Font BOLD_FONT = new Font(FONT_FACE, Font.BOLD, _TITLE)
			.deriveFont(AffineTransform.getScaleInstance(scale, scale));

	boolean initialized = false;

	private ArrayList<TrayEventListener> listeners = new ArrayList<TrayEventListener>();

	private Image mainImage;

	private Image recordImage;

	private MenuItem rollbackOption;

	private SystemTray tray;

	private ActionListener trayActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {

			displayMessage("Krut Computer Recorder",

			"Krut was written by Jonas Östby,"
					+ "\nwith contributions by Kristoffer Berggren" + "\n"
					+ "\nThis tay was added by Cris Pruneri" + "");

		}
	};

	TrayIcon trayIcon;

	public KRUT_System_Tray() {

		if (SystemTray.isSupported()) {
			tray = SystemTray.getSystemTray();
			String pathToIcons = "images/"; //$NON-NLS-1$
			mainImage = getImage(pathToIcons + "trayIcon.gif"); //$NON-NLS-1$
			recordImage = getImage(pathToIcons + "trayIcon-pinged.gif"); //$NON-NLS-1$

			trayIcon = new TrayIcon(mainImage, "", createAllPopup()); //$NON-NLS-1$

			trayIcon.setImage(mainImage);
			initialized = true;
			trayIcon.setImageAutoSize(true);
			trayIcon.addActionListener(trayActionListener);

			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.out.println("TrayIcon could not be added."); //$NON-NLS-1$
			}

		} else {

		}

	}

	public boolean addTrayEventListener(TrayEventListener listener) {
		return listeners.add(listener);
	}

	private PopupMenu createAllPopup() {
		PopupMenu popup = new PopupMenu();
		popup.add(createMenuEntry("Start/Stop Recording",
				TrayEventType.TOGGLE_RECORDING, true));
		popup.add(createMenuEntry("Hop-Start Recording",
				TrayEventType.HOP_RECORD, true));
		popup.add(createMenuEntry("Start/Stop Timer",
				TrayEventType.START_TIMER, true));

		popup.addSeparator();
		popup.add(createMenuEntry("Snapshot", TrayEventType.SNAPSHOT, true));

		popup.addSeparator();
		popup.add(createMenuEntry("Hide Krut", TrayEventType.HIDE_GUI, true));
		popup.add(createMenuEntry("Show Krut", TrayEventType.SHOW_GUI, false));
		popup.addSeparator();
		popup.add(createMenuEntry("Exit Krut", TrayEventType.EXIT_KRUT, true));

		return popup;
	}

	private class TrayUpdater {
		private TreeMap<TrayEventType, MenuItem> map = new TreeMap<TrayEventType, MenuItem>();

		public void add(TrayEventType eventType, MenuItem menuItem) {
			map.put(eventType, menuItem);
		}

		public void setEnabled(TrayEventType trayEventType, boolean b) {
			MenuItem menuItem = map.get(trayEventType);
			if (menuItem != null)
				menuItem.setEnabled(b);
		}

	}

	public void enable(TrayEventType... eventTypes) {
		for (int i = 0; i < eventTypes.length; i++) {
			trayUpdater.setEnabled(eventTypes[i], true);
		}
	}

	public void disable(TrayEventType... eventTypes) {
		for (int i = 0; i < eventTypes.length; i++) {
			trayUpdater.setEnabled(eventTypes[i], false);
		}
	}

	TrayUpdater trayUpdater = new TrayUpdater();

	public MenuItem createMenuEntry(String label,
			final TrayEventType eventType, boolean enabled) {
		MenuItem menuItem = new MenuItem(label);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireTrayEvent(new TrayEvent(e.getSource(), eventType));
			}
		});
		menuItem.setFont(BOLD_FONT);
		menuItem.setEnabled(enabled);
		trayUpdater.add(eventType, menuItem);
		return menuItem;
	}

	public void displayMessage(final String caption, final String msg) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				trayIcon.displayMessage(caption, msg, MessageType.INFO);
			}
		}).start();
	}

	public void dispose() {
		if (trayIcon != null && SystemTray.isSupported()) {

			SystemTray tray = SystemTray.getSystemTray();
			tray.remove(trayIcon);
		}

	}

	public void enableRollBacking(boolean roll) {
		rollbackOption.setEnabled(roll);
	}

	private void fireTrayEvent(TrayEvent ev) {
		Iterator<TrayEventListener> it = listeners.iterator();
		while (it.hasNext())
			it.next().handleTrayEvent(ev);
	}

	private Image getImage(String mainIcon) {
		return Toolkit.getDefaultToolkit().createImage(
				this.getClass().getResource(mainIcon));
	}

	public MenuItem getRollbackOption() {
		return rollbackOption;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void recordIcon() {
		trayIcon.setImage(recordImage);

	}

	public synchronized void pingIcon() {
		if (mainImage.equals(trayIcon.getImage())) {
			trayIcon.setImage(recordImage);
		} else if (recordImage.equals(trayIcon.getImage())) {
			trayIcon.setImage(mainImage);
		}

	}

	public void refresh() {
		trayIcon.setPopupMenu(createAllPopup());
	}

	public boolean removeTrayEventListener(TrayEventListener listener) {
		return listeners.remove(listener);
	}

	public void idleIcon() {
		trayIcon.setImage(mainImage);
	}

	public void setTooltip(String msg) {
		trayIcon.setToolTip(msg);
	}

	public void trayPopUp(String caption, String text, MessageType messageType) {
		trayIcon.displayMessage(caption, text, messageType);
	}

	public void warn(String string) {
		trayPopUp("Krut Computer Recorder", string, MessageType.WARNING);
	}

}