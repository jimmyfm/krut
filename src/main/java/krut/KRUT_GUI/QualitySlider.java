/*
 * QualitySlider.java
 *
 * Created on den 29 december 2004, 23:17
 */

package krut.KRUT_GUI;

/**
 *
 * @author  jonte
 */
import java.awt.Component;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/** This class is a JPanel containing a JSlider, used to handle the
 *  quality of video encoding.
 *  The QualitySlider is displayed by the KrutSettings class,
 *  and communicates with the user through the KrutSettings window.
 */
public class QualitySlider extends JPanel
        implements ChangeListener {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1508918801750073615L;
	/** Video encoding quality, between 0 (lowest) and 100 (highest).
     *  Video is encoded as JPG images, so the present parameter is a
     *  measure of JPG compression.
     */
    public int quality;
    /** An OutputText object that the current class can use to create output. */
    public OutputText myOutput;
    /** Set to true if the slider has been changed by the user. */
    public boolean altered = false;
    public java.awt.GridBagConstraints gridBagConstraints;
    
    /** A text label above the slider. */
    private JLabel sliderLabel;
    /** The JSlider used to display/change the video encoding quality. */
    private JSlider encQuality;
    /** Used to display the right scale of values below the JSlider. */
    private Dictionary<Integer, JLabel> labelTable;
    
    /** This method is called to initiate the QualitySlider.
     *  
     *  @param startQuality The initial video encoding quality
     *                      given as an integer between 0 (lowest)
     *                      and 100 (highest).
     */
    public void init(int startQuality) {
        quality = startQuality;
        initComponents();
    }
    
    /** Initializes the GUI in the QualitySlider. */
    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
                
        /** Create the label. truncate to 2 decimals. */
        String truncer = new String(Float.toString(quality / 100f) + "00");
        sliderLabel = new JLabel("Encoding quality : " +
                truncer.substring(0, 4), JLabel.CENTER);
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        /** Create the slider. */
        encQuality = new JSlider(0,100, quality);
        encQuality.addChangeListener(this);
        encQuality.setMajorTickSpacing(25);
        encQuality.setMinorTickSpacing(5);
        encQuality.setPaintTicks(true);
        
        /** Create the label table. */
        labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put( new Integer( 0 ), new JLabel("0.00") );
        labelTable.put( new Integer( 25 ), new JLabel("0.25") );
        labelTable.put( new Integer( 50 ), new JLabel("0.50") );
        labelTable.put( new Integer( 75 ), new JLabel("0.75") );
        labelTable.put( new Integer( 100 ), new JLabel("1.00") );
        encQuality.setLabelTable( labelTable );
        encQuality.setPaintLabels(true);
        
        /** Put everything together. */
        add(sliderLabel);
        add(encQuality);
        setBorder(BorderFactory.createEmptyBorder(2,10,2,10));        
    }
    
    /** Something has changed with the slider.
     *
     *  @param  e   The Event that caused the change.
     */
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
            quality = (int)source.getValue();
            myOutput.out("New encoding quality : " + quality / 100f);
            myOutput.out("");
            altered = true;
            /** Update label with new, truncated value. */
            String truncer = new String(Float.toString(quality / 100f) + "00");
            sliderLabel.setText("Encoding quality : " + truncer.substring(0, 4));
        }
    }
}
