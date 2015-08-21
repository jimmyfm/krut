/*
 * SoundQuery.java
 *
 * Created on den 29 december 2004, 23:31
 */

package krut.KRUT_GUI;

/**
 *
 * @author  jonte
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import krut.KRUT_GUI.*;

/** This class is a JPanel used to handle the audio options.
 *  The SoundQuery is displayed by the KrutSettings class,
 *  and communicates with the user through the KrutSettings window.
 */
public class SoundQuery extends JPanel implements ActionListener {
    
//    public JFrame queryFrame;
//    public JPanel queryPanel;
    /** The sound sampling frequency in Hz. */
    public int frequency;
    /** An OutputText object that the current class can use to create output. */
    public OutputText myOutput;
    /** true = sound is stereo, false = sound is mono. */
    public boolean stereo;
    /** true = sound is 16-bit, false = sound is 8-bit. */
    public boolean sixteenBit;
    /**  A flag used to signal that the SoundQuery has been changed. */
    public boolean altered = false;
    public java.awt.GridBagConstraints gridBagConstraints;
    /**  The done button. */
    private JButton doneButton;
    /**  Contains the word "Frequency:". */
    private JTextField frequencyText;
    
    /** This method is called to initiate the SoundQuery.
     *  
     *  @param  startF          Default sound sampling frequency in Hz.
     *  @param  startStereo     Default sound quality. true = stereo, false = mono.
     *  @param  startSixteen    Default sound quality. true = 16-bit, false = 8-bit.
     */
    public void init(int startF, boolean startStereo, boolean startSixteen) {
        frequency = startF;
        stereo = startStereo;
        sixteenBit = startSixteen;
        addWidgets();
    }
    
    /** Initiate the GUI. */
    public void addWidgets() {
        
        //Create buttons and labels.
        JRadioButton stereoButton = new JRadioButton("Stereo");
        stereoButton.setMnemonic(KeyEvent.VK_S);
        stereoButton.setActionCommand("Stereo");
        if (stereo) stereoButton.setSelected(true);
        
        JRadioButton monoButton = new JRadioButton("Mono");
        monoButton.setMnemonic(KeyEvent.VK_M);
        monoButton.setActionCommand("Mono");
        if (!stereo) monoButton.setSelected(true);
        
        JRadioButton sixteenButton = new JRadioButton("16 bit sound");
        sixteenButton.setMnemonic(KeyEvent.VK_B);
        sixteenButton.setActionCommand("16 bit sound");
        if (sixteenBit) sixteenButton.setSelected(true);
        
        JRadioButton eightButton = new JRadioButton("8 bit sound");
        eightButton.setMnemonic(KeyEvent.VK_I);
        eightButton.setActionCommand("8 bit sound");
        if (!sixteenBit) eightButton.setSelected(true);
        
        JLabel fQueryLabel = new JLabel("Sample frequency (Hz):");
        
        String[] petStrings = {"11025", "22050", "44100"};
        
        JComboBox petList = new JComboBox(petStrings);
        
        /** If the frequency is not a valid one, this automatically
         *  corrects it to the default value 22050.
         */
        if (frequency == 11025) petList.setSelectedIndex(0);
        else if (frequency == 44100) petList.setSelectedIndex(2);
        else {
            frequency = 22050;
            petList.setSelectedIndex(1);
        }
        
        //Group the radio buttons.
        ButtonGroup stereoGroup = new ButtonGroup();
        stereoGroup.add(stereoButton);
        stereoGroup.add(monoButton);
        
        ButtonGroup bitGroup = new ButtonGroup();
        bitGroup.add(sixteenButton);
        bitGroup.add(eightButton);
        
        //Register a listener for buttons & textfield.
        stereoButton.addActionListener(this);
        monoButton.addActionListener(this);
        sixteenButton.addActionListener(this);
        eightButton.addActionListener(this);
        petList.addActionListener(this);
        
        //Put it all in panels.
        JPanel stereoPanel = new JPanel(new GridLayout(0, 1));
        JPanel bitPanel = new JPanel(new GridLayout(0, 1));
        JPanel inputPanel = new JPanel(new GridLayout(0, 1));
        
        stereoPanel.add(stereoButton);
        stereoPanel.add(monoButton);
        bitPanel.add(sixteenButton);
        bitPanel.add(eightButton);
        inputPanel.add(fQueryLabel);
        inputPanel.add(petList);
        
        add(stereoPanel);
        add(bitPanel);
        add(inputPanel);
    }
    
    /** A radio button has been changed. This method updates
     *  the appropriate parameters, and sets the "altered"-
     *  parameter to true.
     *
     *  @param e    The ActionEvent that caused the change.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "Stereo") {
            stereo = true;
        } else if (e.getActionCommand() == "Mono") {
            stereo = false;
        } else if (e.getActionCommand() == "16 bit sound") {
            sixteenBit = true;
        } else if (e.getActionCommand() == "8 bit sound") {
            sixteenBit = false;
        } else {
            JComboBox cb = (JComboBox) e.getSource();
            String freqString = (String) cb.getSelectedItem();
            try {
                frequency = Integer.parseInt(freqString);
            } catch (NumberFormatException ne) {
                myOutput.out("Strange error" + ne);
            }
        }
        
        //Reset recorder with new parameters.
        
        myOutput.out("New frequency (Hz): " + frequency);
        myOutput.out("Number of channels: " + (stereo ? "stereo" : "mono"));
        myOutput.out("New sample size (bits): " + (sixteenBit ? 16 : 8));
        myOutput.out("");
        altered = true;
    }
}
