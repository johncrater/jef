package jef.pathfinding.blocking;

import jef.core.Player;
import jef.geometry.Direction;
import jef.pathfinding.DefaultInterceptPlayer;
import jef.pathfinding.IPlayers;

public class BlockPlayer extends DefaultInterceptPlayer implements BlockerPathfinder
{

	public BlockPlayer(IPlayers players, Player player, Direction direction, Player targetPlayer)
	{
		super(players, player, direction, targetPlayer);
		// TODO Auto-generated constructor stub
	}

}
