package krut;
/*
 * Run_KRUT.java
 *
 * Created on den 29 december 2004, 23:00
 */

/**
 * @author jonte
 */

/*
 *  @(#)Run_KRUT.java
 *
 *  Some general comments on this program.
 *
 *  First of all, the major part of the program is licenced
 *  under the GPL which you should have recieved a copy of 
 *  along with the program. Some classes come under different
 *  licences, which are specified in the source code to those
 *  classes, and copied in the file readme.txt, which you
 *  should also have recieved a copy of along with this
 *  program.
 *
 *  Secondly, some discussion. The structure of this program
 *  is not always optimal. There are a lot of global
 *  parameters, and you may stumble upon some difficult to
 *  understand usage of things like access levels, and
 *  global parameters. This is pretty much always due simply
 *  to the fact that this program was written as a way of
 *  learning Java. As it turned out, the program
 *  was useful, and I decided to share it. Then the program
 *  also became a little more popular than I had imagined,
 *  so I decided to do my best to clean up the code, so that
 *  it at least became readable. That is where I am right now.
 *  But of course, I realize already that I can not start
 *  changing things like global parameters, since in some places
 *  they are necessary, and in some places they aren't, and
 *  I don't know which is which any longer.
 *
 *  I will leave all parameters that were global global, and
 *  all parameters that were public public, even if there
 *  doesn't seem to be a reason for it, because as far as I
 *  am concerned, no harm is done.
 *
 *  Some times, the remarks in the comments are just notes
 *  to myself. This would usually be the case if they seem
 *  very obvious.
 *
 *  Jonas
 */


import java.io.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Logger;

import krut.KRUT_GUI.*;
import krut.KRUT_Recording.*;


/** This is the main class of the program. When the main() method is
 *  run, the following things will be done:<BR><BR>
 *
 *  - Create and show the GUI.<BR>
 *  - Start two new threads for the Sampler and the ScreenGrabber.<BR>
 *  - Register event and action listeners to all buttons and menus.<BR><BR>
 *
 *  When this is done, the main thread is done, and everything
 *  that is done after is caused by the event and action listeners.
 */
public class Run_KRUT implements ActionListener, ItemListener {

    private final static Logger logger = Logger.getLogger(Run_KRUT.class.getName());

    /** Newline String for the OutputWindow. */
    protected String newline = "\n";
    /** The main frame. */
    public JFrame frame;
    /** The interface to the user for changing the capture area.
     *  This used to be a separate object that was initiated by the Run_KRUT object.
     *  Now the present object is initiated in KrutSettings, but the Run_KRUT object
     *  still needs access so the code will not break.
     */
    public CapSizeQuery capQuery;
    /** The interface to the user for changing the fps values for the
     *  recorded movie.
     *  This used to be a separate object that was initiated by the Run_KRUT object.
     *  Now the present object is initiated in KrutSettings, but the Run_KRUT object
     *  still needs access so the code will not break.
     */
    public FPSQuery fpsQuery;
    /** The interface to the user for changing the movie encoding quality.
     *  This used to be a separate object that was initiated by the Run_KRUT object.
     *  Now the present object is initiated in KrutSettings, but the Run_KRUT object
     *  still needs access so the code will not break.
     */
    public QualitySlider encSlider;
    /** The interface to the user for changing the sound recording quality.
     *  This used to be a separate object that was initiated by the Run_KRUT object.
     *  Now the present object is initiated in KrutSettings, but the Run_KRUT object
     *  still needs access so the code will not break.
     */
    public SoundQuery soundQuery;
    /** The interface to the user for changing the save files.
     *  This used to be a separate object that was initiated by the Run_KRUT object.
     *  Now the present object is initiated in KrutSettings, but the Run_KRUT object
     *  still needs access so the code will not break.
     */
    public SaveFileChooser saveQuery;
    /** The output window for the program. */
    public OutputText outWindow;
    /** The timer object. */
    public KrutTimer timer;
    /** The settings object, containing the interfaces to the user
     *  for changing the properties of the recorded data.
     */
    public KrutSettings krutSettings;
    /** The ScreenGrabber is used for all video and
     *  screenshot functions. */
    public ScreenGrabber myGrabber = null;
    /** The Sampler is used for all audio recording.
     */
    public Sampler mySampler = null;
    /**  The EncodingProgressBar is used to show and abort encoding. */
    public EncodingProgressBar myProgressBar;
    /** A class used for showing the snap shot.
     *  Used in the snapAction() method. */
    public SnapShot imageUtils = new SnapShot();
    /** The initial name of the snapshot file.
     *  This is just passed on to the SaveFileChooser
     *  in the activateGUI() method. */
    public File imageFile = new File("image.jpg");
    /** The initial name of the movie file.
     *  This is just passed on to the SaveFileChooser
     *  in the activateGUI() method. */
    public File movieFile = new File("movie.mov");
    /** The initial name of the audio file.
     *  This is just passed on to the SaveFileChooser
     *  in the activateGUI() method. */
    public File audioFile = new File("audio.wav");
    /** Flag to keep track of if we should record audio.
     *  Used in the recordAction() and stopAction(). This flag
     *  is updated in the checkInited() method, where it is set to
     *  the value of the nextAudio flag. This means that this
     *  flag will not change until a new recording is ready to
     *  start, so there will be no unwanted interruptions of ongoing
     *  recordings.
     */
    protected boolean recAudio = true;
    /** Flag to keep track of if we should record Video.
     *  Used in the recordAction() and stopAction(). This flag
     *  is updated in the checkInited() method, where it is set to
     *  the value of the nextVideo flag. This means that this
     *  flag will not change until a new recording is ready to
     *  start, so there will be no unwanted interruptions of ongoing
     *  recordings.
     */
    protected boolean recVideo = true;
    /** Flag used to keep track of if the NEXT recording
     *  should record audio. This flag is used in the
     *  checkInited() method, where its value is copied into
     *  recAudio. This flag is changed in the
     *  itemStateChanged() method.
     */
    protected boolean nextAudio = true;
    /** Flag used to keep track of if the NEXT recording
     *  should record video. This flag is used in the
     *  checkInited() method, where its value is copied into
     *  recVideo. This flag is changed in the
     *  itemStateChanged() method.
     */
    protected boolean nextVideo = true;
    /** This button is used to switch between the
     *  recording button, the stop button and
     *  the timer button. The active button is the
     *  button actually showing in the left button
     *  position in the main window.
     *  The buttons are added in init(), and used in addButtons()
     *  and makeNavigationButton and addButtons().
     */
    protected JButton activeButton = null;
    /** The recording button.
     *  The buttons are added in init(), and used in addButtons()
     *  and makeNavigationButton and addButtons().
     */
    protected JButton recButton = null;
    /** The stop button.
     *  The buttons are added in init(), and used in addButtons()
     *  and makeNavigationButton and addButtons().
     */
    protected JButton stopButton = null;
    /** The snapshot button.
     *  The buttons are added in init(), and used in addButtons()
     *  and makeNavigationButton and addButtons().
     */
    protected JButton snapshotButton = null;
    /** The mouse pointer button.
     *  The buttons are added in init(), and used in addButtons()
     *  and makeNavigationButton and addButtons().
     */
    protected JButton mouseButton = null;
    /** The button used to indicate that the timer is active.
     *  The buttons are added in init(), and used in addButtons()
     *  and makeNavigationButton and addButtons().
     */
    protected JButton timerButton = null;
    /** The button used to indicate that the timer is running.
     *  The buttons are added in init(), and used in addButtons()
     *  and makeNavigationButton and addButtons().
     */
    protected JButton timerRecButton = null;
    /** The starting value of the capture size.
     *  This is used in the createScreenGrabber() method.
     *  It is also passed on to the KrutSettings constructur
     *  from the init() method.
     */
    protected Rectangle capRect = new Rectangle(0, 0, 360, 240);
    /** This JFrame is used to show the SnapShots in.
     *  Used in the SnapAction() method.
     *  Needs to be initiated before the GUI shows.
     *  Is initiated in the createMainFrame() method.
     *  (If it is initiated earlier, it will not have the correct LookAndFeel)
     */
    protected JFrame snapShotFrame;
    /** The starting value of the recording and playback fps.
     *  This is used in the createScreenGrabber() method.
     *  It is also passed on to the KrutSettings constructur
     *  from the init() method.
     */
    private int startFps = 15;
    /** The starting value of the video encoding quality.
     *  This value is just passed on to KrutSettings via the
     *  constuctor. This value is only used in the init()
     *  method. If this value is changed, the initial value
     *  of the parameter encQuality in ScreenGrabber should
     *  be changed accordingly.
     *
     *  startEncQuality should be a value between 0 and 100.
     *  encQuality in ScreenGrabber should be between 0 and 1.
     */
    private int startEncQuality = 75;
    /** A flag determining if we should start recording audio
     *  in stereo or not. It is set to no in order to reduce
     *  the size of the recorded audio file. This flag is
     *  passed on to KrutSettings via the constructor, in init().
     *  It is also passed on to the Sampler in createSampler().
     */
    private boolean startStereo = false;
    /** A flag determining if we should start recording audio
     *  in 16-bit or not. It is set to no in order to reduce
     *  the size of the recorded audio file. This flag is
     *  just passed on to KrutSettings via the constructor, in init().
     *  It is also passed on to the Sampler in createSampler().
     */
    private boolean startSixteen = false;
    /** The starting value of the frequency for audio recording.
     *  This value is just passed on to KrutSettings via the
     *  constuctor, in init().
     *  It is also passed on to the Sampler in createSampler().
     */
    private int startFrequency = 22050;
    /** This is the menu in the main window of the GUI.
     *  It is defined here because it has to be accessed from
     *  the method init(), to deactivate and reactivate it
     *  before and after the KrutSettings and OutputWindow
     *  objects are initiated.
     */
    private JMenu menu;
    /** The audio CheckBox from the menu. It is defined here,
     *  for a simple way to access it from the init() method,
     *  where it is passed on to the KrutSettings object.
     */
    private JCheckBoxMenuItem acbMenuItem;
    /** The video CheckBox from the menu. It is defined here,
     *  for a simple way to access it from the init() method,
     *  where it is passed on to the KrutSettings object.
     */
    private JCheckBoxMenuItem vcbMenuItem;
    /** The mouse pointer CheckBox from the menu. It is defined here,
     *  for a simple way to access it from the init() method,
     *  where it is passed on to the KrutSettings object.
     */
    private JCheckBoxMenuItem mcbMenuItem;
    /**    Used for the buttons, in addButtons() and actionPerformed(). */
    static final private String RECORD = "record";
    /**    Used for the buttons, in addButtons() and actionPerformed(). */
    static final private String SNAPSHOT = "snapshot";
    /**    Used for the buttons, in addButtons() and actionPerformed(). */
    static final private String STOP = "stop";
    /**    Used for the buttons, in addButtons() and actionPerformed(). */
    static final private String TIMER = "timer";
    /**    Used for the buttons, in addButtons() and actionPerformed(). */
    static final private String MPOINTER = "mpointer";
    /**    Used for the buttons, in addButtons() and actionPerformed(). */
    static final private String EMPTY = "empty";

