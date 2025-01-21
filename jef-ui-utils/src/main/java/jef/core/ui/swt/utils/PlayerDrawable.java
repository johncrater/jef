package jef.core.ui.swt.utils;

import org.eclipse.swt.graphics.GC;

import jef.core.Player;
import jef.core.movement.Location;

public class PlayerDrawable implements DrawableItem
{
	private Player player;
	
	public PlayerDrawable(Player player)
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
