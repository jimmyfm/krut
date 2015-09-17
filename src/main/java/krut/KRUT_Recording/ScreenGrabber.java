/*
 * ScreenGrabber.java
 *
 * Created on den 29 december 2004, 23:45
 */

package krut.KRUT_Recording;

/**
 *
 * @author  jonte
 */

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.*;
import java.net.*;

import krut.Migration.JPEGCodec;
import krut.Migration.JPEGEncodeParam;
import krut.Migration.JPEGImageEncoder;

/** This class is used both to record a movie and to take
 *  separate screen shots. The movie is recorded into a temporary
 *  data file containing a series of screen shots. This temporary
 *  data file is an OutputStream, and the type of this stream can
 *  be changed relatively easy. Originally the stream did not point
 *  to a file but to memory, and because of this some methods that
 *  have no logical place in the present class still remain here. <BR><BR>
 * 
 *  After recording is finished, a DataList object is used to feed a
 *  JpegImagesToMoviesMod object with the images, creating a movie
 *  file. After a movie file has been created, a Merge object is
 *  used to add audio to the movie. <BR><BR>
 *
 *  There are several methods in this class involving speed and memory
 *  tests, which have become deprecated, but still remain to make sure
 *  that the program doesn't break. <BR><BR>
 *
 *  The present also handles the updating of the preview window,
 *  and the movements of the capture area when the mouse is being
 *  followed.
 */
public class ScreenGrabber extends Thread {
    
    /** The capture area for video recording. */
    public Rectangle capRect;
    /** The video encoding quality. The value of this parameter will be
     *  overwritten from Run_KRUT.checkInited(), so changing the initial
     *  value of this parameter will have no effect.
     */
    public float encQuality = 0.75f;
    /** This value represents the limit for when the capRect should move
     *  to be re-centered around the mouse pointer, if we are tracking
     *  the mouse pointer. A comparing value is calculated, and if this 
     *  comparing value is larger than the moveLimit value, the capRect
     *  is re-centered. The comparing value is calculated as follows:<BR><BR>
     *  
     *  d1 =    The distance from the capRect center to the mousepointer.<BR>
     *  d2 =    The distance from the capRect center to the bounding edge of the
     *          capRect, measured along the line through the mousepointer.<BR><BR>
     *
     *  moveLimit = d1 / d2
     */
    public double moveLimit = 1.0;
    /** This value represents the acceleration of the capRect when
     *  we are tracking the mouse pointer. The value is used both for
     *  acceleration and retardation.
     *
     *  Currently, this value is updated in the run() method, at the start
     *  of the main loop, everytime a recording is started, so changing it
     *  here will have no effect.
     */
    public int acceleration = 5;
    /** time should only be read.
     *  time is the time in ms between each frame */
    public double time;
    /** maxNOPics should only be read.
     *  maxNOPics is the maximum number of frames that
     *  can be recorded into memory.
     */
    public int maxNOPics;
    /** cntMovies handles the numbering of the movie files,
     *  and is updated everytime a movie is finished recording.
     *  cntMovies should only be read.
     */
    public int cntMovies = 0;
    /** notFinished is set to false by user to stop recording. */
    public boolean notFinished = false;
    /** recording is set to false at the exact time when capping is finished.
     *  This is used to synchronize shound. */
    public boolean recording = false;
    /** running is set to false when the ScreenGrabber
     *  is ready for another recording. It is set to true
     *  in the run() method and changed back to false in 
     *  the encode() method.
     */
    public boolean running = false;
    /** A flag to indicate that something has gone terribly
     *  wrong with the recording of the movie. Old. Only used in old methods. */
    public boolean unRecoverableError = false;
    /** getMouse selects if the mouse pointer should be in the film.
     *  getMouse can be changed at any time, including during recording,
     *  to start/stop recording mouse positions.
     */
    public boolean getMouse = true;
    /** Should we follow the mouse pointer around the screen or not?
     *  This parameter can be changed at any time, including during recording.
     */
    public boolean followMouse = false;
    /** if preview is true, an image is sent to the SnapShot
     *  object at each frame, where a preview film of the
     *  recording is running.
     */
    public boolean preview = false;
    /** error is just used to signal that something went wrong with video
     *  recording. error is set true if an exception was fired in the run()
     *  method. error is set false in the init() method.
     */
    public boolean error = false;
    /** A flag used to prevent the recording from starting by
     *  mistake while the init() method is running. Changed in 
     *  the init() method, and checked at the beginning of run().
     *  Also changed before and after the call to init() in encode().
     *  The initing parameter could be changed by the user before
     *  and after calling init() for increased reliability on the
     *  flag controls, but in reality reliability is pretty good the
     *  way it is, with changes at the start and end of the init()
     *  method.
     */
    public boolean initing = false;
    /** The name of the tempFiles. This is changed in Run_KRUT.setAllSaveFiles(),
     *  and changing it here has no effect.
     */
    public String tempFile = "temp.mov";
    /** The name and path of the screenshotFile. It is changed from the
     *  setAllSaveFiles() method in the Run_KRUT class.
     */
    public File screenshotFile = null;
    /**  This object is used if the user wants to interrupt the encoding.
     *   In the EncodeThread constuctor, the DataList is also given to
     *   myProgressBar, so that the progressBar can stop the
     *   dumper.
     */
    public krut.KRUT_GUI.EncodingProgressBar myProgressBar;
    /** This flag can be used to get the run() method to hold
     *  at the end, in order to increase sync between audio
     *  and video. If so, it should be set true when audio
     *  recording is started, and then set false once recording
     *  is finished. The practical result will be that the
     *  run() method will take a pause before performing
     *  it's call to init().
     *
     *  After this parameter is set false when audio recording
     *  is finished, the run() method MUST be woken with a call
     *  to wakeUp().
     */
    public boolean audioRecording = false;
    /** It is necessary for the ScreenGrabber to have access to a SaveFileChooser,
     *  because the ScreenGrabber needs to use the getNextFile() method both in
     *  init() and in finished(). This parameter will be updated by Run_KRUT
     *  as soon as the global SaveFileChooser is initiated.
     */
    public krut.KRUT_GUI.SaveFileChooser mySaveQuery =
            new krut.KRUT_GUI.SaveFileChooser();
    