    /** A flag used to tell if there is already a thread
     *  running trying to stop the recording. This flag
     *  is used to (almost) definitely make sure that two
     *  STOP - ActionCommands can not be executed in a row.
     *  This flag is changed to true in the the ActionListener
     *  and changed back to false in restoreGUI.
     */
    protected boolean stopping = false;

    /** A flag used to tell if recording is in progress.
     *  This is used to tell the timer when it should
     *  record and when it should stop. This flag is
     *  changed to true in the ActionListener, and
     *  changed back to false in restoreGUI.
     */
    protected boolean recording = false;

    /**  A class that is used to run the stopAction() method
     *   in a separate thread. This class is used from the
     *   actionPerformed() method.
     */
    private class StopThread extends Thread {

        public void run() {
            try {
                stopAction();
            } catch (Exception e) {
                System.out.println("Stop thread cancelled");
                System.out.println(e);
            } finally {
                myProgressBar.dispose();
            }
        }
    }


    /***************************************************************************
     *  First part :
     *  This part of the code contains methods for the creation of
     *  and user-interaction with the GUI.
     **************************************************************************/


    /** Create the menu bar, and add the menu to it. Three
     *  checkbox items are stored in the global parameters
     *  vcbMenuItem, acbMenuItem and mcbMenuItem.
     *
     *  @return a JMenuBar object with the menu attached to it.
     */
    public JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenuItem menuItem;
        JRadioButtonMenuItem rbMenuItem;
        /** The "Follow Mouse" CheckBox from the menu. */
        JCheckBoxMenuItem fwmMenuItem;
        /** The "Preview Window" CheckBox from the menu. */
        JCheckBoxMenuItem prwMenuItem;


        /** Create the menu bar. */
        menuBar = new JMenuBar();

        /** Build the menu. */
        menu = new JMenu("Menu");
        menu.setMnemonic(KeyEvent.VK_M);
        menu.getAccessibleContext().setAccessibleDescription(
                "Menu");
        menuBar.add(menu);

        /** Add video checkbox. */
        vcbMenuItem = new JCheckBoxMenuItem("Video output", true);
        vcbMenuItem.setMnemonic(KeyEvent.VK_O);
        vcbMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        vcbMenuItem.addItemListener(this);
        menu.add(vcbMenuItem);

