package krut.Migration;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

/**
 * @author Luigi P
 */
public class JPEGImageEncoder {

    private OutputStream out;

    private JPEGEncodeParam JPEGEncodeParam;

    public JPEGImageEncoder(OutputStream out) {
        this.out = out;
    }

    public JPEGEncodeParam getDefaultJPEGEncodeParam(BufferedImage image) {
        return new JPEGEncodeParam();
    }

    public void setJPEGEncodeParam(JPEGEncodeParam JPEGEncodeParam) {
        this.JPEGEncodeParam = JPEGEncodeParam;
    }

    public void encode(BufferedImage image) {
        try {
        	if(this.out!= null)
            ImageIO.write(image, "jpeg", this.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public krut.Migration.JPEGEncodeParam getJPEGEncodeParam() {
        return JPEGEncodeParam;
    }
}
