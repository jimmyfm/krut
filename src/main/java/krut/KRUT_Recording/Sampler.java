/*
 * Sampler.java
 *
 * Created on den 29 december 2004, 23:27
 */

package krut.KRUT_Recording;

/**
 *
 * @author  jonte
 */


import java.io.IOException;
import java.io.File;
import java.io.*;

import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.AudioFileFormat;

/** The audio sampler records sound from a DataLine obtained
 *  from the AudioSystem. The sound is recorded by obtaining a
 *  series of samples buffered in the DataLine and saving them
 *  through an OutputStream into a file. Before the samples are
 *  saved, a synchronization check (optional) is performed, and
 *  if there has been loss of sound, the existing series of
 *  samples are "stretched" through an algorithm explained
 *  in the source comments, until sync with the system-clock is
 *  re-established.
 */
public class Sampler extends Thread {
    /** The recording frequency. Changing this value here may have
     *  no effect, since it may be changed by the user before
     *  recording starts. Is changed from Run_KRUT.createSampler().
     */
    public float frequency = 22050f;
    /** The sample size in bits. Changing this value here may have
     *  no effect, since it may be changed by the user before
     *  recording starts. Is changed from Run_KRUT.createSampler().
     */
    public int sampleSize = 8;
    /** The number of recording channels. Changing this value here may have
     *  no effect, since it may be changed by the user before
     *  recording starts. Is changed from Run_KRUT.createSampler().
     */    
    public int channels = 1;
    /**	The file used for saving the audio data.
     *  If the audiofile is changed, init() needs to be called afterwards.
     */
    public File audioFile = null;
    /**	recording is used to flag if the Sampler is recording.*/
    public boolean recording = false;
    /**  stopped is a flag to tell the Sampler if it should record or not. */
    public boolean stopped = true;
    /**	syncAudio is a flag to tell the sampler if it should sync audio,
     *  whenever it misses frames, or just continue recording. */
    public boolean syncAudio = true;
    /**  This is the counter for how many times the
     *  sampler should wait before correcting a delayed sample.
     *  This value can be changed here, and another, private integer
     *  copies this value on each loop (the run loop).
     */
    public int countTimesLag = 0;
    /**  IMPORTANT: countTimesAhead should always be 0, or
     *  there can be a situation where the sampler can no
     *  longer "catch up" with the clock. It is kept for
     *  facilitating (?) future development.
     */
    public int countTimesAhead = 0;
    /** This is a measure of the speed the sound should be
     *  played back with. It is playbackFPS/recordingFPS.
     *  This parameter is changed by Run_KRUT.checkInited.
     */
    public float speed = 1;
    /** It is necessary for the Sampler to have access to a SaveFileChooser,
     *  because the Sampler needs to use the getNextFile() method both in
     *  init() and in finished(). This parameter will be updated by Run_KRUT
     *  as soon as the global SaveFileChooser is initiated.
     */
    public krut.KRUT_GUI.SaveFileChooser mySaveQuery =
            new krut.KRUT_GUI.SaveFileChooser();
    
