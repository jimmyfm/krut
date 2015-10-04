package krut;

import krut.Run_KRUT;

import java.awt.*;
import java.util.prefs.Preferences;

/**
 * Global class to save preferences and share settings between classes cutting down cross references to variables
 *
 * @author Luigi P
 * @version 0.9.5
 * @since 2015-09-17
 */

public class Settings {

    static {
    }

    private static Preferences prefs = Preferences.userNodeForPackage(Run_KRUT.class);
    public static final String PREF_CAPRECT_X = "PREF_CAPRECT_X";
    public static final String PREF_CAPRECT_Y = "PREF_CAPRECT_Y";
    public static final String PREF_CAPRECT_WIDHT = "PREF_CAPRECT_WIDHT";
    public static final String PREF_CAPRECT_HEIGHT = "PREF_CAPRECT_HEIGHT";


    /**
     * The starting value of the capture size.
     * This is used in the createScreenGrabber() method.
     * It is also passed on to the KrutSettings constructur
     * from the init() method.
     */
    public static Rectangle getCaptureRect() {
        return new Rectangle(
                prefs.getInt(PREF_CAPRECT_X, 0),
                prefs.getInt(PREF_CAPRECT_Y, 0),
                prefs.getInt(PREF_CAPRECT_WIDHT, 360),
                prefs.getInt(PREF_CAPRECT_HEIGHT, 240)
        );
    }

    public static void setCaptureRect(Rectangle captureRect) {
        prefs.putInt(PREF_CAPRECT_X, (int) captureRect.getX());
        prefs.putInt(PREF_CAPRECT_Y, (int) captureRect.getY());
        prefs.putInt(PREF_CAPRECT_WIDHT, (int) captureRect.getWidth());
        prefs.putInt(PREF_CAPRECT_HEIGHT, (int) captureRect.getHeight());
    }


}
