package jef.actions.pathfinding;

import java.util.List;

import jef.actions.pathfinding.blocking.BlockerPathfinder;
import jef.actions.pathfinding.defenders.DefenderPathfinder;
import jef.actions.pathfinding.runners.RunnerPathfinder;
import jef.core.Direction;
import jef.core.Location;
import jef.core.Performance;
import jef.core.PlayerState;
import jef.core.movement.player.Path;
import jef.core.movement.player.PlayerTracker;
import jef.core.movement.player.Steering;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;

public class DefaultInterceptPlayer extends AbstractPathfinder
{
	public static final int IDEAL_INTERCEPT_TICKS_AHEAD = 2;

	private Pathfinder targetPathfinder;
	private List<Location> interceptionPoints;

	public DefaultInterceptPlayer(PlayerState playerState, Direction direction, Pathfinder targetPathfinder)
	{
		super(playerState, direction);
		this.targetPathfinder = targetPathfinder;
	}

	public Pathfinder getTargetPathfinder()
	{
		return this.targetPathfinder;
	}

	@Override
	public boolean calculate(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders,
			List<? extends BlockerPathfinder> blockers, long deltaNanos)
	{
		boolean ret = normalCalculation(deltaNanos);
		this.calculateSteps(runner, defenders, blockers, deltaNanos);
		return ret;
	}

	private boolean normalCalculation(long deltaNanos)
	{
		if (interceptionPoints == null)
			interceptionPoints = targetPathfinder.getSteps();
		
		for (int i = 0; i < interceptionPoints.size(); i++)
		{
			Location loc = interceptionPoints.get(i);
			PlayerState playerState = getPlayerState().newFrom(null, null, null, new Path(new Waypoint(loc, getPlayerState().getMaxSpeed(), DestinationAction.noStop)), null);
			int ticks = Steering.getInstance().calculateTicks(new PlayerTracker(playerState,
					Performance.frameInterval));
			
			if (ticks - i <= IDEAL_INTERCEPT_TICKS_AHEAD)
			{
				setPath(new Path(new Waypoint(loc, getPlayerState().getMaxSpeed(), DestinationAction.noStop)));
				return true;
			}
		}
		
		setPath(new Path(new Waypoint(targetPathfinder.getPlayerState().getLoc(), getPlayerState().getMaxSpeed(), DestinationAction.noStop)));
		return false;
	}

}
