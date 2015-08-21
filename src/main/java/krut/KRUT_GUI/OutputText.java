/*
 * OutputText.java
 *
 * Created on den 29 december 2004, 23:11
 */

package krut.KRUT_GUI;

/**
 *
 * @author  jonte
 */

import java.awt.*;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JFrame;

/** This class is a very simple output window.
 *  The out() method is used to print lines
 *  of text.
 */
public class OutputText  {
    
    /** The text area where the output is printed.
     *  This parameter is public in case there
     *  are parts of the program remaining that still
     *  print directly by adding to this output. Also,
     *  to print in that way would give more flexibility
     *  that the out() method offers.
     */
    public JTextArea output;
    /** The frame for this window. This is public becaise
     *  Run_KRUT uses the setVisible() method of this class.
     */
    public JFrame outFrame;
    /** The scroll pane for this window.
     */
    private JScrollPane scrollPane;
    /** Just a string representation of new line.
     */
    private String newline = "\n";

    /** true if the output window is initiated, false if not.
     *  This parameter is changed at the end of the init
     *  method.
     */
    public boolean inited = false;
    
    /** Just gives the content pane for this frame.
     *
     *  @return     A Container containing the ContentPane
     *              for outFrame.
     */
    private Container createContentPane() {
        /** Create the content-pane-to-be. */
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setOpaque(true);
        
        /** Create a scrolled text area. */
        output = new JTextArea(5, 30);
        output.setEditable(false);
        scrollPane = new JScrollPane(output);
        
        /** Add the text area to the content pane. */
        contentPane.add(scrollPane, BorderLayout.CENTER);
        
        return contentPane;
    }
    
    /** Can be used to wait for this class to be ready to
     * accept output. Users are woken when OutputText is
     * ready.
     *
     * The inited flag must be checked in order
     * to safely determine if OutputText is ready, upon
     * return from this method.
     */
    public synchronized void hold() {
        try {
            wait();
        } catch (InterruptedException ie) {
            System.err.println(ie);
        }
    }
    
    /** Wakes up users waiting for the OutputText to finish
     * initializing. This method is called when OutputText
     * is ready.
     */
    public synchronized void wakeUp() {
        notifyAll();
    }
    
    
    /** Prints a line in the output window. This method
     *  works the same way as System.out.println(),
     *  except for this output window.
     *
     *  @param  outString   A string containg the line to be printed.
     */
    public void out(String outString) {
        output.append(outString + newline);
        output.setCaretPosition(output.getDocument().getLength());
    }
    
    /** Initiate the output window. This method will set
     *  inited = true when the output window is initiated
     *  and ready to accept output.
     *
     *  @param  xPos    The x position on the screen where
     *                  the window should appear.
     *  @param  yPos    The y position on the screen where
     *                  the window should appear.
     */
    public void init(int xPos, int yPos) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        
        /** Create and set up the window. */
        outFrame = new JFrame("Output window");
        outFrame.setLocation(xPos, yPos);
        outFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        
        outFrame.setContentPane(this.createContentPane());
        
        /** Display the window. */
        outFrame.setSize(450, 260);
        outFrame.setVisible(true);
        inited = true;
        /** In case there are any threads waiting for this window
         *  to be initiated, they are now woken.
         */
        wakeUp();
    }
}
