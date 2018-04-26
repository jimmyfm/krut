package net.sourceforge.krut.recording;

import java.awt.Dimension;
import java.io.IOException;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullBufferStream;

/**
 * The source stream to go along with ImageDataSource.
 */
public class ImageSourceStream implements PullBufferStream {

    private DataList JPGImages;
    private VideoFormat format;
    private float frameRate;
    private long seqNo = 0;

    private boolean ended = false;

    public ImageSourceStream(int width, int height, int frameRate, DataList data) {
        this.frameRate = (float) frameRate;
        this.JPGImages = data;
            
          /* The commented out code below is remains from a
           * failed attempt to include avi output. The code is
           * left in the source like this as a reminder to the
           * author
           */
//            format = new VideoFormat(VideoFormat.JPEG,
//                    new Dimension(width, height),
//                    Format.NOT_SPECIFIED,
//                    Format.byteArray,
//                    (float)frameRate);

        format = new VideoFormat(VideoFormat.JPEG,
                new Dimension(width, height),
                Format.NOT_SPECIFIED,
                Format.byteArray,
                (float) frameRate);

            
          /* The commented out code below is remains from a
           * failed attempt to include avi output. The code is
           * left in the source like this as a reminder to the
           * author
           */
//            final int rMask = 0x00ff0000;
//            final int gMask = 0x0000FF00;
//            final int bMask = 0x000000ff;

//            format =
//                new javax.media.format.RGBFormat(
//                    new Dimension(width, height),
//                    Format.NOT_SPECIFIED,
//                    Format.intArray,
//                    frameRate,
//                    24,
//                    rMask,
//                    gMask,
//                    bMask);
    }

    /**
     * We should never need to block assuming data are read from files.
     */
    public boolean willReadBlock() {
        return false;
    }

    /**
     * This is called from the Processor to read a frame worth
     * of video data.
     */
    public void read(Buffer buf) throws IOException {

        // Check if we've finished all the frames.
        if (JPGImages.finished) {
            // We are done.  Set EndOfMedia.
            System.err.println("Done reading all images.");
            System.err.println("Frames: " + JPGImages.totalPics);
            System.err.println("Missed frames: " +
                    (JPGImages.sentPics - JPGImages.totalPics));
            buf.setEOM(true);
            buf.setOffset(0);
            buf.setLength(0);
            ended = true;
            return;
        }

        float time1 = seqNo * (1000 / frameRate) * 1000000;
        long time = (long) time1;
        buf.setTimeStamp(time);

        buf.setSequenceNumber(seqNo++);

        byte[] picBytes = JPGImages.readNode();            // read the next image in line
        // in the DataList.
        byte data[] = null;

//            int data[] = new int[picBytes.length / 4];

        // Read the entire JPEG image from the file.
        data = picBytes;

          /* The commented out code below is remains from a
           * failed attempt to include avi output. The code is
           * left in the source like this as a reminder to the
           * author
           */
//            int dataCnt = 0;
//            int mult;
//            for (int cnt = 0; cnt < data.length; cnt ++) {
//                mult = 256*256*256;
//                for (int loopCnt = 0; loopCnt < 4; loopCnt++) {
//                    data[picCnt] += picBytes[dataCnt++] * mult;
//                    mult /= 256;
//                }
//            }
        buf.setData(data);


        buf.setOffset(0);
        buf.setLength((int) picBytes.length);
        buf.setFormat(format);
        buf.setFlags(buf.getFlags() | buf.FLAG_KEY_FRAME);

    }

    /**
     * Return the format of each video frame.  That will be JPEG.
     */
    public Format getFormat() {
        return format;
    }

    public ContentDescriptor getContentDescriptor() {
        return new ContentDescriptor(ContentDescriptor.RAW);
    }

    public long getContentLength() {
        return 0;
    }

    public boolean endOfStream() {
        return ended;
    }

    public Object[] getControls() {
        return new Object[0];
    }

    public Object getControl(String type) {
        return null;
    }
}