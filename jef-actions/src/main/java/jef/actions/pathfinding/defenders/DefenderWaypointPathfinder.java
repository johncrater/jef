package jef.actions.pathfinding.defenders;

import java.util.List;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.actions.pathfinding.WaypointPathfinder;
import jef.actions.pathfinding.blocking.BlockerPathfinder;
import jef.actions.pathfinding.runners.RunnerPathfinder;
import jef.core.Direction;
import jef.core.Player;
import jef.core.events.Messages;
import jef.core.geometry.LineSegment;
import jef.core.movement.player.Waypoint;

public class DefenderWaypointPathfinder extends WaypointPathfinder implements DefenderPathfinder
{

	public DefenderWaypointPathfinder(Player player, Direction direction)
	{
		super(player, direction);
		// TODO Auto-generated constructor stub
	}


	@Override
	public boolean calculate(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders,
			List<? extends BlockerPathfinder> blockers, long deltaNanos)
	{
		Waypoint previousWp = null;
		for (Waypoint wp : getPath().getWaypoints())
		{
			MessageManager.getInstance().dispatchMessage(Messages.drawInterceptorDestination, wp.getDestination());

			if (previousWp == null)
				MessageManager.getInstance().dispatchMessage(Messages.drawInterceptorPath,
						new LineSegment(this.getPlayer().getLoc(), wp.getDestination()));
			else
				MessageManager.getInstance().dispatchMessage(Messages.drawInterceptorPath,
						new LineSegment(previousWp.getDestination(), wp.getDestination()));
			
			previousWp = wp;
		}

		return true;
	}

}
