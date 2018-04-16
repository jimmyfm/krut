/*
 * KrutTimer.java
 *
 * Created on 17. maj 2007, 14:57
 */

package net.sourceforge.krut.ui;

/**
 *
 * @author  Jonas
 */
 /**  A class used as a timer to start the Krut program. The KrutTimer is a
 *  JPanel that when displayed sits below the main window of the Krut program.
 *  It can be used to program Krut to start and stop recording at given
 *  offset times, or at given system calendar clock times. 
 */
public class KrutTimer extends javax.swing.JPanel {
    
    /** This should contain the JFrame in which the KrutTimer is drawn.
     *  The JButton1ActionPerformed method will call that object and tell
     *  it to re-pack. This parameter is set in the setMainGUI method or the
     *  constructor.
     */
    private javax.swing.JFrame myContainer;

    /** The output window of this KrutTimer. Should be set through
     *  the setOutput method.
     */
    private net.sourceforge.krut.ui.OutputText myOutput;

    /** Timer used to count down to recording start/stop. */
    public javax.swing.Timer timer;
        
    /** When the timer is activated, startRecSeconds
     *  contains the programmed starting time for
     *  recording, counted in seconds from last
     *  midnight. startRecSeconds is set in the
     *  startTimer method. In the tickTimer method,
     *  the current calendar time is compared to
     *  startRecSeconds to see if recording should
     *  start.
     */
    private int startRecSeconds;
    
    /** When the timer is activated, stopRecSeconds
     *  contains the programmed stopping time for
     *  recording, counted in seconds from last
     *  midnight. stopRecSeconds is set in the
     *  startTimer method. In the tickTimer method,
     *  the current calendar time is compared to
     *  stopRecSeconds to see if recording should
     *  stop.
     */
    private int stopRecSeconds;
    
    /** When the user changes the type of time to use in
     *  the KrutTimer from actual time to relative time,
     *  the old start-time fields are switched
     *  with those in recHMemory, recMMemory, and
     *  recSMemory. The switch is done in the
     *  restoreTime method.
     */
    private int recHMemory = 0;
    /** When the user changes the type of time to use in
     *  the KrutTimer from actual time to relative time,
     *  the old start-time fields are switched
     *  with those in recHMemory, recMMemory, and
     *  recSMemory. The switch is done in the
     *  restoreTime method.
     */
    private int recMMemory = 0;
    /** When the user changes the type of time to use in
     *  the KrutTimer from actual time to relative time,
     *  the old start-time fields are switched
     *  with those in recHMemory, recMMemory, and
     *  recSMemory. The switch is done in the
     *  restoreTime method.
     */
    private int recSMemory = 0;

    /** When the user changes the type of time to use in
     *  the KrutTimer from actual time to relative time,
     *  the old stop-time fields are switched
     *  with those in stopHMemory, stopMMemory, and
     *  stopSMemory. The switch is done in the
     *  restoreTime method.
     */
    private int stopHMemory = 0;
    /** When the user changes the type of time to use in
     *  the KrutTimer from actual time to relative time,
     *  the old stop-time fields are switched
     *  with those in stopHMemory, stopMMemory, and
     *  stopSMemory. The switch is done in the
     *  restoreTime method.
     */
    private int stopMMemory = 0;
    /** When the user changes the type of time to use in
     *  the KrutTimer from actual time to relative time,
     *  the old stop-time fields are switched
     *  with those in stopHMemory, stopMMemory, and
     *  stopSMemory. The switch is done in the
     *  restoreTime method.
     */
    private int stopSMemory = 0;
            
    /** The countdown mode for the KrutTimer. */
    public final static int COUNTDOWN = 0;
    /** The countup mode for the KrutTimer. */
    public final static int COUNTUP = 1;
    /** The not active state for the KrutTimer. */
    public final static int NOT_ACTIVE = 2;
    /** The waiting state for the KrutTimer. */
    public final static int WAITING = 3;
    /** The recording state for the KrutTimer. */
    public final static int RECORDING = 4;
    /** The mode of the KrutTimer. This can be either COUNTDOWN or COUNTUP.
     *  In the countdown mode, the KrutTimer counts down from a given time,
     *  and in the countup mode, the KrutTimer counts up towards a given
     *  calendar time.
     *  The mode can be read through getMode().
     */ 
    private int mode = COUNTDOWN;
    
