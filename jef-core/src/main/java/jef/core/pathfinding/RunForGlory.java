package jef.core.pathfinding;

import jef.core.Field;
import jef.core.Player;
import jef.core.movement.DefaultLocation;
import jef.core.movement.player.DefaultPath;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;

/**
 * Runner heads directly for the nearest part of the end zone
 */
public class RunForGlory extends AbstractPathfinder
{
	private Player runner;
	private Direction direction;
	
	public RunForGlory(Player runner, Direction direction)
	{
		super();
		this.runner = runner;
		this.direction = direction;
	}

	@Override
	public boolean calculate()
	{
		long nanos = System.nanoTime();
		setPath(new DefaultPath(new Waypoint(new DefaultLocation(Field.yardLine(100, direction), runner.getLoc().getY()),
				runner.getMaxSpeed(), DestinationAction.noStop)));

		useTime(System.nanoTime() - nanos);
		return true;
	}
}
