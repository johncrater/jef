package jef.pathfinding.defenders;

import java.util.Collections;
import java.util.List;

import jef.actions.pathfinding.AdvancedInterceptPlayer;
import jef.actions.pathfinding.Pathfinder;
import jef.actions.pathfinding.PathfinderBase;
import jef.actions.pathfinding.PlayerStates;
import jef.actions.pathfinding.blocking.BlockerPathfinder;
import jef.actions.pathfinding.runners.RunnerPathfinder;
import jef.core.Direction;
import jef.core.Location;
import jef.core.Player;

public class AdvancedPursueRunner extends PathfinderBase implements DefenderPathfinder
{
	private AdvancedInterceptPlayer interceptPlayer;
	
	public AdvancedPursueRunner(PlayerStates playerStates, Player player, Direction direction)
	{
		super(playerStates, player, direction);
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
	public void calculate()
	{
		if (interceptPlayer == null || interceptPlayer.getTargetPathfinder() != runner)
			interceptPlayer = new AdvancedInterceptPlayer(getPlayerState(), getDirection(), runner);

		boolean ret = interceptPlayer.calculate(runner, defenders, blockers, deltaNanos);
		setPath(interceptPlayer.getPath());
		return ret;
	}

}
