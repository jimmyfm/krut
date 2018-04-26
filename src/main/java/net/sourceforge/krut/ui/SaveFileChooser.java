/*
 * SaveFileChooser.java
 *
 * Created on den 29 december 2004, 23:20
 */

package net.sourceforge.krut.ui;

/**
 *
 * @author  jonte
 */

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;

/** This class is a JPanel used to handle the file names of the save files.
 *  The SaveFileChooser is displayed by the KrutSettings class,
 *  and communicates with the user through the KrutSettings window.
 *
 *  The SaveFileChooser contains several methods for handling the
 *  changing of save file names, including filterFile, which should be
 *  called any time a save file name is changed.
 */
public class SaveFileChooser extends JPanel implements ActionListener {
    /** The file for the screenshot (jpg). */
    public File imageFile;
    /** The file for the audio (wav). */
    public File audioFile;
    /** The file for the movie (mov). */
    public File videoFile;
    /** An OutputText object that the current class can use to create output. */
    public OutputText myOutput;
    public java.awt.GridBagConstraints gridBagConstraints;
    /** This is used to change the textfields for the save files
     *  in the KrutSettings object. In the original program there
     *  were no such textfields. They were added in the KrutSettings
     *  window to avoid large changes to the SaveFileChooser window.
     */
    public KrutSettings myKrutSettings;
    /** Newline. */
    static private final String newline = "\n";
    /** The button to change the movie save file. */
    private JButton movieButton;
    /** The button to change the audio save file. */
    private JButton audioButton;
    /** The button to change the screenshot save file. */
    private JButton imageButton;
    /**  The JFileChooser used to change save files. */
    private JFileChooser fc;
    /**  A string containing the file type extensions. This parameter
     *  is used by the "old" part of the code, which handles user interaction.
     *  If the accepted file types are to be changed, the actionPerformed
     *  method below should be modified to make the new file formats
     *  appear in the SaveFileChooser.
     */
    private String myExtension = "";
    
    
    /** This method is called to initiate the SaveFileChooser.
     *  
     *  @param startImage   The initial save file for the screenshot.
     *  @param startAudio   The initial save file for the audio.
     *  @param startVideo   The initial save file for the movie.
     */
    public void init(File startImage, File startAudio, File startVideo) {
        imageFile = startImage;
        audioFile = startAudio;
        videoFile = startVideo;
        initComponents();
    }
    
    /** Initializes the GUI in the SaveFileChooser. */
    private void initComponents() {
        //Create a file chooser
        fc = new JFileChooser();
        // allow only files of correct type        
        SaveFileFilter filt = new SaveFileFilter();		
        fc.setFileFilter(filt);
        fc.setAcceptAllFileFilterUsed(false);
        
        // create the three buttons.
        movieButton = new JButton("Browse",
                createImageIcon("../images/Save16.gif"));
        movieButton.addActionListener(this);
        
        audioButton = new JButton("Browse",
                createImageIcon("../images/Save16.gif"));
        audioButton.addActionListener(this);
        
        imageButton = new JButton("Browse",
                createImageIcon("../images/Save16.gif"));
        imageButton.addActionListener(this);
        
        JLabel movieLabel = new JLabel("Movie file");
        JLabel audioLabel = new JLabel("Sound file");
        JLabel imageLabel = new JLabel("Screen cap file  ");
                
// 		queryPanel = new JPanel(new GridLayout(3, 2));
        
        add(movieLabel);
        add(movieButton);
        add(audioLabel);
        add(audioButton);
        add(imageLabel);
        add(imageButton);        
    }
    
    
    /** Retrieve the file extension of a file.
     *  
     *  @param  f   A file with an extension.
     *  @return A String containing the extension of the file.
     */
    public String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        
        if ((i > 0) &&  (i < s.length() - 1)) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
    /** Returns a file name minus the extension
     *  @param  f   The file
     *  @return    A String containing the file nane minus the file extension
     */
    public String getFileWithoutExtension(File f) {
        String name = f.getAbsolutePath();
        int i = name.lastIndexOf('.');
        
        if (i > 0 &&  i < name.length() - 1) {
            name = name.substring(0, i);
        }
        return name;
    }
    
