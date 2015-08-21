/*
 * FPSQuery.java
 *
 * Created on den 29 december 2004, 23:15
 */

package krut.KRUT_GUI;

/**
 *
 * @author  jonte
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** A class that creates the part of the KrutSettings window
 *  where the FPS is entered.
 */
public class FPSQuery extends JPanel implements ActionListener {
    /** The fps that the next film we be recorded at. */
    public int fps;
    /** The fps that the next film we be played back at .*/
    public int plb;
    /** The output text window for this class. */
    public OutputText myOutput;
    /** Used to signal that the fps has been altered. This
     *  flag should be changed back to false by the user after
     *  the user has dealt with the change.
     */
    public boolean altered = false;
    public java.awt.GridBagConstraints gridBagConstraints;
    
    private char trunc[] = new char[10];
    private JTextField playbackText, fpsText;
    private JLabel msLabel, messageLabel, emptyLabel, emptyLabel2,  topLeftLabel, topRightLabel;
    private JButton fpsButton, playbackButton;
    final private String FPS = "fps";
    final private String PLB = "plb";
    
    /** Create and set up the window.
     *
     *  @param  fps     The initial fps value in both the 
     *                  recording and the playback fields.
     */
    public void init(int fps) {
        this.fps = fps;
        plb = fps;
        addWidgets();
    }
    
    /**
     * Create and add the widgets.
     */
    private void addWidgets() {
        /** Create the two textfields. */
        fpsText = new JTextField(Integer.toString(fps), 2);
        playbackText = new JTextField(Integer.toString(plb), 2);
        /** Print a truncated value of the maximum captime
         *  in ms. */
        String truncer = new String(Double.toString(1000d / fps) + "          ");
        msLabel = new JLabel(truncer.substring(0, 6) + " ms");
        /** Create the other textlabels. */
        messageLabel = new JLabel("Max captime:   ", SwingConstants.LEFT);
        topLeftLabel = new JLabel("Recording fps: ", SwingConstants.LEFT);
        topRightLabel = new JLabel("Playback fps: ", SwingConstants.LEFT);
        emptyLabel = new JLabel("", SwingConstants.LEFT);
        emptyLabel2 = new JLabel("", SwingConstants.LEFT);
        /** Create the two buttons */
        fpsButton = new JButton("Set");
        playbackButton = new JButton("Set");
        
        /** Listen to events from the done buttons. */
        fpsButton.setActionCommand(FPS);
        fpsButton.addActionListener(this);
        playbackButton.setActionCommand(PLB);
        playbackButton.addActionListener(this);
        
        /** Listen to events from the TextFields. */
        fpsText.setActionCommand(FPS);
        fpsText.addActionListener(this);
        playbackText.setActionCommand(PLB);
        playbackText.addActionListener(this);
        
        add(topLeftLabel);
        add(emptyLabel);
        add(topRightLabel);
        add(emptyLabel2);
        add(fpsText);
        add(fpsButton);
        add(playbackText);
        add(playbackButton);
        add(messageLabel);
        add(msLabel);
    }
    
    /** A new recording or playback fps value has been entered.
     * 
     *  @param  event   The action event that caused this method to be called.
     */
    public void actionPerformed(ActionEvent event) {
        String cmd = event.getActionCommand();
        /** Check if the recording fps value has been changed. */
        if (FPS.equals(cmd)) {
            try {
                fps = Integer.parseInt(fpsText.getText());
            } catch (NumberFormatException ne) {
                myOutput.out("Invalid entry: " + ne);
            }
            if (fps < 1) {
                myOutput.out("Invalid entry: " + fps);
                fps = 1;
            }
            /** Everytime the fps value is changed,
             *  the playback value is changes as well.
             *  This is supposed to be for convenience,
             *  and hopefully usually is.
             */
            plb = fps;
            altered = true;
        /** Check if the playback fps value has been changed. */
        } else if (PLB.equals(cmd)) {
            try {
                plb = Integer.parseInt(playbackText.getText());
            } catch (NumberFormatException ne) {
                myOutput.out("Invalid entry: " + ne);
            }
            if (plb < 1) {
                myOutput.out("Invalid entry: " + plb);
                plb = 1;
            }
            altered = true;
        }
        myOutput.out("New fps: " + fps);
        myOutput.out("New playback fps: " + plb);
        myOutput.out("Frame cap time should not exceed: " + (1000d / fps));
        myOutput.out("");
        /** Update the textfields. */
        fpsText.setText(Integer.toString(fps));
        playbackText.setText(Integer.toString(plb));
        /** Print a truncated value of the maximum captime
         *  in ms. */
        String truncer = new String(Double.toString(1000d / fps) + "          ");
        msLabel.setText(truncer.substring(0, 6) + " ms");
    }
}
