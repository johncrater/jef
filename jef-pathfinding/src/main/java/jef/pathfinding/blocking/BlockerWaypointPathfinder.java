package jef.pathfinding.blocking;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.movement.DebugShape;
import jef.movement.player.Path;
import jef.pathfinding.IPathfinderPlayer;
import jef.pathfinding.PathfinderBase;
import jef.pathfinding.PathfindingMessages;
import jef.pathfinding.PathfindingState;

public class BlockerWaypointPathfinder<T extends IPathfinderPlayer> extends PathfinderBase<T> implements IBlockerPathfinder
{

	public BlockerWaypointPathfinder(PathfindingState<T> pathfindingState, T player)
	{
		super(pathfindingState, player);
	}

	@Override
	public Path calculatePath()
	{
		Path path = getPathfinderState().getPath(getPlayer().getId());
		MessageManager.getInstance().dispatchMessage(PathfindingMessages.drawDebugShape,
				DebugShape.drawPath(this.getPlayerState().getLoc(), path, "#0000FF00"));
		return path;
	}

}