        /** Add audio checkbox. */
        acbMenuItem = new JCheckBoxMenuItem("Audio output", true);
        acbMenuItem.setMnemonic(KeyEvent.VK_U);
        acbMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.ALT_MASK));
        acbMenuItem.addItemListener(this);
        menu.add(acbMenuItem);

        /** Add mouse checkbox. */
        mcbMenuItem = new JCheckBoxMenuItem("Show Mouse", true);
        mcbMenuItem.setMnemonic(KeyEvent.VK_M);
        mcbMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_3, ActionEvent.ALT_MASK));
        mcbMenuItem.addItemListener(this);
        menu.add(mcbMenuItem);

        /** Add follow mouse checkbox. */
        fwmMenuItem = new JCheckBoxMenuItem("Follow Mouse", false);
        fwmMenuItem.setMnemonic(KeyEvent.VK_F);
        fwmMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_4, ActionEvent.ALT_MASK));
        fwmMenuItem.addItemListener(this);
        menu.add(fwmMenuItem);

        /** Add preview window checkbox. */
        prwMenuItem = new JCheckBoxMenuItem("Preview Window", false);
        prwMenuItem.setMnemonic(KeyEvent.VK_P);
        prwMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_5, ActionEvent.ALT_MASK));
        prwMenuItem.addItemListener(this);
        menu.add(prwMenuItem);

        /** Add Settings menu item. */
        menu.addSeparator();
        menuItem = new JMenuItem("Settings/Save Files",
                KeyEvent.VK_S);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_6, ActionEvent.ALT_MASK));
        menuItem.addActionListener(this);
        menu.add(menuItem);

        /** Add output window menu item. */
        menuItem = new JMenuItem("Show output window",
                KeyEvent.VK_H);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_7, ActionEvent.ALT_MASK));
        menuItem.addActionListener(this);
        menu.add(menuItem);

        /** Add output window menu item. */
        menuItem = new JMenuItem("Timer",
                KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_8, ActionEvent.ALT_MASK));
        menuItem.addActionListener(this);
        menu.add(menuItem);

        /** Add speed test menu item. */
        menuItem = new JMenuItem("Run speed test",
                KeyEvent.VK_R);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_0, ActionEvent.ALT_MASK));
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem("Request Feature");
        menuItem.addActionListener(new OpenUrlActionListener("https://sourceforge.net/p/krut/feature-requests/"));
        menu.add(menuItem);

        menuItem = new JMenuItem("Report Bug");
        menuItem.addActionListener(new OpenUrlActionListener("https://sourceforge.net/p/krut/bugs/"));
        menu.add(menuItem);

        menuItem = new JMenuItem("GitHub Page");
        menuItem.addActionListener(new OpenUrlActionListener("https://github.com/jimmyfm/krut"));
        menu.add(menuItem);

        return menuBar;
    }

    /** Returns an ImageIcon, or null if the path was invalid.
     *
     *  @param  The path to the image.
     *  @return The ImageIcon.
     */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = Run_KRUT.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /** Add 3 buttons to a toolbar.
     *  Several buttons are created, and then the toolbar
     *  will switch between the recButton, the
     *  stopButton, and the timerButton. This is done by adding only the button
     *  called activeButton to the toolBar, and then copying
     *  the layout on to that button from one of the
     *  other two.
     *
     *  @param  toolBar The JToolBar that should be used in
     *          the program.
     */
    protected void addButtons(JToolBar toolBar) {

        /** Create a recButton. This will not be added to the
         *  JToolBar, but will serve as a template for the
         *  activeButton, when it is waiting for recording
         *  to start.
         */
        recButton = makeNavigationButton("mellan7.PNG", RECORD,
                "Start recording",
                "Rec ");

        /** Create the active button. This button will be changed
         *  when recording stops and starts to either reflect the
         *  recButton or the stopButton.
         */
        activeButton = makeNavigationButton("mellan7.PNG", RECORD,
                "Start recording",
                "Rec ");

        /** Add first button (activeButton). */
        toolBar.add(activeButton);

        /** Create the snapshotButton and add it to the JToolBar.
         */
        snapshotButton = makeNavigationButton("blue6.PNG", SNAPSHOT,
                "Take screenshot",
                " Snap");

        /** Add second button (snapshotButton). */
        toolBar.add(snapshotButton);

        /** Create a stopButton. This will not be added to the
         *  JToolBar, but will serve as a template for the
         *  activeButton, when it is in record mode.
         */
        stopButton = makeNavigationButton("stop.PNG", STOP,
                "Stop recording",
                "Stop");
        stopButton.setSize(recButton.getSize());

        /** Create a timerButton. This will not be added to the
         *  JToolBar, but will serve as a template for the
         *  activeButton, when it is in timer mode.
         */
        timerButton = makeNavigationButton("timer.PNG", TIMER,
                "The timer is active",
                "Timer");

        /** Create a timerRecButton. This will not be added to the
         *  JToolBar, but will serve as a template for the
         *  activeButton, when it is in timer mode.
         */
        timerRecButton = makeNavigationButton("timer_running.PNG", TIMER,
                "The timer is recording",
                "Timer");

        /** Create the mouse pointer button.
         */
        mouseButton = makeNavigationButton("mus.PNG", MPOINTER,
                "Select capture area with mouse and CTRL button",
                "");

        /** Add the key listener and the focus listener to the
         *  mouse pointer button.
         */
        mouseButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                mouseButtonKeyPressed(evt);
            }

            public void keyReleased(java.awt.event.KeyEvent evt) {
                mouseButtonKeyReleased(evt);
            }
        });
        mouseButton.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                mouseButtonFocusLost(evt);
            }
        });

        /** Add third button (mouseButton). */
        toolBar.add(mouseButton);
    }

    /** When the mouse-pointer button is pressed, this method
     *  fires a KeyPressed event for the mouse-pointer
     *  button in the krutSettings object. Since
     *  the two buttons behave identically, this will
     *  be all that needs to be done.
     *  At the time of writing, the method that will
     *  execute in the krutSettings object is called
     *  jButton1KeyPressed().
     *
     *  @param evt  The event that triggered this KeyListener.
     */
    private void mouseButtonKeyPressed(java.awt.event.KeyEvent evt) {
        java.awt.event.KeyListener[] listeners =
                krutSettings.jButton1.getKeyListeners();
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].keyPressed(evt);
        }
    }

    /** When the mouse-pointer button is released (=CTRL is released),
     *  this method fires a KeyReleased event for the
     *  mouse-pointer button in the krutSettings object. Since the
     *  two buttons behave identically, this will be all
     *  that needs to be done.
     *  At the time of writing, the method that will
     *  execute in the krutSettings object is called
     *  jButton1KeyReleased().
     *
     *  @param evt  The event that triggered this KeyListener.
     */
    private void mouseButtonKeyReleased(java.awt.event.KeyEvent evt) {
        java.awt.event.KeyListener[] listeners =
                krutSettings.jButton1.getKeyListeners();
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].keyReleased(evt);
        }
    }

    /** When the mouse-pointer button looses focus, this method
     *  fires a FocusLost event for the mouse-pointer button
     *  in the krutSettings object. Since the
     *  two buttons behave identically, this will be all
     *  that needs to be done.
     *  At the time of writing, the method that will
     *  execute in the krutSettings object is called
     *  jButton1FocusLost().
     *
     *  @param evt  The event that triggered this FocusListener.
     */
    private void mouseButtonFocusLost(java.awt.event.FocusEvent evt) {
        java.awt.event.FocusListener[] listeners =
                krutSettings.jButton1.getFocusListeners();
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].focusLost(evt);
        }
    }

    /** Creates a navigation button of the specified
     *  appearance, and returns it.
     *
     *  @param  imageName       A String representation of the URL
     *                          to an image that should be displayed
     *                          on this button.
     *  @param  actionCommand   The action command for this button.
     *                          This command is listened for in
     *                          the actionPerformed(ActionEvent e)
     *                          method, to determine which button
     *                          was pressed.
     *  @param  toolTipText     The tooltip text for this button.
     *  @param  altText         The text that should be typed on
     *                          this button, if any.
     *
     *  @return A JButton according to the specifications
     *              given in the parameters.
     */
    protected JButton makeNavigationButton(String imageName,
                                           String actionCommand,
                                           String toolTipText,
                                           String altText) {

        /** Attempt to locate the image */
        String imgLocation = imageName;
        URL imageURL = Run_KRUT.class.getResource(imgLocation);

        /** Create and initialize the button. */
        JButton button = new JButton();
        button.setActionCommand(actionCommand);
        button.setToolTipText(toolTipText);
        button.addActionListener(this);

        /** Add the image if it was succesfully located. */
        if (imageURL != null) {
            button.setIcon(new ImageIcon(imageURL, altText));
        } else {
            System.err.println("Resource not found: "
                    + imgLocation);
        }

        /** Set a text on the button. If there is no String
         *  or an empty String in the parameter altText
         *  no text will appear on the button.
         */
        button.setText(altText);

        return button;
    }

    /** This method delivers the size that the activeButton
     *  parameter should have. This method is used by
     *  the switchActiveButton method. To change the size
     *  of the activeButton, this method can be overridden.
     *
     *  @return A Dimension object containing the
     *              size of the activeButton.
     */
    public Dimension getActiveButtonSize() {
        /** Return the size of the record button, minus the size of the
         *  border, which doesn't seem to show.
         *  (size - margin + (border - margin))
         */
        Dimension returnDim = new Dimension(recButton.getPreferredSize());
        returnDim.width += recButton.getInsets().left - 2 * recButton.getMargin().left;
        returnDim.width += recButton.getInsets().right - 2 * recButton.getMargin().right;
        returnDim.height += recButton.getInsets().top - 2 * recButton.getMargin().top;
        returnDim.height += recButton.getInsets().bottom - 2 * recButton.getMargin().bottom;
        return returnDim;
    }

    /** Switches the activeButton so that the button
     *  displayed is now the one in newButton. The
     *  size of the new activeButton will be the Dimension
     *  value taken from the getActiveButtonSize method.
     *
     *  @param  newButton The new JButton that should be displayed
     *          as the activeButton in the GUI.
     */
    protected void switchActiveButton(JButton newButton) {
        Insets insets = new Insets(0, 0, 0, 0);
        Dimension prefSize = getActiveButtonSize();
        Dimension sizeDiff =
                new Dimension((prefSize.width - newButton.getPreferredSize().width),
                        (prefSize.height - newButton.getPreferredSize().height));

        /** If the button should be smaller, this part will do that. */
        activeButton.setMinimumSize(prefSize);
        activeButton.setMaximumSize(prefSize);
        activeButton.setPreferredSize(prefSize);

        /** If the button should be bigger, we will add to the margins. */
        if (0 <= sizeDiff.width) {
            insets.left += sizeDiff.width / 2;
            insets.right += sizeDiff.width / 2 + sizeDiff.width % 2;
        }
        if (0 <= sizeDiff.height) {
            insets.top += sizeDiff.height / 2;
            insets.bottom += sizeDiff.height / 2 + sizeDiff.height % 2;
        }

        /** Change the activeButton to match our new button . */
        activeButton.setMargin(insets);
        activeButton.setIcon(newButton.getIcon());
        activeButton.setText(newButton.getText());
        activeButton.setToolTipText(newButton.getToolTipText());
        activeButton.setActionCommand(newButton.getActionCommand());
    }

    /** Change the GUI to display a timer button
     *  instead of a recording/stop button. There
     *  will be one button displaying if the timer
     *  is just active and waiting to start a recording,
     *  and another button if the timer is recording.
     *
     *  @param  recording   Should be true if the timer
     *                      is recording, false if it is
     *                      not.
     */
    public void setTimerGUI(boolean recording) {
        JButton setButton;
        if (recording) {
            setButton = timerRecButton;
        } else {
            setButton = timerButton;
        }
        switchActiveButton(setButton);
    }

    /** This is where the GUI is restored after encoding of
     *  a recorded film. This method is called from the
     *  stopAction() method. The GUI should not be restored from anywhere
     *  else, since that could mean a potential problem if
     *  the stopAction() method is still merging a file in the
     *  background, while another StopThread had been started.
     *
     *  The GUI was changed in the recordAction() method.
     */
    public void restoreGUI() {
        switchActiveButton(recButton);
        activeButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        /** This should be redundant (the mouseTimer is started in stopAction()),
         *  but this is kept just in case. */
        if (!krutSettings.mouseTimer.isRunning())
            krutSettings.startMouseTimer();

        /** This allows the STOP action to have effect again. */
        stopping = false;

        /** Allow recording again */
        recording = false;

        /** Now we may need to change the file names for the next recording
         *  session. It depends on whether file names should be overwritten
         *  or not.
         */
        saveQuery.imageFile = saveQuery.filterFile(saveQuery.imageFile);
        saveQuery.audioFile = saveQuery.filterFile(saveQuery.audioFile);
        /** This is a bit of an emergency solution. If we would only
         *  filterFile here, the file from the previos merge would not
         *  yet be created, and filterFile would return the same
         *  file name as we just used. So we need to manually increase
         *  the file index. This will have the unwanted effect that
         *  even if we record video-only or audio-only, the movie file name
         *  will still change. The presumption so far is that this is not
         *  a serious, or maybe even noticed, problem for the user.
         */
        if (!krutSettings.saveEnumCheckbox.isSelected()) {
            saveQuery.videoFile = saveQuery.filterFile(saveQuery.getNextFile(saveQuery.videoFile));
        }
        /** Update displayed file names. */
        krutSettings.changeFileNames();
    }


    /**    If an ActionEvent is fired from one of the
     *  buttons or the menu, this is where we'll end
     *  up. Since there is only one ActionListener
     *  (the Run_KRUT objet itself) listening to all
     *  the different objects, this method will use
     *  the getActionCommand() method of the incoming
     *  ActionEvent to separate them. It is therefore
     *  important that the buttons and menu items are
     *  given a proper ActionCommand.
     *
     *  @param  e   The ActionEvent for this Action.
     */
    public void actionPerformed(ActionEvent e) {

        /** Get the ActiomCommand for this Action. */
        String cmd = e.getActionCommand();

        /** First we check if one of the buttons has been pressed. */
        if (RECORD.equals(cmd)) {
            recording = true;
            recordAction();
        } else if (SNAPSHOT.equals(cmd)) {
            snapAction();
        } else if (TIMER.equals(cmd)) {
            timerAction();
        } else if ("Timer active".equals(cmd)) {
            timerActivatedAction();
        } else if ("Timer recording".equals(cmd)) {
            timerStartedAction();
        } else if ("Timer stopped".equals(cmd)) {
            timerStoppedAction();
        } else if ("Special Stop".equals(cmd)) {
            timerStoppedByStopButtonAction();
        } else if (STOP.equals(cmd) && !stopping) {
            /** If the stop button is clicked,
             *  start by disabling this method to prevent
             *  an eventual second click from launching
             *  another StopThread.
             */
            stopping = true;
            /** Start a new thread to handle the encoding of the movie,
             *  using low priority. In the course of this thread,
             *  the GUI is restored, and by the end of the thread
             *  everything is back to normal.
             */
            StopThread stopThread = new StopThread();
            stopThread.setPriority(Thread.MIN_PRIORITY);
            stopThread.start();
        } else if (MPOINTER.equals(cmd)) {
            /** This starts the changing of the capture area
             *  by using the methods for the mouse pointer
             *  button in krutSettings.
             */
            krutSettings.jButton1.doClick();
        } else if (EMPTY.equals(cmd)) {
        } else {
            /** Now we know that the action was a menu event
             *  (checkboxes events are handled in the method
             *  itemStateChanged() below).
             */
            JMenuItem source = (JMenuItem) (e.getSource());

            if (source.getText() == "Show output window") {
                outWindow.outFrame.setVisible(true);
                return;
            }

            if (source.getText() == "Settings/Save Files") {
                krutSettings.setVisible(true);
                return;
            }
            if (source.getText() == "Timer") {
                timer.setVisible(!timer.isVisible());
                frame.pack();
                return;
            }

            if (source.getText() == "Run speed test") {
                if (!myGrabber.running) {
                    javax.swing.SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            speedTest();
                        }
                    });
                }
                return;
            }
        }
    }

    /**    Something has changed in one of the checkboxes.
     *
     *  @param  e   The ItemEvent that caused the change.
     */
    public void itemStateChanged(ItemEvent e) {
        JMenuItem source = (JMenuItem) e.getSource();
        if (source.getText() == "Video output") {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                outWindow.out("Video recording enabled.");
                outWindow.out("");
                nextVideo = true;
                krutSettings.setVideoCheckBox(true);
            } else {
                outWindow.out("Video recording disabled.");
                outWindow.out("");
                nextVideo = false;
                krutSettings.setVideoCheckBox(false);
            }
        } else if (source.getText() == "Audio output") {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                outWindow.out("Audio recording enabled.");
                outWindow.out("");
                nextAudio = true;
                krutSettings.setAudioCheckBox(true);
            } else {
                outWindow.out("Audio recording disabled.");
                outWindow.out("");
                nextAudio = false;
                krutSettings.setAudioCheckBox(false);
            }
        } else if (source.getText() == "Show Mouse") {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                outWindow.out("Showing mouse position in video.");
                outWindow.out("");
                myGrabber.getMouse = true;
                krutSettings.setMouseCheckBox(true);
            } else {
                outWindow.out("Hiding mouse position in video.");
                outWindow.out("");
                myGrabber.getMouse = false;
                krutSettings.setMouseCheckBox(false);
            }
        } else if (source.getText() == "Follow Mouse") {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                outWindow.out("Moving the screen recording area to follow the mouse pointer");
                outWindow.out("Using the Preview Window is recommended.");
                outWindow.out("");
                myGrabber.followMouse = true;
            } else {
                outWindow.out("The screen recording area is fixed.");
                outWindow.out("Chosen coordinates are available in the Krut Settings window.");
                outWindow.out("");
                myGrabber.followMouse = false;
            }
        } else if (source.getText() == "Preview Window") {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                outWindow.out("Showing a preview of the film while recording.");
                outWindow.out("This can be practical when using a non-fixed recording area.");
                outWindow.out("");
                // Start the preview window.
                previewAction();
                myGrabber.preview = true;
            } else {
                outWindow.out("Showing no preview of the recording.");
                outWindow.out("");
                /*  Stop and remove the preview window, if it was active. */
                if (myGrabber.preview) imageUtils.stopPreviewWindow();
                myGrabber.preview = false;
            }
        }
    }


    /***************************************************************************
     *  Second part :
     *  This part of the code contains methods which are needed to perform
     *  the actions that are launched through the GUI.
     **************************************************************************/


    /** This method takes the snapshot */
    private void snapAction() {
        /** This changes video settings and file names. */
        checkInited();
        /** Take the snapshot */
        myGrabber.snapshot();
        outWindow.out("Snapshot taken, area : ");
        outWindow.out(" " + myGrabber.capRect.x + ", " + myGrabber.capRect.y +
                ", " + myGrabber.capRect.width + ", " + myGrabber.capRect.height);
        outWindow.out("Saved to file: " + myGrabber.screenshotFile.getAbsolutePath());
        /** Try to display the snapshot on screen. */
        try {
//            JFrame tempFrame = new JFrame(myGrabber.screenshotFile.getPath().toString());
            snapShotFrame.setTitle(myGrabber.screenshotFile.getPath().toString());
            /** imageUtils is a SnapShot object, initiated only once, in the declaration. */
            /** load image from file. */
            Image pic = imageUtils.loadPic(myGrabber.screenshotFile.getPath(), snapShotFrame);
            BufferedImage paintBuff = new BufferedImage(pic.getWidth(snapShotFrame),
                    pic.getHeight(snapShotFrame), BufferedImage.TYPE_INT_RGB);
            Graphics paintGraph = paintBuff.getGraphics();
            paintGraph.drawImage(pic, 0, 0, snapShotFrame);
            /** Paint image in a new snapshot window. */
            imageUtils.createAndShowGUI(snapShotFrame, paintBuff);
        } catch (OutOfMemoryError om) {
            outWindow.out("Out of memory, could not display image.");
        }
        outWindow.out("");
        /** Save the image. */
        saveQuery.imageFile = saveQuery.filterFile(saveQuery.imageFile);
        /** Update the file names. */
        krutSettings.changeFileNames();
    }


    /** This method makes sure the preview window is running (if it should).
     *  This method is called from itemStateChanged */
    private void previewAction() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                /** imageUtils is a SnapShot object, initiated only once, in the declaration. */
                imageUtils.initPreviewWindow(myGrabber.capRect.width,
                        myGrabber.capRect.height);
            }
        });
    }

    /** If the timer button is pressed, we make sure that the
     *  timer window is visible.
     */
    private void timerAction() {
        if (!timer.isVisible()) {
            timer.setVisible(true);
            frame.pack();
        }
    }

    /** When the timerToggleButton is toggled on
     *  this is the action that is performed.
     */
    private void timerActivatedAction() {
        if (!recording) setTimerGUI(true);
    }

    /** When the timerToggleButton sends a "Timer started"
     *  ActionEvent, this is the action that is performed.
     */
    private void timerStartedAction() {
        if (!recording) recButton.doClick();
        else this.outWindow.out(
                "Running recording detected, continuing old recording.");
        /** When we return here after clicking the
         *  record button (or even if we didn't),
         *  a copy of the stop button will be showing in the
         *  GUI. The only way we can allow it to
         *  show is if we modify it so that it no
         *  longer does what it used to.
         */
        activeButton.setActionCommand("Special Stop");
    }

    /** When the timerToggleButton is toggled off
     *  this is the action that is performed.
     */
    private void timerStoppedAction() {
        if (recording) stopButton.doClick();
        else restoreGUI();
    }

    /** If recording is stopped by the stop button,
     *  while the timer is running, the timer will not
     *  be properly reset. This is a quick fix to this
     *  issue. The stop button displayed in the GUI
     *  when the timer is running is not a stop button,
     *  but a modified stop button that leads to this
     *  action instead. Here we click the "Stop Timer"
     *  button in the Timer GUI.
     */
    private void timerStoppedByStopButtonAction() {
        timer.getTimerButton().doClick();
    }

    /** This method handles both video and audio recording.
     *  Video and audio recording are interrupted when the stop
     *  button is pressed, which leads to the stopAction() method
     *  being run.
     */
    private void recordAction() {
        /** Stop the mouseTimer to save a very small
         *  amount of resources (no other purpose)
         *  (it is started again in stopAction()).
         */
        krutSettings.stopMouseTimer();

        /** Change the GUI, switch record and stop buttons. */
        switchActiveButton(stopButton);

        /** Check for critical parameter changes (filenames, video or
         *  audio settings). If there are any, they are handled here as well.
         */
        checkInited();

        /** Start the synchronized recording. */
        long syncTime = System.currentTimeMillis();
        mySampler.setSyncTime(syncTime);
        myGrabber.setSyncTime(syncTime);
        if (recAudio) {
            /** We have to tell the ScreenGrabber that audio
             *  is also recording. This will have the effect
             *  that the ScreenGrabber will wait at the end of
             *  it's run() method, to achieve maximum sync
             *  between audio and video.
             */
            myGrabber.audioRecording = true;
            /** Start audio recording. */
            mySampler.stopped = false;
            mySampler.wakeUp();
            outWindow.out("Recording audio");
        }
        if (recVideo) {
            /** Start video recording. */
            myGrabber.notFinished = true;
            myGrabber.wakeUp();
            outWindow.out("Recording video");
        }
        outWindow.out("");

        /*  If the user has closed the preview window without using
         *  the checkbox, it will be brought back here.
         */
        if ((myGrabber.preview) && (imageUtils.previewFrame != null) &&
                (!imageUtils.previewFrame.isShowing())) {
            previewAction();
        }
    }


    /**    Stop recording. If video is recorded, audio
     *  waits for video to finish, in order to get sync.
     *  If both were running, the method then waits for both
     *  output files to be written before calling Merge.
     */
    private void stopAction() {

        /** Disable the activeButton, just to make
         *  things look good.
         */
        activeButton.setActionCommand(EMPTY);
        activeButton.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        /** Create a new EncodingProgressBar, and give it access
         *  to the outWindow
         */
        myProgressBar = new EncodingProgressBar(outWindow);
        myGrabber.myProgressBar = myProgressBar;
        myProgressBar.setStatus(myProgressBar.ENCODING);

        outWindow.out("Stopping recording, please wait ...");

        /** Setting this flag tells the ScreenGrabber to stop recording.
         */
        myGrabber.notFinished = false;

        /** This flag stops the Sampler from recording if
         *  it shouldn't have started yet. If it is already
         *  recording (as it usually is), this flag prevents
         *  the sampler from trying to sync any audio recorded
         *  past this point. This is pretty important, since
         *  the last sample is usually very delayed due to the
         *  ScreenGrabber occupying all available resources. So
         *  the last sample will be lagging a lot behind.
         *
         *  Note that the Sampler is still recording. This is
         *  because if the movie is to short, the Merge class
         *  will sometimes crash. In the ScreenGrabber there
         *  is a check to prevent this. When everything is
         *  ok, the ScreenGrabber sets it's recording flag to
         *  false, and then the Sampler can be stopped.
         *
         *  If all three threads (this one, the Sampler and the
         *  ScreenGrabber) are running simultaneously, this should
         *  work ok. If only one of them runs at a time, it should
         *  work ok as well, as long as there is not another
         *  active thread filling the sample buffer for sound
         *  in the mean time.
         */
        mySampler.stopped = true;

        if (recVideo) {
            /** This should not lock, because the ScreenGrabber does not
             *  do anything extraordinary up until this point.
             */
            while (myGrabber.recording && (!myProgressBar.cancelled)) {
                myGrabber.hold();
            }
            outWindow.out("Finished recording video to: " +
                    myGrabber.tempFile);
        }

        if (recAudio && (!myProgressBar.cancelled)) {
            /** THIS IS A POINT WHERE THINGS CAN STILL CRASH.
             *  It would be better to put up some sort of system
             *  where you gave this method a certain amount of time,
             *  then said that audio recording has failed, and
             *  then reinitialized the sampler.
             */
            mySampler.stopRecording();

            /** Tell the ScreenGrabber that audio recording has
             *  now stopped, and the ScreenGrabber can start
             *  claiming resources again.
             */
            myGrabber.audioRecording = false;
            myGrabber.wakeUp();
        }

        /** This is a good place to start the mouse timer,
         *  since we once stopped it just because it didn't need to
         *  run during recording. (It was stopped in recordAction()).
         */
        krutSettings.startMouseTimer();

        /** Encode video */
        if (recVideo && (!myProgressBar.cancelled)) {
            myGrabber.encode();
        }

        /** Merging audio and video */
        if (recVideo && recAudio) {
            if (myProgressBar.cancelled || myGrabber.unRecoverableError) {
                restoreGUI();
                outWindow.out("Error creating mov file, aborting");
            } else {
                if (myGrabber.error) {
                    outWindow.out("Error recording");
                    outWindow.out("Attempting to proceed creating interrupted movie");
                }
                /** Holds for video track to be written. */
                while (myGrabber.running && (!myProgressBar.cancelled))
                    myGrabber.hold();

                /** Waiting for the sampler here, because if it
                 *  crashes here, the user will be left with at least
                 *  a complete film, and hopefully complete audio as
                 *  well.
                 */
                while (mySampler.recording && (!myProgressBar.cancelled))
                    mySampler.hold();

                outWindow.out("Finished recording sound to: " +
                        mySampler.audioFile.getPath());

                myProgressBar.setStatus(myProgressBar.MERGING);

                /** First we make sure that we can really write to
                 *  the mov-file. If we can not, we will try a new
                 *  one until we can.
                 */
                while (saveQuery.videoFile.exists() && !saveQuery.videoFile.delete()) {
                    saveQuery.videoFile =
                            saveQuery.filterFile(saveQuery.getNextFile(saveQuery.videoFile));
                }

                /** Merging here */
                try {
                    /** First we get all the arguments to the Merge command
                     *  stored in local parameters. */
                    String audioFile = "";
                    audioFile = mySampler.audioFile.toURL().toString();
                    String mergeArguments[] = {"-o", saveQuery.videoFile.toURL().toString(),
                            myGrabber.tempFile, audioFile};
                    EncodingProgressBar argBar = myProgressBar;

                    /** At this point it should be perfectly safe to
                     *  reactivate the GUI and let the merging go on
                     *  in the background.
                     */
                    restoreGUI();
                    if (mergeAudioVideo(mergeArguments, argBar))
                        outWindow.out("Finished recording to: " +
                                mergeArguments[1]);
                } catch (Exception e) {
                    restoreGUI();
                    System.out.println(e);
                }
            }
        } else {
            restoreGUI();
        }
        outWindow.out("");
    }

    /**    Merges audio and video files into one media file.
     *
     *  @param  mergeArguments  A String array containing in order:
     *                          The String "-o",
     *                          A String representation of the
     *                          desired output file URL,
     *                          A String representation of the URL to a mov-file
     *                          containing the video data,
     *                          A String representation of the URL to a file
     *                          containing the audion data.
     *
     *          myProgressBar   An EncodingProgressBar that can be used to manually
     *                          interrupt the merging procedure.
     *
     *  @return true if Merge was completed, false if Merge was interrupted.
     */
    private boolean mergeAudioVideo(String[] mergeArguments,
                                    EncodingProgressBar myProgressBar) {
        try {
            new Merge(mergeArguments, myProgressBar);
            if (!myProgressBar.cancelled) return true;
        } catch (Exception e) {
            outWindow.out("Error merging files");
            outWindow.out("" + e);
        } catch (OutOfMemoryError o) {
            outWindow.out("Error merging files");
            outWindow.out("" + o);
        }
        return false;
    }


    /**    This method is run prior to starting a new recording.
     *  It is also run before speed tests and snap shots.
     *  It checks if the parameters for the
     *  Sampler and the ScreenGrabber has been changed
     *  since it was last created. If they have, they
     *  are updated, and if needed, the affected object
     *  is reinited, before recording starts.
     *  Changes that force the ScreenGrabber to reinit
     *  are changes to cap size, and changes to video encoding
     *  quality. Changes that force the Sampler to reinit
     *  are changes to audio encoding quality. File name
     *  changes are also checked here.
     */
    private void checkInited() {
        /** Used to flag that the ScreenGrabber needs reinitialization */
        boolean grabberCriticalChange = false;
        /** Used to flag that the Sampler needs reinitialization */
        boolean samplerCriticalChange = false;

        outWindow.out("Checking if reinitialization is needed");
        outWindow.out("");

        /** Should we record video & audio (these are flags). */
        recVideo = nextVideo;
        recAudio = nextAudio;

        /** Should we sync audio to the system clock (flag)? */
        mySampler.syncAudio = krutSettings.syncCheckbox.isSelected();

        /** Calculate the ratio between playback and recording fps,
         *  and give this ratio to the sampler. This is used to synchronize
         *  audio to video, even if video is played back at different speeds.
         */
        mySampler.speed = fpsQuery.plb / ((float) fpsQuery.fps);

        /** Optimize some sampler parameters for the different cases
         *  of recording speed. These are empirical values (2006), and they may
         *  not be very good on new systems. There are most certainly better
         *  parameters than these in either case, but they have on average
         *  given pretty good audio quality.
         */
        if (mySampler.speed < 1) {
            mySampler.sleepTime = 900;
            outWindow.out("Slow motion");
        } else if (1 < mySampler.speed) {
            mySampler.sleepTime = 100;
            mySampler.maxAhead = (int) (2000 /
                    (mySampler.sleepTime * (1 - mySampler.speed)));
            outWindow.out("Fast motion");
        } else {
            mySampler.sleepTime = 450;
            mySampler.countTimesLag = 2;
            outWindow.out("Normal speed mode");
        }
        outWindow.out("");

        /** Check if the capture area has changed.
         *  If it has, the ScreenGrabber needs to be reinitialized. */
        if (capQuery.altered) {
            myGrabber.capRect = new Rectangle(capQuery.xVal,
                    capQuery.yVal,
                    capQuery.widthVal,
                    capQuery.heightVal);
            grabberCriticalChange = true;
            capQuery.altered = false;
        }

        /** Check if any of the fps values have changed. */
        if (fpsQuery.altered) {
            myGrabber.setFps(fpsQuery.fps, fpsQuery.plb);
            fpsQuery.altered = false;
        }

        /** Check if the video encoding quality has changed.
         *  If it has, the ScreenGrabber needs to be reinitialized. */
        if (encSlider.altered) {
            myGrabber.encQuality = encSlider.quality / 100f;
            grabberCriticalChange = true;
            encSlider.altered = false;
        }

        /** Check if any of the audio parameters have changed.
         *  If they have, the Sampler needs to be reinitialized. */
        if (soundQuery.altered) {
            mySampler.frequency = soundQuery.frequency;
            mySampler.sampleSize = (soundQuery.sixteenBit ? 16 : 8);
            mySampler.channels = (soundQuery.stereo ? 2 : 1);
            samplerCriticalChange = true;
            soundQuery.altered = false;
        }

        /** Update all the save file names. */
        setAllSaveFiles();

        /** Reinitialize the ScreenGrabber if we have to. */
        if (grabberCriticalChange) {
            outWindow.out("ScreenGrabber critical parameters have been changed");
            outWindow.out("Reinitializing...");
            try {
                myGrabber.init();
            } catch (IOException e) {
                outWindow.out("" + e);
            }
            outWindow.out("Done!");
            outWindow.out("");
        }

        /** Reinitialize the Sampler if we have to. */
        if (samplerCriticalChange) {
            outWindow.out("Sampler critical parameters have been changed");
            outWindow.out("Reinitializing...");
            mySampler.init();
            outWindow.out("Done!");
            outWindow.out("");
        }
    }


    /** Runs the speed test. This gives a very rough estimate
     *  of the maximum fps that can be used for recording without
     *  frame loss. It does not consider hard drive limitations,
     *  or delays caused by audio recording.
     */
    public void speedTest() {
        try {
            /** First reinitialize the ScreenGrabber if needed. */
            checkInited();
            outWindow.out("");
            outWindow.out("Running new speed test...");
            double avgTime = myGrabber.testCapTime();
            outWindow.out("Average frame snapshot time (ms): " + avgTime);
            double[] testValues = myGrabber.testEnc();
            double avgSize = testValues[0];
            double avgEncTime = testValues[1];
            outWindow.out("Average frame size (bytes): " + avgSize);
            outWindow.out("Average encryption time per frame (ms): "
                    + avgEncTime + newline);
            myGrabber.init();
            outWindow.out("Estimated frame cap time (ms): " + (avgTime + avgEncTime));
            outWindow.out("Estimated maximum fps without frameloss:" + (1000 / (avgTime + avgEncTime)));
            if (myGrabber.time < avgTime + avgEncTime)
                outWindow.out("WARNING! Fps is set higher than recommended");
            outWindow.out("");
            //       	outWindow.out("Maximum number of frames: " + myGrabber.maxNOPics);
            //       	outWindow.out("Max total cap time (s): " +
            //       					(myGrabber.time * myGrabber.maxNOPics / 1000));
            //        	outWindow.out("");
            outWindow.out("For an accurate estimate of these parameters,");
            outWindow.out("make sure there is an average image in the cap area.");
            outWindow.out("Hard drive and sound recording will cause additional frame loss.");
            outWindow.out("NOTE: This function was primarily used in development.");
            outWindow.out("It is now inaccurate, but has been kept for comparative purposes.");
            outWindow.out("");
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /** Copies the save files from the SaveFileChooser
     *  to the ScreenGrabber and the Sampler. This
     *  method is called both from the checkInited()
     *  method and from the init() method. The saveQuery.altered
     *  flag which tells those methods to call this method is
     *  changed in the restoreGUI(), snapAction() and
     *  checkInited() methods.
     */
    private void setAllSaveFiles() {
        mySampler.setAudioFile((saveQuery.audioFile.getAbsoluteFile()).toString());
        myGrabber.screenshotFile = saveQuery.imageFile;
        try {
            File path = new File(saveQuery.videoFile.toString());
            path = path.getAbsoluteFile();
            File tempFile = new File(path.getParentFile().getAbsolutePath() + path.separatorChar + "temp.mov");

            /** Get the new tempFile. */
            tempFile = saveQuery.filterFile(tempFile);
            while (tempFile.exists() && (!tempFile.delete())) {
                tempFile = saveQuery.filterFile(saveQuery.getNextFile(tempFile));
            }
            tempFile = saveQuery.filterFile(tempFile);
            myGrabber.tempFile = tempFile.toURL().toString();
        } catch (MalformedURLException mue) {
            System.err.println(mue);
        }
    }

    /** Wait for a given amount of milliseconds.
     *
     *  @param millis   The amount of milliseconds this
     *                  method should hold.
     */
    private synchronized void timedHold(int millis) {
        try {
            wait(millis);
        } catch (InterruptedException ie) {
            System.err.println(ie);
        }
    }


    /***************************************************************************
     *  Third part :
     *  This part of the code contains methods which initialize the class.
     **************************************************************************/


    /** This method just creates the ScreenGrabber.
     *  This method is called from the init() method,
     *  in a separate thread, to try to get some flow in
     *  the display of the program.
     */
    private void createScreenGrabber() {
        /** Make the ScreenGrabber. It will start as a seperate
         *  thread, with highest priority. The constructor to
         *  ScreenGrabber will just perform a speed test, and
         *  then it's done. Both capRect and startFps are
         *  global parameters that are already initiated.
         */
        myGrabber = new ScreenGrabber(capRect, startFps);
        myGrabber.setPriority(Thread.MAX_PRIORITY);
        /** Starts the ScreenGrabber thread. This thread will
         *  basically go through the ScreenGrabber.init() method,
         *  and then wait, until recording is started. See the
         *  ScreenGrabber.run() method.
         */
        myGrabber.start();
    }

    /** This method just creates the Sampler.
     *  This method is called from the init() method,
     *  in a separate thread, to get some flow in
     *  the display of the program.
     */
    private void createSampler() {
        /** Make the Sampler, with the defaultFileName
         *  (a local parameter to the Sampler class) as the
         *  name of the file that will be created by the
         *  sampler. The Sampler is a separate thread, with
         *  highest priority. The constructor will just
         *  create the save file, and then it's done.
         */
        mySampler = new Sampler();

        /** Pass on initial parameters to the sampler */
        mySampler.channels = (startStereo ? 2 : 1);
        mySampler.sampleSize = (startSixteen ? 16 : 8);
        mySampler.frequency = startFrequency;

        mySampler.setPriority(Thread.MAX_PRIORITY);
        /** Starts the Sampler thread. This thread will
         *  basically go through the Sampler.init() method,
         *  and then wait, until recording is started. See the
         *  Sampler.run() method.
         */
        mySampler.start();
    }


    /** The starting message of KRUT.
     */
    private void printStartMessages() {
        outWindow.out("Welcome to Krut Computer Recorder.");
        outWindow.out("This program is licenced under the GPL, see readme.txt for details.");
        outWindow.out("");
        outWindow.out("To record audio, you need to enable the \"Wave out-mix\" (or similar),");
        outWindow.out("under recording in your sound controls.");
        outWindow.out("");
        outWindow.out("To record video from a media player you need to disable video acceleration.");
        outWindow.out("");
        outWindow.out("A tempfile (temp*.mov) without sound is always created");
        outWindow.out("in the path of you movie file.");
        outWindow.out("");
        outWindow.out("You can close this output window at any time (re-open through \"Menu\").");
        outWindow.out("");
    }

    /** Create and show the main JFrame of Krut. */
    private void createMainFrame() {
        try {
            // javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            // javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getCrossPlatformLookAndFeelClassName());
            // javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println(ex);
        }
        JFrame.setDefaultLookAndFeelDecorated(true);

        /** Create the main frame of the program. */
        frame = new JFrame("Krut Computer Recorder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /** The old layouts are kept in the code,
         *  since it is faster to find them this way
         *  than to try and find an old piece of code
         *  if they are needed.
         */
        //new GridLayout(1, 2));

        /** Create the main JPanel in the main frame. */
        JPanel converterPanel = new JPanel();

        /** Create the JToolBar for the main window. */
        JToolBar toolBar = new JToolBar();

        /** Add the buttons to toolbar */
        addButtons(toolBar);
        //frame.setOpaque(true);

        toolBar.setFloatable(false);

        /** Add the menu bar
         *  and the toolbar with buttons to the panel.
         */
        JMenuBar menuBar = createMenuBar();
        menuBar.add(toolBar);
        converterPanel.add(menuBar);

        /** Display the main window. It consists of
         *  two parts, the first is the buttons and the
         *  menu. The second, which is not visible at
         *  startup, is the timer window. This gets added
         *  to the frame in the activateGUI method.
         */
        frame.getContentPane().setLayout(new java.awt.GridBagLayout());

        /** Make the first part of the main window visible,
         *  buttons and menu. They will remain unactivated
         *  until the activateGUI has been executed.
         */
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        frame.getContentPane().add(converterPanel, gridBagConstraints);

        recButton.setEnabled(false);
        activeButton.setEnabled(false);
        snapshotButton.setEnabled(false);
        stopButton.setEnabled(false);
        mouseButton.setEnabled(false);
        menu.setEnabled(false);

        /** This Frame needs to be initiated before the GUI is visible.
         */
        snapShotFrame = new JFrame("Snapshot");

        /** Finished, pack and display frame */
        frame.pack();

        /** This is the original place of
         *  frame.setVisible(true);
         *  It was moved to the end of the activateGUI method.
         */
        frame.setVisible(true);
    }


    /**    Main init function.
     *  First the ScreenGrabber and the Sampler are created,
     *  then all classes for the menu bar functions are properly
     *  inited. After that, the main window is created and made
     *  visible.
     */
    public void init() throws IOException {

        System.out.println("Starting, please wait.");

        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

        createMainFrame();

        outWindow = new OutputText();
        outWindow.init(0, frame.getSize().height + 50);
        outWindow.out("Starting, please wait for the GUI to activate.");

        createScreenGrabber();
        createSampler();
        krutSettings = new KrutSettings(
                capRect, startFps, startEncQuality,
                startStereo, startSixteen, startFrequency);

        timer = new KrutTimer(frame);

        activateGUI();
    }

    /** Start a new thread, that waits for the other threads
     *  to finish, and then activates the buttons in the GUI.
     */
    private void activateGUI() {

        /** The Run_KRUT class need to have access to the
         *  classes making up the krutSettings window because
         *  they were originally all that there was, and unless the
         *  program is to be very rewritten, this class, along with
         *  the Sampler and the ScreenGrabber, still
         *  sometimes need to have direct access to them.
         */
        capQuery = krutSettings.getCapSizeQuery();
        fpsQuery = krutSettings.getFPSQuery();
        encSlider = krutSettings.getQualitySlider();
        soundQuery = krutSettings.getSoundQuery();
        saveQuery = krutSettings.getSaveFileChooser();

        /** Give classes in the GUI access to their output window */
        capQuery.myOutput = outWindow;
        fpsQuery.myOutput = outWindow;
        encSlider.myOutput = outWindow;
        soundQuery.myOutput = outWindow;
        saveQuery.myOutput = outWindow;

        /** This gives the krutSettings object access to the
         *  three checkboxes that appear both in the menu and
         *  the krutSettings window.
         */
        krutSettings.setVChkBoxMenuItem(vcbMenuItem);
        krutSettings.setAChkBoxMenuItem(acbMenuItem);
        krutSettings.setMChkBoxMenuItem(mcbMenuItem);

        /** Set the current save files in the SaveQuery */
        saveQuery.imageFile = saveQuery.filterFile(imageFile);
        saveQuery.audioFile = saveQuery.filterFile(audioFile);
        saveQuery.videoFile = saveQuery.filterFile(movieFile);

        /** Give the Sampler and the ScreenGrabber a copy
         *  of the saveQuery, so that they can know whether
         *  to overwrite files or not.
         */
        mySampler.mySaveQuery = saveQuery;
        myGrabber.mySaveQuery = saveQuery;

        myGrabber.mySnapShot = this.imageUtils;

        imageUtils.setFps(startFps);

        /** Update the save file names in the text fields in
         *  krutSettings
         */
        krutSettings.changeFileNames();

        /** We should listen to events from the
         *  timer toggle button.
         */
        timer.getTimerButton().addActionListener(this);
        timer.setOutput(outWindow);

        /** Print the start messages in the output window */
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                printStartMessages();

                /** Once the timer is created, it is added to the main frame
                 *  of the GUI, although it is not made visible until the
                 *  user chooses "Timer" from the menu.
                 */
                java.awt.GridBagConstraints gridBagConstraints =
                        new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 1;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.weighty = 1.0;
                frame.getContentPane().add(timer, gridBagConstraints);
                timer.setVisible(false);

                /** Activate the buttons and the menu */
                recButton.setEnabled(true);
                activeButton.setEnabled(true);
                snapshotButton.setEnabled(true);
                stopButton.setEnabled(true);
                mouseButton.setEnabled(true);
                menu.setEnabled(true);
            }
        });

        /** This is moved here from the marked position in the
         *  init method. Originally, the GUI was supposed to
         *  be showing, with inactive buttons and menu, while
         *  the rest of the objects were created.
         *  But in reality, java waited for all the other threads
         *  creating the different components to finish
         *  before the drawing of the GUI was completed. This
         *  looked ugly.
         *  Rather than restructuring the initialization of the
         *  program once more, the GUI is for the moment
         *  just kept invisible until everything is initialized.
         */
//        frame.setVisible(true);
    }

    private static void createAndShowGUI() {
        logger.info("Creating and showing GUI");

        Run_KRUT newContentPane = new Run_KRUT();
        try {
            newContentPane.init();
        } catch (IOException e) {
        }
    }

    public static void main(String[] args) throws IOException {
        createAndShowGUI();
    }
}
