package jef.core.pathfinding.blocking;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.core.Direction;
import jef.core.Player;
import jef.core.Players;
import jef.core.events.DebugShape;
import jef.core.events.Messages;
import jef.core.movement.player.Path;
import jef.core.pathfinding.PathfinderBase;

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