    /** It is necessary for the ScreenGrabber to have access to a SnapShot,
     *  in case a preview film is to be displayed during recording.
     *  This parameter will be updated by Run_KRUT immideatly after
     *  after initializing.
     */
    public krut.KRUT_GUI.SnapShot mySnapShot =
            new krut.KRUT_GUI.SnapShot();
    
    /**	Variables for the run() and init() methods only */
    
    /** cntPics is used to count which frame we're on.
     *  This parameter will always come out from the init()
     *  method with the value 1, since the first frame is
     *  already written. This means that we can not miss
     *  the first frame. This is important in the DataList
     *  class.
     *
     *  cntPics is increased with one everytime a frame is
     *  succesfully captured in the run() method.
     */
    private int cntPics;
    /** cntMissed is used to count how many frames we've missed.
     *  This parameter is set to 0 in the init() method, and increased
     *  with each missed frame in the run() method.
     */
    private int cntMissed;
    /** lastFrame is a byte array used to store the encoded frame
     *  that is used as the first and the last frame of the movie.
     *
     *  lastFrame is initiated in the init() method, and is written to
     *  the OutputStream once in the init() method, and once at the 
     *  end of the run() method.
     */
    private byte[] lastFrame;
    /** Keeps track of the size of the ByteArrayOutputStream
     *  used to store frames in memory. oldSize is used in the 
     *  run() method to calculate the exact size of each new frame.
     */
    private int oldSize = 0;
    /**	sizes is used to store sizes of all captured frames. */
    private int[] sizes;
    /**	missedFrames is used to store number of all missedFrames. */
    private int[] missedFrames;
    //	This is where all captured and encoded frames are stored in memory.
    private ByteArrayOutputStream jpgBytesII;
    private FileOutputStream fileBytes;
    private BufferedOutputStream fileBuffer;
    private DataOutputStream jpgBytes;
    /**	Used directly as output file, this file is used to save
     *	frames into after recording is finished, in the run() method. */
    private File dumpFile;
    /**	Used in the run() method to keep capture in sync.
     *	syncTime can be set needs to be set in the setSyncTime() method.
     *	This must be done before recording is started. */
    double syncTime;
    /**	Used in the run() method to keep capture in sync. */
    private long currentTime;
    
    /**	Variables used by many methods */
    
    /** myRuntime is used to check available memory. */
    private Runtime myRuntime;
    /**	The robot used for screen caputure. */
    private Robot robot;
    /**	The BufferedImage used to store captured frames. */
    private BufferedImage image;
    private Graphics2D imageGraphics;
    /**	Used to get the fps value for method startDumper.
     *	Also used to calculate time in method setFps. */
    private int fps;
    /**	The average size of captured and encoded images.
     *	Used to get an estimate of the number of frames
     *	that can be stored in memory. */
    private double avgSize = Double.MAX_VALUE;
    /**	The Encoder, used to
     *	encode captured images. */
    private JPEGImageEncoder encoder;
    /**	The Encoder parameters, used to
     *	encode captured images. */
    private JPEGEncodeParam param;
    /** This object is used to reload the images
     *  from the file they are saved in, and then
     *  supply them to the JpegImagesToMovieMod class.
     *  A new images object is created and set up
     *  at the end of the main recording thread
     *  (in the run() method). When the encode() method
     *  is called by the user, it takes this object and
     *  passes it on to the startDumper() method.
     */
    protected DataList images;
    
    /** This is the screensize of the default screen.
     *  If another screen would be used, this parameter would need
     *  to be updated.
     */
    protected Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    /** The speed with which the capRect is currently moving, if we are
     *  tracking the mouse position.
     */
    private int moveSpeed = 0;
    /** The direction in which the capRect is currently moving, if we are
     *  tracking the mouse position. The only reason this object is global
     *  is to always have access to the Direction.normalize() method. This
     *  object is used in capRectMover.
     */
    private Direction moveDir = new Direction(0,0);