    /** This can be used directly to change a file for another file
     *  with the same name, but a higher index number. This method is a
     *  direct alternative to the filterFile() method. This method
     *  is used by the filterFile() method, as well as the
     *  restoreGUI() method in Run_KRUT.
     *
     *  @param  f   The file to check.
     *  @return    A new file with the next index number, irregardless of
     *              whether such a file already exists or not.
     */
    public File getNextFile(File f) {
            String newFileName = getFileWithoutExtension(f);
            String extension = getExtension(f);
            int pos = newFileName.length();
            int tempNumber, number = 0;
            /** This will find out if the end of the file name
             *  consists of an integer, and if it does it will add
             *  one to the integer and create a new file.
             */
            try {
                while (0 < pos) {
                    /** Note: This has to be here. */
                    pos--;
                    tempNumber = Integer.parseInt(newFileName.substring(pos));
                    number = tempNumber;
                }
            } catch (NumberFormatException ne) {
                pos++;
            }
            newFileName = newFileName.substring(0, pos) + (number + 1);
            if (extension != null) newFileName += ("." + extension);
            return new File(newFileName);        
    }
    
    /** Checks if a file exist, and if files should not be overwritten.
     *  If true, another file is returned
     *  with the same file name, but a higher number.
     *
     *  @param  f   The file to check.
     *  @return    A new file that does not exist.
     */
    public File filterFile(File f) {
        /** This try-catch is in case the SaveFileChooser is poorly
         *  initiated. This could happend in the ones being used by
         *  the Sampler and the ScreenGrabber.
         */
        try {
            if (!myKrutSettings.saveEnumCheckbox.isSelected() && f.exists()) {
                return filterFile(getNextFile(f));
            } else {
                return f;
            }
        } catch (Exception e) {
            System.out.println(e);
            return f;
        }
    }
    
    /** A FileFilter for the FileChooser. The filter will
     *  accept directories, jpg-files, and files of the type
     *  decided by the global parameter myExtension.
     */
    public class SaveFileFilter extends javax.swing.filechooser.FileFilter {
        
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            
            String extension = getExtension(f);
            if (extension != null) {
                if (extension.equals(myExtension)) {	// uses class variable myExtension
                    return true;						// to decide if filetype is correct.
                } else if (myExtension.toLowerCase().equals("jpg") &&
                        extension.equals("jpeg")) {
                    return true;
                } else {
                    return false;
                }
            }
            
            return false;
        }
        
        public String getDescription() {
            return myExtension;
        }
    }
    
    /** Displays a file chooser, asking the user to select a file.
     *  Returns a file with a proper extension. jpg-files are
     *  automatically approved. Other file types are compared
     *  to the file extension in the global parameter myExtension,
     *  and are accepted if they are a match. If a file is not
     *  accepted, the extension in the global parameter myExtension
     *  is added to the file name, after which the file is returned.
     *
     *  @param  currentFile The save file.
     *  @return The save file with a proper extension. null if
     *          the user did not choose a file (cancelled).
     */
    public File selectFile(File currentFile) {
        File saveFile = null;
        fc.setSelectedFile(currentFile);
        fc.setCurrentDirectory(currentFile);
        int returnVal = fc.showSaveDialog(SaveFileChooser.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String extension = getExtension(file);
            // make sure extension is correct.
            if (extension != null) {
                if (extension.equals(myExtension)) {
                    saveFile = file;
                } else if (myExtension.toLowerCase().equals("jpg") &&
                        extension.equals("jpeg")) {
                    saveFile = file;
                } else {
                    saveFile = new File(file.getAbsolutePath() + "." + myExtension);
                }
            } else {
                saveFile = new File(file.getAbsoluteFile() + "." + myExtension);
            }
        }
        return saveFile;
    }
    
    /** A file type button has been clicked by the user.
     *  This method determines which button, and then calls
     *  the selectFile method to open a FileChooser for the appropriate
     *  file type.
     *
     *  @param e    The ActionEvent.
     */
    public void actionPerformed(ActionEvent e) {
        
        File tempFile;
        // check if movie button is selected.
        if (e.getSource() == movieButton) {
            myExtension = "mov";
            // get selected new file.
            tempFile = selectFile(videoFile);
            if (tempFile != null) videoFile = filterFile(tempFile);
            myOutput.out("New video file: " + videoFile.getAbsolutePath());
            myOutput.out("");
        // check if audio button is selected.
        } else if (e.getSource() == audioButton) {
            myExtension = "wav";
            // get selected new file.
            tempFile = selectFile(audioFile);
            if (tempFile != null) audioFile = filterFile(tempFile);
            // change save file in recorder.
            myOutput.out("New audio file: " + audioFile.getAbsolutePath());
            myOutput.out("");
        // check if image button is selected.
        } else if (e.getSource() == imageButton) {
            myExtension = "jpg";
            tempFile = selectFile(imageFile);
            if (tempFile != null) imageFile = filterFile(tempFile);
            myOutput.out("New screenshot file: " + imageFile.getAbsolutePath());
            myOutput.out("");
        }
        // Changing the text fields in krutSettings.
        myKrutSettings.changeFileNames();
    }
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = SaveFileChooser.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}
