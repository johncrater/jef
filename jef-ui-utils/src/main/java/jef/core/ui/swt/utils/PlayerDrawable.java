package jef.core.ui.swt.utils;

import org.eclipse.swt.graphics.GC;

import jef.core.Location;
import jef.core.PlayerState;

public class PlayerDrawable implements DrawableItem
{
	private PlayerState player;
	
	public PlayerDrawable(PlayerState player)
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
