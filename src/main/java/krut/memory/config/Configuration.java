package krut.memory.config;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;

import krut.KRUT_GUI.CapSizeQuery;
import krut.memory.AbstractMemory;

public class Configuration extends AbstractMemory {

	public static final String MEMORY_FILENAME = "krut-config.properties";

	private static Configuration root = null;

	public static Configuration instance() {
		try {
			if (root == null) {
				return instance(getMemoryFile(MEMORY_FILENAME));

			}
		} catch (IOException e) {
//			throw new RuntimeException("Cannot ccrrate config save file!!!!!"); //$NON-NLS-1$
			e.printStackTrace();
		}
		return root;
	}

	public static Configuration instance(File file)
			throws FileNotFoundException, IOException {
		if (root == null) {
			root = new Configuration(file);
		}
		return root;
	}

	public static void reset() {
		getMemoryFile(MEMORY_FILENAME).delete();
	}

	private boolean audioEnabled = false;

	private String audioPath = "";
	private boolean audioSynched = true;
	public int captureHeight = NULL_INT;
	public int captureWidth = NULL_INT;
	public int captureX = NULL_INT;
	public int captureY = NULL_INT;
	private int encQuality;
	private int FPS = NULL_INT;
	private String imagePath = "";
	private boolean mouseSFollowed = false;
	private boolean mouseShowing = false;
	private boolean overWriteAllowed;
	private int plb = NULL_INT;

	private boolean previewWindowInUse = false;
	private int soundFrequency;
	private boolean soundSixteenBit;
	private boolean soundStereo;

	private boolean timerActivated;

	private int timerRecHour;

	private int timerRecMinute;

	private int timerRecSecond;

	private boolean timerStopEnabled;

	private int timerStopHour;

	private int timerStopMinute;

	private int timerStopSecond;

	private boolean timerUsesRelativeTime = true;

	private boolean videoEnabled = false;

	private String videoPath = "";

	private int hopMinutes=30;

	private Configuration(File file) throws InvalidPropertiesFormatException,
			FileNotFoundException, IOException {
		super(file);
		init();
	}

	public boolean doesTimerUseRelativeTime() {
		return timerUsesRelativeTime;
	}

	public String getAudioPath() {
		return audioPath;
	}

	public int getCaptureHeight() {
		return captureHeight;
	}

	public int getCaptureWidth() {
		return captureWidth;
	}

	public int getCaptureX() {
		return captureX;
	}

	public int getCaptureY() {
		return captureY;
	}

	public int getEncQuality() {
		return this.encQuality;
	}

	public int getFPS() {
		return FPS;
	}

	public String getImagePath() {
		return imagePath;
	}

	public int getPlb() {
		return plb;
	}

	public int getSoundFrequency() {
		return soundFrequency;
	}

	public int getTimerRecHour() {
		return timerRecHour;
	}

	public int getTimerRecMinute() {
		return timerRecMinute;
	}

	public int getTimerRecSecond() {
		return timerRecSecond;
	}

	public int getTimerStopHour() {
		return timerStopHour;
	}

	public int getTimerStopMinute() {

		return timerStopMinute;
	}

	public int getTimerStopSecond() {
		return timerStopSecond;
	}

	public String getVideoPath() {
		return videoPath;
	}


	public boolean isAudioEnabled() {
		return audioEnabled;
	}

	public boolean isAudioSynched() {
		return audioSynched;
	}

	public boolean isMouseSFollowed() {
		return mouseSFollowed;
	}

	public boolean isMouseShowing() {
		return mouseShowing;
	}



	public boolean isOverWriteAllowed() {
		return overWriteAllowed;
	}

	public boolean isPreviewWindowInUse() {
		return previewWindowInUse;

	}

	public boolean isSoundSixteenBit() {
		return soundSixteenBit;
	}

	public boolean isSoundStereo() {
		return soundStereo;
	}

	public boolean isTimerActivated() {
		return timerActivated;
	}

	public boolean isTimerStopEnabled() {
		return this.timerStopEnabled;
	}

	public boolean isVideoEnabled() {
		return this.videoEnabled;
	}

	public void setAudioEnabled(boolean audioEnabled) {
		if (!isLoading())
			this.audioEnabled = audioEnabled;

	}

	public void setAudioPath(String audioPath) {
		if (!isLoading())
			this.audioPath = audioPath;

	}

	public void setAudioSynched(boolean audioSynched) {
		if (!isLoading())
			this.audioSynched = audioSynched;
	}

	public void setCapRect(CapSizeQuery capQuery) {
		if (!isLoading()) {
			this.captureX = capQuery.xVal;
			this.captureY = capQuery.yVal;
			this.captureWidth = Math.max(capQuery.widthVal, 10);
			this.captureHeight = Math.max(capQuery.heightVal, 10);
		}

	}

