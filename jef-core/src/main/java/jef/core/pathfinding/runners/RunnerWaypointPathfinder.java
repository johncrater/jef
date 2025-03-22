package jef.core.pathfinding.runners;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.core.Direction;
import jef.core.IPlayers;
import jef.core.Player;
import jef.core.events.DebugShape;
import jef.core.events.Messages;
import jef.core.movement.player.Path;
import jef.core.pathfinding.PathfinderBase;

public class RunnerWaypointPathfinder extends PathfinderBase implements RunnerPathfinder
{

	public RunnerWaypointPathfinder(IPlayers players, Player player, Direction direction)
	{
		super(players, player, direction);
	}

	@Override
	public Path calculatePath()
	{
		// this is just for debugging. It can be deleted when finished.
		Path path = this.getPlayers().getSteps(getPlayer()).getPath();
		MessageManager.getInstance().dispatchMessage(Messages.drawDebugShape, DebugShape.drawPath(this.getPlayerState().getLoc(), path, "#00FF0000"));
		return path;
	}	
}
