package net.sourceforge.krut;

import java.io.IOException;

import net.sourceforge.krut.events.EventBus;

public class Krut {

	public static void main(String[] args) {
		EventBus.get().register(new net.sourceforge.krut.events.LogSubscriber());

		Run_KRUT newContentPane = new Run_KRUT();
		try {
			newContentPane.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}