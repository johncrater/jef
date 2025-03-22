package jef.core.pathfinding.blocking;

import jef.core.Direction;
import jef.core.IPlayers;
import jef.core.Player;
import jef.core.pathfinding.DefaultInterceptPlayer;

public class BlockPlayer extends DefaultInterceptPlayer implements BlockerPathfinder
{

	public BlockPlayer(IPlayers players, Player player, Direction direction, Player targetPlayer)
	{
		super(players, player, direction, targetPlayer);
		// TODO Auto-generated constructor stub
	}

}
