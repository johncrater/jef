package jef.core.ui.swt.utils;

import org.eclipse.swt.graphics.GC;

import jef.core.Location;
import jef.core.movement.player.Steerable;

public class PlayerDrawable implements DrawableItem
{
	private Steerable player;
	
	public PlayerDrawable(Steerable player)
	{
		this.player = player;
	}

	@Override
	public Location getLocation()
	{
		return this.player.getLoc();
	}

	@Override
	public void draw(GC gc)
	{
	}

}
