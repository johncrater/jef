package jef.core.pathfinding;

import java.util.List;

import jef.core.Performance;
import jef.core.Player;
import jef.core.movement.Location;
import jef.core.movement.player.DefaultPath;
import jef.core.movement.player.PlayerTracker;
import jef.core.movement.player.Steering;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;
import jef.core.pathfinding.blocking.BlockerPathfinder;
import jef.core.pathfinding.defenders.DefenderPathfinder;
import jef.core.pathfinding.runners.RunnerPathfinder;

public class DefaultInterceptPlayer extends AbstractPathfinder
{
	public static final int IDEAL_INTERCEPT_TICKS_AHEAD = 2;

	private Pathfinder targetPathfinder;
	private List<Location> interceptionPoints;
//	private Map<Location, Integer> locationToTicks = new HashMap<>();
//	private int minIndex;
//	private int maxIndex;

	public DefaultInterceptPlayer(Player player, Direction direction, Pathfinder targetPathfinder)
	{
		super(player, direction);
		this.targetPathfinder = targetPathfinder;
	}

	@Override
	public void reset()
	{
		super.reset();
		interceptionPoints = null;
//		locationToTicks = new HashMap<>();
//		minIndex = -1;
//		maxIndex = -1;
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
			int ticks = Steering.getInstance().calculateTicks(new PlayerTracker(getPlayer(),
					new DefaultPath(new Waypoint(loc, getPlayer().getMaxSpeed(), DestinationAction.noStop)),
					Performance.frameInterval));
			
			if (ticks - i <= IDEAL_INTERCEPT_TICKS_AHEAD)
			{
				setPath(new DefaultPath(new Waypoint(loc, getPlayer().getMaxSpeed(), DestinationAction.noStop)));
				return true;
			}
		}
		
		setPath(new DefaultPath(new Waypoint(targetPathfinder.getPath().getDestination(), getPlayer().getMaxSpeed(), DestinationAction.noStop)));
		return false;
	}

}
