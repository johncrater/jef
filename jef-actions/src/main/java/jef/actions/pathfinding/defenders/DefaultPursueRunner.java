package jef.actions.pathfinding.defenders;

import java.util.Collections;
import java.util.List;

import jef.actions.pathfinding.AbstractPathfinder;
import jef.actions.pathfinding.DefaultInterceptPlayer;
import jef.actions.pathfinding.Pathfinder;
import jef.actions.pathfinding.blocking.BlockerPathfinder;
import jef.actions.pathfinding.runners.RunnerPathfinder;
import jef.core.Direction;
import jef.core.Location;
import jef.core.PlayerState;

public class DefaultPursueRunner extends AbstractPathfinder implements DefenderPathfinder
{
	private DefaultInterceptPlayer interceptPlayer;
	
	public DefaultPursueRunner(PlayerState player, Direction direction)
	{
		super(player, direction);
	}

	public Pathfinder getTargetPathfinder()
	{
		return this.interceptPlayer.getTargetPathfinder();
	}

	public List<Location> getSteps()
	{
		if (this.interceptPlayer != null)
			return this.interceptPlayer.getSteps();
		
		return Collections.singletonList(getPlayerState().getLoc());
	}

	@Override
	public boolean calculate(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders,
			List<? extends BlockerPathfinder> blockers, long deltaNanos)
	{
		if (interceptPlayer == null || interceptPlayer.getTargetPathfinder() != runner)
			interceptPlayer = new DefaultInterceptPlayer(getPlayerState(), getDirection(), runner);

		boolean ret = interceptPlayer.calculate(runner, defenders, blockers, deltaNanos);
		setPath(interceptPlayer.getPath());
		return ret;
	}
}
