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
		
		return false;
	}

//	private int calculateTicksToLocation(Location loc)
//	{
//		double distance = getPlayer().getLoc().distanceBetween(loc);
//		
//	}
//	
//	private boolean normalCalculation_old(long deltaNanos)
//	{
//		long nanos = System.nanoTime();
//
//		if (interceptionPoints == null)
//			interceptionPoints = targetPathfinder.getSteps();
//
//		this.minIndex = 0;
//		this.maxIndex = interceptionPoints.size() - 1;
//
//		while (true)
////		while (System.nanoTime() - nanos < deltaNanos)
//		{
//			Location maxLoc = interceptionPoints.get(maxIndex);
//			Integer maxTicks = locationToTicks.get(maxLoc);
//			if (maxTicks == null)
//			{
//				maxTicks = Steering.getInstance().calculateTicks(new PlayerTracker(getPlayer(),
//						new DefaultPath(new Waypoint(maxLoc, getPlayer().getMaxSpeed(), DestinationAction.noStop)),
//						Performance.frameInterval));
//				locationToTicks.put(maxLoc, maxTicks);
//			}
//
//			Location minLoc = interceptionPoints.get(minIndex);
//			Integer minTicks = locationToTicks.get(minLoc);
//			if (minTicks == null)
//			{
//				minTicks = Steering.getInstance().calculateTicks(new PlayerTracker(getPlayer(),
//						new DefaultPath(new Waypoint(minLoc, getPlayer().getMaxSpeed(), DestinationAction.noStop)),
//						Performance.frameInterval));
//				locationToTicks.put(minLoc, minTicks);
//			}
//
//			int centerIndex = minIndex + (maxIndex - minIndex) / 2;
//			Location centerLoc = interceptionPoints.get(centerIndex);
//			Integer centerTicks = locationToTicks.get(centerLoc);
//			if (centerTicks == null)
//			{
//				centerTicks = Steering.getInstance().calculateTicks(new PlayerTracker(getPlayer(),
//						new DefaultPath(new Waypoint(centerLoc, getPlayer().getMaxSpeed(), DestinationAction.noStop)),
//						Performance.frameInterval));
//				locationToTicks.put(centerLoc, centerTicks);
//			}
//
//			int maxTickDiff = maxTicks - maxIndex;
//			int minTickDiff = minTicks - minIndex;
//			int centerTickDiff = centerTicks - centerIndex;
//			
//			if (minTicks == centerTicks || maxTicks == centerTicks)
//			{
//				setPath(new DefaultPath(new Waypoint(centerLoc, getPlayer().getMaxSpeed(), DestinationAction.noStop)));
//				return true;
//			}
//
//			if (Math.abs(centerTicks - centerIndex) <= IDEAL_INTERCEPT_TICKS_AHEAD)
//			{
//				setPath(new DefaultPath(new Waypoint(centerLoc, getPlayer().getMaxSpeed(), DestinationAction.noStop)));
//				return true;
//			}
//
//			if (Math.abs(maxTicks - maxIndex) <= IDEAL_INTERCEPT_TICKS_AHEAD)
//			{
//				setPath(new DefaultPath(new Waypoint(maxLoc, getPlayer().getMaxSpeed(), DestinationAction.noStop)));
//				return true;
//			}
//
//			if (Math.abs(minTicks - minIndex) <= IDEAL_INTERCEPT_TICKS_AHEAD)
//			{
//				setPath(new DefaultPath(new Waypoint(minLoc, getPlayer().getMaxSpeed(), DestinationAction.noStop)));
//				return true;
//			}
//
//			if (centerTickDiff < 0)
//			{
//				maxIndex = centerIndex;
//			}
//			else
//			{
//				minIndex = centerIndex;
//			}
//		}
//
////		return false;
//	}
}