    /** Constructor for ScreenGrabber.
     *  Test encoding to get a good
     *  value for avgCapSize set up.
     *  Then setup outfiles.
     *
     *  @param  capSize The initial capture area of the ScreenGrabber.
     *                  This can later be changed by changing the public
     *                  parameter capRect, and then calling the init method
     *                  of this class.
     *  @param  fps     The initial fps of the ScreenGrabber. This can
     *                  later be changed by calling the setFps method of this
     *                  class.
     */
    public ScreenGrabber(Rectangle capSize, int fps) {
        capRect = capSize;
        setFps(fps, fps);
        try {
            // Start the robot, and perform a
            // performance test.
            robot = new Robot();
            System.out.print("Initializing ...");
            testEnc();
            System.out.println(" Done.");
        } catch (AWTException awte) {
            System.err.println("AWTException " + awte);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IOException " + e);
        }
    }
    
    /** Constructor for ScreenGrabber.
     *  Setup the capture Rectangle and the fps
     *  to use. Then test encoding to get a good
     *  value for avgCapSize set up.
     *  Then setup outfiles.
     */
    public ScreenGrabber() {
        this((new Rectangle(0, 0, 100, 100)), 15);
    }
    
    
    /**	Init or reinit the encoder.
     * This needs to be done everytime the amount of memory
     * needed has changed, the sizes of the frames have changed,
     * or just between every recording.
     */
    public void init() throws IOException {
        initing = true;
        jpgBytes = null;
        fileBuffer = null;
        fileBytes = null;
        /** This is an array for storing image data
         *  directly into memory. It is only used in
         *  The initialization process.
         */
        jpgBytesII = new ByteArrayOutputStream();
        /** Clear memory. */
        encoder = null;
        sizes = null;
        missedFrames = null;
        System.gc();
        /** trying to allocate all available memory except 20MB
         *  that are saved for performance purposes. If this fails,
         *  allocate half of the available memory below.
         *
         *  maxMemory = the maximum memory that we can use.
         *  totalMemory = the memory that we have reserved so far.
         *  freeMemory = the part of totalMemory that is not used.
         */
        myRuntime = Runtime.getRuntime();
        Double convert = new Double(myRuntime.maxMemory() -
                myRuntime.totalMemory()	+ myRuntime.freeMemory() - 2097152*10);
        System.out.println("Memory attempt 1: " + convert);
        if (convert.intValue() < 0) {
            convert = new Double((myRuntime.maxMemory() -
                    myRuntime.totalMemory() + myRuntime.freeMemory()) * 0.5d);
        }
        System.out.println("Memory attempt 2: " + convert);
        /** Set up a save file for encoded jpg images.
         *  This file is then used directly as output.
         */
        dumpFile = new File("dumpFile" + cntMovies);
        /** This is just a safety check to see that the
         *  file can really be written too. */
        while (dumpFile.exists() && !dumpFile.delete()) {
            dumpFile = mySaveQuery.getNextFile(dumpFile);
        }
        dumpFile.deleteOnExit();
        /** Allocate memory for frame storage in a file
         *  buffer for the output file.
         */
        fileBytes = new FileOutputStream(dumpFile);
        System.out.println("filebuffer should use: " + 
                (convert.intValue() / 2));
        fileBuffer = new BufferedOutputStream(fileBytes,
                (convert.intValue() / 2));
        /** jpgBytes will be the output stream that we write
         *  to later on in the program.
         */
        jpgBytes = new DataOutputStream(fileBuffer);
        /** Figure out how many integers we can hold in the
         *  remaining memory. An integer takes 4B, and we will
         *  have 2 arrays of integers. We don't want to fill the
         *  entire memory, and we have used a third, or possibly
         *  even half, already. So we will allocate 2 arrays of
         *  integers, each using a sixth of the available memory.
         *  That means we can at the most have
         *  convert.intValue() / 24
         *  integers in each array. That also means we can at
         *  the most have that amount of frames in a film.
         *
         *  As a comparison, an integer array of 1 MB can hold
         *  262144 integers, which would be enough for
         *  2h25m38s of film at 30 FPS.
         */
        maxNOPics = (int) (convert.intValue() / 24);
        System.out.println("Memory after filebuffer: " +
                (myRuntime.maxMemory() - myRuntime.totalMemory() + 
                myRuntime.freeMemory()));
        /** Init the encoder to store encoded images directly to memory.
         *  This will be changed later, but is an easy way of getting a
         *  first frame for the film, since that frame is to be kept in
         *  memory anyway  (see below).
         *
         *  First we take an "average (=random)" image for the method
         *  encoder.getDefaultJPEGEncodeParam(image) below.
         */
        image = robot.createScreenCapture(capRect);
        /** Set the encoder to the OutputStream that stores in memory. */
        encoder = JPEGCodec.createJPEGEncoder(jpgBytesII);
        /** Get an "average" JPEGEncodeParam */
        param = encoder.getDefaultJPEGEncodeParam(image);
        /** Set encoding quality */
        param.setQuality(encQuality, false);
        encoder.setJPEGEncodeParam(param);
        /** Store an empty image in the first frame of the film.
         *  Then keep that jpg image in memory to also be used as the
         *  last frame in the film as well
         */
        image = new BufferedImage(image.getWidth(),
                image.getHeight(),
                image.getType());
        encoder.encode(image);
        lastFrame = jpgBytesII.toByteArray();
        /** Clear this output stream, which we will not use for 
         *  anything more than this initialization procedure, 
         *  to save some memory */
        jpgBytesII = null;
        /** Change the encoder to store encoded image in the
         *  buffered FileOutputStream.
         *
         *  First we take another "average" screenshot.
         */
        image = robot.createScreenCapture(capRect);
        /** Set the encoder to the FileOutputStream */        
        encoder = JPEGCodec.createJPEGEncoder(jpgBytes);
        /** Get an "average" JPEGEncodeParam */
        param = encoder.getDefaultJPEGEncodeParam(image);
        /** Set encoding quality */
        param.setQuality(encQuality, false);
        encoder.setJPEGEncodeParam(param);
        /** We write the black frame into the outputstream,
         *  since we need at least one frame to prevent the
         *  situation that we would miss capturing the first
         *  frame, and then try to fill it by repeating a
         *  frame that never existed.
         */
        jpgBytes.write(lastFrame, 0, lastFrame.length);
        /** Allocate int Arrays for storing image sizes,
         *  and missed images. Setup remaining variables.
         */
        System.out.println("arrays should use (in total): " +
                    (maxNOPics * 8));
        boolean memError = true;
        /** Allocate an integer array for storing the sizes of
         *  each recorded image. Allocate an additional array
         *  to store every frame that was missed. The number of
         *  integers in these arrays represents the maximum number
         *  of frames that can be recorded in a single film
         *  using this design of the program. This is not pretty.
         *
         *  A very good alternative would be to store an integer
         *  or possibly even something bigger, before each frame
         *  in OutputStream, to indicate the size of each frame.
         *  0 or -1 could be used for missing frames.
         *
         *  The reason for doing this with arrays with prereserved
         *  memory in the first place was speed. It was unnecessary,
         *  beacuse it does not give much speed, but now it will
         *  not be changed now until the procedure described above
         *  is implemented.
         */
        while (memError) {
            memError = false;
            try {
                sizes = new int[maxNOPics];
                //	Allow as many missed frames.
                missedFrames = new int[maxNOPics];
            } catch (OutOfMemoryError oe) {
                System.err.println("Unexpected memory error in ScreenGrabber.init()");
                System.err.println("Trying to allocate smaller arrays.");
                memError = true;
                maxNOPics /= 2;
            }
        }
        System.out.println("Memory after arrays: " +
                (myRuntime.maxMemory() - myRuntime.totalMemory() + 
                myRuntime.freeMemory()));        
        /** This is needed in case no frames are missed, since
         *  missedFrames must contain a value. If a frame is missed,
         *  this value will be overwritten.
         */
        missedFrames[0] = maxNOPics + 1;
        /** One frame is already caught, setup size for that frame */
        sizes[0] = jpgBytes.size();
        /** Setup size counter. */
        oldSize = sizes[0];
        /** Setup frame counter */
        cntPics = 1;
        cntMissed = 0;
        unRecoverableError = false;
        error = false;
        /** These last two lines are for waking up the
         *  run method, in the unlikely event that it
         *  is waiting on init to finish.
         */
        initing = false;
        wakeUp();
    }
    
