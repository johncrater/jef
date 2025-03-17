package jef.pathfinding.blocking;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.actions.pathfinding.PathfinderBase;
import jef.actions.pathfinding.PlayerStates;
import jef.core.Direction;
import jef.core.Player;
import jef.core.events.Messages;
import jef.core.geometry.LineSegment;
import jef.core.movement.player.Waypoint;

public class BlockerWaypointPathfinder extends PathfinderBase implements BlockerPathfinder
{

	public BlockerWaypointPathfinder(PlayerStates players, Player player, Direction direction)
	{
		super(players, player, direction);
	}

	@Override
	public void calculate()
	{
		Waypoint previousWp = null;
		for (Waypoint wp : getPath().getWaypoints())
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
	}

}
