package jef.core.pathfinding;

import java.util.List;

import jef.core.Player;
import jef.core.movement.player.Path;
import jef.core.pathfinding.blocking.BlockerPathfinder;
import jef.core.pathfinding.defenders.DefenderPathfinder;
import jef.core.pathfinding.runners.RunnerPathfinder;

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
