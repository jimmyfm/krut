/*
 * OutputText.java
 *
 * Created on den 29 december 2004, 23:11
 */

package krut.KRUT_GUI.alerts;

/**
 *
 * @author  jonte
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * This class is a very simple output window. The out() method is used to print
 * lines of text.
 */
public class TimerAlert {

	/**
	 * The text area where the output is printed. This parameter is public in
	 * case there are parts of the program remaining that still print directly
	 * by adding to this output. Also, to print in that way would give more
	 * flexibility that the out() method offers.
	 */
	private JTextArea output;
	/**
	 * The frame for this window. This is public becaise Run_KRUT uses the
	 * setVisible() method of this class.
	 */
	private JFrame frame;
	private Font baseFont;
	private double currentMagnification = 1;
	private boolean colorsAreStraight=false;

	/**
	 * The scroll pane for this window.
	 */

	/**
	 * Just gives the content pane for this frame.
	 * 
	 * @return A Container containing the ContentPane for outFrame.
	 */
	protected Container createContentPane() {
		/** Create the content-pane-to-be. */
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);

		/** Create a scrolled text area. */
		output = new JTextArea(5, 30);

		output.setEditable(false);
		this.baseFont = output.getFont();

		/** Add the text area to the content pane. */
		contentPane.add(output, BorderLayout.CENTER);

		return contentPane;
	}

	/**
	 * Initiate the output window. This method will set inited = true when the
	 * output window is initiated and ready to accept output.
	 * 
	 * @param xPos
	 *            The x position on the screen where the window should appear.
	 * @param yPos
	 *            The y position on the screen where the window should appear.
	 */
	public void init() {
		JFrame.setDefaultLookAndFeelDecorated(true);

		/** Create and set up the window. */
		frame = new JFrame("Krut Timer Alert");
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		frame.setContentPane(this.createContentPane());

		/** Display the window. */
		frame.setSize(600, 260);
		output.setSize(550, 200);
		setColors(true);
		frame.setVisible(false);
		/**
		 * In case there are any threads waiting for this window to be
		 * initiated, they are now woken.
		 */
		centerOnMainScreen();
	}

	public void setColors(boolean straight_reverse) {
		Color backGround = Color.yellow;
		Color foreground = new Color(80, 125, 80);

		if (colorsAreStraight != straight_reverse) {

			if (straight_reverse) {

				output.setBackground(backGround);
				output.setForeground(foreground);

			} else {
				output.setBackground(foreground);
				output.setForeground(backGround);
			}
			colorsAreStraight = straight_reverse;
		}

	}

	protected void centerOnMainScreen() {
		Dimension dim = frame.getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();

		frame.setLocation((int) ((width - dim.getWidth()) / 2),
				(int) ((height - dim.getHeight()) / 2));
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setText(String string, double magnification) {
		if (magnification != 0 && this.currentMagnification != magnification) {
			output.setFont(baseFont.deriveFont(AffineTransform
					.getScaleInstance(magnification, magnification)));
			this.currentMagnification = magnification;
		}
		setText(string);
	}

	public void setText(String string) {
		output.setText(string);
	}

	public void setVisible(boolean b, final int milliSeconds) {
		setVisible(b);

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(milliSeconds);
					setVisible(false);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	public boolean isVisible() {
		return frame.isVisible();
	}

	public void setVisible(boolean b) {
		frame.setVisible(b);
	}

}