    /**	Set the fps values.
     *
     *  @param  fps The recording fps.
     *  @param  playbackFps The playback fps.
     */
    public void setFps(int fps, int playbackFps) {
        this.fps = playbackFps;
        time = 1000d / fps;
        mySnapShot.setFps(fps);
    }
    
    
    /**	Creates and returns the mouse cursor, given the
     *  position of the mouse. This is the only method used
     *  to draw the mouse pointer, so this the mouse pointer
     *  can be completely changed here (as long as it is still
     *  a polygon, and still black with a white border). This
     *  method is called from the main loop of the run() method
     *  once for every frame. The resulting Polygon is drawn
     *  into the frame in the run() method.
     *
     *  @param  mousePos    The position where the mouse should be
     *                      drawn into the frame.
     *
     *  @return     A Polygon representation of the mouse pointer,
     *              ready to be drawn directly into the frame.
     */
    private Polygon createMouse(Point mousePos) {
        Polygon polly = new Polygon();
        polly.addPoint(mousePos.x - capRect.x, mousePos.y - capRect.y);
        polly.addPoint(mousePos.x - capRect.x, mousePos.y  - capRect.y + 17);
        polly.addPoint(mousePos.x - capRect.x + 5, mousePos.y  - capRect.y + 12);
        polly.addPoint(mousePos.x - capRect.x + 12, mousePos.y  - capRect.y + 12);
        
        /** This is the old mousepointer */
        // polly.addPoint(mousePos.x - capRect.x, mousePos.y - capRect.y);
        // polly.addPoint(mousePos.x - capRect.x + 15, mousePos.y  - capRect.y + 7);
        // polly.addPoint(mousePos.x - capRect.x + 9, mousePos.y  - capRect.y + 9);
        // polly.addPoint(mousePos.x - capRect.x + 7, mousePos.y  - capRect.y + 15);
        return polly;
    }
        
