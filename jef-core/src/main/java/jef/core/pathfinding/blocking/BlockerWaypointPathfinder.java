package jef.pathfinding.blocking;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.Players;
import jef.core.Direction;
import jef.core.Player;
import jef.core.events.DebugShape;
import jef.core.events.Messages;
import jef.core.geometry.LineSegment;
import jef.core.movement.player.Path;
import jef.core.movement.player.Waypoint;
import jef.pathfinding.PathfinderBase;

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
