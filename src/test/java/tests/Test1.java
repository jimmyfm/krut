package tests;

import krut.KRUT_GUI.KrutSettings;

public class Test1 {

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new KrutSettings(new java.awt.Rectangle(0, 0, 360, 240), 15,
						15, 50, false, false, 22050).setVisible(true);
			}
		});
	}
}
