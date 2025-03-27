package jef.ui.swt.utils;

import org.eclipse.swt.graphics.GC;

import jef.geometry.Location;

public interface DrawableItem
{
	public Location getLocation();
	public void draw(GC gc);
}