    /** This method gives the Point on the intersection between
     *  the bounding edge of a Rectangle, and a line going from
     *  the center of the Rectangle in direction (xDir, yDir).
     *
     *  The position returned by this method is used by the
     *  capRectMover() method to check if the distance between
     *  the mouse pointer and the center of the capRect is big enough
     *  to start moving the capRect.
     *
     *  @param  rect    A Rectangle.
     *  @param  dir     A Direction object representing the direction
     *                  from the center of rect to the mouse pointer.
     *
     *  @return     A point representing the position of the intersection
     *              between the bounding edge of the rectangle and the line
     *              from the center of the rectangle in the direction dir.
     */
    private Point getDirectionEdgeIntersection(Rectangle rect, Direction dir) {
        /** The x position of the vertical side of the Rectangle which is
         *  closest to the intersection.
         */
        int xSide;
        /** The y position of the horizontal side of the Rectangle which is
         *  closest to the intersection.
         */
        int ySide;
        
        /** The length of the line going from the center of the
         *  rectangle to the x-edge.
         */
        double xLineLength = screenSize.width;
        /** The length of the line going from the center of the
         *  rectangle to the y-edge.
         */
        double yLineLength = screenSize.height;
        
        /** The length of the shortest of the two lines
         *  xLineLength and yLineLength.
         */
        double lineLength;
        
        /** The x-coordinate of the intersection. */
        int xIntersection;
        
        /** The y-coordinate of the intersection. */
        int yIntersection;
        
        /** Check which of the vertical sides of the rectangle that is
         *  closest to the intersection.
         */
        if (dir.x < 0) {
            xSide = rect.x;
        } else if (0 < dir.x) {
            xSide = rect.x + rect.width;
        } else {
            /** The direction has no x-component. */
            xSide = -1;
        }
        
        /** Check which of the horizontal sides of the rectangle that is
         *  closest to the intersection.
         */
        if (dir.y < 0) {
            ySide = rect.y;
        } else if (0 < dir.y) {
            ySide = rect.y + rect.height;
        } else {
            /** The direction has no y-component. */
            ySide = -1;
        }
        
        /** Figure out the distance from the center of the rectangle,
         *  to the intersection between a line going straight from the
         *  center of the rectangle, and a line following the vertical
         *  side of the rectangle.
         */
        if (0 <= xSide) {
            xLineLength = (xSide - rect.getCenterX()) / dir.x;
        }
        
        /** Figure out the distance from the center of the rectangle,
         *  to the intersection between a line going straight from the
         *  center of the rectangle, and a line following the horizontal
         *  side of the rectangle.
         */
        if (0 <= ySide) {
            yLineLength = (ySide - rect.getCenterY()) / dir.y;
        }
        
        /** The shortest one of the two distances xLineLength and yLineLength,
         *  will be the distance from the center of the rectangle to the
         *  edge of the rectangle, in the given direction.
         */
        lineLength = Math.min(xLineLength, yLineLength);
                
        /** Calculate and return the x and y coordinates of the intersection. */
        xIntersection = (int) (rect.getCenterX() + lineLength * dir.x);
        yIntersection = (int) (rect.getCenterY() + lineLength * dir.y);
        return new Point(xIntersection, yIntersection);
    }
    
    /** This method handles the movement of the capRect. If we're
     *  tracking the mouse pointer, this method is called from the
     *  run() method once every time a frame is recorded.
     *
     *  See the comments to the parameter moveLimit, for a clarification
     *  on how the movement works.
     *
     *  @param realMousePos A point representing the position of
     *                      the mouse pointer.
     */
    private void capRectMover(Point realMousePos) {
        /** Used to measure the distance between the center of the
         *  capRect, and the mouse pointer.
         */
        int distance;
                
        /** Figure out where the capRect would be positioned if it
         *  was centered around the mouse pointer.
         */ 
        Point moveTarget = new Point(realMousePos.x - capRect.width / 2,
                                        realMousePos.y - capRect.height / 2);
                        
        /** Are we in the process of moving the capRect right now? */
        if (moveSpeed != 0) {    
            /** We're moving. */
                        
            /** Make sure we don't move out of the screen. */
            if (moveTarget.x < 0) {
                moveTarget.x = 0;
            }
            if (screenSize.width < capRect.width + moveTarget.x) {
                moveTarget.x = screenSize.width - capRect.width;
            }
            if (moveTarget.y < 0) {
                moveTarget.y = 0;
            }
            if (screenSize.height < capRect.height + moveTarget.y) {
                moveTarget.y = screenSize.height - capRect.height;
            }

            /** Figure out how far we have to move before we reach our target. */
            distance = (int) moveTarget.distance(capRect.getLocation());
            
            /** Figure out in which direction we are moving. */
            moveDir = moveDir.normalize(moveTarget.x - capRect.x,
                                        moveTarget.y - capRect.y);

            /** Check if we should stop moving */
            if ((moveSpeed <= 0) || (distance <= moveSpeed)) {
                /** Stop moving. */
                moveSpeed = 0;
                distance = 0;
                capRect.setLocation(moveTarget);
            } else {
                /** Move to the new position */
                capRect.translate((int) (moveDir.x * moveSpeed),
                                    (int) (moveDir.y * moveSpeed));
                /** Change the speed of the movement based on how close
                 *  we are to our target.
                 */
                if (distance <= (0.5 * moveSpeed * moveSpeed / acceleration)) {
                    moveSpeed -= acceleration;
                } else {
                    moveSpeed += acceleration;
                }
            }
        }
        
        /** Is the capRect stationary right now? */
        if (moveSpeed == 0) {
            /** The capRect is not moving. */
            
            /** Check the distance between the mouse pointer and the center
             *  of the capRect.
             */
            distance = (int) moveTarget.distance(capRect.getLocation());
            
            /** Figure out the direction from the center of the capRect to
             *  the mouse pointer.
             */
            moveDir = moveDir.normalize(moveTarget.x - capRect.x,
                                        moveTarget.y - capRect.y);
            
            /** Check if we should start moving. */
            double compDistance = 
                    getDirectionEdgeIntersection(capRect, moveDir).distance(
                    capRect.getCenterX(), capRect.getCenterY());
            double compValue = distance / compDistance;
            if (moveLimit < compValue) {
                
                /** We should start moving. */
                moveSpeed += acceleration;
            }
        }        
    }
    
    
    /**	Set the syncTime for the run() method
     * This MUST be done before wakeUp() is called
     * to start the recording.
     *
     *  @param  syncTime    A parameter determining how many millis
     *                      behind the fps the capturing can fall,
     *                      before a frame is doubled to compensate.
     */
    public void setSyncTime(long syncTime) {
        this.syncTime = syncTime;
    }
    
    
    
