package jef.core.pathfinding.defenders;

import java.util.Collections;
import java.util.List;

import jef.core.Player;
import jef.core.movement.Location;
import jef.core.pathfinding.AbstractPathfinder;
import jef.core.pathfinding.Direction;
import jef.core.pathfinding.AdvancedInterceptPlayer;
import jef.core.pathfinding.Pathfinder;
import jef.core.pathfinding.blocking.BlockerPathfinder;
import jef.core.pathfinding.runners.RunnerPathfinder;

public class AdvancedPursueRunner extends AbstractPathfinder implements DefenderPathfinder
{
	private AdvancedInterceptPlayer interceptPlayer;
	
	public AdvancedPursueRunner(Player player, Direction direction)
	{
		super(player, direction);
	}

	@Override
	public void reset()
	{
		super.reset();
		this.interceptPlayer = null;
	}

	public Pathfinder getTargetPathfinder()
	{
		return this.interceptPlayer.getTargetPathfinder();
	}

	public List<Location> getSteps()
	{
		if (this.interceptPlayer != null)
			return this.interceptPlayer.getSteps();
		
		return Collections.singletonList(getPlayer().getLoc());
	}

	@Override
	public boolean calculate(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders,
			List<? extends BlockerPathfinder> blockers, long deltaNanos)
	{
		if (interceptPlayer == null || interceptPlayer.getTargetPathfinder() != runner)
			interceptPlayer = new AdvancedInterceptPlayer(getPlayer(), getDirection(), runner);

		boolean ret = interceptPlayer.calculate(runner, defenders, blockers, deltaNanos);
		setPath(interceptPlayer.getPath());
		return ret;
	}

}
