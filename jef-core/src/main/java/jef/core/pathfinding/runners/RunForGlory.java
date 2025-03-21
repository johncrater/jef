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
import jef.core.pathfinding.blocking.BlockerPathfinder;
import jef.core.pathfinding.defenders.DefenderPathfinder;

/**
 * Runner heads directly for the nearest part of the end zone
 */
public class RunForGlory extends AbstractPathfinder implements RunnerPathfinder
{
	public RunForGlory(Player runner, Direction direction)
	{
		super(runner, direction);
	}

	@Override
	public boolean calculate(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders, List<? extends BlockerPathfinder> blockers, long deltaNanos)
	{
		if (getPath() == null)
		{
			setPath(new DefaultPath(new Waypoint(new DefaultLocation(Field.yardLine(110, getDirection()), getPlayer().getLoc().getY()),
					getPlayer().getMaxSpeed(), getPlayer().getMaxSpeed(), DestinationAction.noStop)));
		}		

		return this.calculateSteps(runner, defenders, blockers, deltaNanos);
	}
}
