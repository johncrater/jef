package jef.pathfinding.blocking;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.core.Player;
import jef.core.events.Messages;
import jef.geometry.Direction;
import jef.movement.DebugShape;
import jef.movement.player.Path;
import jef.pathfinding.PathfinderBase;
import jef.pathfinding.Players;

public class BlockerWaypointPathfinder extends PathfinderBase implements BlockerPathfinder
{

	public BlockerWaypointPathfinder(Players players, Player player, Direction direction)
	{
		super(players, player, direction);
	}

	@Override
	public Path calculatePath()
	{
		Path path = getPlayers().getPath(getPlayer());
		MessageManager.getInstance().dispatchMessage(Messages.drawDebugShape,
				DebugShape.drawPath(this.getPlayerState().getLoc(), path, "#0000FF00"));
		return path;
	}

}
