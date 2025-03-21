package jef.core.ui.swt.utils;

import org.eclipse.swt.graphics.GC;

import jef.core.Location;

public interface DrawableItem
{
	public Location getLocation();
	public void draw(GC gc);
}