    // --------------------------------------------------------------------
    //  Remark.
    //  For smooth sampling:
    //  1 < speed --> (speed - 1) * sleepTime > 1000 / maxAhead
    //  speed < 1 --> (1 - speed) * sleepTime > 1000 / maxLag
    //  This means that a maxLag 50, sleeptime 50, would require a speed
    //  lower than 0.6 or higher than 1.4 to work smoothly.
    //
    //  These parameters are changed in the Run_KRUT.checkInited() method.
    //
    //  ---
    //
    /**	The time the sampler sleeps between each time emptying the buffer,
     *	in milliseconds. */
    public long sleepTime = 900;
    /**  The maximum lag times in parts of a second, like this:
     *  maxLag = 50 gives the max lag 1/50 s = 20ms. */
    public int maxLag = 50;
    /**  The maximum ahead times in parts of a second, like this:
     *  maxAhead = 50 gives the max ahead 1/50 s = 20ms. */
    public int maxAhead = 50;
    // --------------------------------------------------------------------
    /**	The size of the memory buffer for the save file, in bytes. */
    private int memoryBufferSize = 1024*1024;
    /**	The default filename used for saving wav data.
     *	Wav- save file name is changed in setAudioFile() */
    private String defaultFileName = new String("sampleaudio.wav");
    /**	The default filename used for saving raw audio data.
     *  This file name can be changed here. */
    private String bufferFileName = new String("samplebuffer.buf");
    /**	The output stream where the audio data go. It is set up in init() */
    private OutputStream audioOutStream;
    /**	The line for raw audio data, set up in init, and used everywhere. */
    private TargetDataLine m_line;
    /** The final audio save format (will be wav) for raw audio data,
     *  set up in init, and used everywhere. */
    private AudioFileFormat.Type	m_targetType;
    /**	The input audio format for raw audio data,
     *  set up in init, and used everywhere. */
    private AudioFormat audioFormat;
    /** The save file for raw audio data,
     *  set up in init, and used everywhere. */
    private File m_outputFile = null;
    //	run() method variables (some are set up in init())
    //	syncTime is set in setSyncTime(). This MUST be done before sampling.
    private int numBytesRead, sampleSizeInBytes, acceptedLag, totalBytes, addedBytes;
    private int bytesToCut, acceptedAhead;
    private int bytesToRead, missingFrames, missingBytes, bufferSize;
    private long currentTime, syncTime = 0, currentFrame, totalFrames, bufferSizeInFrames;
    private long checkTime, checkT2;
    //	The two byte arrays used to store sample data. emptyBytes is filled with
    //	0s, and used to fill up the audio stream back to sync, whenever
    //	samples are lost. They are set up in init() and used in run().
    private byte[] data, emptyBytes;
    //  This is a set of parameters for setting the sound back in sync if there
    //  were missed frames. (or just missing frames compared to the system clock).
    private int readFrames, framesMultiplicity, bonusMultiplicity;
    private int frameBytePos, bonusFrames;
    //  This is the counter for how many times the
    //  sampler should wait before correcting a delayed sample.
    //  This value can not be changed here, countTimes above should be
    //  used for changing this. That value will be copied into countdown on
    //  each iteration of the run loop.
    private int countdown = 1, countup = 4, testCnt = 0;
    //  Parameters introduced for hiTechFill, they are, as
    //  all others, global just to add one nanosecond to the
    //  execution time. They are all explained in hiTechFill.
    private int framePos, intervalCnt, loopInterval, multiplicityCnt;
    private int restFrames, added, bonusCnt;
    
    
    
    /** Creates a new instance of Sampler
     *  Calling this constructor will create a Sampler that uses
     *  an output File with the specified filename.
     *
     *  The file name can be changed by a call to the setAudioFile
     *  method.
     *
     *  @param  fileName    The file name of the audio file that
     *                      is created by this Sampler.
     */
    public Sampler(String fileName) {
        if (!setAudioFile(fileName))
            System.out.println("Error! " +
                    "Failed to open output file " +
                    fileName + ".");
    }
    
    /** Creates a new instance of Sampler
     *  Calling this constructor will create a Sampler that uses
     *  an output File with the default filename given by the 
     *  private parameter defaultFileName.
     *
     *  The file name can be changed by a call to the setAudioFile
     *  method.
     */
    public Sampler() {
        if (!setAudioFile(defaultFileName))
            System.out.println("Error! " +
                    "Failed to open output file " +
                    defaultFileName + ".");
    }
    
    
    /** This method sets up all the input streams and output streams
     *  that are used in the recording. This method should be called every
     *  time a parameter affecting those streams has been changed
     *  (eg. sampling frequency). This method does not have to be called
     *  for just a change in save file name, since a temporary buffer file
     *  is used for output during recording.
     *  The method is called by the run method
     *  once every time at the start of recording.
     */
    public void init() {
        /*  First of all, make sure the line isn't already running. */
        if (m_line != null)
            stopRecording();
        /*  Setting the AudioFormat */
        sampleSizeInBytes = (int) sampleSize * channels / 8;
        audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                frequency, sampleSize, channels,
                sampleSizeInBytes, frequency, false);
        
        /** Trying to get a TargetDataLine. The TargetDataLine
         *  is used later to read audio data from.
         *  If requesting the line was successful, it is opened.
         */
        
