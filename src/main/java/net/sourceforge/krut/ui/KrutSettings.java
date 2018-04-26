package net.sourceforge.krut.ui;

import java.awt.Rectangle;

import net.sourceforge.krut.Settings;

/**
 * This is a class that is used to display many user interface functions. In
 * earlier versions of the program, most of the user interface functions were
 * handled through free-floating windows. These windows were then collected
 * inside the GUI created by this class. Because of this, there are both user
 * manageable settings for the program handled directly by this class, and also
 * several settings handled by other classes, which in turn are displayed
 * through this class.
 *
 * @since 18. december 2006, 22:52
 * @author Jonas
 */
public class KrutSettings extends javax.swing.JFrame {
    
    /** If the present KrutSettings window is properly initiated,
     *  vCMBItem holds the Record Video checkbox from the main
     *  Krut window Menu. This parameter is used for easy access
     *  in case the Record Video checkbox from the present
     *  KrutSetting window is changed.
     */
    public javax.swing.JCheckBoxMenuItem vCBMItem;
    
    /** If the present KrutSettings window is properly initiated,
     *  aCMBItem holds the Record Audio checkbox from the main
     *  Krut window Menu. This parameter is used for easy access
     *  in case the Record Audio checkbox from the present
     *  KrutSetting window is changed.
     */
    public javax.swing.JCheckBoxMenuItem aCBMItem;

    /** If the present KrutSettings window is properly initiated,
     *  mCMBItem holds the Record Mouse Pointer checkbox from the main
     *  Krut window Menu. This parameter is used for easy access
     *  in case the Record Mouse Pointer checkbox from the present
     *  KrutSetting window is changed.
     */
    public javax.swing.JCheckBoxMenuItem mCBMItem;

    /** How often we sample for the mouse position in ms.
     *  The sampling is done in startMouseTimer. */
    public int mouseSampleDelay = 100;
    
    /** True if the setting window is initiated, false if not
     */
    public boolean isInited = false;
    
    /** Mouse postition in stored here when CTRL is pressed */
    private java.awt.Point mouseStartPos;
    
    /** This is used to store the current mouse position
     *  in startMouseTimer. */
    private java.awt.Point mousePos;

    
    /** This is a flag to keep track of whether the capture size
     *  is being changed. */
    private boolean ctrlDown = false;
        
    /** This is a flag to keep track of whether the capture size
     *  is being changed. */
    private boolean capButtonPressed = false;
    
    /** Times used to track the mouse position for both
     *  the mouse position window, and the capture size
     *  window */
    public javax.swing.Timer mouseTimer;    
    
    /** Creates new form KrutSettings */
    public KrutSettings(int startFps, int startEncQuality,
        boolean startStereo, boolean startSixteen, int startFrequency) {

        initComponents();
        
        soundQuery1.init(startFrequency, startStereo, startSixteen);
        qualitySlider1.init(startEncQuality);
        fPSQuery1.init(startFps);
        Rectangle capRect = Settings.getCaptureRect();
        capSizeQuery1.init(capRect.x, capRect.y, capRect.width, capRect.height);
        saveFileChooser1.init(null, null, null);
        saveFileChooser1.myKrutSettings = this;

        pack();
        startMouseTimer();
        isInited = true;
    }
            
    /** Change the save file names in the text fields for
     *  the save files. This is done by copying the absolute paths
     *  from the SaveFileChooser. Notice that the actual filenames
     *  are often set elsewhere
     *  (normally the checkInited() method of Run_KRUT).
     */
    public void changeFileNames() {
        if (saveFileChooser1.videoFile != null) {
            movieFile.setText(saveFileChooser1.videoFile.getAbsolutePath());
        }
        if (saveFileChooser1.audioFile != null) {
            audioFile.setText(saveFileChooser1.audioFile.getAbsolutePath());
        }
        if (saveFileChooser1.imageFile != null) {
            screenFile.setText(saveFileChooser1.imageFile.getAbsolutePath());
        }
    }
        
    /**  Return the current CapSizeQuery of the KrutSettings window.
     *
     *  @return The current CapSizeQuery object.
     */
    public CapSizeQuery getCapSizeQuery() {
        return capSizeQuery1;
    }
    
