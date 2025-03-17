package jef.pathfinding.runners;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.core.Direction;
import jef.core.Player;
import jef.core.events.Messages;
import jef.core.geometry.LineSegment;
import jef.core.movement.player.Path;
import jef.core.movement.player.Waypoint;
import jef.pathfinding.PathfinderBase;
import jef.pathfinding.Players;

public class RunnerWaypointPathfinder extends PathfinderBase implements RunnerPathfinder
{

	public RunnerWaypointPathfinder(Players players, Player player, Direction direction)
	{
		super(players, player, direction);
	}

	@Override
	public Path calculatePath()
	{
		// this is just for debugging. It can be deleted when finished.
		Waypoint previousWp = null;
		Path path = this.getPlayers().getSteps(getPlayer()).getPath();
		for (Waypoint wp : path.getWaypoints())
		{
			MessageManager.getInstance().dispatchMessage(Messages.drawRunnerDestination, wp.getDestination());

			if (previousWp == null)
				MessageManager.getInstance().dispatchMessage(Messages.drawRunnerPath,
						new LineSegment(this.getPlayerState().getLoc(), wp.getDestination()));
			else
				MessageManager.getInstance().dispatchMessage(Messages.drawRunnerPath,
						new LineSegment(previousWp.getDestination(), wp.getDestination()));
			
			previousWp = wp;
		}

		return path;
	}	
}
