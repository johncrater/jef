package jef.pathfinding.blocking;

import jef.pathfinding.DefaultInterceptPlayer;
import jef.pathfinding.IPathfinderPlayer;
import jef.pathfinding.IPathfinderState;

public class BlockPlayer<T extends IPathfinderPlayer> extends DefaultInterceptPlayer<T> implements IBlockerPathfinder
{

	public BlockPlayer(IPathfinderState<T> pathfinderState, T player,
			T targetPlayer)
	{
		super(pathfinderState, player, targetPlayer);
	}

}