    /**  Return the current SoundQuery of the KrutSettings window.
     *
     *  @return The current SoundQuery object.
     */
    public SoundQuery getSoundQuery() {
        return soundQuery1;
    }
    
    /**  Return the current FPSQuery of the KrutSettings window.
     *
     *  @return The current FPSQuery object.
     */
    public FPSQuery getFPSQuery() {
        return fPSQuery1;
    }
    
    /**  Return the current QualitySlider of the KrutSettings window.
     *
     *  @return The current QualitySlider object.
     */
    public QualitySlider getQualitySlider() {
        return qualitySlider1;
    }
    
    /**  Return the current SaveFileChooser of the KrutSettings window.
     *
     *  @return The current SaveFileChooser object.
     */
    public SaveFileChooser getSaveFileChooser() {
        return saveFileChooser1;
    }
    
    /** Set the Record Video checkbox of the main Krut window.
     *  This checkbox is called whenever the the Record Video 
     *  checkbox in this KrutSettings window is changed.
     *
     *  @param vBox The Record Video checkbox of the main Krut window.
     */
    public void setVChkBoxMenuItem(javax.swing.JCheckBoxMenuItem vBox) {
        vCBMItem = vBox;
    }

    /** Set the Record Audio checkbox of the main Krut window.
     *  This checkbox is called whenever the the Record Audio 
     *  checkbox in this KrutSettings window is changed.
     *
     *  @param aBox The Record Audio checkbox of the main Krut window.
     */    
    public void setAChkBoxMenuItem(javax.swing.JCheckBoxMenuItem aBox) {
        aCBMItem = aBox;
    }

    /** Set the Record Mouse Pointer checkbox of the main Krut window.
     *  This checkbox is called whenever the the Record Mouse Pointer 
     *  checkbox in this KrutSettings window is changed.
     *
     *  @param mBox The Record Video checkbox of the main Krut window.
     */
    public void setMChkBoxMenuItem(javax.swing.JCheckBoxMenuItem mBox) {
        mCBMItem = mBox;
    }
    
    /**  Change the value of the Record Video checkbox in the
     *  KrutSettings window.
     *
     *  @param  newVal  A boolean representing the new value of the
     *                  checkbox.
     */ 
    public void setVideoCheckBox(boolean newVal) {
             videoOutCheckbox.setSelected(newVal);
    }

    /**  Change the value of the Record Audio checkbox in the
     *  KrutSettings window.
     *
     *  @param  newVal  A boolean representing the new value of the
     *                  checkbox.
     */ 
    public void setAudioCheckBox(boolean newVal) {
             recAudioCheckbox.setSelected(newVal);
    }

    /**  Change the value of the Record Mouse Pointer checkbox in the
     *  KrutSettings window.
     *
     *  @param  newVal  A boolean representing the new value of the
     *                  checkbox.
     */ 
    public void setMouseCheckBox(boolean newVal) {
             mouseCheckbox.setSelected(newVal);
    }

