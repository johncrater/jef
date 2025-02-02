package jef.core.pathfinding.runners;

import java.util.List;

import jef.core.Field;
import jef.core.Player;
import jef.core.movement.DefaultLocation;
import jef.core.movement.player.DefaultPath;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;
import jef.core.pathfinding.AbstractPathfinder;
import jef.core.pathfinding.Direction;
import jef.core.pathfinding.Pathfinder;
import jef.core.pathfinding.blocking.BlockerPathfinder;
import jef.core.pathfinding.defenders.DefenderPathfinder;

/**
 * Runner heads directly for the nearest part of the end zone
 */
public class RunForGlory extends AbstractPathfinder implements RunnerPathfinder
{
	public RunForGlory(Player runner, Direction direction, double deltaTime)
	{
		super(runner, direction, deltaTime);
	}

	@Override
	public boolean calculate(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders, List<? extends BlockerPathfinder> blockers)
	{
		if (getPath() == null)
		{
			long nanos = System.nanoTime();
			setPath(new DefaultPath(new Waypoint(new DefaultLocation(Field.yardLine(100, getDirection()), getPlayer().getLoc().getY()),
					getPlayer().getMaxSpeed(), DestinationAction.noStop)));
			useTime(System.nanoTime() - nanos);
		}		

		return this.calculateSteps(runner, defenders, blockers);
	}
}
