package tests;

import krut.KRUT_GUI.EncodingProgressBar;

public class Test2 {

	   /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EncodingProgressBar("Testing progress bar").setVisible(true);
            }
        });
    }
}
