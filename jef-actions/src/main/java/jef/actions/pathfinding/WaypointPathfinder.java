package jef.actions.pathfinding;

import java.util.List;

import jef.actions.pathfinding.blocking.BlockerPathfinder;
import jef.actions.pathfinding.defenders.DefenderPathfinder;
import jef.actions.pathfinding.runners.RunnerPathfinder;
import jef.core.Direction;
import jef.core.Player;
import jef.core.movement.player.Path;

public abstract class WaypointPathfinder extends AbstractPathfinder
{

	public WaypointPathfinder(Player player, Direction direction)
	{
		super(player, direction);
	}

	@Override
	public boolean calculate(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders,
			List<? extends BlockerPathfinder> blockers, long deltaNanos)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Path getPath()
	{
		return getPlayer().getPath();
	}
}
