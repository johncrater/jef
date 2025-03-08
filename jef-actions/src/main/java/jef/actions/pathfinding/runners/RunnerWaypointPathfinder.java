package jef.actions.pathfinding.runners;

import java.util.List;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.actions.pathfinding.WaypointPathfinder;
import jef.actions.pathfinding.blocking.BlockerPathfinder;
import jef.actions.pathfinding.defenders.DefenderPathfinder;
import jef.core.Direction;
import jef.core.PlayerState;
import jef.core.events.Messages;
import jef.core.geometry.LineSegment;
import jef.core.movement.player.Waypoint;

public class RunnerWaypointPathfinder extends WaypointPathfinder implements RunnerPathfinder
{

	public RunnerWaypointPathfinder(PlayerState playerState, Direction direction)
	{
		super(playerState, direction);
	}

	@Override
	public boolean calculate(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders,
			List<? extends BlockerPathfinder> blockers, long deltaNanos)
	{
		Waypoint previousWp = null;
		for (Waypoint wp : getPath().getWaypoints())
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

		return this.calculateSteps(runner, defenders, blockers, deltaNanos);
	}	
}
