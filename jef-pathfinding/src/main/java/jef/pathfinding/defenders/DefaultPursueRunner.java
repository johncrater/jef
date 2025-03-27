package jef.pathfinding.defenders;

import jef.core.Player;
import jef.geometry.Direction;
import jef.pathfinding.DefaultInterceptPlayer;
import jef.pathfinding.IPlayers;

public class DefaultPursueRunner extends DefaultInterceptPlayer implements DefenderPathfinder
{
	public DefaultPursueRunner(IPlayers players, Player player, Direction direction, Player runner)
	{
		super(players, player, direction, runner);
	}
}
