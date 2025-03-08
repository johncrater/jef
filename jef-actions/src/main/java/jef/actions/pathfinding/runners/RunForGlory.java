package jef.actions.pathfinding.runners;

import java.util.List;

import jef.actions.pathfinding.AbstractPathfinder;
import jef.actions.pathfinding.blocking.BlockerPathfinder;
import jef.actions.pathfinding.defenders.DefenderPathfinder;
import jef.core.Direction;
import jef.core.Field;
import jef.core.Location;
import jef.core.PlayerState;
import jef.core.movement.player.Path;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;

/**
 * Runner heads directly for the nearest part of the end zone
 */
public class RunForGlory extends AbstractPathfinder implements RunnerPathfinder
{
	public RunForGlory(PlayerState runner, Direction direction)
	{
		super(runner, direction);
	}

	@Override
	public boolean calculate(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders,
			List<? extends BlockerPathfinder> blockers, long deltaNanos)
	{
		if (getPath() == null)
		{
			setPath(new Path(
					new Waypoint(new Location(Field.yardLine(110, getDirection()), getPlayerState().getLoc().getY()),
							getPlayerState().getMaxSpeed(), getPlayerState().getMaxSpeed(), DestinationAction.noStop)));
		}

		return this.calculateSteps(runner, defenders, blockers, deltaNanos);
	}
}