        /** Try to get a buffer of 1s. */
        DataLine.Info info = new DataLine.Info(TargetDataLine.class,
                audioFormat, (int) (frequency * sampleSizeInBytes));
        TargetDataLine	targetDataLine = null;
        try {
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            /** Use the maximum buffersize available. */
            targetDataLine.open(audioFormat, info.getMaxBufferSize());
        } catch (LineUnavailableException e) {
            System.out.println("unable to get a recording line");
            e.printStackTrace();
        }
        /** Set the audio file type. */
        AudioFileFormat.Type	targetType = AudioFileFormat.Type.WAVE;
        
        m_line = targetDataLine;
        m_targetType = targetType;
        
        bufferSize = m_line.getBufferSize();
        bufferSizeInFrames = bufferSize / sampleSizeInBytes;
        
        /** This is how many samples of lag will be tolerated compared
         *  to the system clock, before the Sampler compensates
         *  by "stretching" the sample currently in memory
         *  to get back into sync. Stretching done in 
         *  hiTechFill method.
         */
        acceptedLag = (int) frequency / maxLag;
        /** This is how many samples of "speeding" will be tolerated compared
         *  to the system clock, before the Sampler compensates
         *  by dropping part of the sample currently in memory
         *  to get back into sync. Dropping is done in 
         *  hiTechFill method.
         */
        acceptedAhead = (int) frequency / maxAhead;
        /** Data is where the sampled data will end up being read
         *  into from the sample buffer. Reading is done in the
         *  run method.
         */
        data = new byte[bufferSize];
        /** Opening an outputfile, with buffer size
         *  memoryBufferSize, to write audio data into.
         *  This is not the final wav-file, just a temporary
         *  storage.
         */
        try {
            m_outputFile = new File(bufferFileName);
            while (m_outputFile.exists() && !m_outputFile.delete())
                m_outputFile = mySaveQuery.getNextFile(m_outputFile);
            FileOutputStream outFileStream = new FileOutputStream(m_outputFile);
            audioOutStream = new BufferedOutputStream(outFileStream, memoryBufferSize);
        } catch (FileNotFoundException fe) {
            System.err.println(fe);
        } catch (OutOfMemoryError oe) {
            System.err.println(oe);
        }
    }
    
    
    /** Stops the recording.
     * To make sure the recording has stopped, the recording flag can be
     * checked after calling this method.
     * eg. while(thisSampler.recording) thisSampler.hold();
     *
     * It's not a good idea to call this method just 'stop()'
     * because stop() is a (deprecated) method of the class 'Thread'.
     * And we don't want to override this method.
     *
     * Before this method is called, stopped should be set true by
     * the user.
     * This method must be called to stop the recording!
     * To start recording, stopped must be set to false again,
     * and wakeUp() must be called.
     */
    public void stopRecording() {
        try {
            m_line.stop();
            m_line.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    
    /**	Set the syncTime for the run() method
     * This MUST be done before wakeUp() is called
     * to start the recording. If not, the audio will
     * be extremely out of sync.
     *
     *  @param  syncTime    The time to wait (in millis) between each
     *                      audio sync check.
     */
    public void setSyncTime(long syncTime) {
        this.syncTime = syncTime;
    }
    
    /** Used to sync audio.
     * Other threads may call this method, and are then woken
     * when all audio recording is finished. The recording flag
     * must be checked in order to safely determine if recording
     * is running upon return from this method.
     */
    public synchronized void hold() {
        try {
            wait(3000);
        } catch (InterruptedException ie) {
            System.err.println(ie);
        }
    }
    
    
    /** Wakes up threads waiting for the sampler.
     * This is used to start the actual recording, if stopped is set to false
     * before calling this method. Also syncTime MUST be set, by calling
     * setSyncTime, before wakeUp() is called.
     * This method is called once when all audio data are written to the
     * OutputStream.
     */
    public synchronized void wakeUp() {
        notifyAll();
    }
    
    /** This is just a helpfunction for the run loop, so
     *  it gets at least a little more readable.
     *  This method fills up a gap in the line instantly,
     *  by using just whatever samples are in memory right
     *  now and stretching them to fit.
     *
     *  Concept:    We have x frames, we wish to record x + y
     *              to fill a gap (y = missingFrames).
     *              The max multipicity of all frames is given
     *              by the integer division 1 + y/x.
     *              This leaves y%x bonusFrames that also
     *              need to be added through the interval.
     *              The integer division x/bonusFrames will give
     *              the highest constant integer distance you
     *              can put between each frame to get them all
     *              in. If we put framesMultiplicity plus one
     *              extra frame in every x/bonusFrames frames,
     *              we will get all the bonusFrames in, and then
     *              we fill up just using framesMulitplicity
     *              until we are out of frames
     */
    private void hiTechFill() throws Exception {
        // The parameters:
        // framePos, intervalCnt, loopInterval, multiplicityCnt
        // restFrames
        // are not used anywhere else, and as such should be
        // pretty self explanatory in this method.
        //
        // The number of frames we've read.
        readFrames = numBytesRead / sampleSizeInBytes;
        // The number of times every frame should be repeated.
        framesMultiplicity = missingFrames / readFrames + 1;
        // System.out.println();
        // System.out.print("adding. read: " + readFrames + " mult: " + framesMultiplicity);
        // The amount of frames that have to be repeated one extra time.
        bonusFrames = missingFrames % readFrames;
        frameBytePos = 0;
        added = 0;
        loopInterval = readFrames / bonusFrames;
        // System.out.print(" bonus: " + bonusFrames + " interval: " + loopInterval);
        // First a loop to get all the bonusFrames out of the system.
        // bonusFrames is used below, and cant be changed in the loop.
        bonusCnt = bonusFrames;
        while (0 < bonusCnt--) {
            // First we write the bonusFrame
            audioOutStream.write(data, frameBytePos, sampleSizeInBytes);
            added++;
            // Then we write the rest of the frames.
            intervalCnt = loopInterval;
            while (0 < intervalCnt--) {
                multiplicityCnt = framesMultiplicity;
                while (0 < multiplicityCnt--) {
                    audioOutStream.write(data, frameBytePos, sampleSizeInBytes);
                    added++;
                }
                frameBytePos += sampleSizeInBytes;
            }
        }
        // The position of the frame we are about to write to.
        framePos = bonusFrames * loopInterval;
        // This is the right amount, do not add one extra.
        // The subtraction is after the control, so this will loop
        // the desired amount of times.
        restFrames = readFrames - framePos;
        // System.out.print(" rest: " + restFrames);
        while (0 < restFrames--) {
            multiplicityCnt = framesMultiplicity;
            while (0 < multiplicityCnt--) {
                audioOutStream.write(data, frameBytePos, sampleSizeInBytes);
                added++;
            }
            frameBytePos += sampleSizeInBytes;
        }
        // System.out.print(" treue added: " + added);
    }
    
    
    
    /** Main working method.
     *  Checks how much data is available in the line
     *  and then calls getSamples() to get them.
     *  It then sleeps for sleepTime milliseconds, and
     *  repeats until the line is closed.
     */
    public void run() {
        //	This loop will run until the VM exits, from somewhere else.
        while (true) {
            //	init() needs to be called at least once for
            //	each recording. If critical parameters,
            //	such as audio quality, are changed while run()
            //	holds, init() needs to be called again by the user
            //	before setting stopped to false and wakeUp() is called.
            init();
            addedBytes = 0;
            totalBytes = 0;
            //	Wait for recording to start.
            //	When the user sets stopped to false, and calls
            //	wakeUp(), recording will begin. At this point,
            //	the user must have set a correct syncTime using
            //	setSyncTime(), or audio will behave strangely.
            //	Recording will finish when stopped is set to
            //	true, and stopRecording is called by the user.
            //	run() will then end up here again.
            while (stopped) hold();
            try {
                m_line.start();
                // read one sample to make sure the line is running.
                // then wake up any other threads waiting
                // on audio capture.
                //	data is inited in init().
                numBytesRead = m_line.read(data, 0, sampleSizeInBytes);
                recording = true;
                //	Write the first sample read to output.
                audioOutStream.write(data, 0, numBytesRead);
                totalBytes += numBytesRead;
                while (m_line.isRunning()) {
                    //	Check if there is data to be read.
                    //	If not, just sleep and repeat.
                    /** A comment on the available method.
                     *  It seems that whenever there is package loss from the
                     *  line, it is caused by the fact that m_line.read blocks, in spite
                     *  of that I never ask for more than available gives. When I try to get
                     *  ca 450ms of data (only tested on that number) the m_line.read method
                     *  seems, on occation, to take 750ms to return. 3 things are being done
                     *  to try and compensate for this.
                     *
                     *  1. Lower the frequency of data retrieval to 250ms.
                     *  2. Never ask for the full amount of data, leave 100 frames in the buffer.
                     *  3. Shorten the sleeptime if there has been a lag (removed).
                     *
                     *  These compensations have not yet been tested (since I haven't so far
                     *  been able to produce such a lag while using them, which might mean that
                     *  2 works.)
                     *
                     *  Addition: I now change these parameters in Run_KRUT.checkInited().
                     */
                    currentTime = System.currentTimeMillis();
                    bytesToRead = m_line.available() - 100*sampleSizeInBytes;
//                    System.out.print("frames To read: " + (bytesToRead / sampleSizeInBytes));
                    if (0 < bytesToRead - sampleSizeInBytes) {
                        //	Only try to read the exact amount of data
                        //	available to prevent blocking.
//                        checkTime = System.currentTimeMillis();
//                        System.out.print(" lag1: " + (checkTime - currentTime));
                        numBytesRead = m_line.read(data, 0, bytesToRead);
                        //	Check time again, for sync purposes.
                        currentTime = System.currentTimeMillis();
                        //	Write the number of bytes that were actually read.
//                        System.out.print(" lag1: " + (currentTime - checkTime));
                        totalBytes += numBytesRead;
                        //	Calculate the frame position both from number
                        //	of read frames, and from System clock.
                        //	The difference is used later to get back in sync.
                        currentFrame = (long) (frequency / speed) *
                                (currentTime - syncTime) / 1000L;
                        /** Note that the only reason m_line.getLongFramePosition is used
                         *  here, is because it gives a fast estimate of the number of
                         *  missing frames (it seems to give the total number of frames
                         *  read from the line plus the number left in the line). Since
                         *  frames are unintentionally left in the line, it can take a
                         *  while for the total number of frames read to reach to where
                         *  we have a correct value for missing frames (one more
                         *  iteration in any normal case). But that is actually the only
                         *  safe way to do it, and it is supposed to be that way, it just
                         *  hasn't been changed yet. This way of doing it depends on
                         *  getLongFramePosition to keep behaving like this.
                         */
                        missingFrames = (int) (currentFrame -
                                m_line.getLongFramePosition() -
                                addedBytes / sampleSizeInBytes);
//                        System.out.print(" Read: " + (numBytesRead / sampleSizeInBytes) +
//                                            " missingframes: " + missingFrames + " clock frame(current): " + currentFrame
//                                            + " calculated frame: " + (totalBytes / sampleSizeInBytes));
                        // If were out of sync, we need to get back.
                        // But we don't have to try and sync the
                        // last frame (it's often late and sounds bad if we do)
                        if (!stopped && syncAudio &&
                                (0 < missingFrames - acceptedLag)) {
                            countup = countTimesAhead;
                            // If we have looped countTimes times, and
                            // were still late, we should correct.
                            if (0 < 1 - countdown--) {
                                missingBytes = missingFrames * sampleSizeInBytes;
                                // This method just fills the gap in the audiostream.
                                checkT2 = System.currentTimeMillis();
                                hiTechFill();
//                               System.out.print(" Filltime: " + (System.currentTimeMillis() - checkT2));
                                totalBytes += missingBytes;
                                addedBytes += missingBytes;
//                                System.out.print(" Added: " + missingFrames);
                                countdown = countTimesLag;
                            } else {
                                audioOutStream.write(data, 0, numBytesRead);
                            }
                            // Either way, we want to write what we got in the end.
                            // The total amount of written bytes is already updated above.
                        } else if (syncAudio &&
                                (missingFrames + acceptedAhead < 0)) {
                            countdown = countTimesLag;
                            if (0 < 1 - countup--) {
                                // missingFrames = 5*missingFrames;
                                readFrames = numBytesRead / sampleSizeInBytes;
                                // missingFrames is negative here!
                                bytesToCut = - missingFrames * sampleSizeInBytes;
                                if (0 <= readFrames + missingFrames) {
                                    audioOutStream.write(data, bytesToCut, numBytesRead - bytesToCut);
                                    totalBytes -= bytesToCut;
                                    addedBytes -= bytesToCut;
//                                    System.out.print(" Threw away: " + missingFrames);
                                    countdown = countTimesLag;
                                } else {
                                    totalBytes -= numBytesRead;
                                    addedBytes -= numBytesRead;
//                                    System.out.print(" Threw away all: " + readFrames);
                                }
                                countup = countTimesAhead;
                            } else {
                                audioOutStream.write(data, 0, numBytesRead);
                            }
                        } else {
                            audioOutStream.write(data, 0, numBytesRead);
                            countdown = countTimesLag;
                            countup = countTimesAhead;
                        }
                    }
                    // sleep.
//                    if (currentTime - checkTime < sleepTime)
//                        Thread.sleep(sleepTime - (currentTime - checkTime));
                    Thread.sleep(sleepTime);
                }
            } catch (OutOfMemoryError o) {
                System.out.println(o);
            } catch (ArrayIndexOutOfBoundsException oe) {
                System.out.println(oe);
                System.out.println("numBytesRead: " + numBytesRead);
                System.out.println("bytesToRead: " + bytesToRead);
                System.out.println("missingFrames: " + missingFrames);
                
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                // were finished recording audio.
//                System.out.println("Missed audio frames: " +
//                        missingFrames);
//                System.out.println("Added audio frames: " +
//                        (addedBytes / sampleSizeInBytes));
                // Make a wav-file from saved audio data.
                finish();
            }
        }
    }
    
    
    /** Change the name of the final audio output file to.
     *  Since the output file will always be a
     *  .wav file, the file name should end with .wav, but
     *  this is not controlled. It usually shouldn't matter
     *  for any practical purposes. This method can actually be called
     *  while recording is in progress (recording writes to a temporary
     *  file, named by the parameter bufferFileName), but calling it while
     *  writing to the actual output file will cause it to block
     *  until the writing is finished (writing to output file is done
     *  by the method finish).
     *
     *  @param fileName The name of the file that should be
     *                  created by this Sampler.
     *
     *  @return     true if the file was successfully created,
     *              false if not.
     */
    public synchronized boolean setAudioFile(String fileName) {
        boolean set = false;
        try {
            audioFile = new File(fileName);
            set = true;
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            return set;
        }
    }
    
    
    /**	Finish up, writing the final audio file from the
     * temporary file already created.
     */
    public synchronized void finish() {
        try {
            audioOutStream.close();
            /** Read Back from the sample file we just closed. */
            FileInputStream audioInAgain =
                    new FileInputStream(m_outputFile);
            long sampleBytes = m_outputFile.length();
            long sizeOfFrame = (long) sampleSize * channels / 8;
            /** Make the InputStream buffered. */
            BufferedInputStream buffAudioIn =
                    new BufferedInputStream(audioInAgain, memoryBufferSize);
            /**	Make an AudioInputStream from our sample file. */
            AudioInputStream a_input =
                    new AudioInputStream(buffAudioIn,
                    audioFormat,
                    sampleBytes / sizeOfFrame);
            /** One last check to see that we can really write
             *  to the audioFile right now. We can not delete it
             *  after the first round (I assume this is due to some
             *  lock held by Merge), and it has not been properly
             *  tested if another session of KRUT running at the
             *  same time would or would not allow you to write
             *  to a file, but this is just in case such a case
             *  would not allow you to write to a file.
             */ 
            while (audioFile.exists() && !audioFile.canWrite()) {
                System.out.println("audio exists cannot be overwritten");
                audioFile = mySaveQuery.filterFile(mySaveQuery.getNextFile(audioFile));
            }
            /** Write the entire AudioInputstream to a wav file.
             *  (if targetType is set to wav) */
            AudioSystem.write(a_input, m_targetType, audioFile);
            buffAudioIn.close();
            /**	remove the old buffer file. */
            m_outputFile.delete();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            recording = false;
            wakeUp();
        }
    }
    
}