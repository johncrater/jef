package jef.core.pathfinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.core.Performance;
import jef.core.Player;
import jef.core.events.Messages;
import jef.core.geometry.LineSegment;
import jef.core.movement.Location;
import jef.core.movement.player.DefaultPath;
import jef.core.movement.player.PlayerTracker;
import jef.core.movement.player.Steering;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;

public class InterceptPlayer extends AbstractPathfinder
{
	private RunnerPathfinder runnerPathfinder;

	private List<Location> interceptionPoints;
	private Map<Location, Integer> locationToTicks = new HashMap<>();
	private int minIndex;
	private int maxIndex;

	public InterceptPlayer(Player player, RunnerPathfinder runnerPathfinder)
	{
		super(player);
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

	private boolean exhaustiveCalculation()
	{
		if (interceptionPoints == null)
			interceptionPoints = runnerPathfinder.getSteps();

		if (interceptionPoints == null)
			return false;
		
		this.minIndex = 0;
		this.maxIndex = interceptionPoints.size() - 1;

		int minIndex = 0;
		int minTicks = Integer.MAX_VALUE;
		for (int i = minIndex; i <= maxIndex; i++)
		{
			Location loc = interceptionPoints.get(i);
			Integer ticks = locationToTicks.get(loc);
			if (ticks == null)
			{
				ticks = Steering.calculateTicks(new PlayerTracker(getPlayer(),
						new DefaultPath(new Waypoint(loc, getPlayer().getMaxSpeed(), DestinationAction.noStop)),
						Performance.frameInterval));
				locationToTicks.put(loc, ticks);
			}

			int tickDiff = ticks - i;
			if ((minTicks > 0 && tickDiff < minTicks) || (minTicks < 0 && tickDiff <= 0 && tickDiff > minTicks))
			{
				minTicks = tickDiff;
				minIndex = i;
			}
		}
		
		Location interceptionPoint = this.interceptionPoints.get(minIndex);
		setPath(new DefaultPath(new Waypoint(interceptionPoint, getPlayer().getMaxSpeed(), DestinationAction.noStop)));
		MessageManager.getInstance().dispatchMessage(Messages.drawInterceptorPath, interceptionPoint);
		MessageManager.getInstance().dispatchMessage(Messages.drawInterceptorDestination, new LineSegment(getPlayer().getLoc(), interceptionPoint));
		
		return true;
	}
	
	@Override
	public boolean calculate()
	{
		return exhaustiveCalculation();
	}
	
	private boolean normalCalculation()
	{
		if (interceptionPoints == null)
		{
			long nanos = System.nanoTime();
			interceptionPoints = runnerPathfinder.getSteps();
//			this.useTime(System.nanoTime() - nanos);
		}

		this.minIndex = 0;
		this.maxIndex = interceptionPoints.size() - 1;

		while (this.getTimeRemaining() > 0)
		{
			long nanos = System.nanoTime();

			Location maxLoc = interceptionPoints.get(maxIndex);
			Integer maxTicks = locationToTicks.get(maxLoc);
			if (maxTicks == null)
			{
				maxTicks = Steering.calculateTicks(new PlayerTracker(getPlayer(),
						new DefaultPath(new Waypoint(maxLoc, getPlayer().getMaxSpeed(), DestinationAction.noStop)),
						Performance.frameInterval));
				locationToTicks.put(maxLoc, maxTicks);
			}

//			this.useTime(System.nanoTime() - nanos);
//			if (this.getTimeRemaining() <= 0)
//				return false;
//			
//			nanos = System.nanoTime();

//			if (Math.abs(maxTicks - maxIndex) <= 1 || maxTicks > maxIndex)
//			{
//				setPath(new DefaultPath(new Waypoint(maxLoc, getPlayer().getMaxSpeed(), DestinationAction.noStop)));
//				return true;
//			}
//
			Location minLoc = interceptionPoints.get(minIndex);
			Integer minTicks = locationToTicks.get(minLoc);
			if (minTicks == null)
			{
				minTicks = Steering.calculateTicks(new PlayerTracker(getPlayer(),
						new DefaultPath(new Waypoint(minLoc, getPlayer().getMaxSpeed(), DestinationAction.noStop)),
						Performance.frameInterval));
				locationToTicks.put(minLoc, minTicks);
			}

//			this.useTime(System.nanoTime() - nanos);
//			if (this.getTimeRemaining() <= 0)
//				return false;
//
//			nanos = System.nanoTime();

//			if (Math.abs(minTicks - minIndex) <= 1)
//			{
//				setPath(new DefaultPath(new Waypoint(minLoc, getPlayer().getMaxSpeed(), DestinationAction.noStop)));
//				return true;
//			}

			int centerIndex = minIndex + (maxIndex - minIndex) / 2;
			Location centerLoc = interceptionPoints.get(centerIndex);
			Integer centerTicks = locationToTicks.get(centerLoc);
			if (centerTicks == null)
			{
				centerTicks = Steering.calculateTicks(new PlayerTracker(getPlayer(),
						new DefaultPath(new Waypoint(centerLoc, getPlayer().getMaxSpeed(), DestinationAction.noStop)),
						Performance.frameInterval));
				locationToTicks.put(centerLoc, centerTicks);
			}

//			this.useTime(System.nanoTime() - nanos);
//			if (this.getTimeRemaining() <= 0)
//				return false;
//
//			nanos = System.nanoTime();

			if (Math.abs(centerTicks - centerIndex) <= 1)
			{
				setPath(new DefaultPath(new Waypoint(centerLoc, getPlayer().getMaxSpeed(), DestinationAction.noStop)));
				return true;
			}

			if (Math.abs(maxTicks - maxIndex) <= 1)
			{
				setPath(new DefaultPath(new Waypoint(maxLoc, getPlayer().getMaxSpeed(), DestinationAction.noStop)));
				return true;
			}

			if (Math.abs(minTicks - minIndex) <= 1)
			{
				setPath(new DefaultPath(new Waypoint(minLoc, getPlayer().getMaxSpeed(), DestinationAction.noStop)));
				return true;
			}

			if (minTicks == centerTicks || maxTicks == centerTicks)
			{
				setPath(new DefaultPath(new Waypoint(centerLoc, getPlayer().getMaxSpeed(), DestinationAction.noStop)));
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
