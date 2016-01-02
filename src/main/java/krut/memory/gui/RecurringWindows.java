package krut.memory.gui;

public class RecurringWindows {
	
	public static final String PREVIEW_LOWER_QUALITY_FPS = "Preview (lower Quality FPS)";
	public static final String KRUT_DATA_PROCESSING = "Krut Data Processing";

	/**
	 * Recognize the IDS the Recurring Windows of 
	 * 
	 * */
	public static boolean isArecurringWindow(String c_ID) {
		boolean rv =  KRUT_DATA_PROCESSING.equals(c_ID);
				rv |= PREVIEW_LOWER_QUALITY_FPS.equals(c_ID);
		return rv;
	}
}