    /** The state of the KrutTimer. This can be NOT_ACTIVE, WAITING, or
     *  RECORDING. When the KrutTimer is not active, it waits for the
     *  user to activate it through the "Activate Timer" JToggleButton.
     *  When the KrutTimer is waiting, it waits for the time to reach
     *  the given start time for recording. When the KrutTimer is
     *  recording, it waits for the KrutTimer to reach the given stop
     *  timer for recording, if there is one.
     *  The state can be read through getState().
     */
    private int state = NOT_ACTIVE;
        
    /** This is a class we use to make sure the numbers displayed in
     *  the JFormattedTextFields for the spinners are always two digit numbers,
     *  even if their values are below 10. The values of the text fields
     *  are never changed, just the way they are displayed.
     *
     *  @return A String representation of the value in the JFormattedTextField.
     */
    private class DoubleDigitFormatter
            extends javax.swing.JFormattedTextField.AbstractFormatter {
        
        /** Give a String representation of the value of
         *  the JFormattedTextField to which this DoubleDigitFormatter is
         *  attached.
         *
         *  @param  value   The Object which represents the value of the
         *                  JFormattedTextField. This will always be an integer,
         *                  since we only use SpinnerNumberModels for the
         *                  spinners.
         *
         *  @return     A String representation of the value. If the value is
         *              not a valid integer, "00" is returned.
         */
        public String valueToString(Object value) {
            String returnString = "00";
            try {
                int val = (Integer) value;
                /** First we must check that we have a valid number. */
                if (val < 0) {}
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
        
        /** Give the integer value of the String read from the JFormattedTextField
         *  to which this DoubleDigitFormatter is attached.
         *
         *  @param  text    The String read from the JFormattedTextField.
         * 
         *  @return     An Integer representing the value of the input parameter
         *              text, or 0 if there is no such Integer representation.
         */
        public Integer stringToValue(String text) {
            Integer returnInt = 0;
            try {
                returnInt = Integer.parseInt(text);
            } catch (Exception e) {
                System.out.println("Error in KrutTimer.DoubleDigitFormatter.stringToValue: ");
                System.out.println(e);
                /** If the user made an invalid edit, this will change it to 0. */
                getFormattedTextField().setValue(0);
            }
            return returnInt;
        }
    }

    
    /** This class is used to deliver our DoubleDigitFormatter, whenever
     *  someone asks the JFormattedTextFields of the spinners for their
     *  AbstractFormatter.
     */
    private class DoubleDigitFactory extends javax.swing.JFormattedTextField.AbstractFormatterFactory {
        /** Deliver the DoubleDigitFormatter
         *
         *  @param  tf  A JFormattedTextField to which this DoubleDigitFactory is
         *              attached.
         *
         *  @return     A new DoubleDigitFormatter object.
         */
        public DoubleDigitFormatter getFormatter(javax.swing.JFormattedTextField tf) {
            return new DoubleDigitFormatter();
        }
    }
    
    /** A class used to limit the highest number that can be written
     *  in the JFormattedTextField beloning to a JSpinner to 59. If
     *  a higher number is entered, the spinner will start over from
     *  0, and its parent will add 1 to it's value. The parent must
     *  be set using the setParent method before using an object of
     *  this class.
     */
    private class TimeModel extends javax.swing.SpinnerNumberModel {
        
        /** The JSpinner which is parent to the JSpinner that this
         *  TimeModel belongs to. Eg. if this is the TimeModel for the
         *  recS (recording seconds) spinner, the parent would be the
         *  recM (recording minutes) spinner.
         */
        private javax.swing.JSpinner myParentSpinner;
        
        /** Constructor for TimeModel. See SpinnerNumberModel for details.
         *
         *  @param  value       The current (non null) value of the component.
         *  @param  min         The first number in the sequence, or null.
         *  @param  max         The last number in the sequence, or null.
         *  @param  stepSize    The difference between elements of the sequence.
         */
        TimeModel(Number value, Comparable min, Comparable max, Number stepSize) {
            super(value, min, max, stepSize);
        }
                
        /** A TimeModel needs to know the JSpinner which is parent to the
         *  JSpinner the TimeModel belongs to. Eg. if this is the TimeModel for the
         *  recS (recording seconds) spinner, the parent would be the
         *  recM (recording minutes) spinner.
         *
         *  This is the method used to set this parent.
         *
         *  @param parent   The parent JSpinner to the JSpinner that this
         *                  TimeModel belongs to.
         */
        public void setParent(javax.swing.JSpinner parent) {
            myParentSpinner = parent;
        }
        
        /** Set the value of the JSpinner that this TimeModel belongs to.
         *  Since this is a SpinnerNumberModel, the value should be an
         *  integer number.
         *
         *  @param  value   The integer number that should be used as
         *                  the value of the JSpinner that this TimeModel
         *                  belongs to.
         */
        public void setValue(Object value) {
            
            /** Used to store the value of the input parameter, converted
             *  to an integer.
             */
            int val = 0;
            
            /** Used to keep track of how much we should increase the
             *  parent when we are done.
             */
            int parentIncrease = 0;
            
            /** See description of bug at the end of this method.
             */
            boolean unclearBugFix = false;
            
            /** Convert value to an int. */
            try {
                val = (Integer) value;
            } catch (Exception e) {
                System.out.println("Error in KrutTimer.TimeModel.setValue");
                System.out.println(e);
            }
            
            /** Make sure that the value is below 60. Increase the parent
             *  JSpinner if the value was 60 or above.
             */
            while (60 <= val) {
                val -= 60;
                parentIncrease++;
                unclearBugFix = true;
            }
            
            /** If we have decreased the value below zero, we should 
             *  decrease parent by one, and start over at 59. This part
             *  is horribly ugly, and should be replaced with different
             *  SpinnerNumberModels for hours, minutes, and seconds.
             */
            if (val == -1) {
                
                /** If the parent spinner is at its lowest value,
                 *  we should just stay at 0.
                 */
                if (myParentSpinner.getPreviousValue() == null) {
                    val = 0;
                } else {
                    /** Get an integer representation of the parents
                     *  previous value.
                     */
                    int tempVal = (Integer) myParentSpinner.getPreviousValue();
                    if (tempVal == -1) {
                        /** The only scenario that can go wrong, is if we are
                         *  in the field for seconds and the time is 00:00:00,
                         *  and we try to decrease the seconds. This is a bugfix
                         *  for that case. We try to change the minutes down,
                         *  and see what happend. If they change to 59, then we change
                         *  the seconds as well. If the minutes stay at 00, then
                         *  the seconds stay as well.
                         */
                        myParentSpinner.setValue(myParentSpinner.getPreviousValue());
                        /** Get an integer representation of the parents value.
                         */
                        int testMin = (Integer) myParentSpinner.getValue();
                        if (testMin == 0) {
                            val = 0;
                        } else {
                            val = 59;
                        }
                    } else {
                        /** If we get this far, the parents previous value is
                         *  not -1, and not null, which means that the parent has
                         *  a value of over 0, which means we should decrease it,
                         *  and change the current field to 59.
                         */
                        val = 59;
                        myParentSpinner.setValue(myParentSpinner.getPreviousValue());
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

            /** It seems that some values when inputted in
             *  the text fields by the user, does not change
             *  the way they should after the proper HH:MM:SS
             *  value has been calculated. It seems to occur
             *  for some values that are even multiples of 300
             *  more than the final value that should end up in the
             *  text field of the spinner. For those cases,
             *  the two digits that will be the final text in
             *  window will be the same as the last two digits of
             *  the larger number which was originally there,
             *  which is a probable cause of the problem.
             *  Ex: If the first value put in by the user after
             *      the window is opened is 300 in the recS field,
             *      the minutes will update to 5, but the seconds
             *      have repeatedly been found to remain at 300,
             *      instead of 00. This was found with the default
             *      windows XP look and feel.  
             *  Calling fireStateChanged() seems to fix this issue.
             */
            if (unclearBugFix) fireStateChanged();
        }
    }

    /** The SpinnerNumberModel used for the recH spinner. */
    private javax.swing.SpinnerNumberModel recHModel =
            new javax.swing.SpinnerNumberModel(0, 0, null, 1);
    /** The SpinnerNumberModel used for the stopH spinner. */
    private javax.swing.SpinnerNumberModel stopHModel =
            new javax.swing.SpinnerNumberModel(0, 0, null, 1);
    /** The TimeModel used for the recM spinner. */
    private TimeModel recMModel = new TimeModel(0, -1, null, 1);
    /** The TimeModel used for the stopM spinner. */
    private TimeModel stopMModel = new TimeModel(0, -1, null, 1);
    /** The TimeModel used for the recS spinner. */
    private TimeModel recSModel = new TimeModel(0, -1, null, 1);
    /** The TimeModel used for the stopS spinner. */
    private TimeModel stopSModel = new TimeModel(0, -1, null, 1);
    
    /** Creates new KrutTimer.
     *
     *  @param  GUI   The JFrame that this KrutTimer is
     *                  being drawn in.
     */
    public KrutTimer(javax.swing.JFrame GUI) {
        myContainer = GUI;
        initComponents();
        initSpinners();
        /** Initialize a timer that updates once per second. */
        initTimer(1000);
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jCheckBox1.setFont(jTextField1.getFont());
    }
    
    /** This is used to give the KrutTimer direct access to the
     *  main GUI, for easy communication.
     */
    public void setMainGUI(javax.swing.JFrame GUI) {
        myContainer = GUI;
    }
    
    /** Used to set the output window for this KrutTimer.
     *
     *  @param  output  An OutputText object that this
     *                  KrutTimer can use for its output.
     */
    public void setOutput(net.sourceforge.krut.ui.OutputText output) {
        myOutput = output;
    }
        
    /** Set up the spinners to use SpinnerNumberModels, and also to
     *  interoperate with each other.
     */
    private void initSpinners() {
        /** Set the NumberModel to each spinner. */
        recH.setModel(recHModel);
        stopH.setModel(stopHModel);
        recM.setModel(recMModel);
        stopM.setModel(stopMModel);
        recS.setModel(recSModel);
        stopS.setModel(stopSModel);
               
        /** Make the NumberModels aware of the parent JSPinners. */
        recMModel.setParent(recH);
        stopMModel.setParent(stopH);
        recSModel.setParent(recM);
        stopSModel.setParent(stopM);
        
        /** The DoubleDigitFactory used to change the editors of the
         *  JFormattedTextFields in the spinners to double digit,
         *  (01 instead of 1, etc.)
         */
        DoubleDigitFactory ddf = new DoubleDigitFactory();
       
        javax.swing.JSpinner.NumberEditor tempEditor;
        
        /** Change the recS spinner. */
        tempEditor= (javax.swing.JSpinner.NumberEditor) recS.getEditor();
        tempEditor.getTextField().setFormatterFactory(ddf);
        
        /** Change the stopS spinner. */
        tempEditor= (javax.swing.JSpinner.NumberEditor) stopS.getEditor();
        tempEditor.getTextField().setFormatterFactory(ddf);
        
        /** Change the recM spinner. */
        tempEditor= (javax.swing.JSpinner.NumberEditor) recM.getEditor();
        tempEditor.getTextField().setFormatterFactory(ddf);
        
        /** Change the stopM spinner. */
        tempEditor= (javax.swing.JSpinner.NumberEditor) stopM.getEditor();
        tempEditor.getTextField().setFormatterFactory(ddf);
        
        /** Change the recH spinner. */
        tempEditor= (javax.swing.JSpinner.NumberEditor) recH.getEditor();
        tempEditor.getTextField().setFormatterFactory(ddf);
        
        /** Change the stopH spinner. */
        tempEditor= (javax.swing.JSpinner.NumberEditor) stopH.getEditor();
        tempEditor.getTextField().setFormatterFactory(ddf);        
    }
    

    /** This initializes the timer, and makes it start to
     *  keep track of the calendar time. After this is done,
     *  the startTimer method must be called to start the KrutTimer,
     *  and the stopTimer method will stop the KrutTimer.
     *
     *  @param  msDelay     The delay in ms between each
     *                      update for the timer.
     */
    public void initTimer(int msDelay) {
        java.awt.event.ActionListener timerTask = 
                new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timerAction();
            }
        };
        timer = new javax.swing.Timer(msDelay, timerTask);
        timer.start();
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
    
    /** Stop the KrutTimer */
    public void stopTimer() {
        stopRecording();
        enableComponents(true);
    }
    
    /** Sets the enabled state of selected components
     *  in the KrutTimer. This is used to disable some
     *  components while the timer is running.
     *
     *  @param  enabled     The enabled state of the compontents.
     */
    private void enableComponents(boolean enabled) {
        jRadioButton1.setEnabled(enabled);
        jRadioButton2.setEnabled(enabled);
        jCheckBox1.setEnabled(enabled);
        recH.setEnabled(enabled);
        recM.setEnabled(enabled);
        recS.setEnabled(enabled);
        if (jCheckBox1.isSelected()) {
            stopH.setEnabled(enabled);
            stopM.setEnabled(enabled);
            stopS.setEnabled(enabled);
        }
    }
    
    /** Sets the mode of the KrutTimer. The mode should be
     *  set to either COUNTDOWN or COUNTUP.
     *  COUNTDOWN means that the timer will count down the number of
     *  hours, minutes and seconds given in the start-time in the GUI before starting
     *  to record, and then count down the time given in the stop-time in the GUI
     *  before stopping recording.
     *  COUNTUP means that the timer will start recording when the system clock
     *  reaches the time given in the start-time in the GUI, and stop recording
     *  when the system time reaches the stop-time given in the GUI.
     *
     *  @param  newMode     An integer containing either COUNTDOWN
     *                      or COUNTUP.
     */
    public void setMode(int newMode) {
        mode = newMode;
    }
        
    /** This is where the timer starts every new cycle.
     */
    private void timerAction() {
        if (mode == COUNTUP) checkMidnight();
        if (state == NOT_ACTIVE) {
            /** There would be no point in updating
             *  countdown times here (They are constant). */
            updateCalendarTimes();            
        } else if (state == WAITING) {
            /** We do not care about updating calendar times
             *  when the timer is active. It looks bad. */
            updateCountdownTimes();
            if (startRecSeconds <= getCalendarSeconds()) {
                startRecording();
            }
        } else if ((state == RECORDING) && jCheckBox1.isSelected()) {
            /** We do not care about updating calendar times
             *  when the timer is active. It looks bad. */
            updateCountdownTimes();
            if (stopRecSeconds <= getCalendarSeconds()) {
                /** We quickly enable the button and perform a click,
                 *  then we restore it to the state it is supposed to have.
                 */
                jToggleButton1.setEnabled(true);
                jToggleButton1.doClick();
                updateToggleButton();
            }
        }
    }
        
    /** This method is executed when the countUp or countDown
     *  methods find that the programmed time to start
     *  recording has been reached.
     */
    private void startRecording() {
        java.util.GregorianCalendar currentTime =
                new java.util.GregorianCalendar();
        int currentHour = currentTime.get(currentTime.HOUR_OF_DAY);
        int currentMinute = currentTime.get(currentTime.MINUTE);
        int currentSecond = currentTime.get(currentTime.SECOND);
        state = RECORDING;
        output("Timer started recording at " +
                            currentHour + ":" +
                            ((currentMinute < 10) ? "0" : "") +
                            currentMinute + ":" +
                            ((currentSecond < 10) ? "0" : "") +
                            currentSecond);
        /** We temporarily change the action command of the
         *  JToggleButton to send a message to users that are
         *  listening for the "Start recording" action event.
         *  Then we enable it (which shouldn't be necessary, 
         *  because it should always be enabled when the timer
         *  is active) and push it once. The action command
         *  will be restored by the action event in this class.
         *  We then do a updateToggleButton just to make sure we
         *  have the right value for the enabled state of the
         *  toggle button.
         */
        jToggleButton1.setActionCommand("Timer recording");
        jToggleButton1.setEnabled(true);
        jToggleButton1.doClick();
        updateToggleButton();
    }
    
    /** This method is executed when the countUp or countDown
     *  methods find that the programmed time to stop
     *  recording has been reached.
     */
    private void stopRecording() {
        java.util.GregorianCalendar currentTime =
                new java.util.GregorianCalendar();
        int currentHour = currentTime.get(currentTime.HOUR_OF_DAY);
        int currentMinute = currentTime.get(currentTime.MINUTE);
        int currentSecond = currentTime.get(currentTime.SECOND);
        if (state == RECORDING) {
            output("Timer stopped recording at " +
                        currentHour + ":" +
                        ((currentMinute < 10) ? "0" : "") +
                        currentMinute + ":" +
                        ((currentSecond < 10) ? "0" : "") +
                        currentSecond);
        }
        state = NOT_ACTIVE;
    }
    
    /** Send a string to the OutputText object in myOutput.
     *
     *  @param  The string to be printed in by myOutput.
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
    
    /** Updates the time in the start-recording fields given the
     *  amount of seconds since last 00:00:00;
     *
     *  @param  seconds     The number of seconds to be
     *                      converted into a time and
     *                      displayed in the start-
     *                      recording fields.
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

    /** Updates the time in the stop-recording fields given the
     *  amount of seconds since last 00:00:00;
     *
     *  @param  seconds     The number of seconds to be
     *                      converted into a time and
     *                      displayed in the stop-
     *                      recording fields.
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
    
    /** Updates the recording time and stopping time
     *  fields during a countdown.
     */
    private void updateCountdownTimes() {
        if (mode == COUNTUP) {
            /** If the mode is COUNTUP, we shouldn't do
             *  anything here.
             */            
        } else if (state == WAITING) {
            setRecFields(startRecSeconds - getCalendarSeconds());
            if (jCheckBox1.isSelected()) {
                setStopFields(stopRecSeconds - getCalendarSeconds());
            }
        } else if ((state == RECORDING) && jCheckBox1.isSelected()){
                setStopFields(stopRecSeconds - getCalendarSeconds());
        }
    }
    
    /** Updates the time fields so that the time
     *  given in them is never lower than the current
     *  time.
     */
    private void updateCalendarTimes() {
        /** If the mode is COUNTDOWN, we shouldn't do
         *  anything here.
         */
        if (mode == COUNTDOWN) return;
        int calendarSeconds = getCalendarSeconds();
        if (getRecSeconds() < calendarSeconds) {
            setRecFields(calendarSeconds);
        }        
    }
    
    /** Changes the time in the stop-time fields to read
     *  exactly the same as the time in the rec-time fields.
     */
    private void syncTimes() {
        setStopFields(getRecSeconds());
    }
    
    /** Checks if the time given in the stop-time fields is lower than
     *  the one in the rec-time fields. Also check jCheckBox1 before returning
     *  an answer.
     *
     *  @return     true if the time in the rec-time fields is higher than
     *              the one in the stop-time fields, or if jCheckBox1 is not
     *              selected. false if the time in the rec-time fields is
     *              lower than the one in the stop-time fields and jCheckBox1
     *              is selected.
     */
    private boolean checkTimes() {
        /** The only combination that will give false here is:
         *  box selected + stop < rec */ 
        return (!jCheckBox1.isSelected() ||
                (getRecSeconds() <= getStopSeconds()));
    }
    
    /** Controls that the total amount of hours
     *  since last midnight is not more than 24.
     *  Subtracts an integer multiple of 24h
     *  until a proper time is reached, and
     *  resets the time field. This is done
     *  for both start and stop times.
     */
    private void checkMidnight() {
        /** Get the start and stop times in seconds */
        int rs = getRecSeconds();
        int ss = getStopSeconds();
        boolean rec = false, stop = false;
        /** Are they over 24h? */
        while (24 <= getHours(rs)) {
            rs -= 24*3600;
            rec = true;
        }
        while (24 <= getHours(ss)) {
            ss -= 24*3600;
            stop = true;
        }
        /** Correct if they are. */
        if (rec) setRecFields(rs);
        if (stop) setStopFields(ss);
    }
    
    /** Get the value of the time entered into the record
     *  time fields, expressed in seconds.
     *
     *  return  The total amount of seconds entered into the record time fields.
     */
    private int getRecSeconds() {
        int recHour = (Integer) recH.getValue();
        int recMinute = (Integer) recM.getValue();
        int recSecond = (Integer) recS.getValue();
        return getSeconds(recHour, recMinute, recSecond);
    }

    /** Get the value of the time entered into the stop
     *  time fields, expressed in seconds.
     *
     *  @return  The total amount of seconds entered into the stop time fields.
     */
    private int getStopSeconds() {
        int stopHour = (Integer) stopH.getValue();
        int stopMinute = (Integer) stopM.getValue();
        int stopSecond = (Integer) stopS.getValue();
        return getSeconds(stopHour, stopMinute, stopSecond);
    }
    
    /** Get the value of the current calendar time,
     *  expressed in seconds.
     *
     *  @return     The total amount of seconds since last midnight.
     */
    private int getCalendarSeconds() {
        java.util.GregorianCalendar currentTime =
                new java.util.GregorianCalendar();
        int currentHour = currentTime.get(currentTime.HOUR_OF_DAY);
        int currentMinute = currentTime.get(currentTime.MINUTE);
        int currentSecond = currentTime.get(currentTime.SECOND);
        return getSeconds(currentHour, currentMinute, currentSecond);
    }
        
    /** Returns the number of whole hours in a time given in
     *  seconds.
     *
     *  @param  seconds The total number of seconds
     * 
     *  @return     The total number of whole hours.
     */
    public int getHours(int seconds) {
        return (seconds / 3600);
    }
    
    /** Returns the number of whole minutes in a time given in
     *  seconds.
     *
     *  @param  seconds The total number of seconds
     * 
     *  @return     The total number of whole minutes.
     */
    public int getMinutes(int seconds) {
        return (seconds / 60);
    }
    
    /** Gives the number of seconds in the time given in the
     *  input parameters.
     *
     *  @param  hours   The number of hours
     *  @param  minutes The number of minutes
     *  @param  seconds The number of seconds
     *
     *  @return     The total number of seconds.
     */
    public int getSeconds(int hours, int minutes, int seconds) {
        return seconds + 60 * minutes + 3600 * hours;
    }
    
    /** When the user switches between relative time and
     *  actual time, this method is used to save the
     *  time that was entered in the time fields,
     *  and restore the calendar/actual time that was
     *  last entered.
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
    
    /** Controls if the "Activate Timer"-buttons should be
     *  enabled or disabled. It should be enabled if the
     *  given starting time of recording is more than 0s
     *  into the future. This method also enables or disables
     *  the button based on the results.
     *
     *  This method also makes sure that the toggleButton is
     *  selected when the timer is activated and deselected
     *  when the timer not activated.
     */
    public void updateToggleButton() {

        if (state == NOT_ACTIVE) {
            jToggleButton1.setSelected(false);
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
            jToggleButton1.setSelected(true);
        }
    }
    
    /** Disables the "Activate Timer" togglebutton.
     */
    public void disableToggleButton() {
        jToggleButton1.setEnabled(false);
        jToggleButton1.setToolTipText("<HTML>The Start time must be more than 0 seconds away<BR>" +
                                        "for this button to activate.</HTML>");
    }

    /** Enables the "Activate Timer" togglebutton.
     */
    public void enableToggleButton() {
        jToggleButton1.setEnabled(true);
        jToggleButton1.setToolTipText("");
    }
    
    /** This method returns the toggle button that activates
     *  the timer, so that the user can decide what to do
     *  when the timer is activated. The following ActionEvents
     *  will be fired to all ActionListeners on the button:
     *
     *  Action:             Action name:
     *  Timer activated     "Timer active"
     *  Recording started   "Timer recording"
     *  Recording stopped   "Timer stopped"
     *
     *  @return the JToggleButton that activates the timer.
     */
    public javax.swing.JToggleButton getTimerButton() {
        return jToggleButton1;
    }
    
    /** Returns the mode of the timer, either
     *  COUNTDOWN or COUNTUP.
     *
     *  @return     The mode of the timer.
     */
    public int getMode() {
        return mode;
    }
    
    /** Returns the state of the timer, either
     *  NOT_ACTIVE, RECORDING or WAITING.
     *
     *  @return     The state of the timer.
     */
    public int getState() {
        return state;
    }
            
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        recH = new javax.swing.JSpinner();
        recS = new javax.swing.JSpinner();
        recM = new javax.swing.JSpinner();
        stopH = new javax.swing.JSpinner();
        stopS = new javax.swing.JSpinner();
        stopM = new javax.swing.JSpinner();
        jCheckBox1 = new javax.swing.JCheckBox();
        jToggleButton1 = new javax.swing.JToggleButton();
        jButton1 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();

        setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setBorder(null);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(new javax.swing.border.TitledBorder(null, "Use:"));
        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Relative time");
        jRadioButton1.setToolTipText("<HTML>Start and stop recording at the given times  (HH:MM:SS),<BR>\ncounted from when the \"Activate Timer\"-button is pressed</HTML>");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jRadioButton1, gridBagConstraints);

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Actual time");
        jRadioButton2.setToolTipText("<HTML>Start and stop recording when the calendar clock of<BR>your system reaches the given times (HH:MM:SS)</HTML>");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jRadioButton2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        jPanel3.add(jPanel1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jPanel2.setBorder(new javax.swing.border.TitledBorder(null, "Recording timer"));
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

        jCheckBox1.setText("Stop in:");
        jCheckBox1.setToolTipText("<HTML>Check box to enable stop-time.<BR>\nIf this box is unchecked, recording will not stop automatically.</HTML>");
        jCheckBox1.addItemListener(new java.awt.event.ItemListener() {
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
        jPanel2.add(jCheckBox1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        jPanel3.add(jPanel2, gridBagConstraints);

        jToggleButton1.setText("Activate Timer");
        jToggleButton1.setToolTipText("<HTML>The Start time must be more than 0 seconds away<BR>for this button to activate.</HTML>");
        jToggleButton1.setActionCommand("Timer active");
        jToggleButton1.setEnabled(false);
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
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
        jPanel3.add(jToggleButton1, gridBagConstraints);

        jButton1.setFont(new java.awt.Font("Tahoma", 0, 10));
        jButton1.setForeground(java.awt.SystemColor.activeCaption);
        jButton1.setText("Close");
        jButton1.setBorder(null);
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setIconTextGap(0);
        jButton1.setMaximumSize(new java.awt.Dimension(27, 13));
        jButton1.setPreferredSize(new java.awt.Dimension(27, 9));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
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
        jPanel3.add(jButton1, gridBagConstraints);

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

    }
    // </editor-fold>//GEN-END:initComponents

    private void recSStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_recSStateChanged
// TODO add your handling code here:
        if (!checkTimes()) syncTimes();
        updateToggleButton();
    }//GEN-LAST:event_recSStateChanged

    private void recMStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_recMStateChanged
// TODO add your handling code here:
        if (!checkTimes()) syncTimes();
        updateToggleButton();
    }//GEN-LAST:event_recMStateChanged

    private void recHStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_recHStateChanged
// TODO add your handling code here:
        if (!checkTimes()) syncTimes();
        updateToggleButton();
    }//GEN-LAST:event_recHStateChanged

    private void stopHStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_stopHStateChanged
// TODO add your handling code here:
        if (!checkTimes()) syncTimes();
        updateToggleButton();
    }//GEN-LAST:event_stopHStateChanged

    private void stopMStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_stopMStateChanged
// TODO add your handling code here:
        if (!checkTimes()) syncTimes();
        updateToggleButton();
    }//GEN-LAST:event_stopMStateChanged

    private void stopSStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_stopSStateChanged
// TODO add your handling code here:
        if (!checkTimes()) syncTimes();
        updateToggleButton();
    }//GEN-LAST:event_stopSStateChanged