    /** Test the Robot cap time per frame.
     *  Capture iterations frames in the loop,
     *  and calculate and return an average.
     *  This method is called by the speedTest()
     *  method in Run_KRUT.
     *
     *  @return The average capture time in milliseconds.
     */
    public double testCapTime() throws IOException{
        long syncTime;
        int iterations = 30;
        double avgTime = 0;
        try {
            for (int cnt = 0; cnt < iterations ; cnt++) {
                syncTime = System.currentTimeMillis();
                // image is a class BufferedImage.
                image = robot.createScreenCapture(capRect);
                avgTime = System.currentTimeMillis() - syncTime + avgTime;
            }
            avgTime /= iterations;
            return avgTime;
        } catch (OutOfMemoryError oe) {
            System.err.println("Unable to perform cap time test.");
            System.err.println(oe);
            return Double.MAX_VALUE;
        }
    }
    
    
    /**	Test the average encoder encoding size and time.
     * Encode iterations frames in the loop,
     * and calculate and return average values of
     * speed and size. avgSize is also set
     * as a class variable, for simplicity,
     * since other methods in the ScreenGrabber
     * have use for it.
     * For this method to deliver reasonable values,
     * there should be an 'average' picture
     * in the capture area.
     */
    public double[] testEnc() throws IOException {
        // Create a new, local ByteArrayOutputStream to
        // store frames.
        ByteArrayOutputStream jpgBytes = new ByteArrayOutputStream();
        int iterations = 20;
        double avgSize = 0, avgEncTime = 0;
        long syncTime;
        try {
            //	Capture one image in case there is none
            //	in memory. image is a class BufferedImage.
            image = robot.createScreenCapture(capRect);
            //	Initialize a new JPEGEncoder for local jpgBytes
            encoder = JPEGCodec.createJPEGEncoder(jpgBytes);
            param = encoder.getDefaultJPEGEncodeParam(image);
            param.setQuality(encQuality, false);
            encoder.setJPEGEncodeParam(param);
            // Encode one image to get a very rough
            // estimate of the average size
            // capRect is set in the constructor.
            // image is a class BufferedImage.
            encoder.encode(image);
            avgSize = jpgBytes.size();
            // Reserve twice the average size for each
            // frame. This is done for speed. Then make
            // a new JPEGEncoder for this new jpgBytes.
            jpgBytes = new ByteArrayOutputStream((int) avgSize * iterations * 2);
            encoder = JPEGCodec.createJPEGEncoder(jpgBytes);
            param = encoder.getDefaultJPEGEncodeParam(image);
            param.setQuality(encQuality, false);
            encoder.setJPEGEncodeParam(param);
            for (int cnt = 0; cnt < iterations ; cnt++) {
                syncTime = System.currentTimeMillis();
                encoder.encode(image);
                avgEncTime = System.currentTimeMillis() - syncTime + avgEncTime;
            }
            avgSize = jpgBytes.size() / iterations;
            avgEncTime /= iterations;
            // Set class variable avgSize.
            this.avgSize = avgSize;
            // Return values.
            double[] values = new double[2];
            values[0] = avgSize;
            values[1] = avgEncTime;
            return values;
        } catch (OutOfMemoryError oe) {
            System.err.println("Unable to perform size and encoding time test.");
            System.err.println(oe);
            double[] errors = {Double.MAX_VALUE, Double.MAX_VALUE};
            return errors;
        }
    }
    
    /**	Take a snapshot of the selected screencap area
     *  and save to a new screenshot file. Overwrites any
     *  prior screenshot file with the same name.
     *  (In a normal case, the Run_KRUT class that calls
     *  this method from Run_KRUT.snapaction() will be in
     *  control of whether files should be overwritten or not,
     *  and make sure that the global parameter screenshotFile
     *  has a correct filename.)
     */
    public void snapshot() {
        try {
            FileOutputStream outFile = new FileOutputStream(screenshotFile);
            // image is a BufferedImage.
            image = robot.createScreenCapture(capRect);
            JPEGImageEncoder snapEncoder = JPEGCodec.createJPEGEncoder(outFile);
            snapEncoder.setJPEGEncodeParam(param);
            snapEncoder.encode(image);
            outFile.close();
        } catch (IOException e) {
            System.out.println(e);
        } catch (OutOfMemoryError oe) {
            System.err.println(oe);
        }
        
    }
  
