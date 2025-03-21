package jef.core.ui.swt.utils;

import org.eclipse.swt.graphics.GC;

import jef.core.Location;

public class DrawableItemImpl implements DrawableItem
{
	private Location location;
	private OnFieldMarkup markup;

	public DrawableItemImpl(Location location, OnFieldMarkup markup)
	{
		super();
		this.location = location;
		this.markup = markup;
	}

	@Override
	public Location getLocation()
	{
		return location;
	}

	@Override
	public void draw(GC gc)
	{
		markup.draw(gc);
	}
}