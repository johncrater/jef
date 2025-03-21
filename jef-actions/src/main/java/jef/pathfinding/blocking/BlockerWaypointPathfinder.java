package jef.pathfinding.blocking;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.Players;
import jef.core.Direction;
import jef.core.Player;
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
		Waypoint previousWp = null;
		for (Waypoint wp : getPlayers().getPath(getPlayer()).getWaypoints())
		{
			MessageManager.getInstance().dispatchMessage(Messages.drawBlockerDestination, wp.getDestination());

			if (previousWp == null)
				MessageManager.getInstance().dispatchMessage(Messages.drawBlockerPath,
						new LineSegment(this.getPlayerState().getLoc(), wp.getDestination()));
			else
				MessageManager.getInstance().dispatchMessage(Messages.drawBlockerPath,
						new LineSegment(previousWp.getDestination(), wp.getDestination()));
			
			previousWp = wp;
		}
		
		return null;
	}

}
