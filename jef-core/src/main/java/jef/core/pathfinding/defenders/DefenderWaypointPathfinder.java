package jef.core.pathfinding.defenders;

import java.util.List;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.core.Player;
import jef.core.events.Messages;
import jef.core.geometry.LineSegment;
import jef.core.movement.player.Waypoint;
import jef.core.pathfinding.Direction;
import jef.core.pathfinding.WaypointPathfinder;
import jef.core.pathfinding.blocking.BlockerPathfinder;
import jef.core.pathfinding.runners.RunnerPathfinder;

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
			MessageManager.getInstance().dispatchMessage(Messages.drawIntercepterDestination, wp.getDestination());

			if (previousWp == null)
				MessageManager.getInstance().dispatchMessage(Messages.drawIntercepterPath,
						new LineSegment(this.getPlayer().getLoc(), wp.getDestination()));
			else
				MessageManager.getInstance().dispatchMessage(Messages.drawIntercepterPath,
						new LineSegment(previousWp.getDestination(), wp.getDestination()));
			
			previousWp = wp;
		}

		return true;
	}

}
