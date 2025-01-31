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
public class RunForGlory extends AbstractRunnerPathfinder
{
	public RunForGlory(Player runner, Direction direction, double deltaTime)
	{
		super(runner, direction, deltaTime);
	}

	@Override
	public boolean calculate()
	{
		if (getPath() == null)
		{
			long nanos = System.nanoTime();
			setPath(new DefaultPath(new Waypoint(new DefaultLocation(Field.yardLine(100, getDirection()), getPlayer().getLoc().getY()),
					getPlayer().getMaxSpeed(), DestinationAction.noStop)));
			useTime(System.nanoTime() - nanos);
		}		

		return this.calculateSteps();
	}
}