	public void setCapRect(Rectangle capRect) {
		if (!isLoading()) {
			this.captureX = capRect.x;
			this.captureY = capRect.y;
			this.captureWidth = Math.max(capRect.width, 10);
			this.captureHeight = Math.max(capRect.height, 10);
		}
	}

	public void setCaptureHeight(int heightScreen) {
		if (!isLoading())
			this.captureHeight = heightScreen;

	}

	public void setCaptureWidth(int widthScreen) {
		if (!isLoading())
			this.captureWidth = widthScreen;

	}

	public void setCaptureX(int leftScreen) {
		if (!isLoading())
			this.captureX = leftScreen;

	}

	public void setCaptureY(int topScreen) {
		if (!isLoading())
			this.captureY = topScreen;

	}

	public void setEncQuality(int encQuality) {
		if (!isLoading())
			this.encQuality = encQuality;

	}

	public void setFPS(int fPS) {
		FPS = fPS;
	}

	public void setImagePath(String imagePath) {
		if (!isLoading())
			this.imagePath = imagePath;

	}

	public void setMouseSFollowed(boolean mouseSFollowed) {
		if (!isLoading())
			this.mouseSFollowed = mouseSFollowed;

	}

	public void setMouseShowing(boolean mouseShowing) {
		if (!isLoading())
			this.mouseShowing = mouseShowing;

	}



	public void setOverWriteAllowed(boolean b) {
		if (!isLoading())
			this.overWriteAllowed = b;
	}

	public void setPlb(int plb) {
		if (!isLoading())
			this.plb = plb;
	}

	public void setPreviewWindowInUse(boolean previewWindowInUse) {
		if (!isLoading())
			this.previewWindowInUse = previewWindowInUse;

	}

	public void setSound16Bits(boolean sixteenBit) {
		if (!isLoading())
			this.soundSixteenBit = sixteenBit;

	}

	public void setSound1Stereo(boolean stereo) {
		if (!isLoading())
			this.soundStereo = stereo;

	}

	public void setSoundFrequency(int frequency) {
		if (!isLoading())
			this.soundFrequency = frequency;

	}

	public void setSoundSixteenBit(boolean soundSixteenBit) {
		if (!isLoading())
			this.soundSixteenBit = soundSixteenBit;
	}

	public void setSoundStereo(boolean soundStereo) {
		if (!isLoading())
			this.soundStereo = soundStereo;
	}

	public void setTimerActivated(boolean b) {
		if (!isLoading())
		this.timerActivated=b; 
		
	}

	public void setTimerRecHour(int timerRecHour) {
		if (!isLoading())

			this.timerRecHour = timerRecHour;
	}

	public void setTimerRecHour(Integer integer) {
		if (!isLoading())
			this.timerRecHour = integer.intValue();

	}

	public void setTimerRecMinute(int timerRecMinute) {
		if (!isLoading())

			this.timerRecMinute = timerRecMinute;
	}

	public void setTimerRecMinute(Integer value) {

		if (!isLoading())
			this.timerRecMinute = value.intValue();

	}

	public void setTimerRecSecond(int timerRecSecond) {
		if (!isLoading())

			this.timerRecSecond = timerRecSecond;
	}

	public void setTimerRecSecond(Integer value) {
		if (!isLoading())
			this.timerRecSecond = value.intValue();

	}

	public void setTimerStopEnabled(boolean timerStopInEnabled) {
		if (!isLoading())
			this.timerStopEnabled = timerStopInEnabled;
	}

	public void setTimerStopHour(int timerStopHour) {
		if (!isLoading())

			this.timerStopHour = timerStopHour;
	}

	public void setTimerStopHour(Integer integer) {
		if (!isLoading())
			this.timerStopHour = integer.intValue();

	}

	public void setTimerStopMinute(int timerStopMinute) {
		if (!isLoading())

			this.timerStopMinute = timerStopMinute;
	}

	public void setTimerStopMinute(Integer value) {
		if (!isLoading())
			this.timerStopMinute = value.intValue();

	}

	public void setTimerStopSecond(int timerStopSecond) {
		if (!isLoading())

			this.timerStopSecond = timerStopSecond;
	}

	public void setTimerStopSecond(Integer value) {
		if (!isLoading())
			this.timerStopSecond = value.intValue();

	}

	public void setTimerUsesRelativeTime(boolean b) {
		if (!isLoading())
			this.timerUsesRelativeTime = b;
	}

	public void setVideoEnabled(boolean videoEnabled) {
		if (!isLoading())
			this.videoEnabled = videoEnabled;
	}

	public void setVideoPath(String moviePath) {
		if (!isLoading())
			this.videoPath = moviePath;

	}

	public int getHopMinutes() {
		return this.hopMinutes;
	}

	public void setHopMinutes(int hopMinutes) {
		if (!isLoading())
		this.hopMinutes = hopMinutes;
	}

}
