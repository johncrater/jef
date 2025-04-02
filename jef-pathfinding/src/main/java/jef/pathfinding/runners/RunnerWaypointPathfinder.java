package jef.pathfinding.runners;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.movement.DebugShape;
import jef.movement.player.Path;
import jef.pathfinding.IPathfinderPlayer;
import jef.pathfinding.IPathfinderState;
import jef.pathfinding.PathfinderBase;
import jef.pathfinding.PathfindingMessages;

public class RunnerWaypointPathfinder<T extends IPathfinderPlayer> extends PathfinderBase<T> implements IRunnerPathfinder
{

	public RunnerWaypointPathfinder(IPathfinderState<T> pathfinderState, T player)
	{
		super(pathfinderState, player);
	}

	@Override
	public Path calculatePath()
	{
		// this is just for debugging. It can be deleted when finished.
		Path path = this.getPathfinderState().getPlayerSteps(getPlayer().getId()).getPath();
		MessageManager.getInstance().dispatchMessage(PathfindingMessages.drawDebugShape, DebugShape.drawPath(this.getPlayerState().getLoc(), path, "#00FF0000"));
		return path;
	}	
}
