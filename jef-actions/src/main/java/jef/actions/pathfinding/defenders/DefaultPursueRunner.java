package jef.actions.pathfinding.defenders;

import jef.actions.pathfinding.DefaultInterceptPlayer;
import jef.actions.pathfinding.PlayerStates;
import jef.core.Direction;
import jef.core.Player;

public class DefaultPursueRunner extends DefaultInterceptPlayer implements DefenderPathfinder
{
	public DefaultPursueRunner(PlayerStates players, Player player, Direction direction, Player runner)
	{
		super(players, player, direction, runner);
	}
}