    /**	Starts a new JpegImagesToMovie, and waits for it to finish
     * making a mov file of the jpg images.
     *
     *  @param  images  A DataList object that is set-up to read images from
     *                  the temp-file containing a recently recorded
     *                  movie.
     */
    private void startDumper(DataList images) {
        // Create a new tempfile filename for each movie.
        try {
            File testFile = new File(tempFile);
            while (testFile.exists() && !testFile.delete()) {
                testFile = mySaveQuery.filterFile(mySaveQuery.getNextFile(testFile));
            }

            String tempTotal = testFile.getPath();
            String arguments[] = { "-w", Integer.toString(capRect.width),
                    "-h", Integer.toString(capRect.height),
                    "-f", Integer.toString(fps), "-o", tempTotal};

            // Create a new dumper.
            JpegImagesToMovieMod dumper = new JpegImagesToMovieMod(arguments);
            // Point dumper to datasource.
            dumper.setDataList(images);
            // Run dumper, and wait for it to finish with waitFor().
            dumper.setPriority(Thread.NORM_PRIORITY);
            dumper.finished = false;
            dumper.start();
            dumper.waitFor();
        } catch (Exception e) {
            // unRecoverableError can be used to check if
            // making the movie succeded. This is used in
            // stead of returning a value, in case one wants to
            // run the method in a separate thread.
            unRecoverableError = true;
            System.err.println(e);
        } catch (OutOfMemoryError o) {
            unRecoverableError = true;
            System.out.println(o);
        }
    }
    
