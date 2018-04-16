package net.sourceforge.krut.jpeg;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Luigi P
 */
public class JPEGImageEncoder {

	private final OutputStream out;
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
			ImageIO.write(image, "jpeg", this.out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public net.sourceforge.krut.jpeg.JPEGEncodeParam getJPEGEncodeParam() {
		return JPEGEncodeParam;
	}
}