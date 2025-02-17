package jef.core.pathfinding;

import jef.core.Player;
import jef.core.movement.player.Path;

public abstract class WaypointPathfinder extends AbstractPathfinder
{

	public WaypointPathfinder(Player player, Direction direction)
	{
		super(player, direction);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Path getPath()
	{
		return this.getPlayer().getPath();
	}
}
