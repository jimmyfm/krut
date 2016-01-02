package krut.memory.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

public class CompInfo {
	public static final String CI = "ci[";
	int top = 0;
	int left = 0;
	int width = 0;
	int height = 0;

	private transient boolean persistent = false;

	boolean visible = false;
	boolean visibleEnabled = false;
	boolean resizeEnabled = false;

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width =Math.max(200, width);
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = Math.max(20, height);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisibleEnabled() {
		return visibleEnabled;
	}

	public void setVisibleEnabled(boolean visibleEnabled) {
		this.visibleEnabled = visibleEnabled;
	}

	public void update(Component component) {

		Point pt = new Point(component.getLocation());
		Dimension size = component.getSize();

		setLeft(pt.x);
		setTop(pt.y);
		setWidth(size.width);
		setHeight(size.height);
		setVisible(component.isVisible());
	}

	public boolean restore(Component component) {
		if (this.persistent) {
			
			
			component.setLocation(new Point(getLeft(), getTop()));

			if (resizeEnabled)
				component.setSize(new Dimension(getWidth(),
						getHeight()));

			if (isVisibleEnabled())
				component.setVisible(isVisible());
			return true;
		}
		return false;
	}

	public String toProp() {
		// TODO Auto-generated method stub
		return CI + getLeft() 
				+ ":" + getTop() 
				+ ":" + getWidth() 
				+ ":"+ getHeight() 
				+ ":" + isVisible() 
				+ ":" + isVisibleEnabled()
				+ ":" + isResizeEnabled()
				+ "]";
	}

	public static boolean isInfo(String t) {
		return t.indexOf(CI) == 0;
	}

	public static CompInfo fromProp(String prop) {
		CompInfo inf = new CompInfo();
		int sq0 = prop.indexOf('[');
		int sqe = prop.lastIndexOf(']');

		String inner = prop.substring(sq0+1, sqe);
		String[] blocks = inner.split("\\s*\\:\\s*");
		inf.setLeft(Integer.parseInt(blocks[0].trim()));
		inf.setTop(Integer.parseInt(blocks[1]));
		inf.setWidth(Integer.parseInt(blocks[2]));
		inf.setHeight(Integer.parseInt(blocks[3]));
		inf.setVisible(Boolean.parseBoolean(blocks[4]));
		inf.setVisibleEnabled(Boolean.parseBoolean(blocks[5]));
		inf.setResizeEnabled(Boolean.parseBoolean(blocks[6].trim()));
		inf.persistent = true;
		return inf;
	}

	public boolean isResizeEnabled() {
		return resizeEnabled;
	}

	public void setResizeEnabled(boolean resizeEnabled) {
		this.resizeEnabled = resizeEnabled;
	}

	public boolean isPersistent() {
		return persistent;
	}

	public void setPersistent(boolean persisted) {
		this.persistent = persisted;
	}
}