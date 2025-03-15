package jef.actions.pathfinding.defenders;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.actions.pathfinding.PathfinderBase;
import jef.actions.pathfinding.PlayerStates;
import jef.core.Direction;
import jef.core.Player;
import jef.core.events.Messages;
import jef.core.geometry.LineSegment;
import jef.core.movement.player.Waypoint;

public class DefenderWaypointPathfinder extends PathfinderBase implements DefenderPathfinder
{

	public DefenderWaypointPathfinder(PlayerStates players, Player player, Direction direction)
	{
		super(players, player, direction);
	}


	@Override
	public void calculate()
	{
		Waypoint previousWp = null;
		for (Waypoint wp : this.getSteps().getFirst().getPath().getWaypoints())
		{
			MessageManager.getInstance().dispatchMessage(Messages.drawInterceptorDestination, wp.getDestination());

			if (previousWp == null)
				MessageManager.getInstance().dispatchMessage(Messages.drawInterceptorPath,
						new LineSegment(this.getPlayerState().getLoc(), wp.getDestination()));
			else
				MessageManager.getInstance().dispatchMessage(Messages.drawInterceptorPath,
						new LineSegment(previousWp.getDestination(), wp.getDestination()));
			
			previousWp = wp;
		}
	}

}
