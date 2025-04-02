package jef.pathfinding.defenders;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.movement.DebugShape;
import jef.movement.player.Path;
import jef.pathfinding.IPathfinderPlayer;
import jef.pathfinding.IPathfinderState;
import jef.pathfinding.PathfinderBase;
import jef.pathfinding.PathfindingMessages;

public class DefenderWaypointPathfinder<T extends IPathfinderPlayer> extends PathfinderBase<T> implements IDefenderPathfinder
{

	public DefenderWaypointPathfinder(IPathfinderState<T> pathfinderState, T player)
	{
		super(pathfinderState, player);
	}

	@Override
	public Path calculatePath()
	{
		Path path = this.getPathfinderState().getPlayerSteps(getPlayer().getId()).getPath();
		MessageManager.getInstance().dispatchMessage(PathfindingMessages.drawDebugShape,
				DebugShape.drawPath(this.getPlayerState().getLoc(), path, "#FF000000"));
		return path;
	}

}