    /** The mouse timer is the timer that
     *  checks the mouse position on the screen
     *  for the GUI, while recording isn't running.
     *  The timer is stopped when recording is started,
     *  and started again when recording is stopped.
     */
    public void startMouseTimer() {
        java.awt.event.ActionListener timerTask = 
                new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mousePos = java.awt.MouseInfo.getPointerInfo().getLocation();
                xValText.setText(Integer.toString(mousePos.x));
                yValText.setText(Integer.toString(mousePos.y));
                if (ctrlDown && capButtonPressed) {
                    capSizeQuery1.updateNumbersOnly(
                            mouseStartPos.x,
                            mouseStartPos.y,
                            mousePos.x,
                            mousePos.y);
                }   
            }
        };
        mouseTimer = new javax.swing.Timer(mouseSampleDelay, timerTask);
        mouseTimer.start();
    }
    
    /** The mouse timer is the timer that
     *  checks the mouse position on the screen
     *  for the GUI, while recording isn't running.
     *  The timer is stopped when recording is started,
     *  and started again when recording is stopped.
     */
    public void stopMouseTimer() {
        mouseTimer.stop();
    }
       
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        videoPanel = new javax.swing.JPanel();
        capSizePanel = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jTextArea1 = new javax.swing.JTextArea();
        capQueryPanel = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        capSizeQuery1 = new net.sourceforge.krut.ui.CapSizeQuery();
        miscPanel = new javax.swing.JPanel();
        videoOutCheckbox = new javax.swing.JCheckBox();
        mouseCheckbox = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel4 = new javax.swing.JPanel();
        jToolBar4 = new javax.swing.JToolBar();
        jPanel1 = new javax.swing.JPanel();
        mousePosText = new javax.swing.JTextField();
        mousePosText2 = new javax.swing.JTextField();
        xText = new javax.swing.JTextField();
        xValText = new javax.swing.JTextField();
        yText = new javax.swing.JTextField();
        yValText = new javax.swing.JTextField();
        qSliderPanel = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        qualitySlider1 = new net.sourceforge.krut.ui.QualitySlider();
        fpsQueryPanel = new javax.swing.JPanel();
        jToolBar3 = new javax.swing.JToolBar();
        fPSQuery1 = new net.sourceforge.krut.ui.FPSQuery();
        jLabel1 = new javax.swing.JLabel();
        jLabel1.setVisible(false);
        audioPanel = new javax.swing.JPanel();
        miscAudio = new javax.swing.JPanel();
        recAudioCheckbox = new javax.swing.JCheckBox();
        syncCheckbox = new javax.swing.JCheckBox();
        jSeparator2 = new javax.swing.JSeparator();
        sQueryPanel = new javax.swing.JPanel();
        soundQueryToolBar = new javax.swing.JToolBar();
        soundQuery1 = new net.sourceforge.krut.ui.SoundQuery();
        mainSavePanel = new javax.swing.JPanel();
        saveToolbar = new javax.swing.JToolBar();
        savePanel = new javax.swing.JPanel();
        saveEnumCheckbox = new javax.swing.JCheckBox();
        currentFilesPanel = new javax.swing.JPanel();
        movieFile = new javax.swing.JTextField();
        audioFile = new javax.swing.JTextField();
        screenFile = new javax.swing.JTextField();
        saveFileChooser1 = new net.sourceforge.krut.ui.SaveFileChooser();
        logoPanel = new javax.swing.JPanel();
        jFormattedTextField3 = new javax.swing.JFormattedTextField();
        jButton2 = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle("Krut Settings");
        jPanel2.setLayout(new java.awt.GridBagLayout());

        videoPanel.setLayout(new java.awt.GridBagLayout());

        videoPanel.setBorder(new javax.swing.border.TitledBorder("Video"));
        capSizePanel.setLayout(new java.awt.GridBagLayout());

        capSizePanel.setBorder(new javax.swing.border.TitledBorder("Capture Area"));
        try {
            jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("../mus.PNG")));
        } catch (NullPointerException ne) {
            System.out.println("Image missing");
        }
        jButton1.setToolTipText("Select capture area using mouse and CTRL-button");
        jButton1.setMaximumSize(new java.awt.Dimension(25, 23));
        jButton1.setMinimumSize(new java.awt.Dimension(25, 23));
        jButton1.setPreferredSize(new java.awt.Dimension(25, 23));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jButton1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jButton1FocusLost(evt);
            }
        });
        jButton1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jButton1KeyReleased(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 0);
        capSizePanel.add(jButton1, gridBagConstraints);

        jTextArea1.setBackground(javax.swing.UIManager.getDefaults().getColor("TextField.inactiveBackground"));
        jTextArea1.setEditable(false);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("To select capture area, press this mouse pointer button. Then press and hold CTRL-button at the top left corner of the capture area. Move mouse to the bottom right corner of the capture area, and release CTRL-button. To abort at any time, press ESC.");
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 255), 1, true), " Set capture area with mouse ", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        jTextArea1.setPreferredSize(new java.awt.Dimension(220, 142));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 0);
        capSizePanel.add(jTextArea1, gridBagConstraints);

        capQueryPanel.setLayout(new java.awt.BorderLayout());

        capSizeQuery1.setLayout(new java.awt.GridLayout(5, 2));

        capSizeQuery1.setBorder(new javax.swing.border.EtchedBorder());
        jToolBar1.add(capSizeQuery1);

        capQueryPanel.add(jToolBar1, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        capSizePanel.add(capQueryPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        videoPanel.add(capSizePanel, gridBagConstraints);

        miscPanel.setLayout(new java.awt.GridBagLayout());

        miscPanel.setBorder(new javax.swing.border.TitledBorder("Misc."));
        videoOutCheckbox.setSelected(true);
        videoOutCheckbox.setText("Video output");
        videoOutCheckbox.setBorder(null);
        videoOutCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                videoOutCheckboxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        miscPanel.add(videoOutCheckbox, gridBagConstraints);

        mouseCheckbox.setSelected(true);
        mouseCheckbox.setText("Show mouse");
        mouseCheckbox.setToolTipText("<html>This value can be changed during recording,<br>\nby this button, the main menu, or by pressing Alt-3,<br>\nto stop or start recording the mouse pointer.</html>");
        mouseCheckbox.setBorder(null);
        mouseCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mouseCheckboxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        miscPanel.add(mouseCheckbox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        miscPanel.add(jSeparator1, gridBagConstraints);

        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(new javax.swing.border.EtchedBorder());
        mousePosText.setEditable(false);
        mousePosText.setFont(new java.awt.Font("Tahoma", 1, 11));
        mousePosText.setText("Current");
        mousePosText.setBorder(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel1.add(mousePosText, gridBagConstraints);

        mousePosText2.setEditable(false);
        mousePosText2.setFont(new java.awt.Font("Tahoma", 1, 11));
        mousePosText2.setText("mouse pos.");
        mousePosText2.setBorder(null);
        mousePosText2.setPreferredSize(new java.awt.Dimension(68, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel1.add(mousePosText2, gridBagConstraints);

        xText.setEditable(false);
        xText.setFont(new java.awt.Font("Tahoma", 1, 11));
        xText.setText("x:");
        xText.setBorder(null);
        xText.setPreferredSize(new java.awt.Dimension(19, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 0);
        jPanel1.add(xText, gridBagConstraints);

        xValText.setEditable(false);
        xValText.setPreferredSize(new java.awt.Dimension(55, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 3);
        jPanel1.add(xValText, gridBagConstraints);

        yText.setEditable(false);
        yText.setFont(new java.awt.Font("Tahoma", 1, 11));
        yText.setText("y:");
        yText.setBorder(null);
        yText.setPreferredSize(new java.awt.Dimension(19, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 3, 0);
        jPanel1.add(yText, gridBagConstraints);

        yValText.setEditable(false);
        yValText.setPreferredSize(new java.awt.Dimension(55, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 3);
        jPanel1.add(yValText, gridBagConstraints);

        jToolBar4.add(jPanel1);

        jPanel4.add(jToolBar4, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        miscPanel.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        videoPanel.add(miscPanel, gridBagConstraints);

        qSliderPanel.setLayout(new java.awt.BorderLayout());

        qualitySlider1.setLayout(null);

        jToolBar2.add(qualitySlider1);

        qSliderPanel.add(jToolBar2, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 5, 5);
        videoPanel.add(qSliderPanel, gridBagConstraints);

        fpsQueryPanel.setLayout(new java.awt.BorderLayout());

        fPSQuery1.setLayout(new java.awt.GridLayout(3, 4));

        jToolBar3.add(fPSQuery1);

        fpsQueryPanel.add(jToolBar3, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 3, 15);
        videoPanel.add(fpsQueryPanel, gridBagConstraints);

        try {
            jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("../images/kurt_test4.PNG")));
        } catch (NullPointerException ne) {
            System.out.println("Image missing");
        }
        jLabel1.setText("<HTML><BR><BR>HEY!</HTML>");
        jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        videoPanel.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel2.add(videoPanel, gridBagConstraints);

        audioPanel.setLayout(new java.awt.GridBagLayout());

        audioPanel.setBorder(new javax.swing.border.TitledBorder("Audio"));
        miscAudio.setLayout(new java.awt.GridBagLayout());

        recAudioCheckbox.setSelected(true);
        recAudioCheckbox.setText("Audio output");
        recAudioCheckbox.setBorder(null);
        recAudioCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recAudioCheckboxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 2);
        miscAudio.add(recAudioCheckbox, gridBagConstraints);

        syncCheckbox.setSelected(true);
        syncCheckbox.setText("Synchronize audio");
        syncCheckbox.setBorder(null);
        syncCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                syncCheckboxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 2);
        miscAudio.add(syncCheckbox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 3, 0);
        miscAudio.add(jSeparator2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        audioPanel.add(miscAudio, gridBagConstraints);

        sQueryPanel.setLayout(new java.awt.BorderLayout());

        soundQuery1.setLayout(new java.awt.GridLayout(3, 1));

        soundQuery1.setPreferredSize(new java.awt.Dimension(131, 150));
        soundQueryToolBar.add(soundQuery1);

        sQueryPanel.add(soundQueryToolBar, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        audioPanel.add(sQueryPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel2.add(audioPanel, gridBagConstraints);

        mainSavePanel.setLayout(new java.awt.BorderLayout());

        mainSavePanel.setBorder(new javax.swing.border.TitledBorder("Save files"));
        savePanel.setLayout(new java.awt.GridBagLayout());

        saveEnumCheckbox.setSelected(true);
        saveEnumCheckbox.setText("Overwrite save files");
        saveEnumCheckbox.setToolTipText("Toggle between overwrite and enumeration");
        saveEnumCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveEnumCheckboxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        savePanel.add(saveEnumCheckbox, gridBagConstraints);

        currentFilesPanel.setLayout(new java.awt.GridBagLayout());

        movieFile.setPreferredSize(new java.awt.Dimension(110, 19));
        movieFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                movieFileActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        currentFilesPanel.add(movieFile, gridBagConstraints);

        audioFile.setPreferredSize(new java.awt.Dimension(110, 19));
        audioFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                audioFileActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        currentFilesPanel.add(audioFile, gridBagConstraints);

        screenFile.setPreferredSize(new java.awt.Dimension(110, 19));
        screenFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                screenFileActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        currentFilesPanel.add(screenFile, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 3, 0);
        savePanel.add(currentFilesPanel, gridBagConstraints);

        saveFileChooser1.setLayout(new java.awt.GridLayout(3, 2));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 3, 3);
        savePanel.add(saveFileChooser1, gridBagConstraints);

        saveToolbar.add(savePanel);

        mainSavePanel.add(saveToolbar, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel2.add(mainSavePanel, gridBagConstraints);

        logoPanel.setLayout(new java.awt.GridBagLayout());

        jFormattedTextField3.setBorder(null);
        jFormattedTextField3.setEditable(false);
        jFormattedTextField3.setForeground(new java.awt.Color(204, 204, 255));
        jFormattedTextField3.setText("Settings");
        jFormattedTextField3.setFont(new java.awt.Font("SansSerif", 1, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        logoPanel.add(jFormattedTextField3, gridBagConstraints);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("../logo.PNG")));
        jButton2.setBorder(null);
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        logoPanel.add(jButton2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel2.add(logoPanel, gridBagConstraints);

        jScrollPane1.setViewportView(jPanel2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jScrollPane1, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents

    /**  Easter egg */
    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        if((evt.getButton() == evt.BUTTON1) &&
                (evt.getX() <= (evt.getComponent().getPreferredSize().width / 2))) {
            jLabel1.setVisible(!jLabel1.isVisible());
        }
    }//GEN-LAST:event_jButton2MouseClicked

    /**  The checkbox for toggling automatic audio synchronization to video has been
     *  changed by the user.
     *
     *  @param  evt The ActionEvent that caused the change.
     */
    private void syncCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_syncCheckboxActionPerformed
        if (syncCheckbox.isSelected()) {
            soundQuery1.myOutput.out("Audio is synchronized to system clock");
        } else {
            soundQuery1.myOutput.out("Audio is not synchronized to system clock");            
        }
        soundQuery1.myOutput.out("");
    }//GEN-LAST:event_syncCheckboxActionPerformed

    /**  The checkbox for toggling between overwriting files or using enumeration
     *  in file names has been changed by the user.
     *
     *  @param  evt The ActionEvent that caused the change.
     */
    private void saveEnumCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveEnumCheckboxActionPerformed
        saveFileChooser1.imageFile = 
                saveFileChooser1.filterFile(saveFileChooser1.imageFile);
        saveFileChooser1.audioFile = 
                saveFileChooser1.filterFile(saveFileChooser1.audioFile);
        saveFileChooser1.videoFile = 
                saveFileChooser1.filterFile(saveFileChooser1.videoFile);
        changeFileNames();
	saveFileChooser1.myOutput.out("New screenshot file: " + 
                saveFileChooser1.imageFile.getAbsolutePath());
	saveFileChooser1.myOutput.out("New audio file: " + 
                saveFileChooser1.audioFile.getAbsolutePath());
	saveFileChooser1.myOutput.out("New video file: " + 
                saveFileChooser1.videoFile.getAbsolutePath());
	saveFileChooser1.myOutput.out("");        
    }//GEN-LAST:event_saveEnumCheckboxActionPerformed

    /**  The screenshot file has been changed by the user.
     *
     *  @param  evt The ActionEvent that caused the change.
     */
    private void screenFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_screenFileActionPerformed
        saveFileChooser1.imageFile = saveFileChooser1.filterFile(new java.io.File(screenFile.getText()));
        changeFileNames();
	saveFileChooser1.myOutput.out("New screenshot file: " + 
                saveFileChooser1.imageFile.getAbsolutePath());
	saveFileChooser1.myOutput.out("");
    }//GEN-LAST:event_screenFileActionPerformed

    /**  The audio save file has been changed by the user.
     *
     *  @param  evt The ActionEvent that caused the change.
     */
    private void audioFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_audioFileActionPerformed
        saveFileChooser1.audioFile = saveFileChooser1.filterFile(new java.io.File(audioFile.getText()));
        changeFileNames();
	saveFileChooser1.myOutput.out("New audio file: " + 
                saveFileChooser1.audioFile.getAbsolutePath());
	saveFileChooser1.myOutput.out("");
    }//GEN-LAST:event_audioFileActionPerformed

    /**  The movie save file has been changed by the user.
     *
     *  @param  evt The ActionEvent that caused the change.
     */
    private void movieFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_movieFileActionPerformed
        saveFileChooser1.videoFile = saveFileChooser1.filterFile(new java.io.File(movieFile.getText()));
        changeFileNames();
	saveFileChooser1.myOutput.out("New video file: " + 
                saveFileChooser1.videoFile.getAbsolutePath());
	saveFileChooser1.myOutput.out("");         
    }//GEN-LAST:event_movieFileActionPerformed

    /**  The focus of the button for changing the capture area has
     *  been lost. If the button had been pressed, and the capture
     *  area had not yet been changed by the user, the current method
     *  resets the capture area parameters to their previous values,
     *  and issues a warning to the user through the output window.
     *
     *  @param  evt The ActionEvent that caused the focus loss.
     */
    private void jButton1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jButton1FocusLost
        if (capButtonPressed) {
            capSizeQuery1.myOutput.out("Focus lost, changing capture area aborted");
            capSizeQuery1.myOutput.out("");        
        }
        capButtonPressed = false;
        ctrlDown = false;
        capSizeQuery1.resetTextFields();       
    }//GEN-LAST:event_jButton1FocusLost

    /** The button for changing the capture area has been pressed. This
     *  can happen in two ways: The user has directly pressed the button
     *  in the present KrutSettings window, or (more commonly) the user
     *  has pressed the mouse pointer button in the main Krut window, and
     *  that button has subsequently fired this action event.
     *
     *  @param  evt The ActionEvent.
     */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        capButtonPressed = true;
        capSizeQuery1.myOutput.out("To change the capture area, now press CTRL");
        capSizeQuery1.myOutput.out("at the top left corner of the new capture area");
        capSizeQuery1.myOutput.out("");        
    }//GEN-LAST:event_jButton1ActionPerformed

    /** A key on the keyboard has been released while the button for
     *  changing the capture area is in focus. The present method
     *  checks if the key was the CTRL key, in which case the 
     *  capture area is changed through a call to capSizeQuery1.
     *
     *  @param  evt The ActionEvent.
     */
    private void jButton1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyReleased
        if (capButtonPressed && (evt.getKeyCode() == evt.VK_CONTROL)) {
            ctrlDown = false;
            capSizeQuery1.actionPerformed(null);
            capButtonPressed = false;        
        }
    }//GEN-LAST:event_jButton1KeyReleased

    /** A key on the keyboard has been pressed while the button for
     *  changing the capture area is in focus.
     * 
     *  The present method checks if the key was the CTRL key, in
     *  which case the ctrlDown parameter is set to true. This will
     *  make the MouseTimer start updating the capture area text
     *  fields.
     *
     *  The present method also checks if the pressed key was the
     *  ESC key, in which case the changing of capture size is
     *  aborted.
     *
     *  @param  evt The ActionEvent.
     */    
    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        if (capButtonPressed && (!ctrlDown) &&
                (evt.getKeyCode() == evt.VK_CONTROL)) {
            mouseStartPos = java.awt.MouseInfo.getPointerInfo().getLocation();
            ctrlDown = true;
            capSizeQuery1.myOutput.out("Now move the mouse pointer to the bottom right corner");        
            capSizeQuery1.myOutput.out("of the new capture area, and release control button");        
            capSizeQuery1.myOutput.out("");        

        }
        if (evt.getKeyCode() == evt.VK_ESCAPE) {
            if (capButtonPressed) {
                capSizeQuery1.myOutput.out("Changing capture area aborted");
                capSizeQuery1.myOutput.out("");        
            }
            capButtonPressed = false;
            ctrlDown = false;
            capSizeQuery1.resetTextFields();
        }
    }//GEN-LAST:event_jButton1KeyPressed

    /** The Record Audio checkbox has been altered. The present
     *  method simply clicks the corresponding button in the
     *  main Krut window Menu.
     *
     *  @param  evt The ActionEvent that caused the change.
     */
    private void recAudioCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recAudioCheckboxActionPerformed
        if (aCBMItem != null)
            aCBMItem.doClick();
    }//GEN-LAST:event_recAudioCheckboxActionPerformed

    /** The Record Mouse Position checkbox has been altered. The present
     *  method simply clicks the corresponding button in the
     *  main Krut window Menu.
     *
     *  @param  evt The ActionEvent that caused the change.
     */    
    private void mouseCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mouseCheckboxActionPerformed
        if (mCBMItem != null)
            mCBMItem.doClick();
    }//GEN-LAST:event_mouseCheckboxActionPerformed

    /** The Record Video Checkbox has been altered. The present
     *  method simply clicks the corresponding button in the
     *  main Krut window Menu.
     *
     *  @param  evt The ActionEvent that caused the change.
     */    
    private void videoOutCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_videoOutCheckboxActionPerformed
        if (vCBMItem != null)
            vCBMItem.doClick();
    }//GEN-LAST:event_videoOutCheckboxActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new KrutSettings(15, 50, false, false, 22050).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField audioFile;
    private javax.swing.JPanel audioPanel;
    private javax.swing.JPanel capQueryPanel;
    private javax.swing.JPanel capSizePanel;
    private net.sourceforge.krut.ui.CapSizeQuery capSizeQuery1;
    private javax.swing.JPanel currentFilesPanel;
    private net.sourceforge.krut.ui.FPSQuery fPSQuery1;
    private javax.swing.JPanel fpsQueryPanel;
    public javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JFormattedTextField jFormattedTextField3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JPanel mainSavePanel;
    private javax.swing.JPanel miscAudio;
    private javax.swing.JPanel miscPanel;
    private javax.swing.JCheckBox mouseCheckbox;
    private javax.swing.JTextField mousePosText;
    private javax.swing.JTextField mousePosText2;
    private javax.swing.JTextField movieFile;
    private javax.swing.JPanel qSliderPanel;
    private net.sourceforge.krut.ui.QualitySlider qualitySlider1;
    private javax.swing.JCheckBox recAudioCheckbox;
    private javax.swing.JPanel sQueryPanel;
    public javax.swing.JCheckBox saveEnumCheckbox;
    private net.sourceforge.krut.ui.SaveFileChooser saveFileChooser1;
    private javax.swing.JPanel savePanel;
    private javax.swing.JToolBar saveToolbar;
    private javax.swing.JTextField screenFile;
    private net.sourceforge.krut.ui.SoundQuery soundQuery1;
    private javax.swing.JToolBar soundQueryToolBar;
    public javax.swing.JCheckBox syncCheckbox;
    private javax.swing.JCheckBox videoOutCheckbox;
    private javax.swing.JPanel videoPanel;
    private javax.swing.JTextField xText;
    private javax.swing.JTextField xValText;
    private javax.swing.JTextField yText;
    private javax.swing.JTextField yValText;
    // End of variables declaration//GEN-END:variables
    
}
