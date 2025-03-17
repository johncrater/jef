package jef.pathfinding.defenders;

import jef.core.Direction;
import jef.core.Player;
import jef.pathfinding.DefaultInterceptPlayer;
import jef.pathfinding.Players;

public class DefaultPursueRunner extends DefaultInterceptPlayer implements DefenderPathfinder
{
	public DefaultPursueRunner(Players players, Player player, Direction direction, Player runner)
	{
		super(players, player, direction, runner);
	}
}
