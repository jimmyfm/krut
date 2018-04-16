package krut.Migration;

/**
 * @author Luigi P
 */
public class JPEGEncodeParam {

	private float encQuality;
	private boolean b;

	public void setQuality(float encQuality, boolean b) {
		this.encQuality = encQuality;
		this.b = b;
	}

	public float getEncQuality() {
		return this.encQuality;
	}

	public boolean getB() {
		return this.b;
	}
}