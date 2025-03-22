package jef.core.pathfinding.defenders;

import jef.core.Direction;
import jef.core.IPlayers;
import jef.core.Player;
import jef.core.pathfinding.DefaultInterceptPlayer;

public class DefaultPursueRunner extends DefaultInterceptPlayer implements DefenderPathfinder
{
	public DefaultPursueRunner(IPlayers players, Player player, Direction direction, Player runner)
	{
		super(players, player, direction, runner);
	}
}
