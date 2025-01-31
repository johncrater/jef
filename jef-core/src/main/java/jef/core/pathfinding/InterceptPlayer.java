package jef.core.pathfinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jef.core.Performance;
import jef.core.Player;
import jef.core.movement.Location;
import jef.core.movement.player.DefaultPath;
import jef.core.movement.player.DefaultSteerable;
import jef.core.movement.player.PlayerTracker;
import jef.core.movement.player.Steering;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;

public class InterceptPlayer extends AbstractPathfinder
{
	private Player player;
	private Player target;
	private RunnerPathfinder runnerPathfinder;
	
	private List<Location> interceptionPoints;
	private Map<Location, Integer> locationToTicks = new HashMap<>();
	private int minIndex;
	private int maxIndex;

	public InterceptPlayer(Player player, RunnerPathfinder runnerPathfinder)
	{
		super();
		this.player = player;
		this.runnerPathfinder = runnerPathfinder;
	}

	@Override
	public void reset()
	{
		super.reset();
		interceptionPoints = null;
		locationToTicks = new HashMap<>();
		minIndex = -1;
		maxIndex = -1;
	}

	@Override
	public boolean calculate()
	{
		if (interceptionPoints == null)
		{
			long nanos = System.nanoTime();
			interceptionPoints = runnerPathfinder.getSteps();
			this.minIndex = 0;
			this.maxIndex = interceptionPoints.size() - 1;
			this.useTime(System.nanoTime() - nanos);
		}
		
		while (this.getTimeRemaining() > 0)
		{
			long nanos = System.nanoTime();

			Location maxLoc = interceptionPoints.get(maxIndex);
			Integer maxTicks = locationToTicks.get(maxLoc);
			if (maxTicks == null)
			{
				DefaultSteerable s = new DefaultSteerable(player, new DefaultPath(new Waypoint(maxLoc, player.getMaxSpeed(), DestinationAction.noStop)));
				maxTicks = Steering.calculateTicks(new PlayerTracker(s, Performance.frameInterval));
				locationToTicks.put(maxLoc, maxTicks);
			}
			
//			this.useTime(System.nanoTime() - nanos);
//			if (this.getTimeRemaining() <= 0)
//				return false;
//			
//			nanos = System.nanoTime();

			if (maxTicks == maxIndex || maxTicks == maxIndex - 1 || maxTicks > maxIndex)
			{
				setPath(new DefaultPath(new Waypoint(maxLoc, player.getMaxSpeed(), DestinationAction.noStop)));
				return true;
			}
			
			Location minLoc = interceptionPoints.get(minIndex);
			Integer minTicks = locationToTicks.get(minLoc);
			if (minTicks == null)
			{
				DefaultSteerable s = new DefaultSteerable(player, new DefaultPath(new Waypoint(minLoc, player.getMaxSpeed(), DestinationAction.noStop)));
				minTicks = Steering.calculateTicks(new PlayerTracker(s, Performance.frameInterval));
				locationToTicks.put(minLoc, minTicks);
			}
			
//			this.useTime(System.nanoTime() - nanos);
//			if (this.getTimeRemaining() <= 0)
//				return false;
//
//			nanos = System.nanoTime();

			if (minTicks == minIndex || minTicks == minIndex - 1)
			{
				setPath(new DefaultPath(new Waypoint(minLoc, player.getMaxSpeed(), DestinationAction.noStop)));
				return true;
			}
			
			int centerIndex = minIndex + (maxIndex - minIndex) / 2;
			Location centerLoc = interceptionPoints.get(centerIndex);
			Integer centerTicks = locationToTicks.get(centerLoc);
			if (centerTicks == null)
			{
				DefaultSteerable s = new DefaultSteerable(player, new DefaultPath(new Waypoint(centerLoc, player.getMaxSpeed(), DestinationAction.noStop)));
				centerTicks = Steering.calculateTicks(new PlayerTracker(s, Performance.frameInterval));
				locationToTicks.put(centerLoc, centerTicks);
			}
			
//			this.useTime(System.nanoTime() - nanos);
//			if (this.getTimeRemaining() <= 0)
//				return false;
//
//			nanos = System.nanoTime();

			if (centerTicks == centerIndex || centerTicks == centerIndex - 1)
			{
				setPath(new DefaultPath(new Waypoint(centerLoc, player.getMaxSpeed(), DestinationAction.noStop)));
				return true;
			}
			
			if (minTicks == centerTicks && maxTicks == centerTicks)
			{
				setPath(new DefaultPath(new Waypoint(centerLoc, player.getMaxSpeed(), DestinationAction.noStop)));
				return true;
			}

//			int minTickDiff = minTicks - minIndex;
			int centerTickDiff = centerTicks - centerIndex;
//			int maxTickDiff = maxTicks - maxIndex;
			
			if (centerTickDiff < 0)
			{
				maxIndex = centerIndex;
			}
			else
			{
				minIndex = centerIndex;
			}

//			this.useTime(System.nanoTime() - nanos);
		}
		
		return false;
	}
}
