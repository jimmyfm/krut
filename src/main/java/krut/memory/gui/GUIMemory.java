package krut.memory.gui;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.JFrame;

import krut.memory.AbstractMemory;
/***
 * 
 * @author CRISTIANO
 *
 */
public class GUIMemory extends AbstractMemory {

	public static final String MEMORY_FILENAME = "krut-gui-memory.properties";
	private static GUIMemory root;

	private transient HashSet<Component> known = new HashSet<Component>();
	private transient HashSet<String> newlyAdded = new HashSet<String>();
	private HashMap<String, CompInfo> map = new HashMap<String, CompInfo>();

	public static GUIMemory instance() {
		try {
			if (root == null) {
				return instance(getMemoryFile(MEMORY_FILENAME));

			}
		} catch (IOException e) {

			throw new RuntimeException("Cannot create config save file!!!!!"); //$NON-NLS-1$
		}
		return root;
	}

	public static GUIMemory instance(File file) throws FileNotFoundException,
			IOException {
		if (root == null) {
			root = new GUIMemory(file);
		}
		return root;
	}

	public static void reset() {
		getMemoryFile(MEMORY_FILENAME).delete();
	}

	public GUIMemory(File file2) throws InvalidPropertiesFormatException,
			FileNotFoundException, IOException {
		super(file2);
		init();
	}

	public boolean add(Component component, boolean autoVisibilityEnabled,
			boolean autoresizeEnabled) {

		if (known.contains(component))
			return false;
		known.add(component);

		String c_ID = null;

		if (component instanceof JFrame) {
			JFrame jFrame = (JFrame) component;
			/*
			 * Not exactly a genial idea, as it depends on the screen title...
			 * however, the frame names were a bit idiotic for the most part.
			 * 
			 * By the way, this means that, if someone uses more than a
			 * language, this thing will prserve different settings per
			 * language, as well as per user...
			 * 
			 * **
			 */
			c_ID = jFrame.getTitle();
			if (c_ID == null || "".equals(c_ID)) {
				c_ID = jFrame.getName();
			}
		}
		if (!RecurringWindows.isArecurringWindow(c_ID)) {

			if (c_ID == null)
				c_ID = component.getClass().getCanonicalName();

			if (c_ID == null)
				c_ID = "unknown-class";

			if (newlyAdded.contains(c_ID)) {
				int i = 1;
				String cn;
				while (newlyAdded.contains(cn = c_ID + "_" + i))
					i++;
				c_ID = cn;
			}
			newlyAdded.add(c_ID);
		}

		CompInfo info = getInfo(c_ID);
		info.setVisibleEnabled(autoVisibilityEnabled);
		info.setResizeEnabled(autoresizeEnabled);
		boolean rv = info.restore(component);

		if (!rv) {
			update(c_ID, component);
		}
		component.addComponentListener(new ComponentInterrogator(c_ID, this));
		return rv;
	}

	@Override
	protected void alignProperties(Properties props) {
		props.setProperty("saveTime", "" + this.getSaveTime());
		Iterator<Entry<String, CompInfo>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, CompInfo> next = it.next();
			CompInfo value = next.getValue();
			props.setProperty(next.getKey(), value.toProp());
			value.setPersistent(true);
		}
	}

	@Override
	protected void fillFromProperties(Properties props) {

		ScreenBoundariesChecker boundariesChecker = new ScreenBoundariesChecker();
		Iterator<Entry<Object, Object>> it = props.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Object, Object> next = it.next();
			String string = next.getValue().toString();
			if (CompInfo.isInfo(string)) {
				CompInfo fromProp = CompInfo.fromProp(string);
				/*
				 * ever since when the settings have been recorded, the screens
				 * configuration may have changed... anything out of bounds 
				 * that cannot be reached and dragged back to a usable position
				 * must be "forgotten", so that the window will be reinitialized 
				 */
				if (boundariesChecker.checks(fromProp))
					map.put(next.getKey().toString(), fromProp);
				else
					it.remove();
			}
		}
	}

	protected CompInfo getInfo(String canonicalName) {
		CompInfo inf = map.get(canonicalName);
		if (inf == null) {
			map.put(canonicalName, inf = new CompInfo());
		}
		return inf;
	}

	public void update(String componentID, Component component) {
		getInfo(componentID).update(component);
		persist();
	}

}
