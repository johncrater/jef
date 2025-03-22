package jef.pathfinding.defenders;

import jef.IPlayers;
import jef.core.Direction;
import jef.core.Player;
import jef.pathfinding.DefaultInterceptPlayer;

public class DefaultPursueRunner extends DefaultInterceptPlayer implements DefenderPathfinder
{
	public DefaultPursueRunner(IPlayers players, Player player, Direction direction, Player runner)
	{
		super(players, player, direction, runner);
	}
}
