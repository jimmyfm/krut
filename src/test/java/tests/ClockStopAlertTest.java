package tests;

import krut.KRUT_GUI.alerts.TimerAlert;

import org.junit.Test;

public class ClockStopAlertTest {

	@Test
	public void test() throws InterruptedException {
		TimerAlert clockStopAlert = new TimerAlert();
		clockStopAlert.init(); 
		clockStopAlert.getFrame().setVisible(true);
		Thread.sleep(5000); 
	}

}
