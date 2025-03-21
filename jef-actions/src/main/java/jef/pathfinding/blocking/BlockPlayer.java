package jef.pathfinding.blocking;

import jef.IPlayers;
import jef.core.Direction;
import jef.core.Player;
import jef.pathfinding.DefaultInterceptPlayer;

public class BlockPlayer extends DefaultInterceptPlayer implements BlockerPathfinder
{

	public BlockPlayer(IPlayers players, Player player, Direction direction, Player targetPlayer)
	{
		super(players, player, direction, targetPlayer);
		// TODO Auto-generated constructor stub
	}

}
