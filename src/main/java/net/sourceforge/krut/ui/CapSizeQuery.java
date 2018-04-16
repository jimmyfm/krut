/*
 * CapSizeQuery.java
 *
 * Created on den 29 december 2004, 23:08
 */

package net.sourceforge.krut.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/** This class is a JPanel used to handle the size of the capture area.
 *  The CapSizeQuery is displayed by the KrutSettings class,
 *  and communicates with the user through the KrutSettings window.
 */
public class CapSizeQuery extends JPanel implements ActionListener {
    
	private static final long serialVersionUID = 1L;
	/** An OutputText object that the current class can use to create output. */
    public OutputText myOutput;
    /** A flag to signal that the capture sizes have changed.*/
    public boolean altered = false;
    /** The x coordinate of the upper left corner of the capture area. */
    public int xVal;
    /** The y coordinate of the upper left corner of the capture area. */
    public int yVal;
    /** The width of the capture area. */
    public int widthVal;
    /** The height of the capture area. */
    public int heightVal;
    public java.awt.GridBagConstraints gridBagConstraints;
    private JTextField xText, yText, widthText, heightText;
    private JLabel xLabel, yLabel, widthLabel, heightLabel, emptyLabel;
    private JButton doneButton;
    
    /** Create a CapSizeQuery with the specified dimensions
     *  
     *  @param  xPos    The x coordinate of the upper left corner of the capture area.
     *  @param  yPos    The y coordinate of the upper left corner of the capture area.
     *  @param  width   The width of the capture area.
     *  @param  height  The height of the capture area.
     */
    public void init(int xPos, int yPos, int width, int height) {
        //Create and set up the window.
        xVal = xPos;
        yVal = yPos;
        widthVal = width;
        heightVal = height;
        addWidgets();
    }
    
    /** Create and add the widgets for the GUI. */
    private void addWidgets() {
        //Create widgets.
        xText = new JTextField(Integer.toString(xVal), 2);
        xText.addActionListener(this);
        yText = new JTextField(Integer.toString(yVal), 2);
        yText.addActionListener(this);
        widthText = new JTextField(Integer.toString(widthVal), 2);
        widthText.addActionListener(this);
        heightText = new JTextField(Integer.toString(heightVal), 2);
        heightText.addActionListener(this);
        xLabel = new JLabel("X Pos:                         ", SwingConstants.LEFT);
        yLabel = new JLabel("Y Pos:", SwingConstants.LEFT);
        widthLabel = new JLabel("Width:", SwingConstants.LEFT);
        heightLabel = new JLabel("Height:", SwingConstants.LEFT);
        emptyLabel = new JLabel("", SwingConstants.LEFT);
        doneButton = new JButton("Set");
        
        //Listen to events from the done button.
        doneButton.addActionListener(this);
        
        add(xLabel);
        add(xText);
        add(yLabel);
        add(yText);
        add(widthLabel);
        add(widthText);
        add(heightLabel);
        add(heightText);
        add(emptyLabel);
        add(doneButton);
    }
    
    /** This updates the numbers in the textfields,
     *  but does not update the actual values of the
     *  capture area on the screen.
     *  In order to change the capture area on the
     *  screen, first call this method, then call
     *  actionPerformed(new actionEvent()).
     *
     *  @param  xPos1   The top left corner x coordinate of the new capture area.
     *  @param  yPos1   The top left corner y coordinate of the new capture area.
     *  @param  xPos2   The bottom right corner x coordinate of the new capture area.
     *  @param  yPos2   The bottom right corner y coordinate of the new capture area.
     */
    public void updateNumbersOnly(int xPos1, int yPos1, int xPos2, int yPos2) {
        int width = xPos2 - xPos1;
        int height = yPos2 - yPos1;
        xText.setText(Integer.toString(xPos1));
        yText.setText(Integer.toString(yPos1));
        widthText.setText(Integer.toString(width));
        heightText.setText(Integer.toString(height));
    }
    
    /** Can be called to change the textfields back to the values stored in
     *  xVal, yVal, widthVal and heightVal.
     */
    public void resetTextFields() {
        xText.setText(Integer.toString(xVal));
        yText.setText(Integer.toString(yVal));
        widthText.setText(Integer.toString(widthVal));
        heightText.setText(Integer.toString(heightVal));
    }
    
    /** This method is automatically called when the
     *  text fields for capture size are changed by the
     *  user.
     *
     *  @param  event   The action event that caused this method to be called.
     */
    public void actionPerformed(ActionEvent event) {
        try {
            xVal = Integer.parseInt(xText.getText());
            yVal = Integer.parseInt(yText.getText());
            widthVal = Integer.parseInt(widthText.getText());
            heightVal = Integer.parseInt(heightText.getText());
        } catch (NumberFormatException ne) {
            myOutput.out("Invalid entry, only numbers. " + ne);
        }
        if (xVal < 0) {
            myOutput.out("Invalid entry: " + xVal);
            xVal = 0;
        }
        if (yVal < 0) {
            myOutput.out("Invalid entry: " + yVal);
            yVal = 0;
        }
        if (widthVal < 1) {
            myOutput.out("Invalid entry: " + widthVal);
            widthVal = 1;
        }
        if (heightVal < 1) {
            myOutput.out("Invalid entry: " + heightVal);
            heightVal = 1;
        }
        myOutput.out("Cap area changed to:");
        myOutput.out(xVal + ", " + yVal + ", "
                + widthVal + ", " + heightVal);
        myOutput.out("To see the selected area, press the snap button.");
        myOutput.out("");
        xText.setText(Integer.toString(xVal));
        yText.setText(Integer.toString(yVal));
        widthText.setText(Integer.toString(widthVal));
        heightText.setText(Integer.toString(heightVal));
        altered = true;
    }
}
