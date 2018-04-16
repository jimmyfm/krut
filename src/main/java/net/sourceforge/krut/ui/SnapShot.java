package net.sourceforge.krut.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

    /**	This class is used to show a snapshot. Originally
     *  the snapshot was only used to show a single 
     *  screenshot; however it was later expanded
     *  to include the preview window. The new methods
     *  (the preview related ones) use the old methods
     *  for initialization. This update was done more
     *  like a hack than as a restructuring, and the
     *  readability of the code suffered. This should
     *  be worked on.
     *  The original code for showing just a
     *  screenshot is found in the snapshot-related
     *  methods in RunKRUT and ScreenGrabber, and the
     *  new calls for updating the preview window are
     *  made from the run method in the ScreenGrabber
     *  class. 
     *
     *@since 29 december 2004, 23:24
     * @author  jonte
     */
public class SnapShot extends Thread {
    
    /** The JFrame used to
     *  show the animation of the film as it records.*/
    public JFrame previewFrame;
    /** The class that paints the preview images. */
    private ShowPic preview;
    
    /** This is the amount of milliseconds to sleep between each update
     *  of the window. The window will only update if updatePreviewImage
     *  has been called since the last update.
     */
    private int sleepMillis = 1000;
    
    /** A flag used to keep track of if the update Thread is running or not.
     */
    private boolean isRunning = false;
    
    /** True if nextImage has been updated, and the window should be
     *  repainted, false if not.
     */
    private boolean imageUpdated = false;
    
    /** The image that will be displayed at the next update of the
     *  window.
     */
    private BufferedImage nextImage;
    
    /**  As long as the isRunning parameter is true,
     *  the present method sleeps an amount of time
     *  determined by the sleepMillis parameter. After
     *  sleeping, the updateImage method is called if
     *  the imageUpdated parameter is true. Then the
     *  present method sleeps again.
     */
    @Override
    public void run() {
        while (isRunning)
        try {
            sleep(sleepMillis);
            if (imageUpdated) updateImage();
        } catch (InterruptedException ie) {
            System.out.println(ie);
        }
    }
        
    /** Load a picture from file into an Image
     *
     *  @param  fileName    The name of the file containing the picture
     *  @param  comp        A component (like the frame where the image
     *                      should be drawn) to use for the MediaTracker.
     *
     *  @return     The Image.
     */
    public Image loadPic(String fileName, java.awt.Component comp)  throws OutOfMemoryError {
        Image loadImage = Toolkit.getDefaultToolkit().createImage(fileName);
        MediaTracker mediaTracker = new MediaTracker(comp);
        mediaTracker.addImage(loadImage, 0);
        try {
            mediaTracker.waitForID(0);
        } catch (InterruptedException ie) {
            System.err.println(ie);
        }
        return loadImage;
    }
    
    /** Initializes and shows the preview window, 
     *  displaying a new BufferedImage.
     *
     *  @param  width   The width of the preview window.
     *  @param  height  The height of the preview window.
     */
    public void initPreviewWindow(int width, int height) {
        if (previewFrame == null) 
            previewFrame = new JFrame("Preview (lower quality FPS)");
        nextImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        try {
            preview = new ShowPic(nextImage);
            JComponent newContentPane = preview;
            newContentPane.setOpaque(true);
            previewFrame.setContentPane(newContentPane);
            previewFrame.pack();
            previewFrame.setVisible(true);
        } catch (OutOfMemoryError o) {
            System.out.println(o);
        }
        if (!isRunning) {
            isRunning = true;
            start();
        }
        setPriority(MIN_PRIORITY);
    }
    
    /** Changes the update frequency of the preview window.
     *
     *  @param  fps     The update frequency given as updates per second.
     */
    public void setFps(int fps) {
        sleepMillis = 1000 / fps;
    }
    
    /** Closes the preview window and free system resources. */
    public void stopPreviewWindow() {
//        isRunning = false;
        previewFrame.dispose();
        /** Is this necessary? Can't remember. */
        previewFrame = null;
        preview = null;
    }
    
    /** Update the image in the preview window. The actual updating of
     *  the image seen in the window is done by the run method.
     *
     *  @param  nextImage   The BufferedImage that should be shown in
     *                      the preview window at the next update.
     */
    public synchronized void updatePreviewImage(BufferedImage nextImage) {
        this.nextImage = nextImage;
        imageUpdated = true;
    }

    
    /** Updates the image seen in the preview window to the one
     *  waiting in nextImage.
     */
    private synchronized void updateImage() {
        if (preview != null) preview.setImage(nextImage);
        imageUpdated = false;
    }
        
    /** This class shows the image.
     */
    private class ShowPic extends JPanel {
        private JPanel drawingPane;
        private BufferedImage image;
        private Graphics graph;
        private int imSizeX, imSizeY;
        private ShowPic(BufferedImage imageToShow) throws OutOfMemoryError {
            super(new BorderLayout());
            
            imSizeX = imageToShow.getWidth();
            imSizeY = imageToShow.getHeight();
//            image = new BufferedImage(imSizeX, imSizeY, imageToShow.getType());
//            graph = image.getGraphics();
//            graph.drawImage(imageToShow, 0, 0, this);
            image = imageToShow;
            drawingPane = new DrawingPane();
            drawingPane.setBackground(Color.white);
            JScrollPane scroller = new JScrollPane(drawingPane);
            /** The size of the scroller is set to the size of the image
             *  + the size of all the relevant (?) insets in order to make
             *  the scroller not show as the window displays. If it does not
             *  work, it is problably not a disaster. It is just cosmetic.
             */
            Insets scInsets = scroller.getInsets(), myInsets = this.getInsets();
            /** ^^^ */
            scroller.setPreferredSize(new Dimension(
                                        imSizeX + scInsets.left + scInsets.right
                                        + myInsets.left + myInsets.right,
                                        imSizeY + scInsets.top + scInsets.bottom
                                        + myInsets.top + myInsets.bottom));
            add(scroller, BorderLayout.CENTER);
            drawingPane.setPreferredSize(new Dimension(imSizeX, imSizeY));
            drawingPane.revalidate();
        }
        
        /** Change the image that shows in this window.
         *
         *  @param imageToShow  A BufferedImage representing the
         *                      new image to show.
         */
        public void setImage(BufferedImage imageToShow) {
            image = imageToShow;
            repaint();
        }
        
        public class DrawingPane extends JPanel {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                g.drawImage(image, 0, 0, this);
            }
        }
    }
    
    /** Show a new image in a new SnapShot window.
     *
     *  @param  frame   The frame to show the image in.
     *  @param  image   The image.
     */
    public void createAndShowGUI(JFrame frame, BufferedImage image) throws OutOfMemoryError {
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JComponent newContentPane = new ShowPic(image);
        newContentPane.setOpaque(true);
        frame.setContentPane(newContentPane);
        
        frame.pack();
        frame.setVisible(true);
    }   
}