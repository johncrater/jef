package jef.pathfinding.defenders;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.core.Direction;
import jef.core.Player;
import jef.core.events.Messages;
import jef.core.geometry.LineSegment;
import jef.core.movement.player.Path;
import jef.core.movement.player.Waypoint;
import jef.pathfinding.PathfinderBase;
import jef.pathfinding.Players;

public class DefenderWaypointPathfinder extends PathfinderBase implements DefenderPathfinder
{

	public DefenderWaypointPathfinder(Players players, Player player, Direction direction)
	{
		super(players, player, direction);
	}


	@Override
	public Path calculatePath()
	{
		Path path = this.getPlayers().getSteps(getPlayer()).getPath();
		Waypoint previousWp = null;
		for (Waypoint wp : path.getWaypoints())
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
		
		return path;
	}

}