    private void jCheckBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBox1ItemStateChanged
// TODO add your handling code here:
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            stopS.setEnabled(true);
            stopM.setEnabled(true);
            stopH.setEnabled(true);
            if (!checkTimes()) syncTimes();
        } else if (evt.getStateChange() == java.awt.event.ItemEvent.DESELECTED) {
            stopS.setEnabled(false);
            stopM.setEnabled(false);
            stopH.setEnabled(false);            
        }

    }//GEN-LAST:event_jCheckBox1ItemStateChanged

    private void jButton1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseExited
// TODO add your handling code here:
        jButton1.setFont(new java.awt.Font("Tahoma", 0, 10));
    }//GEN-LAST:event_jButton1MouseExited

    private void jButton1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseEntered
// TODO add your handling code here:
        jButton1.setFont(new java.awt.Font("Tahoma", 1, 10));
    }//GEN-LAST:event_jButton1MouseEntered

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
// TODO add your handling code here:
        setVisible(false);
        if (myContainer != null) {
            myContainer.pack();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
// TODO add your handling code here:
        if (jRadioButton1.isSelected() && (mode == COUNTUP)) {
            setMode(COUNTDOWN);
            jTextField1.setText("Start in:");
            jCheckBox1.setText("Stop in:");
            restoreTime();
            timerAction();
        }
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
// TODO add your handling code here:
        if (jRadioButton2.isSelected() && (mode == COUNTDOWN)) {
            setMode(COUNTUP);
            jTextField1.setText("Start at:");
            jCheckBox1.setText("Stop at:");        
            restoreTime();
            timerAction();
        }
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
// TODO add your handling code here:
        if (evt.getActionCommand().equals("Timer active")) {
            jToggleButton1.setText("Stop Timer");
            startTimer();
            jToggleButton1.setActionCommand("Timer stopped");
        } else if(evt.getActionCommand().equals("Timer recording")) {
            jToggleButton1.setActionCommand("Timer stopped");
        } else if (evt.getActionCommand().equals("Timer stopped")) {
            jToggleButton1.setText("Activate Timer");
            stopTimer();
            jToggleButton1.setActionCommand("Timer active");
            updateToggleButton();
        }
        repaint();
    }//GEN-LAST:event_jToggleButton1ActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JSpinner recH;
    private javax.swing.JSpinner recM;
    private javax.swing.JSpinner recS;
    private javax.swing.JSpinner stopH;
    private javax.swing.JSpinner stopM;
    private javax.swing.JSpinner stopS;
    // End of variables declaration//GEN-END:variables
    
}
