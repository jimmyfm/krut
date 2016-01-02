/*
 * ScreenMarkerLine.java
 *
 * Created on 29. maj 2007, 16:37
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package krut.KRUT_GUI;

/**
 *
 * @author Jonas
 */
 /**  This is a class to draw lines that usually look like they are drawn
 *  directly on the screen. It can be used to mark an area of the screen.
 *  At the moment, only straight lines can be drawn.
 *
 *  The drawing is performed very crudly by using an undecorated JFrame
 *  to draw a series of pixels, lines, or rectangular shapes over a
 *  snapshot that has been taken of the background. These drawings are aligned
 *  and added up to a line of the specified dimensions.
  *
  * This class is almost, but not entirely, finished. It is not
  * currently used in the Krut program.
 */
public class ScreenMarkerLine {
    
    /** The robot used to take the snapshots of the background.
     */
    private java.awt.Robot robot = null;

    /** The JPanel class that is used to draw the ScreenMarkerLine.
     */
    private class LinePanel extends javax.swing.JPanel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 3374045436410808865L;
		java.awt.Color backupColor;
        
        public void paintComponent(java.awt.Graphics g) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
            if (!segmentHidden) {
                super.paintComponent(g);
                g2.drawImage(line, 0, 0, this);
                backupColor = g2.getColor();
                g2.setColor(safeColor);
                g2.drawLine(0, 0, 0, 0);
                g2.setColor(backupColor);
                System.out.println("painted picture");
            }
            else {
                super.paintComponent(g);
                backupColor = g2.getColor();
                g2.setColor(marker);
                g2.drawLine(0, 0, 0, 0);
                g2.setColor(backupColor);
                System.out.println("painted marker");
            }
        }
    }
    
    public java.awt.Color marker = java.awt.Color.CYAN;
    public java.awt.Color safeColor = java.awt.Color.BLACK;
    
    private javax.swing.JFrame segment;
    
    private LinePanel fill;
    
    /** This flag is true if the segment is hidden,
     *  and false if it is visible.
     */
    private boolean segmentHidden = false;
    
    /** The color used to paint the border of the segments
     *  of the line.
     */
    private java.awt.Color segmentBorderColor = java.awt.Color.LIGHT_GRAY;

    /** The color used to paint the segments of the line.
     */
    private java.awt.Color segmentColor = java.awt.Color.WHITE;
    
    /** The BufferedImage that is drawn on to the ScreenMarkerLine.
     */
    private java.awt.image.BufferedImage line;
    
    /** The size of each segment of the line.
     *  The length in pixels.
     */
    private int segmentSize = 0;
    
    /** The gap in each segment. The length in
     *  pixels. The gap is a part of the 
     *  segment, so obviously the gap can not
     *  be bigger than the segment size.
     */
    private int gapSize = 0;
    
    /** The bounds of this line. If the width or the
     *  height of the line are 0, the line will never
     *  be drawn.
     */
    private java.awt.Rectangle bounds = new java.awt.Rectangle(0, 0, 0, 0);
    
    /** The horizontal orientation for this class. */
    public final static int HORIZONTAL = 0;
    /** The vertical orientation for this class. */
    public final static int VERTICAL = 1;
    
    /** The orientation for this line.
     *  This is changed in the setOrientation method,
     *  and read through the getOrientation method.
     */
    private int orientation = HORIZONTAL;
    
    /** This is used to tell the paintThread when it
     *  should start launching events that look for a
     *  marker.
     */
    private boolean waitForMarker = false;
    
    /** This is used to tell the paintThread when it
     *  should take a snapshot.
     */
 //   private boolean takeSnapshot = false;
    
    /** The time in ms that the paintThread holds between
     *  each attempt to find a marker.
     */
    private int holdTime = 20;
    
    private Thread paintThread = new Thread(new Runnable(){
            public void run() {
                while (true) {
//                    System.out.println("Waiting to check for marker");
                    while (!waitForMarker) hold(0);
                    waitForMarker = false;
//                    System.out.println("Checking for marker");
                    do {
                        hold(holdTime);
                    } while (!checkForMarker());
//                    System.out.println("Updating graphics");
                    java.awt.EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            updateLineGraphics();
                            showSegment();
                            /** We need an extra one of these here in
                             *  case the graphics have been moved again.
                             */
                            waitForMarker = false;
                        }
                    });
                }
            }
        });
    
    /** Creates a new instance of ScreenMarkerLine with
     *  no size and orientation HORIZONTAL.
     */
    public ScreenMarkerLine() {
        this(new java.awt.Rectangle (0, 0, 0, 0), HORIZONTAL);
    }
    
    /** Creates a new instance of ScreenMarkerLine with
     *  the given bounds and orientation, provided they are valid,
     *  with one segment and no gap size.
     *
     *  @param  bounds          Should be a Rectangle with only 
     *                          positive integer values.
     *  @param  orientation     Should be either HORIZONTAL or VERTICAL.
     */
    public ScreenMarkerLine(java.awt.Rectangle bounds, int orientation) {
        this(bounds, orientation,
                (orientation == HORIZONTAL) ? bounds.width : bounds.height, 
                (orientation == HORIZONTAL) ? bounds.width : bounds.height);
    }
    
    /** Creates a new instance of ScreenMarkerLine with
     *  the given parameters, provided they are valid.
     *
     *  @param  bounds          Should be a Rectangle with only 
     *                          positive integer values.
     *  @param  orientation     Should be either HORIZONTAL or VERTICAL.
     *  @param  segmentSize     Should be a positive integer larger than 0.
     *  @param  gapSize         Should be a positive integer no larger than
     *                          the orientation.
     */
    public ScreenMarkerLine(java.awt.Rectangle bounds, int orientation,
                            int segmentSize, int gapSize) {
        setBounds(bounds);
        setOrientation(orientation);
        setSegmentSize(segmentSize);
        setGapSize(gapSize);
        try {
            robot = new java.awt.Robot();
        } catch (Exception e) {
            System.out.println("e");
        }
        paintThread.setPriority(Thread.MIN_PRIORITY);
        paintThread.start();
        System.out.println("made ScreenMarkerLine");
    }
    
    /** Used as a place to wait for sync in the updating
     *  of the graphics.
     *
     *  @param ms   The time to wait in ms. If 0 is given,
     *              the wait will be indefinite (until a
     *              notifyAll).
     */
    public synchronized void hold(long ms) {
        try {
            System.out.println("waiting " + ms + "ms.");
            wait(ms);
        } catch (InterruptedException ie) {
            System.err.println(ie);
        }
    }
    
    /** Called by the user to start capturing images.
     *
     * Also wakes up users waiting for the grabber to finish.
     * This method is called once when the last frame is captured,
     * and once more when all video data are written to the
     * temp file.
     */
    public synchronized void wakeUp() {
        notifyAll();
    }
    
    
    /** Returns the length of the line. */
    public int getLength() {
        if (orientation == HORIZONTAL)
            return bounds.width;
        else
            return bounds.height;
    }
    
    /** Returns the width of the line. */
    public int getWidth() {
        if (orientation == VERTICAL)
            return bounds.width;
        else
            return bounds.height;        
    }
    
    /** Sets the segment size in the orientation of
     *  the line. If the requested segment size is
     *  0 or negative, the line length
     *  will be set as the new segment size.
     *  The segment size perpendicular to
     *  the orientation of the line will always be
     *  the same as the line width.
     *
     *  @param  size    The requested segment size
     *                  in the orientation of the line.
     *
     *  @return     The segment size that was set.
     */
    public int setSegmentSize(int size) {
        if (0 < size)
            segmentSize = size;
        else
            segmentSize = getLength();
        return segmentSize;
    }
    
    /** Gives the segment size in the orientation of
     *  the line.
     *
     *  @return     The segment size of the line.
     */
    public int getSegmentSize() {
        return segmentSize;
    }
    
    /** Sets the gap size in the orientation of the
     *  line. If the gap size is larger than the
     *  segment size, the gap size will be set to
     *  the same as the segment size. 
     *  The gap size perpendicular to
     *  the orientation of the line will always
     *  be zero.
     *
     *  @param  size    The requested gap size in
     *                  the orientation of the line.
     *
     *  @return     The gap size that was set.
     */
    public int setGapSize(int size) {
        int tempSize = getSegmentSize();
        if (tempSize < size)
            gapSize = size;
        else
            gapSize = tempSize;
        return gapSize;        
    }
    
    /** Gives the gap size in the orientation of
     *  the line.
     *
     *  @return     The gap size of the line.
     */
    public int getGapSize() {
        return gapSize;
    }
   
    /** Moves the top left corner of the line
     *  to the position (x, y) and updates
     *  the line once.
     *
     *  @param  x   The new x position of the
     *              line. Should be a positive
     *              integer.
     *  @param y    The new y position of the 
     *              line. Should be a positive
     *              integer.
     *
     *  @return     0 if the move was succesful,
     *              -1 if the move was not succesful.
     */
    public int moveLine(int x, int y) {
        if ((0 <= x) && (0 <= y)) {
            bounds.x = x;
            bounds.y = y;
            segment.setLocation(x, y);
            System.out.println("pos:" + bounds.x + " " + bounds.y);
            segmentHide();
            return 0;
        } else
            return -1;
    }
    
    /** Sets the bounds of this line. The bounds should
     *  all be positive integers, otherwise they will be
     *  changed to 0.
     *
     *  @param  bounds  The requested new bounds for the line.
     *
     *  @return     0 if everything was fine, -1 if one
     *              or more of the bounds were changed.
     */
    public int setBounds(java.awt.Rectangle bounds) {
        int returnVal = 0;
        if (bounds.x < 0) {
            bounds.x = 0;
            returnVal = -1;
        }
        if (bounds.y < 0) {
            bounds.y = 0;
            returnVal = -1;
        }
        if (bounds.width < 0) {
            bounds.width = 0;
            returnVal = -1;
        }
        if (bounds.height < 0) {
            bounds.height = 0;
            returnVal = -1;
        }
        this.bounds.setBounds(bounds);
        return returnVal;
    }
    
    /** Get the bounds for this line.
     *
     *  @return     A Rectangle containting the bounds for 
     *              this ScreenMarkerLine.
     */
    public java.awt.Rectangle getBounds() {
        return bounds;
    }
    
    /** Sets the orientation of this line.
     *  The orientation should be one of
     *  HORIZONTAL or VERTICAL, otherwise
     *  the orientation will be set to VERTICAL.
     *  Changing the orientation will remove the
     *  current segment size and gap size.
     *
     *  @param  orientation     The requested orientation.
     *
     *  @return     The orientation that was set.
     */
    public int setOrientation(int orientation) {
        segmentSize = getLength();
        gapSize = 0;
        if (orientation == HORIZONTAL)
            this.orientation = orientation;
        else
            this.orientation = VERTICAL;
        return this.orientation;
    }
    
    /** This takes a new snapshot of the
     *  background, and fills in the line segments.
     */
    public void updateLineGraphics() {
//        System.out.println("getting snapshot");
       line = robot.createScreenCapture(bounds);
//       System.out.println("got snapshot");
       java.awt.Graphics2D lineGraphics = line.createGraphics();
       int segments = getLength() / getSegmentSize();
       java.awt.Rectangle fillRect;
       if (orientation == HORIZONTAL) {
           fillRect = new java.awt.Rectangle(
                   0, 0, getSegmentSize() - getGapSize(), getWidth());
       } else {           
           fillRect = new java.awt.Rectangle(
                   0, 0, getWidth(), getSegmentSize() - getGapSize());
       }
       int linePos = 0;
       while (0 < segments--) {
//           System.out.print("adding segment");
           linePos += getGapSize() / 2 + 1;
           if (orientation == HORIZONTAL)
               fillRect.setLocation(linePos,  0);
           else
               fillRect.setLocation(0, linePos);
           if (2 < getWidth()) {
               lineGraphics.setColor(segmentBorderColor);
               lineGraphics.drawRect(fillRect.x, fillRect.y, 
                                        fillRect.width, fillRect.height);
               lineGraphics.setColor(segmentColor);
               lineGraphics.fillRect(fillRect.x + 1, fillRect.y + 1,
                                        fillRect.width - 2, fillRect.height - 2);
           } else {
                lineGraphics.setColor(segmentBorderColor);
                if (orientation == HORIZONTAL) {
                   lineGraphics.drawLine(fillRect.x,
                                            fillRect.y,
                                            fillRect.x,
                                            fillRect.y + fillRect.height);
                   lineGraphics.drawLine(fillRect.x + fillRect.width,
                                            fillRect.y,
                                            fillRect.x + fillRect.width,
                                            fillRect.y + fillRect.height);
                   lineGraphics.setColor(segmentColor);
                   lineGraphics.fillRect(fillRect.x + 1, fillRect.y,
                                            fillRect.width - 2, 
                                            fillRect.height);
                } else {
                   lineGraphics.drawLine(fillRect.x,
                                            fillRect.y,
                                            fillRect.x + fillRect.width,
                                            fillRect.y);
                   lineGraphics.drawLine(fillRect.x,
                                            fillRect.y + fillRect.height,
                                            fillRect.x + fillRect.width,
                                            fillRect.y + fillRect.height);
                   lineGraphics.setColor(segmentColor);
                   lineGraphics.fillRect(fillRect.x, fillRect.y + 1,
                                            fillRect.width, 
                                            fillRect.height - 2);
                }
           }
       }
       //segment.repaint();
//       System.out.println("");
    }
    
    /** This is just for testing. */
    public void setFrame(javax.swing.JFrame inFrame) {
        segment = inFrame;
//        segment.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        fill = new LinePanel();
        fill.setPreferredSize(bounds.getSize());
        segment.getContentPane().add(fill, java.awt.BorderLayout.CENTER);
        segment.setLocation(new java.awt.Point(bounds.getLocation()));
        segment.pack();
        System.out.println("set frame");
    }
    
    /** The action that is performed everytime the segment
     *  is hidden. The correct way to update the graphics
     *  of the segment is to hide it, and then this method
     *  will update the graphics and display the segment again.
     */
    private void segmentHide() {
        segmentHidden = true;
//        fill.setPreferredSize(new java.awt.Dimension(1,1));
//        segment.setPreferredSize(new java.awt.Dimension(1,1));
        segment.setSize(1,1);
//        System.out.println("Segment was hidden");
        waitForMarker = true;
        segment.repaint();
        wakeUp();
    }
    
    private void showSegment() {
//        segment.setPreferredSize(bounds.getSize());
//        fill.setPreferredSize(bounds.getSize());
        segment.setSize(bounds.getSize());
        segmentHidden = false;
        segment.repaint();        
    }
       
    private boolean checkForMarker() {
        return robot.getPixelColor(bounds.x, bounds.y).equals(marker);
    }
    
    /** Create the JFrame that the line is drawn in.
     *
     */
    public void createLine() {
        segment = new javax.swing.JFrame();
        segment.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        segment.setUndecorated(true);
//        segment.addComponentListener(new java.awt.event.ComponentAdapter() {
//            public void componentHidden(java.awt.event.ComponentEvent evt) {
//                segmentHidden();
//            }
//        });
        fill = new LinePanel();
        fill.setPreferredSize(bounds.getSize());
        fill.setOpaque(true);
        segment.setContentPane(fill);
//        segment.getContentPane().add(fill, java.awt.BorderLayout.CENTER);
        segment.setLocation(bounds.getLocation());
        segment.pack();
        segment.setVisible(true);
    }
}