    /** Used to sync video.
     * Users may call this method, and are then woken
     * once when the last frame is captured, and once more
     * when the recording is completely finished.
     *
     * The recording and running flags must be checked in order
     * to safely determine if recording is running upon return
     * from this method.
     *
     * The ScreenGrabber itself calls this method from run(), and
     * then waits here for capture to start.
     */
    public synchronized void hold() {
        try {
            wait(3000);
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
    
    /** Currently unused method to convert a BufferedImage to
     *  a byte array.
     *
     *  @param  inImage The BufferedImage.
     *  @return A byte array containging inImage.
     */
    private byte[] bufferedImageToByteArray(BufferedImage inImage) {
        int[] intArray = inImage.getRGB(0, 0,
                                        inImage.getWidth(),
                                        inImage.getHeight(),
                                        null,
                                        0, 
                                        inImage.getWidth());
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(temp);
        for (int cnt = 0; cnt < intArray.length; cnt++) {
            try {
                data.writeInt(intArray[cnt]);
            } catch (Exception e) {
                System.out.println("total error");
            }
        }
        return temp.toByteArray();
    }
    
    /** Main working method.
     *  It captures a frame, and sleeps for the amount of time
     *  left until the next frame should be captured.
     *	If capturing the frame took longer than fps would allow,
     *	the frame is marked as missed, and then copied the number
     *  of times required to get back in sync.
     *	The method ends with a call to startDumper(), where a temp
     *	mov file is made, then this method finally changes the
     *  running parameter, and makes one last call to wakeUp().
     *	The recording is started when the user sets the notFinished
     *	flag to true, and then calls ScreenGrabber.wakeUp(). It is
     *	stopped when the user sets the notFinished flag to false.
     */
    public void run() {
        /** The polygon to draw the mouse cursor into */
        Polygon mousePointer;
        /** The point to stor mouse position in. */
        Point mousePos;
        
        /** This loop will run until the VM exits, from somewhere else. */
        while (true) {
            try {
                /** Make sure the grabber is inited. */
                init();
                /** Wait for recording to start. */
                while (!notFinished) hold();
                /** Safety check */
                while (initing) hold();
                /** 2 Flags that are readable by the user. */
                recording = true;
                running = true;
                /** Change the acceleration of the capRect in case we are
                 *  tracking the mouse pointer.
                 */
                acceleration = 75 / fps;
                /** Main recording loop. notFinished is set to false
                 *  by user to stop recording. */
                while (notFinished) {
                    /** Get location of mouse. */
                    mousePos = MouseInfo.getPointerInfo().getLocation();
                    /** If we are tracking the mouse we should update
                     *  the position of the capRect.
                     */
                    if (followMouse) {
                        capRectMover(mousePos);
                    }
                    /** This is where we capture the image. */
                    image = robot.createScreenCapture(capRect);
                    /** Add mouse cursor to image.  */
                    if (getMouse) {
                        /** Get graphics to paint in. */
                        imageGraphics = image.createGraphics();
                        /** Get the cursor to draw. */
                        mousePointer = createMouse(mousePos);
                        /** Draw cursor. */
                        imageGraphics.setColor(Color.WHITE);
                        imageGraphics.fill(mousePointer);
                        imageGraphics.setColor(Color.DARK_GRAY);
                        imageGraphics.draw(mousePointer);
                    }
                    /** If the preview window is visible, we should
                     *  update the image showing there.
                     */
                    if (preview) {
                        mySnapShot.updatePreviewImage(image);
                    }
                    /** Encode a jpg directly to the OutputStream. */
                    encoder.encode(image);
//                    jpgBytes.write(this.bufferedImageToByteArray(image));
                    /** Save the size of the jpg in a separate array */
                    sizes[cntPics] = jpgBytes.size() - oldSize;
                    oldSize += sizes[cntPics];
                    /** The next part is used to stay in sync. */
                    syncTime += time;
                    currentTime = System.currentTimeMillis();
                    while (syncTime < currentTime) {
                        missedFrames[cntMissed++] = cntPics;
                        syncTime += time;
                    }
                    cntPics++;
                    /** The loop is finished. */
                    Thread.sleep((long) syncTime - currentTime);
                }
            } catch (OutOfMemoryError o) {
                error = true;
                Runtime myRuntime = Runtime.getRuntime();
                long mem = myRuntime.maxMemory() - myRuntime.totalMemory() +
                        myRuntime.freeMemory();
                System.out.println("Interrupted! Memory to low");
                System.out.println("Memory: " + mem);
                System.out.println(o);
            } catch (Exception e) {
                error = true;
                Runtime myRuntime = Runtime.getRuntime();
                long mem = myRuntime.maxMemory() - myRuntime.totalMemory() +
                        myRuntime.freeMemory();
                System.out.println("Interrupted! Possibly max number of frames" +
                        " exceeded, or Memory to low");
                System.out.println("Max number of pics: " + maxNOPics);
                System.out.println("Memory: " + mem);
                System.out.println(e);
            } finally {
                /** We're finished recording video. */
                try {
                    cntMovies++;
                    /** Make sure the audiotrack is at least 2s long. A
                     *  security measure to prevent crashes from Merge class. */
                    if (((cntPics + cntMissed) * time) < 2000) {
                        int delayCounter = 0;
                        while (((cntPics + cntMissed + delayCounter++) * time) < 2000) {
                            Thread.sleep((long) time);
                        }
                    }
                    /** Make sure the film is at least 2s long. A
                     *  security measure to prevent crashes from Merge class. */
                    while (((cntPics + cntMissed) * time) < 2000) {
                        missedFrames[cntMissed++] = cntPics;
                    }
                    /** Write a final frame, and close the file for temporary
                     *  image data. */
                    try {
                        jpgBytes.write(lastFrame, 0, lastFrame.length);
                    } catch (IOException e) {
                        System.err.println(e);
                    }
                    sizes[cntPics] = lastFrame.length;
                    /** At this point we are done recording.
                     *  We create a new DataList object for this movie.
                     *  The DataList object acts as an input source
                     *  for the JpegImagesToMovieMod class.
                     *  The DataList object, images, is global, so
                     *  that the encode() method has access to it as well.
                     */
                    images = new DataList();
                    images.totalPics = cntPics;
                    images.picSizes = sizes;
                    images.missedFrames = missedFrames;
                    images.setInFile(dumpFile);
                    recording = false;
                    /** wake up users waiting to sync audio. */
                    wakeUp();
                    /** Recording is now finished, encoding starts
                     *  when someone calls the encode() method.
                     *  In case there is a thread waiting to stop 
                     *  Audio recording, this thread will now hold,
                     *  briefly before doing anything else. This is
                     *  done only for maximum sync between audio and
                     *  video. After audio recording is stopped, this
                     *  thread will loop back to the top of this
                     *  method and call the init() method again.
                     */
                    while (audioRecording) hold();
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
        }
    }
    
    /** This is the method where the encoding of
     *  the mov-file takes place. In essence, this
     *  method picks up exactly where the run() method
     *  left off. The reason for keeping this code
     *  outside the run() method, is that the user may
     *  want to record at one thread priority (typically
     *  the highest), and encode at another.
     *
     *  Before calling the encode() method, the user should
     *  assign an EncoderProgressBar to this ScreenGrabber
     *  object by setting the myProgressBar parameter.
     */
    public void encode() {
        /** Make sure the progress bar has direct access
         *  to the DataList, so it can stop encoding if 
         *  the user requests it.
         */
        myProgressBar.myDataList = images;
        /** Start encoding. */
        startDumper(images);
        /** We return here when we are done encoding,
         *  or when the encoding has been interrupted.
         *  Now we can set the running flag, to tell
         *  the user waiting to merge audio and video
         *  that we are ready.
         */
        running = false;
        /** Try to delete the file that we used as a
         *  data source for the movie (the old dumpfile).
         */
        try {
            images.inStream.close();
            images.inFile.delete();
        } catch (Exception e) {
            System.out.println(e);
        }
        /** Free some memory from the arrays, and then init
         *  again to be able to use it */
        images.picSizes = null;
        images.missedFrames = null;
        try {
            initing = true;
            if (!recording) init();
        } catch (Exception e) {
            System.out.println(e);
        }
        /** Wake up users waiting to use this ScreenGrabber
         *  again, and users waiting to merge the encoded
         *  movie file.
         */
        initing = false;
        wakeUp();
    }
}