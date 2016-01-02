package tests;

import static org.junit.Assert.*;
import krut.KRUT_GUI.tray.KRUT_System_Tray;

import org.junit.Test;

public class TrayIcon {

	@Test
	public void test() throws InterruptedException {
		assertNotNull("Not yet implemented");
		
		KRUT_System_Tray tray= new KRUT_System_Tray(); 
		tray.displayMessage("Vediamo", "Che si veda"); 
		
		
		Thread.sleep(30000);
	}

}
