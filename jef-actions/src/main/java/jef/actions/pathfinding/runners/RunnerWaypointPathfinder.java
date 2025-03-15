package jef.actions.pathfinding.runners;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.actions.pathfinding.PathfinderBase;
import jef.actions.pathfinding.PlayerStates;
import jef.core.Direction;
import jef.core.Player;
import jef.core.events.Messages;
import jef.core.geometry.LineSegment;
import jef.core.movement.player.Waypoint;

public class RunnerWaypointPathfinder extends PathfinderBase implements RunnerPathfinder
{

	public RunnerWaypointPathfinder(PlayerStates players, Player player, Direction direction)
	{
		super(players, player, direction);
	}

	@Override
	public void calculate()
	{
		super.calculate();
		
		// this is just for debugging. It can be deleted when finished.
		Waypoint previousWp = null;
		for (Waypoint wp : this.getSteps().getFirst().getPath().getWaypoints())
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

	}	
}
