/*
 * DataList.java
 *
 * Created on den 29 december 2004, 23:33
 */

package krut.KRUT_Recording;

/**
 *
 * @author  jonte
 */

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

/** DataList is a class used to reload the images
 *  from the file they are saved in by the ScreenGrabber,
 *  and then supply them to the JpegImagesToMovieMod class.
 *  In other words, this is a class for communication between
 *  the ScreenGrabber class and the JpegImagesToMovieMod class.
 *  It opens the file containing the images from the grabber, and
 *  then supplies the JpegImagesToMovieMod with the images one by
 *  one, through the readNode method.
 *
 *  This class does not take any arguments to it's constructor,
 *  but nevertheless, the following things need to be done before
 *  the DataList class can be used:
 *
 *  The input file containing the data of the images making up
 *  every frame must be set using the setInFile(File) method.
 *
 *  A user must also provide an integer array containting the size
 *  in bytes for every image in the File inFile.
 *
 *  Additionally, an integer array containing the numbers of images
 *  marked as "missed" must be provided.
 *
 *  Finally, a user must provide an integer value of the total
 *  number of frames.
 *  
 *  See the parameter comments for more details.
 */

public class DataList {
    
    /** This flag is set to true by the program when all the
     *  frames have been delivered. It should be checked by the user
     *  of this class. This is the only way of telling if all
     *  the frames are processed or not.
     *
     *  When a user presses "cancel" in an EndodingProgressBar,
     *  that class will set this flag to true (in it's cancelAction()
     *  method), and fake that the frames have run out.
     */
    public boolean finished = false;
    
    /** This is an integer containing the total number of frames
     *  in the picSizes array. That means that the totalPics plus
     *  the total number of missed frames will equal the total
     *  number of frames in the final film. totalPics is the
     *  total number of unique frames.
     *
     *  The reason why a parameter of this kind is needed is
     *  because the picSizes array will typically be much
     *  larger than the number of actual frames. Look at the
     *  comments in the ScreenGrabber.init() method for more info.
     *
     *  (There is some redundancy, as the length of the picSizes
     *  array must be the same as totalPics) 
     */
    public int totalPics = 0;
    /** A simple counter of how many unique frames we have sent.
     *  Unique as in not doubles sent to compensate for a missing
     *  frame.
     */
    public int donePics = 0;
    /** How many frames have been sent in total, including
     *  doublets.
     */
    public int sentPics = 0;
    
    /** The stream wrapping inFile. */
    public FileInputStream inStream;
    /** The input file for the frames. */
    public File inFile;
    /** The parameter picSizes should, before encoding is started,
     *  contain an array of the size in bytes of every single
     *  image in the image file inFile. This parameter
     *  must be directly set by the user before encoding begins.
     *  It should contain an amount of integers equal to the
     *  amount of images in the file inFile. So if a film of
     *  200 unique frames is to be encoded, this parameter
     *  should be an array of more than 200 integers.
     */
    public int[] picSizes;
    /** The parameter missedFrames should, before encoding is started,
     *  contain an array of the frame number of every frame that is
     *  missing from the image file inFile. This parameter
     *  must be directly set by the user before encoding begins.
     *  If there are no missing frames in the image file inFile,
     *  this parameter must still contain at least one integer.
     * 
     *  Examples:
     *
     *  If there are no missing frames, the array missedFrames
     *  can contain only zero's, EXCEPT for in the first position.
     *  The first position should in this case contain something
     *  that could not possibly be a valid frame number in the film,
     *  such as picSizes.length + 1;
     *
     *  If the frames 10 and 12 are missing, the array should start with
     *  {10, 12}, and after that continue with any number lower than the
     *  current frame number. The easiest way to avoid stupid mistakes
     *  is to have the rest of the array stay the way it is: filled with
     *  zero's.
     */
    public int[] missedFrames;
    /** An internal counter of how many missed frames we have
     *  sent doubles for so far.
     */
    private int cntMissed;
    /** The carrier of the data. Data is read into nodeData from
     *  the data file, and is returned from nodeData from the readNode()
     *  method.
     */
    private byte[] nodeData;
    
    DataList() {
        cntMissed = 0;
    }
    
    /** Sets the file containing the frames.
     *
     *  @param  input   A file containg the frames for the movie.
     *
     *  @return     true if a FileInputStream wrapping input could
     *              be opened, false if not.
     */
    public boolean setInFile(File input) {
        try {
            inFile = input;
            inStream = new FileInputStream(inFile);
            return true;
        } catch (FileNotFoundException e) {
            System.err.println(e);
            return false;
        }
    }
    
    /** Read the next frame
     *
     *  @return     a byte array containing the frame data.
     */
    public synchronized byte[] readNode() {
        try {
            if (donePics < totalPics) {
                /** Check that the upcoming frame is not a missed one. */
                if (missedFrames[cntMissed] != donePics) {
                    /** Make a byte array of the exact size of the next frame. */
                    nodeData = new byte[picSizes[donePics]];
                    inStream.read(nodeData, 0, picSizes[donePics]);
                    donePics++;
                } else {
                    /** If this frame is a missed one we just keep all the data
                     *  in memory, and send the last frame once more (there
                     *  is always an empty frame at the start of the film, not
                     *  to worry).
                     */
                    cntMissed++;
                }
                /** When we are done we just set finished = true,
                 *  and expect users to behave and never call again.
                 *  The cntMissed check is nothing but a bugcheck,
                 *  it will never happend.
                 */
                if ((donePics == totalPics) ||
                        (cntMissed == missedFrames.length))
                    finished = true;
                sentPics++;
                /** Return the frame. */
                return nodeData;
            } else {
                System.err.println("Error, too many frames");
                return null;
            }
        } catch (IOException e) {
            System.err.println("File error " + e);
            return null;
        }
    }
}
