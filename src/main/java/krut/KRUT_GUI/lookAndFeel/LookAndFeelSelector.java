package krut.KRUT_GUI.lookAndFeel;


public class LookAndFeelSelector {
	
	
   private static final String[] lookAndFeels= new String[]{
	//   "com.seaglasslookandfeel.SeaGlassLookAndFeel",
	   javax.swing.UIManager.getCrossPlatformLookAndFeelClassName(),
	   "com.birosoft.liquid.LiquidLookAndFeel",
	   "javax.swing.plaf.metal.MetalLookAndFeel",
	   "com.sun.java.swing.plaf.motif.MotifLookAndFeel",
	   "com.sticazzi.nofeel"
   };
	
	
	
	public static void setLookAndFeel() {

		for ( int i=0; i < lookAndFeels.length && ! tryLookAndFeel(lookAndFeels[i]); i++); 
	
	}

	public static boolean tryLookAndFeel(String lookAndFeelClassName) {
		boolean rv=false; 
		try {
			javax.swing.UIManager.setLookAndFeel(lookAndFeelClassName);
			rv=true;  
		} catch (Throwable e){}
		return rv;
	}
}
