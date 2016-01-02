package krut.memory;

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.STATIC;
import static java.lang.reflect.Modifier.TRANSIENT;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Properties;



public class AbstractMemory {

	private transient File file;
	private  transient Properties innerProperties = new Properties();
	// private static final String ENCODING = "UTF-8";
	public static final int NOGO = STATIC | FINAL | TRANSIENT;

	public static final int NULL_INT = Integer.MIN_VALUE;

	private ArrayList<PropertyChangeListener> changeListeners = new ArrayList<PropertyChangeListener>();

	private boolean loading = false;

	private long saveTime = NULL_INT;

	public AbstractMemory(File file2)
			throws InvalidPropertiesFormatException, FileNotFoundException,
			IOException {
		this.file = file2;
		
	}
	
	public static File getMemoryFile(String file){
		File home=new File (System.getProperty("user.home"));  
		File dir= new File (home, ".krut-screen-recorder");
		dir.mkdirs(); 
		return new File(dir, file); 
	}
	
	


	public boolean addPropertyChangeListener(
			PropertyChangeListener propertyChangeListener) {
		return changeListeners.add(propertyChangeListener);
	}



	protected boolean checkChanges(Properties p2) {
		// if( changeListeners .size() ==0 )return;
		Properties p3 = new Properties();
		p3.putAll(p2);
		Iterator<Entry<Object, Object>> it = innerProperties.entrySet()
				.iterator();
		boolean hasChanges = false;
		while (it.hasNext()) {
			Entry<Object, Object> entry = (Entry<Object, Object>) it.next();
			Object key = entry.getKey();
			Object val3 = p3.get(key);
			Object value = entry.getValue();
			if (val3 != value /* possible only for two null2 */
					&& val3 != null && !val3.equals(value)) {
				firePropertyChange((String) key, value, val3);
				hasChanges = true;

			}
			p3.remove(key);
		}
		/*
		 * the new version of the configuration class has added some properties,
		 * since last file was saved!!!
		 */
		return hasChanges || (p3.size() > 0);
	}

	protected void firePropertyChange(String key, Object value, Object val3) {

		PropertyChangeEvent evt = new PropertyChangeEvent(this, key, value,
				val3);

		ListIterator<PropertyChangeListener> it = changeListeners
				.listIterator();
		while (it.hasNext()) {
			it.next().propertyChange(evt);

		}
	}

	protected String firstCap(String name) {
		String firstCap = name.substring(0, 1).toUpperCase()
				+ name.substring(1);
		return firstCap;
	}

	public File getFile() {
		return file;
	}

	public long getSaveTime() {
		return saveTime;
	}

	public void markUnLoaded() {
		this.saveTime = NULL_INT;
	}

	
	public boolean hasBeenLoaded() {
		return saveTime > NULL_INT;
	}

	protected void init() throws InvalidPropertiesFormatException,
			FileNotFoundException, IOException {
		innerProperties = new Properties();
		if (file.isFile()) {
			load();
		} else
			persist();

	}

	public void load() throws InvalidPropertiesFormatException,
			FileNotFoundException, IOException {
		if (!file.isFile())
			return;

		loadFromFile();
		loading = true;
		
		setSaveTime(
				Long.parseLong(innerProperties.getProperty("saveTime").trim())
		); 
		
		fillFromProperties(innerProperties);
		loading = false;
	}

	protected void alignProperties(Properties props) {
		Field[] fds = getClass().getDeclaredFields();
		props.setProperty("saveTime", ""+this.saveTime); 
		
		for (int i = 0; i < fds.length; i++) {
			Field field = fds[i];
			String name = field.getName();
			if (0 == (field.getModifiers() & NOGO))
				try {
					Method m = getClass().getDeclaredMethod(
							"get" + firstCap(name)); //$NON-NLS-1$
					Object rv = m.invoke(this, new Object[0]);
					if (rv != null)
						props.setProperty(name, rv.toString());

				} catch (Exception e) {
					try {
						Method m = getClass().getDeclaredMethod(
								"is" + firstCap(name)); //$NON-NLS-1$
						Object rv = m.invoke(this, new Object[0]);
						if (rv != null)
							props.setProperty(name, rv.toString());
					} catch (Exception e1) {

					}
				}
		}
	}
	
	protected void fillFromProperties(Properties props ) {
		Field[] fds = getClass().getDeclaredFields();
		for (int i = 0; i < fds.length; i++) {
			Field field = fds[i];
			if (0 == (field.getModifiers() & NOGO))
				try {
					String name = field.getName();

					String property = props.getProperty(name);

					if (property != null) {
						boolean wasAccessible = field.isAccessible();
						field.setAccessible(true);

						Class<?> type = field.getType();

						if (type.isAssignableFrom(int.class)) {
							field.setInt(this, Integer.parseInt(property));
						} else if (type.isAssignableFrom(boolean.class)) {
							field.setBoolean(this,
									Boolean.parseBoolean(property));
						} else

						if (type.isAssignableFrom(double.class)) {
							field.setDouble(this, Double.parseDouble(property));
						} else

						if (type.isAssignableFrom(long.class)) {
							field.setLong(this, Long.parseLong(property));
						} else if (type.isAssignableFrom(float.class)) {
							field.setFloat(this, Float.parseFloat(property));
						} else if (type.isAssignableFrom(short.class)) {
							field.setShort (this, Short.parseShort(property));
						} else

						if (type.isAssignableFrom(char.class)) {
							field.setChar(this,
									property.length() > 0 ? property.charAt(0)
											: ' ');
						} else if (type.isAssignableFrom(byte.class)) {
							field.setByte(this, Byte.parseByte(property));
						} else if (type.isAssignableFrom(String.class)) {
							field.set(this, property);
						}

						field.setAccessible(wasAccessible);
					}

				} catch (Exception e) {
				} finally {
				}
		}
	}

	protected void loadFromFile() throws IOException,
			InvalidPropertiesFormatException, FileNotFoundException {
		innerProperties.load(new FileInputStream(file));
	}

	public void persist() {
		if (loading)
			return;
		Properties p2 = new Properties();
		alignProperties(p2);
		if (checkChanges(p2)) {
			innerProperties = p2;
			try {
				storeToFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setSaveTime(long saveTime) {
		this.saveTime = saveTime;
	}

	protected void storeToFile() throws IOException, FileNotFoundException {
		String comment = "Generated: " + new Date(); //$NON-NLS-1$
		try {
			innerProperties.store(new FileOutputStream(file), comment);// ,
																		// ENCODING);
		} catch (Exception e) {

		}
	}

	public boolean isLoading() {
		return loading;
	}

	public void setLoading(boolean isLoading) {
		this.loading = isLoading;
	}

	public Properties getInnerProperties() {
		return innerProperties;
	}

}
