/*
 * KrutTimer.java
 *
 * Created on 17. maj 2007, 14:57
 */

package krut.KRUT_GUI;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import krut.KRUT_GUI.alerts.TimerAlert;
import krut.memory.config.Configuration;
import krut.memory.gui.GUIMemory;

/**
 *
 * @author  Jonas
 */
/**
 * A class used as a timer to start the Krut program. The KrutTimer is a JPanel
 * that when displayed sits below the main window of the Krut program. It can be
 * used to program Krut to start and stop recording at given offset times, or at
 * given system calendar clock times.
 */
public class KrutTimer extends javax.swing.JPanel {

	/**
	 * This class is used to deliver our DoubleDigitFormatter, whenever someone
	 * asks the JFormattedTextFields of the spinners for their
	 * AbstractFormatter.
	 */
	private class DoubleDigitFactory extends
			javax.swing.JFormattedTextField.AbstractFormatterFactory {
		/**
		 * Deliver the DoubleDigitFormatter
		 * 
		 * @param tf
		 *            A JFormattedTextField to which this DoubleDigitFactory is
		 *            attached.
		 * 
		 * @return A new DoubleDigitFormatter object.
		 */
		public DoubleDigitFormatter getFormatter(
				javax.swing.JFormattedTextField tf) {
			return new DoubleDigitFormatter();
		}
	}

	/**
	 * This is a class we use to make sure the numbers displayed in the
	 * JFormattedTextFields for the spinners are always two digit numbers, even
	 * if their values are below 10. The values of the text fields are never
	 * changed, just the way they are displayed.
	 * 
	 * @return A String representation of the value in the JFormattedTextField.
	 */
	private class DoubleDigitFormatter extends
			javax.swing.JFormattedTextField.AbstractFormatter {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Give the integer value of the String read from the
		 * JFormattedTextField to which this DoubleDigitFormatter is attached.
		 * 
		 * @param text
		 *            The String read from the JFormattedTextField.
		 * 
		 * @return An Integer representing the value of the input parameter
		 *         text, or 0 if there is no such Integer representation.
		 */
		public Integer stringToValue(String text) {
			Integer returnInt = 0;
			try {
				returnInt = Integer.parseInt(text);
			} catch (Exception e) {
				System.out
						.println("Error in KrutTimer.DoubleDigitFormatter.stringToValue: ");
				System.out.println(e);
				/** If the user made an invalid edit, this will change it to 0. */
				getFormattedTextField().setValue(0);
			}
			return returnInt;
		}

		/**
		 * Give a String representation of the value of the JFormattedTextField
		 * to which this DoubleDigitFormatter is attached.
		 * 
		 * @param value
		 *            The Object which represents the value of the
		 *            JFormattedTextField. This will always be an integer, since
		 *            we only use SpinnerNumberModels for the spinners.
		 * 
		 * @return A String representation of the value. If the value is not a
		 *         valid integer, "00" is returned.
		 */
		public String valueToString(Object value) {
			String returnString = "00";
			try {
				int val = (Integer) value;
				/** First we must check that we have a valid number. */
				if (val < 0) {
				}
				/** Add a zero if it is a one-digit number. */
				else if (val < 10) {
					returnString = "0" + val;
				} else {
					returnString = "" + val;
				}
			} catch (Exception e) {
				System.out.println(e);
			}
			return returnString;
		}
	}

	/**
	 * A class used to limit the highest number that can be written in the
	 * JFormattedTextField beloning to a JSpinner to 59. If a higher number is
	 * entered, the spinner will start over from 0, and its parent will add 1 to
	 * it's value. The parent must be set using the setParent method before
	 * using an object of this class.
	 */
	private class TimeModel extends javax.swing.SpinnerNumberModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/**
		 * The JSpinner which is parent to the JSpinner that this TimeModel
		 * belongs to. Eg. if this is the TimeModel for the recS (recording
		 * seconds) spinner, the parent would be the recM (recording minutes)
		 * spinner.
		 */
		private javax.swing.JSpinner myParentSpinner;

		/**
		 * Constructor for TimeModel. See SpinnerNumberModel for details.
		 * 
		 * @param value
		 *            The current (non null) value of the component.
		 * @param min
		 *            The first number in the sequence, or null.
		 * @param max
		 *            The last number in the sequence, or null.
		 * @param stepSize
		 *            The difference between elements of the sequence.
		 */
		@SuppressWarnings("rawtypes")
		TimeModel(Number value, Comparable min, Comparable max, Number stepSize) {
			super(value, min, max, stepSize);
		}

		/**
		 * A TimeModel needs to know the JSpinner which is parent to the
		 * JSpinner the TimeModel belongs to. Eg. if this is the TimeModel for
		 * the recS (recording seconds) spinner, the parent would be the recM
		 * (recording minutes) spinner.
		 * 
		 * This is the method used to set this parent.
		 * 
		 * @param parent
		 *            The parent JSpinner to the JSpinner that this TimeModel
		 *            belongs to.
		 */
		public void setParent(javax.swing.JSpinner parent) {
			myParentSpinner = parent;
		}

