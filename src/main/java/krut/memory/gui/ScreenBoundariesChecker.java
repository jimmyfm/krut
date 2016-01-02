package krut.memory.gui;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * 
 * Checks that windows positions as defined in the CompInfo data are retrievable
 * 
 * Its set of limits are different from the classic margins, as windows are
 * asymmetric.
 * 
 * 
 * */
public class ScreenBoundariesChecker {

	private Rectangle[] allScreenBoundaries;

	public ScreenBoundariesChecker() {
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();

		ArrayList<Rectangle> allBoundaries = new ArrayList<Rectangle>(
				4 + gs.length);
		for (int j = 0; j < gs.length; j++) {
			GraphicsDevice gd = gs[j];
			GraphicsConfiguration[] gc = gd.getConfigurations();
			for (int i = 0; i < gc.length; i++) {
				// virtualBounds =
				// virtualBounds.union(gc[i].getBounds());

				allBoundaries.add(gc[i].getBounds());
			}
		}
		this.allScreenBoundaries = new Rectangle[allBoundaries.size()];
		allBoundaries.toArray(this.allScreenBoundaries);

	}

	/**
	 * 
	 * */
	public boolean checks(CompInfo fromProp) {
		boolean rv = false;

		for (int i = 0; !rv  && i < allScreenBoundaries.length; i++) {
			rv |= check(allScreenBoundaries[i], fromProp);
		}

		return rv;
	}

	/**
	 * the relative position of the upper left corner of the virtual 
	 * rectangle in which, on At Least One screen, the window must be included 
	 * 
	 * */
	private Point upperLeftCorner = new Point(30, -4);
	
	
	/**
	 * the relative position of the bottom right corner of the virtual 
	 * rectangle in which, on At Least One screen, the window must be included 
	 *
	 * */
	private Point bottomRightCorner = new Point(-10, -10);

	private boolean check(Rectangle screen, CompInfo fromProp) {

		return   fromProp.getTop() > screen.y + upperLeftCorner.y
				&& fromProp.getTop() < (screen.y + screen.height + bottomRightCorner.y)
				&& fromProp.getLeft() < (screen.x + screen.width + bottomRightCorner.x)
				&& (fromProp.getLeft() + fromProp.getWidth()) > screen.x
						+ upperLeftCorner.x;
	}

	/**
	 * Return the "relative" coordinate, taken from a screen upper left corner.
	 * It should represents the limit for a window stored position's right top
	 * corner, beyond which it will become probably difficult or impossible to
	 * retrieve the window by mouse
	 * 
	 * Coordinates directions are the standard for screens 
	 * 
	 * @param upperCorner
	 */

	public Point getUpperLeftCorner() {
		return upperLeftCorner;
	}

	/**
	 * This is a "relative" coordinate, taken from a screen upper left corner.
	 * It represents the limit for a window stored position's right top corner,
	 * beyond which it will become probably difficult or impossible to retrieve 
	 * the window by mouse 
	 * 
	 * Coordinates directions are the standard for screens 
	 * 
	 * @param upperCorner
	 */
	public void setUpperLeftCorner(Point upperCorner) {
		this.upperLeftCorner = upperCorner;
	}

	/**
	 * Return the "relative" coordinate, taken from any screen  bottom right corner.
	 * It should represents the limit for a window stored position's left top
	 * corner, beyond which it will become probably difficult or impossible to
	 * retrieve the window by mouse
	 * 
	 * Coordinates directions are the standard for screens 
	 * 
	 * @param upperCorner
	 */
	public Point getBottomRightCorner() {
		return bottomRightCorner;
	}

	/**
	 * This is a "relative" coordinate, taken from any screen bottom right corner.
	 * It represents the limit for a window stored position's left top corner,
	 * beyond which it will become probably difficult or impossible to retrieve
	 * the window by mouse
	 * 
	 * Coordinates directions are the standard for screens 
	 * 
	 * @param lowerCorner
	 */
	public void setBottomRightCorner(Point lowerCorner) {
		this.bottomRightCorner = lowerCorner;
	}

	/**
	 * Returns a set of boundaries describing the positions of screens in the desktop spaces
	 * 
	 * @return  Rectangle[] 
	 */
	public Rectangle[] getAllScreenBoundaries() {
		return allScreenBoundaries;
	}

}
