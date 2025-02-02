package jef.core.pathfinding.defenders;

import java.util.Collections;
import java.util.List;

import jef.core.Player;
import jef.core.movement.Location;
import jef.core.pathfinding.AbstractPathfinder;
import jef.core.pathfinding.Direction;
import jef.core.pathfinding.InterceptPlayer;
import jef.core.pathfinding.Pathfinder;
import jef.core.pathfinding.blocking.BlockerPathfinder;
import jef.core.pathfinding.runners.RunnerPathfinder;

public class PursueRunner extends AbstractPathfinder implements DefenderPathfinder
{
	private InterceptPlayer interceptPlayer;
	
	public PursueRunner(Player player, Direction direction, double deltaTime)
	{
		super(player, direction, deltaTime);
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
			List<? extends BlockerPathfinder> blockers)
	{
		if (interceptPlayer == null || interceptPlayer.getTargetPathfinder() != runner)
			interceptPlayer = new InterceptPlayer(getPlayer(), getDirection(), getDeltaTime(), runner);

		interceptPlayer.setTimeRemaining(getTimeRemaining());
		boolean ret = interceptPlayer.calculate(runner, defenders, blockers);
		this.setPath(interceptPlayer.getPath());
		this.setTimeRemaining(this.interceptPlayer.getTimeRemaining());
		
		return ret;
	}

}
