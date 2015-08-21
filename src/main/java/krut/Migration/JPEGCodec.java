package krut.Migration;

import java.io.OutputStream;

/**
 * @author Luigi P
 */
public class JPEGCodec {

    public static JPEGImageEncoder createJPEGEncoder(OutputStream jpgBytes) {
        return new JPEGImageEncoder(jpgBytes);
    }
}
