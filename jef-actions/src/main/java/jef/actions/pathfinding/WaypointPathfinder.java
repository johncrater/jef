package jef.actions.pathfinding;

import java.util.List;

import jef.actions.pathfinding.blocking.BlockerPathfinder;
import jef.actions.pathfinding.defenders.DefenderPathfinder;
import jef.actions.pathfinding.runners.RunnerPathfinder;
import jef.core.Direction;
import jef.core.PlayerState;
import jef.core.movement.player.Path;

public abstract class WaypointPathfinder extends AbstractPathfinder
{

	public WaypointPathfinder(PlayerState playerState, Direction direction)
	{
		super(playerState, direction);
	}

	@Override
	public boolean calculate(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders,
			List<? extends BlockerPathfinder> blockers, long deltaNanos)
	{
		// true meaning the calculations have completed
		return true;
	}
}