		/**
		 * Set the value of the JSpinner that this TimeModel belongs to. Since
		 * this is a SpinnerNumberModel, the value should be an integer number.
		 * 
		 * @param value
		 *            The integer number that should be used as the value of the
		 *            JSpinner that this TimeModel belongs to.
		 */
		public void setValue(Object value) {

			/**
			 * Used to store the value of the input parameter, converted to an
			 * integer.
			 */
			int val = 0;

			/**
			 * Used to keep track of how much we should increase the parent when
			 * we are done.
			 */
			int parentIncrease = 0;

			/**
			 * See description of bug at the end of this method.
			 */
			boolean unclearBugFix = false;

			/** Convert value to an int. */
			try {
				val = (Integer) value;
			} catch (Exception e) {
				System.out.println("Error in KrutTimer.TimeModel.setValue");
				System.out.println(e);
			}

			/**
			 * Make sure that the value is below 60. Increase the parent
			 * JSpinner if the value was 60 or above.
			 */
			while (60 <= val) {
				val -= 60;
				parentIncrease++;
				unclearBugFix = true;
			}

			/**
			 * If we have decreased the value below zero, we should decrease
			 * parent by one, and start over at 59. This part is horribly ugly,
			 * and should be replaced with different SpinnerNumberModels for
			 * hours, minutes, and seconds.
			 */
			if (val == -1) {

				/**
				 * If the parent spinner is at its lowest value, we should just
				 * stay at 0.
				 */
				if (myParentSpinner.getPreviousValue() == null) {
					val = 0;
				} else {
					/**
					 * Get an integer representation of the parents previous
					 * value.
					 */
					int tempVal = (Integer) myParentSpinner.getPreviousValue();
					if (tempVal == -1) {
						/**
						 * The only scenario that can go wrong, is if we are in
						 * the field for seconds and the time is 00:00:00, and
						 * we try to decrease the seconds. This is a bugfix for
						 * that case. We try to change the minutes down, and see
						 * what happend. If they change to 59, then we change
						 * the seconds as well. If the minutes stay at 00, then
						 * the seconds stay as well.
						 */
						myParentSpinner.setValue(myParentSpinner
								.getPreviousValue());
						/**
						 * Get an integer representation of the parents value.
						 */
						int testMin = (Integer) myParentSpinner.getValue();
						if (testMin == 0) {
							val = 0;
						} else {
							val = 59;
						}
					} else {
						/**
						 * If we get this far, the parents previous value is not
						 * -1, and not null, which means that the parent has a
						 * value of over 0, which means we should decrease it,
						 * and change the current field to 59.
						 */
						val = 59;
						myParentSpinner.setValue(myParentSpinner
								.getPreviousValue());
					}
				}
			}

			/** Set the value of this field. */
			super.setValue(val);

			/** Increase the parent if necessary. */
			if (0 < parentIncrease) {
				int parentVal = (Integer) myParentSpinner.getValue();
				myParentSpinner.setValue(parentVal + parentIncrease);
			}

			/**
			 * It seems that some values when inputted in the text fields by the
			 * user, does not change the way they should after the proper
			 * HH:MM:SS value has been calculated. It seems to occur for some
			 * values that are even multiples of 300 more than the final value
			 * that should end up in the text field of the spinner. For those
			 * cases, the two digits that will be the final text in window will
			 * be the same as the last two digits of the larger number which was
			 * originally there, which is a probable cause of the problem. Ex:
			 * If the first value put in by the user after the window is opened
			 * is 300 in the recS field, the minutes will update to 5, but the
			 * seconds have repeatedly been found to remain at 300, instead of
			 * 00. This was found with the default windows XP look and feel.
			 * Calling fireStateChanged() seems to fix this issue.
			 */
			if (unclearBugFix)
				fireStateChanged();
		}
	}

	/** The countdown mode for the KrutTimer. */
	public final static int COUNTDOWN = 0;

	/** The countup mode for the KrutTimer. */
	public final static int COUNTUP = 1;

	/** The not active state for the KrutTimer. */
	public final static int NOT_ACTIVE = 2;

	/** The recording state for the KrutTimer. */
	public final static int RECORDING = 4;
	/**
	 * 
	 */
	private static final long serialVersionUID = 5583399516727643979L;
	/** The waiting state for the KrutTimer. */
	public final static int WAITING = 3;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.ButtonGroup buttonGroup1;
	private javax.swing.JButton jButton_Close;
	private JButton jButtonHopRec;

	private javax.swing.JCheckBox jCheckBox_StopIn;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JRadioButton jRadioButton_ActualTime;
	private javax.swing.JRadioButton jRadioButton_RelativeTime;

	private javax.swing.JScrollPane jScrollPane1;

	private javax.swing.JSeparator jSeparator1;

	private javax.swing.JTextField jTextField1;

	private javax.swing.JToggleButton jToggleButton_ActivateTimer;

	/**
	 * The mode of the KrutTimer. This can be either COUNTDOWN or COUNTUP. In
	 * the countdown mode, the KrutTimer counts down from a given time, and in
	 * the countup mode, the KrutTimer counts up towards a given calendar time.
	 * The mode can be read through getMode().
	 */
	private int mode = COUNTDOWN;
	/**
	 * This should contain the JFrame in which the KrutTimer is drawn. The
	 * JButton1ActionPerformed method will call that object and tell it to
	 * re-pack. This parameter is set in the setMainGUI method or the
	 * constructor.
	 */
	private javax.swing.JFrame myContainer;
	/**
	 * The output window of this KrutTimer. Should be set through the setOutput
	 * method.
	 */
	private krut.KRUT_GUI.OutputText myOutput;
	private javax.swing.JSpinner recH;
	/**
	 * When the user changes the type of time to use in the KrutTimer from
	 * actual time to relative time, the old start-time fields are switched with
	 * those in recHMemory, recMMemory, and recSMemory. The switch is done in
	 * the restoreTime method.
	 */
	private int recHMemory = 0;
	/** The SpinnerNumberModel used for the recH spinner. */
	private javax.swing.SpinnerNumberModel recHModel = new javax.swing.SpinnerNumberModel(
			0, 0, null, 1);

	private javax.swing.JSpinner recM;

	/**
	 * When the user changes the type of time to use in the KrutTimer from
	 * actual time to relative time, the old start-time fields are switched with
	 * those in recHMemory, recMMemory, and recSMemory. The switch is done in
	 * the restoreTime method.
	 */
	private int recMMemory = 0;

	/** The TimeModel used for the recM spinner. */
	private TimeModel recMModel = new TimeModel(0, -1, null, 1);

	private javax.swing.JSpinner recS;

	/**
	 * When the user changes the type of time to use in the KrutTimer from
	 * actual time to relative time, the old start-time fields are switched with
	 * those in recHMemory, recMMemory, and recSMemory. The switch is done in
	 * the restoreTime method.
	 */
	private int recSMemory = 0;

	/** The TimeModel used for the recS spinner. */
	private TimeModel recSModel = new TimeModel(0, -1, null, 1);

	/**
	 * When the timer is activated, startRecSeconds contains the programmed
	 * starting time for recording, counted in seconds from last midnight.
	 * startRecSeconds is set in the startTimer method. In the tickTimer method,
	 * the current calendar time is compared to startRecSeconds to see if
	 * recording should start.
	 */
	private int startRecSeconds;

	/**
	 * The state of the KrutTimer. This can be NOT_ACTIVE, WAITING, or
	 * RECORDING. When the KrutTimer is not active, it waits for the user to
	 * activate it through the "Activate Timer" JToggleButton. When the
	 * KrutTimer is waiting, it waits for the time to reach the given start time
	 * for recording. When the KrutTimer is recording, it waits for the
	 * KrutTimer to reach the given stop timer for recording, if there is one.
	 * The state can be read through getState().
	 */
	private int state = NOT_ACTIVE;

	private TimerAlert timerAlertFrame;

	private javax.swing.JSpinner stopH;

	/**
	 * When the user changes the type of time to use in the KrutTimer from
	 * actual time to relative time, the old stop-time fields are switched with
	 * those in stopHMemory, stopMMemory, and stopSMemory. The switch is done in
	 * the restoreTime method.
	 */
	private int stopHMemory = 0;

	/** The SpinnerNumberModel used for the stopH spinner. */
	private javax.swing.SpinnerNumberModel stopHModel = new javax.swing.SpinnerNumberModel(
			0, 0, null, 1);
	private javax.swing.JSpinner stopM;
	private javax.swing.JSpinner hopM;

	/**
	 * When the user changes the type of time to use in the KrutTimer from
	 * actual time to relative time, the old stop-time fields are switched with
	 * those in stopHMemory, stopMMemory, and stopSMemory. The switch is done in
	 * the restoreTime method.
	 */
	private int stopMMemory = 0;

	/** The TimeModel used for the stopM spinner. */
	private TimeModel stopMModel = new TimeModel(0, -1, null, 1);

	/** The TimeModel used for the hopM spinner. */
	private TimeModel hopMModel = new TimeModel(0, -1, null, 1);

	/**
	 * When the timer is activated, stopRecSeconds contains the programmed
	 * stopping time for recording, counted in seconds from last midnight.
	 * stopRecSeconds is set in the startTimer method. In the tickTimer method,
	 * the current calendar time is compared to stopRecSeconds to see if
	 * recording should stop.
	 */
	private int stopRecSeconds;

	private javax.swing.JSpinner stopS;
	// End of variables declaration//GEN-END:variables

	/**
	 * When the user changes the type of time to use in the KrutTimer from
	 * actual time to relative time, the old stop-time fields are switched with
	 * those in stopHMemory, stopMMemory, and stopSMemory. The switch is done in
	 * the restoreTime method.
	 */
	private int stopSMemory = 0;

	/** The TimeModel used for the stopS spinner. */
	private TimeModel stopSModel = new TimeModel(0, -1, null, 1);

	/** Timer used to count down to recording start/stop. */
	public javax.swing.Timer timer;

	/**
	 * Creates new KrutTimer.
	 * 
	 * @param GUI
	 *            The JFrame that this KrutTimer is being drawn in.
	 */
	public KrutTimer(javax.swing.JFrame GUI) {
		myContainer = GUI;
		initComponents();
		initSpinners();
		/** Initialize a timer that updates once per second. */
		initTimer(1000);
		jButton_Close
				.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		jCheckBox_StopIn.setFont(jTextField1.getFont());
	}

	/**
	 * Controls that the total amount of hours since last midnight is not more
	 * than 24. Subtracts an integer multiple of 24h until a proper time is
	 * reached, and resets the time field. This is done for both start and stop
	 * times.
	 */
	private void checkMidnight() {
		/** Get the start and stop times in seconds */
		int rs = getRecSeconds();
		int ss = getStopSeconds();
		boolean rec = false, stop = false;
		/** Are they over 24h? */
		while (24 <= getHours(rs)) {
			rs -= 24 * 3600;
			rec = true;
		}
		while (24 <= getHours(ss)) {
			ss -= 24 * 3600;
			stop = true;
		}
		/** Correct if they are. */
		if (rec)
			setRecFields(rs);
		if (stop)
			setStopFields(ss);
	}

	/**
	 * Checks if the time given in the stop-time fields is lower than the one in
	 * the rec-time fields. Also check jCheckBox1 before returning an answer.
	 * 
	 * @return true if the time in the rec-time fields is higher than the one in
	 *         the stop-time fields, or if jCheckBox1 is not selected. false if
	 *         the time in the rec-time fields is lower than the one in the
	 *         stop-time fields and jCheckBox1 is selected.
	 */
	private boolean checkTimes() {
		/**
		 * The only combination that will give false here is: box selected +
		 * stop < rec
		 */
		return (!jCheckBox_StopIn.isSelected() || (getRecSeconds() <= getStopSeconds()));
	}

	private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed

		setVisible(false);
		if (myContainer != null) {
			myContainer.pack();
		}
	}// GEN-LAST:event_jButton1ActionPerformed

	/**
	 * Disables the "Activate Timer" togglebutton.
	 */
	public void disableToggleButton() {
		jToggleButton_ActivateTimer.setEnabled(false);
		jToggleButton_ActivateTimer
				.setToolTipText("<HTML>The Start time must be more than 0 seconds away<BR>"
						+ "for this button to activate.</HTML>");
		jButtonHopRec.setEnabled(true);
		hopM.setEnabled(true);

	}

	/**
	 * Sets the enabled state of selected components in the KrutTimer. This is
	 * used to disable some components while the timer is running.
	 * 
	 * @param enabled
	 *            The enabled state of the compontents.
	 */
	private void enableComponents(boolean enabled) {
		jRadioButton_RelativeTime.setEnabled(enabled);
		jRadioButton_ActualTime.setEnabled(enabled);
		jCheckBox_StopIn.setEnabled(enabled);
		recH.setEnabled(enabled);
		recM.setEnabled(enabled);
		recS.setEnabled(enabled);
		if (jCheckBox_StopIn.isSelected()) {
			stopH.setEnabled(enabled);
			stopM.setEnabled(enabled);
			stopS.setEnabled(enabled);
		}
	}

	/**
	 * Enables the "Activate Timer" togglebutton.
	 */
	public void enableToggleButton() {
		jToggleButton_ActivateTimer.setEnabled(true);
		jToggleButton_ActivateTimer.setToolTipText("");
	}

	/**
	 * Get the value of the current calendar time, expressed in seconds.
	 * 
	 * @return The total amount of seconds since last midnight.
	 */
	private int getCalendarSeconds() {
		java.util.GregorianCalendar currentTime = new java.util.GregorianCalendar();
		int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
		int currentMinute = currentTime.get(Calendar.MINUTE);
		int currentSecond = currentTime.get(Calendar.SECOND);
		return getSeconds(currentHour, currentMinute, currentSecond);
	}

	/**
	 * Returns the number of whole hours in a time given in seconds.
	 * 
	 * @param seconds
	 *            The total number of seconds
	 * 
	 * @return The total number of whole hours.
	 */
	public int getHours(int seconds) {
		return (seconds / 3600);
	}

	/**
	 * Returns the number of whole minutes in a time given in seconds.
	 * 
	 * @param seconds
	 *            The total number of seconds
	 * 
	 * @return The total number of whole minutes.
	 */
	public int getMinutes(int seconds) {
		return (seconds / 60);
	}

	/**
	 * Returns the mode of the timer, either COUNTDOWN or COUNTUP.
	 * 
	 * @return The mode of the timer.
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * Get the value of the time entered into the record time fields, expressed
	 * in seconds.
	 * 
	 * return The total amount of seconds entered into the record time fields.
	 */
	private int getRecSeconds() {
		int recHour = (Integer) recH.getValue();
		int recMinute = (Integer) recM.getValue();
		int recSecond = (Integer) recS.getValue();
		return getSeconds(recHour, recMinute, recSecond);
	}

	/**
	 * Gives the number of seconds in the time given in the input parameters.
	 * 
	 * @param hours
	 *            The number of hours
	 * @param minutes
	 *            The number of minutes
	 * @param seconds
	 *            The number of seconds
	 * 
	 * @return The total number of seconds.
	 */
	public int getSeconds(int hours, int minutes, int seconds) {
		return seconds + 60 * minutes + 3600 * hours;
	}

	/**
	 * Returns the state of the timer, either NOT_ACTIVE, RECORDING or WAITING.
	 * 
	 * @return The state of the timer.
	 */
	public int getState() {
		return state;
	}

	/**
	 * Get the value of the time entered into the stop time fields, expressed in
	 * seconds.
	 * 
	 * @return The total amount of seconds entered into the stop time fields.
	 */
	private int getStopSeconds() {
		int stopHour = (Integer) stopH.getValue();
		int stopMinute = (Integer) stopM.getValue();
		int stopSecond = (Integer) stopS.getValue();
		return getSeconds(stopHour, stopMinute, stopSecond);
	}

	/**
	 * This method returns the toggle button that activates the timer, so that
	 * the user can decide what to do when the timer is activated. The following
	 * ActionEvents will be fired to all ActionListeners on the button:
	 * 
	 * Action: Action name: Timer activated "Timer active" Recording started
	 * "Timer recording" Recording stopped "Timer stopped"
	 * 
	 * @return the JToggleButton that activates the timer.
	 */
	public javax.swing.JToggleButton getTimerButton() {
		return jToggleButton_ActivateTimer;
	}

	// </editor-fold>//GEN-END:initComponents

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	// desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		buttonGroup1 = new javax.swing.ButtonGroup();
		jScrollPane1 = new javax.swing.JScrollPane();
		jPanel3 = new javax.swing.JPanel();
		jPanel1 = new javax.swing.JPanel();
		jRadioButton_RelativeTime = new javax.swing.JRadioButton();
		jRadioButton_ActualTime = new javax.swing.JRadioButton();
		jPanel2 = new javax.swing.JPanel();
		jTextField1 = new javax.swing.JTextField();

		recH = new javax.swing.JSpinner();
		recS = new javax.swing.JSpinner();
		recM = new javax.swing.JSpinner();

		stopH = new javax.swing.JSpinner();
		stopS = new javax.swing.JSpinner();
		stopM = new javax.swing.JSpinner();

		hopM = new javax.swing.JSpinner();

		jCheckBox_StopIn = new javax.swing.JCheckBox();
		jToggleButton_ActivateTimer = new javax.swing.JToggleButton();
		jButton_Close = new javax.swing.JButton();
		jSeparator1 = new javax.swing.JSeparator();

		setLayout(new java.awt.GridBagLayout());

		jScrollPane1.setBorder(null);
		jPanel3.setLayout(new java.awt.GridBagLayout());

		jPanel1.setLayout(new java.awt.GridBagLayout());

		jPanel1.setBorder(new javax.swing.border.TitledBorder(null, "Use:"));
		buttonGroup1.add(jRadioButton_RelativeTime);

		jRadioButton_RelativeTime.setText("Relative time");
		jRadioButton_RelativeTime
				.setToolTipText("<HTML>Start and stop recording at the given times  (HH:MM:SS),<BR>\ncounted from when the \"Activate Timer\"-button is pressed</HTML>");
		jRadioButton_RelativeTime
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jRadioButton_RelativeTime_ActionPerformed(evt);
					}
				});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		jPanel1.add(jRadioButton_RelativeTime, gridBagConstraints);

		buttonGroup1.add(jRadioButton_ActualTime);
		jRadioButton_ActualTime.setText("Actual time");
		jRadioButton_ActualTime
				.setToolTipText("<HTML>Start and stop recording when the calendar clock of<BR>your system reaches the given times (HH:MM:SS)</HTML>");
		jRadioButton_ActualTime
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jRadioButton_ActualTime_ActionPerformed(evt);
					}
				});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		jPanel1.add(jRadioButton_ActualTime, gridBagConstraints);

		addHopButtonAndSpinner();

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 0.5;
		jPanel3.add(jPanel1, gridBagConstraints);

		jPanel2.setLayout(new java.awt.GridBagLayout());

		jPanel2.setBorder(new javax.swing.border.TitledBorder(null,
				"Recording timer"));
		jTextField1.setEditable(false);
		jTextField1.setText("Start in:");
		jTextField1.setBorder(null);
		jTextField1.setPreferredSize(new java.awt.Dimension(50, 14));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		jPanel2.add(jTextField1, gridBagConstraints);

		recH.setToolTipText("Hours");
		recH.setPreferredSize(new java.awt.Dimension(35, 18));
		recH.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				recHStateChanged(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		jPanel2.add(recH, gridBagConstraints);

		recS.setToolTipText("Seconds");
		recS.setPreferredSize(new java.awt.Dimension(32, 18));
		recS.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				recSStateChanged(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.weighty = 0.5;
		jPanel2.add(recS, gridBagConstraints);

		recM.setToolTipText("Minutes");
		recM.setPreferredSize(new java.awt.Dimension(32, 18));
		recM.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				recMStateChanged(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.weighty = 0.5;
		jPanel2.add(recM, gridBagConstraints);

		stopH.setToolTipText("Hours");
		stopH.setEnabled(false);
		stopH.setPreferredSize(new java.awt.Dimension(35, 18));
		stopH.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				stopHStateChanged(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		jPanel2.add(stopH, gridBagConstraints);

		stopS.setToolTipText("Seconds");
		stopS.setEnabled(false);
		stopS.setPreferredSize(new java.awt.Dimension(32, 18));
		stopS.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				stopSStateChanged(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.weighty = 0.5;
		jPanel2.add(stopS, gridBagConstraints);

		stopM.setToolTipText("Minutes");
		stopM.setEnabled(false);
		stopM.setPreferredSize(new java.awt.Dimension(32, 18));
		stopM.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				stopMStateChanged(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.weighty = 0.5;
		jPanel2.add(stopM, gridBagConstraints);

		jCheckBox_StopIn.setText("Stop in:");
		jCheckBox_StopIn
				.setToolTipText("<HTML>Check box to enable stop-time.<BR>\nIf this box is unchecked, recording will not stop automatically.</HTML>");
		jCheckBox_StopIn.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				jCheckBox1ItemStateChanged(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		jPanel2.add(jCheckBox_StopIn, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 0.1;
		jPanel3.add(jPanel2, gridBagConstraints);

		jToggleButton_ActivateTimer.setText("Activate Timer");
		jToggleButton_ActivateTimer
				.setToolTipText("<HTML>The Start time must be more than 0 seconds away<BR>for this button to activate.</HTML>");
		jToggleButton_ActivateTimer.setActionCommand("Timer active");
		jToggleButton_ActivateTimer.setEnabled(false);
		jToggleButton_ActivateTimer
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jToggleButton1ActionPerformed(evt);
					}
				});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 0.1;
		jPanel3.add(jToggleButton_ActivateTimer, gridBagConstraints);

		jButton_Close.setFont(new java.awt.Font("Tahoma", 0, 10));
		jButton_Close.setForeground(java.awt.SystemColor.activeCaption);
		jButton_Close.setText("Close");
		jButton_Close.setBorder(null);
		jButton_Close.setBorderPainted(false);
		jButton_Close.setContentAreaFilled(false);
		jButton_Close
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jButton_Close.setIconTextGap(0);
		jButton_Close.setMaximumSize(new java.awt.Dimension(27, 13));
		jButton_Close.setPreferredSize(new java.awt.Dimension(27, 9));
		jButton_Close.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				closeButtonActionPerformed(evt);
			}
		});
		jButton_Close.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				jButton1MouseEntered(evt);
			}

			public void mouseExited(java.awt.event.MouseEvent evt) {
				jButton1MouseExited(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
		gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 2);
		jPanel3.add(jButton_Close, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		jPanel3.add(jSeparator1, gridBagConstraints);

		jScrollPane1.setViewportView(jPanel3);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(jScrollPane1, gridBagConstraints);

		timerAlertFrame = new TimerAlert();
		timerAlertFrame.init();
		GUIMemory.instance().add(timerAlertFrame.getFrame(), false, true);
	}

	public void addHopButtonAndSpinner() {
		java.awt.GridBagConstraints gridBagConstraints;
		jButtonHopRec = new JButton("Hop");
		Insets insets = new Insets(10, 20, 10, 20);
		jButtonHopRec.setMargin(insets);
		jButtonHopRec.setFont(jButtonHopRec.getFont().deriveFont(
				AffineTransform.getScaleInstance(0.8, 0.8)));
		jButtonHopRec.setBackground(Color.ORANGE);
		jButtonHopRec.setForeground(Color.GRAY);

		jButtonHopRec
				.setToolTipText("<HTML>Automatically sets the timer at thirty minutes time, start after three seconds, and launches the recorder</HTML>");
		jButtonHopRec.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonHopRec_ActionPerformed(evt);

			}
		});
		// jButtonHopRec.setBorder(null);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.NONE;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		jPanel1.add(jButtonHopRec, gridBagConstraints);

		hopM.setToolTipText("<HTML><HTML>");

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.NONE;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.weightx = 0.0;
		gridBagConstraints.weighty = 1.0;
		jPanel1.add(hopM, gridBagConstraints);
		hopM.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				Configuration.instance().setHopMinutes(
						((Integer) hopM.getValue()).intValue());
			}
		});
	}

	/**
	 * Set up the spinners to use SpinnerNumberModels, and also to interoperate
	 * with each other.
	 */
	private void initSpinners() {
		/** Set the NumberModel to each spinner. */
		recH.setModel(recHModel);
		stopH.setModel(stopHModel);
		recM.setModel(recMModel);
		stopM.setModel(stopMModel);
		recS.setModel(recSModel);
		stopS.setModel(stopSModel);

		hopM.setModel(hopMModel);

		/** Make the NumberModels aware of the parent JSPinners. */
		recMModel.setParent(recH);
		stopMModel.setParent(stopH);
		recSModel.setParent(recM);
		stopSModel.setParent(stopM);
		hopMModel.setParent(hopM);

		/**
		 * The DoubleDigitFactory used to change the editors of the
		 * JFormattedTextFields in the spinners to double digit, (01 instead of
		 * 1, etc.)
		 */
		DoubleDigitFactory ddf = new DoubleDigitFactory();

		javax.swing.JSpinner.NumberEditor tempEditor;

		/** Change the recS spinner. */
		tempEditor = (javax.swing.JSpinner.NumberEditor) recS.getEditor();
		tempEditor.getTextField().setFormatterFactory(ddf);

		/** Change the stopS spinner. */
		tempEditor = (javax.swing.JSpinner.NumberEditor) stopS.getEditor();
		tempEditor.getTextField().setFormatterFactory(ddf);

		/** Change the recM spinner. */
		tempEditor = (javax.swing.JSpinner.NumberEditor) recM.getEditor();
		tempEditor.getTextField().setFormatterFactory(ddf);

		/** Change the stopM spinner. */
		tempEditor = (javax.swing.JSpinner.NumberEditor) stopM.getEditor();
		tempEditor.getTextField().setFormatterFactory(ddf);

		/** Change the stopM spinner. */
		tempEditor = (javax.swing.JSpinner.NumberEditor) hopM.getEditor();
		tempEditor.getTextField().setFormatterFactory(ddf);

		/** Change the recH spinner. */
		tempEditor = (javax.swing.JSpinner.NumberEditor) recH.getEditor();
		tempEditor.getTextField().setFormatterFactory(ddf);

		/** Change the stopH spinner. */
		tempEditor = (javax.swing.JSpinner.NumberEditor) stopH.getEditor();
		tempEditor.getTextField().setFormatterFactory(ddf);

	}

	/**
	 * This initializes the timer, and makes it start to keep track of the
	 * calendar time. After this is done, the startTimer method must be called
	 * to start the KrutTimer, and the stopTimer method will stop the KrutTimer.
	 * 
	 * @param msDelay
	 *            The delay in ms between each update for the timer.
	 */
	public void initTimer(int msDelay) {
		java.awt.event.ActionListener timerTask = new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				timerAction();
			}
		};
		timer = new javax.swing.Timer(msDelay, timerTask);
		timer.start();
	}

	private void jButton1MouseEntered(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jButton1MouseEntered

		jButton_Close.setFont(new java.awt.Font("Tahoma", 1, 10));
	}// GEN-LAST:event_jButton1MouseEntered

	private void jButton1MouseExited(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jButton1MouseExited

		jButton_Close.setFont(new java.awt.Font("Tahoma", 0, 10));
	}// GEN-LAST:event_jButton1MouseExited

	private void jCheckBox1ItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_jCheckBox1ItemStateChanged

		if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
			stopS.setEnabled(true);
			stopM.setEnabled(true);
			stopH.setEnabled(true);
			if (!checkTimes())
				syncTimes();
		} else if (evt.getStateChange() == java.awt.event.ItemEvent.DESELECTED) {
			stopS.setEnabled(false);
			stopM.setEnabled(false);
			stopH.setEnabled(false);
		}

	}// GEN-LAST:event_jCheckBox1ItemStateChanged

	private void jRadioButton_RelativeTime_ActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jRadioButton1ActionPerformed

		if (jRadioButton_RelativeTime.isSelected() && (mode == COUNTUP)) {
			setMode(COUNTDOWN);
			jTextField1.setText("Start in:");
			jCheckBox_StopIn.setText("Stop in:");
			restoreTime();
			timerAction();
			Configuration.instance().setTimerUsesRelativeTime(true);
		}
	}// GEN-LAST:event_jRadioButton1ActionPerformed

	private void jRadioButton_ActualTime_ActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jRadioButton2ActionPerformed

		if (jRadioButton_ActualTime.isSelected() && (mode == COUNTDOWN)) {
			setMode(COUNTUP);
			jTextField1.setText("Start at:");
			jCheckBox_StopIn.setText("Stop at:");
			restoreTime();
			timerAction();
			Configuration.instance().setTimerUsesRelativeTime(false);
		}
	}// GEN-LAST:event_jRadioButton2ActionPerformed

	private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jToggleButton1ActionPerformed

		if (evt.getActionCommand().equals("Timer active")) {
			jToggleButton_ActivateTimer.setText("Stop Timer");
			startTimer();
			jToggleButton_ActivateTimer.setActionCommand("Timer stopped");
		} else if (evt.getActionCommand().equals("Timer recording")) {
			jToggleButton_ActivateTimer.setActionCommand("Timer stopped");
		} else if (evt.getActionCommand().equals("Timer stopped")) {
			jToggleButton_ActivateTimer.setText("Activate Timer");
			stopTimer();
			jToggleButton_ActivateTimer.setActionCommand("Timer active");
			updateToggleButton();
		}
		repaint();
	}// GEN-LAST:event_jToggleButton1ActionPerformed

	public void linkToConfig() {

		Configuration conf = Configuration.instance();

		if (conf.hasBeenLoaded()) {
			boolean userelativetime = conf.doesTimerUseRelativeTime();
			jRadioButton_RelativeTime.setSelected(userelativetime);
			jRadioButton_ActualTime.setSelected(!userelativetime);
			recH.setValue(new Integer(conf.getTimerRecHour()));
			recS.setValue(new Integer(conf.getTimerRecSecond()));
			recM.setValue(new Integer(conf.getTimerRecMinute()));

			stopH.setValue(new Integer(conf.getTimerStopHour()));
			stopS.setValue(new Integer(conf.getTimerStopSecond()));
			stopM.setValue(new Integer(conf.getTimerStopMinute()));
			hopMModel.setValue(new Integer(conf.getHopMinutes()));
			boolean enableStopIn = Configuration.instance().hasBeenLoaded()
					&& Configuration.instance().isTimerStopEnabled();
			jCheckBox_StopIn.setSelected(enableStopIn);

		} else {
			hopMModel.setValue(new Integer(30));
			jRadioButton_RelativeTime.setSelected(true);
			jCheckBox_StopIn.setSelected(false);
		}

		jCheckBox_StopIn.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
					Configuration.instance().setTimerStopEnabled(true);
				} else if (evt.getStateChange() == java.awt.event.ItemEvent.DESELECTED) {
					Configuration.instance().setTimerStopEnabled(false);
				}
			}
		});

		recH.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				Configuration.instance().setTimerRecHour(
						(Integer) recH.getValue());
			}
		});

		recS.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				Configuration.instance().setTimerRecSecond(
						(Integer) recH.getValue());
			}
		});

		recM.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				Configuration.instance().setTimerRecMinute(
						(Integer) recH.getValue());
			}
		});

		stopH.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				Configuration.instance().setTimerStopHour(
						(Integer) stopH.getValue());
			}
		});

		stopS.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				Configuration.instance().setTimerStopSecond(
						(Integer) stopH.getValue());
			}
		});

		stopM.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				Configuration.instance().setTimerStopMinute(
						(Integer) stopH.getValue());
			}
		});

		if (conf.isTimerActivated()) {
			setVisible(true);
			myContainer.pack();
		}

		this.addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(ComponentEvent e) {
				Configuration config = Configuration.instance();
				config.setTimerActivated(false);
				config.persist();

			}

			@Override
			public void componentMoved(ComponentEvent e) {

			}

			@Override
			public void componentResized(ComponentEvent e) {

			}

			@Override
			public void componentShown(ComponentEvent e) {
				Configuration config = Configuration.instance();
				config.setTimerActivated(true);
				config.persist();
			}
		});
	}

	/**
	 * Send a string to the OutputText object in myOutput.
	 * 
	 * @param The
	 *            string to be printed in by myOutput.
	 */
	private void output(String outString) {
		try {
			myOutput.out(outString);
			myOutput.out("");
		} catch (Exception e) {
			System.out.println("output error, KrutRecording.output");
			System.out.println("");
		}
	}

	private void recHStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_recHStateChanged

		if (!checkTimes())
			syncTimes();
		updateToggleButton();
	}// GEN-LAST:event_recHStateChanged

	private void recMStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_recMStateChanged

		if (!checkTimes())
			syncTimes();
		updateToggleButton();
	}// GEN-LAST:event_recMStateChanged

	private void recSStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_recSStateChanged

		if (!checkTimes())
			syncTimes();
		updateToggleButton();
	}// GEN-LAST:event_recSStateChanged

	/**
	 * When the user switches between relative time and actual time, this method
	 * is used to save the time that was entered in the time fields, and restore
	 * the calendar/actual time that was last entered.
	 */
	private void restoreTime() {
		int tempRecH = (Integer) recH.getValue();
		int tempRecM = (Integer) recM.getValue();
		int tempRecS = (Integer) recS.getValue();
		int tempStopH = (Integer) stopH.getValue();
		int tempStopM = (Integer) stopM.getValue();
		int tempStopS = (Integer) stopS.getValue();

		setRecFields(getSeconds(recHMemory, recMMemory, recSMemory));
		setStopFields(getSeconds(stopHMemory, stopMMemory, stopSMemory));

		recSMemory = tempRecS;
		recMMemory = tempRecM;
		recHMemory = tempRecH;
		stopSMemory = tempStopS;
		stopMMemory = tempStopM;
		stopHMemory = tempStopH;
	}

	/**
	 * This is used to give the KrutTimer direct access to the main GUI, for
	 * easy communication.
	 */
	public void setMainGUI(javax.swing.JFrame GUI) {
		myContainer = GUI;
	}

	/**
	 * Sets the mode of the KrutTimer. The mode should be set to either
	 * COUNTDOWN or COUNTUP. COUNTDOWN means that the timer will count down the
	 * number of hours, minutes and seconds given in the start-time in the GUI
	 * before starting to record, and then count down the time given in the
	 * stop-time in the GUI before stopping recording. COUNTUP means that the
	 * timer will start recording when the system clock reaches the time given
	 * in the start-time in the GUI, and stop recording when the system time
	 * reaches the stop-time given in the GUI.
	 * 
	 * @param newMode
	 *            An integer containing either COUNTDOWN or COUNTUP.
	 */
	public void setMode(int newMode) {
		mode = newMode;
	}

	/**
	 * Used to set the output window for this KrutTimer.
	 * 
	 * @param output
	 *            An OutputText object that this KrutTimer can use for its
	 *            output.
	 */
	public void setOutput(krut.KRUT_GUI.OutputText output) {
		myOutput = output;
	}

	/**
	 * Updates the time in the start-recording fields given the amount of
	 * seconds since last 00:00:00;
	 * 
	 * @param seconds
	 *            The number of seconds to be converted into a time and
	 *            displayed in the start- recording fields.
	 */
	private void setRecFields(int seconds) {
		int hours = getHours(seconds);
		seconds -= hours * 3600;
		int minutes = getMinutes(seconds);
		seconds -= minutes * 60;
		recS.setValue(seconds);
		recM.setValue(minutes);
		recH.setValue(hours);
	}

	/**
	 * Updates the time in the stop-recording fields given the amount of seconds
	 * since last 00:00:00;
	 * 
	 * @param seconds
	 *            The number of seconds to be converted into a time and
	 *            displayed in the stop- recording fields.
	 */
	private void setStopFields(int seconds) {
		int hours = getHours(seconds);
		seconds -= hours * 3600;
		int minutes = getMinutes(seconds);
		seconds -= minutes * 60;
		stopS.setValue(seconds);
		stopM.setValue(minutes);
		stopH.setValue(hours);
	}

	/**
	 * This method is executed when the countUp or countDown methods find that
	 * the programmed time to start recording has been reached.
	 */
	private void startRecording() {
		java.util.GregorianCalendar currentTime = new java.util.GregorianCalendar();
		int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
		int currentMinute = currentTime.get(Calendar.MINUTE);
		int currentSecond = currentTime.get(Calendar.SECOND);
		state = RECORDING;
		output("Timer started recording at " + currentHour + ":"
				+ ((currentMinute < 10) ? "0" : "") + currentMinute + ":"
				+ ((currentSecond < 10) ? "0" : "") + currentSecond);
		/**
		 * We temporarily change the action command of the JToggleButton to send
		 * a message to users that are listening for the "Start recording"
		 * action event. Then we enable it (which shouldn't be necessary,
		 * because it should always be enabled when the timer is active) and
		 * push it once. The action command will be restored by the action event
		 * in this class. We then do a updateToggleButton just to make sure we
		 * have the right value for the enabled state of the toggle button.
		 */
		jToggleButton_ActivateTimer.setActionCommand("Timer recording");
		jToggleButton_ActivateTimer.setEnabled(true);
		jToggleButton_ActivateTimer.doClick();
		updateToggleButton();
	}

	/** Start the KrutTimer */
	public void startTimer() {
		enableComponents(false);
		startRecSeconds = getRecSeconds();
		stopRecSeconds = getStopSeconds();
		if (mode == COUNTDOWN) {
			int currentTime = getCalendarSeconds();
			startRecSeconds += currentTime;
			stopRecSeconds += currentTime;
		}
		state = WAITING;
	}

	private void stopHStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_stopHStateChanged

		if (!checkTimes())
			syncTimes();
		updateToggleButton();
	}// GEN-LAST:event_stopHStateChanged

	private void stopMStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_stopMStateChanged

		if (!checkTimes())
			syncTimes();
		updateToggleButton();
	}// GEN-LAST:event_stopMStateChanged

	/**
	 * This method is executed when the countUp or countDown methods find that
	 * the programmed time to stop recording has been reached.
	 */
	private void stopRecording() {
		java.util.GregorianCalendar currentTime = new java.util.GregorianCalendar();
		int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
		int currentMinute = currentTime.get(Calendar.MINUTE);
		int currentSecond = currentTime.get(Calendar.SECOND);
		if (state == RECORDING) {
			output("Timer stopped recording at " + currentHour + ":"
					+ ((currentMinute < 10) ? "0" : "") + currentMinute + ":"
					+ ((currentSecond < 10) ? "0" : "") + currentSecond);
		}
		state = NOT_ACTIVE;
	}

	private void stopSStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_stopSStateChanged

		if (!checkTimes())
			syncTimes();
		updateToggleButton();
	}// GEN-LAST:event_stopSStateChanged

	/** Stop the KrutTimer */
	public void stopTimer() {
		stopRecording();
		enableComponents(true);
	}

	/**
	 * Changes the time in the stop-time fields to read exactly the same as the
	 * time in the rec-time fields.
	 */
	private void syncTimes() {
		setStopFields(getRecSeconds());
	}

	/**
	 * This is where the timer starts every new cycle.
	 */
	private void timerAction() {
		if (mode == COUNTUP)
			checkMidnight();
		if (state == NOT_ACTIVE) {
			/**
			 * There would be no point in updating countdown times here (They
			 * are constant).
			 */
			updateCalendarTimes();
		} else if (state == WAITING) {
			/**
			 * We do not care about updating calendar times when the timer is
			 * active. It looks bad.
			 */
			updateCountdownTimes();
			if (startRecSeconds <= getCalendarSeconds()) {
				startRecording();
			}
		} else if ((state == RECORDING) && jCheckBox_StopIn.isSelected()) {
			/**
			 * We do not care about updating calendar times when the timer is
			 * active. It looks bad.
			 */
			updateCountdownTimes();
			if (stopRecSeconds <= getCalendarSeconds()) {
				/**
				 * We quickly enable the button and perform a click, then we
				 * restore it to the state it is supposed to have.
				 */
				jToggleButton_ActivateTimer.setEnabled(true);
				jToggleButton_ActivateTimer.doClick();
				updateToggleButton();
				signalRecordingEnd();
			}
		}
	}

	/**
	 * Updates the time fields so that the time given in them is never lower
	 * than the current time.
	 */
	private void updateCalendarTimes() {
		/**
		 * If the mode is COUNTDOWN, we shouldn't do anything here.
		 */
		if (mode == COUNTDOWN)
			return;
		int calendarSeconds = getCalendarSeconds();
		if (getRecSeconds() < calendarSeconds) {
			setRecFields(calendarSeconds);
		}
	}

	/**
	 * Updates the recording time and stopping time fields during a countdown.
	 */
	private void updateCountdownTimes() {
		if (mode == COUNTUP) {
			/**
			 * If the mode is COUNTUP, we shouldn't do anything here.
			 */
		} else if (state == WAITING) {
			int seconds = startRecSeconds - getCalendarSeconds();
			setRecFields(seconds);
			if (jCheckBox_StopIn.isSelected()) {
				setStopFields(stopRecSeconds - getCalendarSeconds());
			}
			signalRecordingStart(seconds - 1);

		} else if ((state == RECORDING) && jCheckBox_StopIn.isSelected()) {
			setStopFields(stopRecSeconds - getCalendarSeconds());
		}
	}

	protected void signalRecordingStart(int seconds) {
		if (seconds > 1 && seconds < 5) {

			timerAlertFrame.setColors(false);
			timerAlertFrame.setText(" Go in(" + (seconds - 1) + ")", 11.5);
			timerAlertFrame.setVisible(true);

		} else if (seconds == 1) {

			timerAlertFrame.setColors(false);
			timerAlertFrame.setText("  Ciack!", 13);
			timerAlertFrame.setVisible(true, 750);
			java.awt.Toolkit.getDefaultToolkit().beep();

		} else
			timerAlertFrame.setVisible(false);

	}

	protected void signalRecordingEnd() {
		timerAlertFrame.setColors(true);
		timerAlertFrame.setText("\n  Krut Recorder has Stopped", 4);
		timerAlertFrame.setVisible(true);

		java.awt.Toolkit.getDefaultToolkit().beep();

	}

	/**
	 * Controls if the "Activate Timer"-buttons should be enabled or disabled.
	 * It should be enabled if the given starting time of recording is more than
	 * 0s into the future. This method also enables or disables the button based
	 * on the results.
	 * 
	 * This method also makes sure that the toggleButton is selected when the
	 * timer is activated and deselected when the timer not activated.
	 */
	public void updateToggleButton() {

		if (state == NOT_ACTIVE) {
			jToggleButton_ActivateTimer.setSelected(false);
			if (mode == COUNTDOWN) {
				if (getRecSeconds() == 0) {
					disableToggleButton();
				} else {
					enableToggleButton();
				}
			} else if (mode == COUNTUP) {
				if (getCalendarSeconds() < getRecSeconds()) {
					enableToggleButton();
				} else {
					disableToggleButton();
				}
			}
		} else {
			jToggleButton_ActivateTimer.setSelected(true);
		}
	}

	public void jButtonHopRec_ActionPerformed(ActionEvent evt) {
		jRadioButton_RelativeTime.doClick();

		if (!jCheckBox_StopIn.isSelected())
			jCheckBox_StopIn.doClick();

		int startDelay = 5;
		recS.setValue(new Integer(startDelay));
		recM.setValue(new Integer(0));
		recH.setValue(new Integer(0));

		stopS.setValue(new Integer(startDelay));
		stopM.setValue(hopM.getValue());
		stopH.setValue(new Integer(0));

		jToggleButton_ActivateTimer.doClick();
		jButtonHopRec.setEnabled(false);
		hopM.setEnabled(false);
	}

	public javax.swing.JToggleButton getActivateTimerButton() {
		return jToggleButton_ActivateTimer;
	}



}
