package jef.actions.pathfinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.actions.pathfinding.blocking.BlockerPathfinder;
import jef.actions.pathfinding.defenders.DefenderPathfinder;
import jef.actions.pathfinding.runners.RunnerPathfinder;
import jef.core.Direction;
import jef.core.Location;
import jef.core.Performance;
import jef.core.PlayerState;
import jef.core.events.Messages;
import jef.core.geometry.LineSegment;
import jef.core.movement.player.Path;
import jef.core.movement.player.PlayerTracker;
import jef.core.movement.player.Steering;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;

public class AdvancedInterceptPlayer extends PathfinderBase
{
	public static final int IDEAL_INTERCEPT_TICKS_AHEAD = 2;

	private Pathfinder targetPathfinder;
	private List<Location> interceptionPoints;
	private Map<Location, Integer> locationToTicks = new HashMap<>();
	private int minIndex;
	private int maxIndex;

	public AdvancedInterceptPlayer(PlayerState playerState, Direction direction, Pathfinder targetPathfinder)
	{
		super(playerState, direction);
		this.targetPathfinder = targetPathfinder;
	}

	public Pathfinder getTargetPathfinder()
	{
		return this.targetPathfinder;
	}

	private boolean exhaustiveCalculation(long deltaNanos)
	{
		long nanos = System.nanoTime();

		if (interceptionPoints == null)
			interceptionPoints = targetPathfinder.getSteps();

		if (interceptionPoints == null || interceptionPoints.size() == 0)
			return false;

		this.minIndex = 0;
		this.maxIndex = interceptionPoints.size() - 1;

		int selectedIndex = 0;
		int selectedTicks = Integer.MAX_VALUE;
		for (int i = minIndex; i <= maxIndex; i++)
		{
			Location loc = interceptionPoints.get(i);
			Integer ticks = locationToTicks.get(loc);
			if (ticks == null)
			{
				PlayerState playerState = this.getPlayerState().newFrom(null, null, null,
						new Path(new Waypoint(loc, this.getPlayerState().getPlayer().getSpeedMatrix().getJoggingSpeed(),
								getPlayerState().getMaxSpeed(), DestinationAction.noStop)),
						null);
				ticks = Steering.getInstance()
						.calculateTicks(new PlayerTracker(getPlayerState(), Performance.frameInterval));
				locationToTicks.put(loc, ticks);
			}

			// negative tickDiff means interceptor arrives before target
			int tickDiff = ticks - i;
			if ((selectedTicks > -IDEAL_INTERCEPT_TICKS_AHEAD && tickDiff < selectedTicks)
					|| (selectedTicks < -IDEAL_INTERCEPT_TICKS_AHEAD && tickDiff <= -IDEAL_INTERCEPT_TICKS_AHEAD
							&& tickDiff > selectedTicks))
			{
				selectedTicks = tickDiff;
				selectedIndex = i;
			}
		}

		Location interceptionPoint = this.interceptionPoints.get(selectedIndex);
		setPath(new Path(new Waypoint(interceptionPoint, this.getPlayerState().getPlayer().getSpeedMatrix().getJoggingSpeed(),
				getPlayerState().getMaxSpeed(), DestinationAction.noStop)));

		MessageManager.getInstance().dispatchMessage(Messages.drawInterceptorPath, interceptionPoint);
		MessageManager.getInstance().dispatchMessage(Messages.drawInterceptorDestination,
				new LineSegment(getPlayerState().getLoc(), interceptionPoint));

		return true;
	}

	@Override
	public boolean calculate(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders,
			List<? extends BlockerPathfinder> blockers, long deltaNanos)
	{
		boolean ret = exhaustiveCalculation(deltaNanos);
		this.calculateSteps(runner, defenders, blockers, deltaNanos);
		return ret;
	}

//	private boolean normalCalculation(long deltaNanos)
//	{
//		long nanos = System.nanoTime();
//
//		if (interceptionPoints == null)
//			interceptionPoints = targetPathfinder.getSteps();
//
//		this.minIndex = 0;
//		this.maxIndex = interceptionPoints.size() - 1;
//
////		while (System.nanoTime() - nanos < deltaNanos)
//		{
//			Location maxLoc = interceptionPoints.get(maxIndex);
//			Integer maxTicks = locationToTicks.get(maxLoc);
//			if (maxTicks == null)
//			{
//				maxTicks = Steering.calculateTicks(new PlayerTracker(getPlayer(),
//						new DefaultPath(new Waypoint(maxLoc, getPlayer().getMaxSpeed(), DestinationAction.noStop)),
//						Performance.frameInterval));
//				locationToTicks.put(maxLoc, maxTicks);
//			}
//
////			if (Math.abs(maxTicks - maxIndex) <= IDEAL_INTERCEPT_TICKS_AHEAD || maxTicks > maxIndex)
////			{
////				setPath(new DefaultPath(new Waypoint(maxLoc, getPlayer().getMaxSpeed(), DestinationAction.noStop)));
////				return true;
////			}
////
//			Location minLoc = interceptionPoints.get(minIndex);
//			Integer minTicks = locationToTicks.get(minLoc);
//			if (minTicks == null)
//			{
//				minTicks = Steering.calculateTicks(new PlayerTracker(getPlayer(),
//						new DefaultPath(new Waypoint(minLoc, getPlayer().getMaxSpeed(), DestinationAction.noStop)),
//						Performance.frameInterval));
//				locationToTicks.put(minLoc, minTicks);
//			}
//
////			if (Math.abs(minTicks - minIndex) <= IDEAL_INTERCEPT_TICKS_AHEAD)
////			{
////				setPath(new DefaultPath(new Waypoint(minLoc, getPlayer().getMaxSpeed(), DestinationAction.noStop)));
////				return true;
////			}
//
//			int centerIndex = minIndex + (maxIndex - minIndex) / 2;
//			Location centerLoc = interceptionPoints.get(centerIndex);
//			Integer centerTicks = locationToTicks.get(centerLoc);
//			if (centerTicks == null)
//			{
//				centerTicks = Steering.calculateTicks(new PlayerTracker(getPlayer(),
//						new DefaultPath(new Waypoint(centerLoc, getPlayer().getMaxSpeed(), DestinationAction.noStop)),
//						Performance.frameInterval));
//				locationToTicks.put(centerLoc, centerTicks);
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
//			if (minTicks == centerTicks || maxTicks == centerTicks)
//			{
//				setPath(new DefaultPath(new Waypoint(centerLoc, getPlayer().getMaxSpeed(), DestinationAction.noStop)));
//				return true;
//			}
//
////			int minTickDiff = minTicks - minIndex;
//			int centerTickDiff = centerTicks - centerIndex;
////			int maxTickDiff = maxTicks - maxIndex;
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
//		return false;
//	}
}
