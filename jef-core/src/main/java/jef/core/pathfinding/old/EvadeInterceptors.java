package jef.core.pathfinding.old;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.core.DefaultPlayer;
import jef.core.Field;
import jef.core.Performance;
import jef.core.Player;
import jef.core.events.Messages;
import jef.core.geometry.LineSegment;
import jef.core.movement.DefaultLocation;
import jef.core.movement.LinearVelocity;
import jef.core.movement.Location;
import jef.core.movement.RelativeLocation;
import jef.core.movement.player.DefaultPath;
import jef.core.movement.player.Path;
import jef.core.movement.player.PlayerTracker;
import jef.core.movement.player.Steering;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;
import jef.core.pathfinding.Direction;
import jef.core.pathfinding.Pathfinder;

public class EvadeInterceptors implements Pathfinder
{
	private final Player player;
	private final Collection<Player> interceptors;
	private final Collection<Player> blockers;
	private final Direction direction;

	public EvadeInterceptors(final Player player, final Collection<Player> interceptors, final Collection<Player> blockers,
			final Direction direction)
	{
		this.player = player;
		this.interceptors = interceptors;
		this.blockers = blockers;
		this.direction = direction;
	}

	@Override
	public Path getPath()
	{
		return this.getPath(Integer.MAX_VALUE);
	}

	@Override
	public Path getPath(int maximumNanosecondsToSpend)
	{
		List<Player> tmpPlayers = new ArrayList<Player>();
		tmpPlayers.add(player);
		tmpPlayers.addAll(interceptors);
		tmpPlayers = tmpPlayers.stream().sorted((p1, p2) -> (direction == Direction.west ? 1 : -1) * Double.compare(p1.getLoc().getX(), p2.getLoc().getX())).toList();
		if (tmpPlayers.getFirst() == player)
		{
			// if there are no defenders between the runner and the end zone
			Location endZone = new DefaultLocation((direction == Direction.west) ? Field.WEST_END_ZONE_X : Field.EAST_END_ZONE_X, player.getLoc().getY());
			MessageManager.getInstance().dispatchMessage(Messages.drawRunnerDestination, endZone);
			MessageManager.getInstance().dispatchMessage(Messages.drawRunnerPath, new LineSegment(player.getLoc(), endZone));
			return new DefaultPath(new Waypoint(endZone, this.player.getMaxSpeed(), DestinationAction.noStop));
		}
		
		final HashSet<LineSegment> segments = this.getBoundingLines(player, interceptors);
		segments.addAll(
				Arrays.asList(Field.EAST_END_ZONE, Field.WEST_END_ZONE, Field.NORTH_SIDELINE, Field.SOUTH_SIDELINE));

		for (final Player interceptor : this.interceptors)
			segments.addAll(this.getBoundingLines(interceptor, this.blockers));

		Set<LineSegment> boundingLines = Collections.unmodifiableSet(this.splitLines(segments));

		boundingLines.stream().forEach(s -> MessageManager.getInstance().dispatchMessage(Messages.drawEvasionBoundingSegments, s));

		Set<Location> locationsOfIntersection = Collections
				.unmodifiableSet(this.getPointsOfIntersection(boundingLines));

		locationsOfIntersection.stream().forEach(s -> MessageManager.getInstance().dispatchMessage(Messages.drawEvasionIntersections, s));

		final Set<Location> interceptorReachableLocations = new HashSet<>();
		for (final Player interceptor : this.interceptors)
			interceptorReachableLocations
					.addAll(this.getReachableLocations(interceptor, locationsOfIntersection, boundingLines));

		interceptorReachableLocations.stream().forEach(s -> MessageManager.getInstance().dispatchMessage(Messages.drawEvasionInterceptorReachableLocations, s));

		final var commonReachableLines = this.removeObsoleteLines(interceptorReachableLocations, boundingLines);
		commonReachableLines.stream().forEach(s -> MessageManager.getInstance().dispatchMessage(Messages.drawEvasionCommonReachableLines, s));

		final var commonReachableLocations = this.getReachableLocations(this.player, interceptorReachableLocations,
				commonReachableLines);

		commonReachableLocations.stream().forEach(s -> MessageManager.getInstance().dispatchMessage(Messages.drawEvasionCommonReachableLocations, s));

		Location destination = this.getFarthestReachableLocation(this.player, commonReachableLocations);
		if (destination == null)
			return null;

		MessageManager.getInstance().dispatchMessage(Messages.drawRunnerDestination, destination);
		MessageManager.getInstance().dispatchMessage(Messages.drawRunnerPath, new LineSegment(player.getLoc(), destination));

		return new DefaultPath(new Waypoint(destination, this.player.getMaxSpeed(), DestinationAction.noStop));
	}

	private Location getFarthestReachableLocation(final Player player, final Set<Location> locations)
	{
		return locations.stream().sorted((l1, l2) ->
		{
			double l1x = l1.getX();
			double l2x = l2.getX();

			var ret = (direction == Direction.east ? -1 : 1) * Double.compare(l1x, l2x);
			if (ret == 0)
 				ret = Double.compare(player.getLoc().distanceBetween(l1), player.getLoc().distanceBetween(l2));
			return ret;
		}).toList().getFirst();
	}

	private Location getFastestReachableLocation(final Player player, final Set<Location> locations)
	{
		Map<Location, PlayerTracker> locationToTracker = new HashMap<>();
		locations.forEach(l -> 
		{
			if (RelativeLocation.getFromAngle(player.getLoc().angleTo(l), direction).isBehind())
				return;

			Player p = new DefaultPlayer(player);
			p.setPath(new DefaultPath(new Waypoint(l, player.getMaxSpeed(), DestinationAction.noStop)));
			PlayerTracker tracker = new PlayerTracker(p, Performance.frameInterval);
			locationToTracker.put(l, tracker);
		});

		while (true)
		{
			for (Location loc : locationToTracker.keySet())
			{
				PlayerTracker tracker = locationToTracker.get(loc);
				tracker.setPctRemaining(1);
				Steering steering = new Steering(tracker);
				steering.next(tracker);
				if (tracker.getPath().getCurrentWaypoint() == null)
					return loc;
			}
		}
		
	}

	private Set<LineSegment> removeObsoleteLines(final Set<Location> reachableLocations, final Set<LineSegment> boundingLines)
	{
		return new HashSet<>(boundingLines.stream()
				.filter(s -> reachableLocations.contains(s.getLoc1()) && reachableLocations.contains(s.getLoc2()))
				.toList());
	}

	private Set<Location> getReachableLocations(final Player player, final Set<Location> locationsOfIntersection,
			final Set<LineSegment> boundingLines)
	{
		final var ret = new HashSet<Location>();

		for (final Location l : locationsOfIntersection)
		{
			final LineSegment lsToPoi = new LineSegment(player.getLoc(), l);
			for (final LineSegment ls : boundingLines)
			{
				final Location poi = lsToPoi.xyIntersection(ls);
				if (poi == null || poi.isInPlayableArea() == false || ls.intersects(poi) == false
						|| lsToPoi.intersects(poi) == false || poi.closeEnoughTo(l) || player.getLoc().equals(poi))
					continue;

				ret.add(l);
				break;
			}
		}

		final var tmp = new HashSet<>(locationsOfIntersection);
		tmp.removeAll(ret);
		return tmp;
	}

	private HashSet<Location> getPointsOfIntersection(final Set<LineSegment> boundingLines)
	{
		final var ret = new HashSet<Location>();

		for (final LineSegment s : boundingLines)
		{
			ret.add(s.getLoc1());
			ret.add(s.getLoc2());
		}

		return ret;
	}

	private HashSet<LineSegment> getBoundingLines(Player p1, Collection<Player> p2)
	{
		return new HashSet<>(p2.stream().map(p ->
		{
			double ratio = .5;
			final double denom = this.player.getLV().getSpeed() + p.getLV().getSpeed();
			if (denom != 0)
			{
				ratio = this.player.getLV().getSpeed() / denom;
			}

			final LineSegment seg = new LineSegment(p1.getLoc(), p.getLoc());
			LinearVelocity segLV = seg.getDirection();
			Location pointAlong = seg.getPoint(ratio);

			// make it longer than field distances and then chop it down to size to make
			// sure it fits
			return new LineSegment(pointAlong.add(segLV.add(-Math.PI / 2, 0, 200)),
					pointAlong.add(segLV.add(Math.PI / 2, 0, 200))).restrictToBetweenEndZones();
		}).filter(l -> l != null).toList());
	}

	private Set<LineSegment> splitLines(final Set<LineSegment> segments)
	{
		final var segmentsQueue = new HashSet<>(segments);
		final var ret = new HashSet<LineSegment>();

		while (segmentsQueue.size() > 0)
		{
			LineSegment s1 = segmentsQueue.iterator().next();
			segmentsQueue.remove(s1);

			boolean found = false;
			for (final LineSegment s2 : segments)
			{
				if (s1.equals(s2))
					continue;

				final Location intersection = s1.xyIntersection(s2);
				if (intersection != null && s1.intersects(intersection) && s2.intersects(intersection))
				{
					if (intersection.equals(s1.getLoc1()) || intersection.equals(s1.getLoc2()))
						continue;

					final var newS1 = new LineSegment(s1.getLoc1(), intersection);
					final var newS2 = new LineSegment(s1.getLoc2(), intersection);

					segmentsQueue.add(newS1);
					segmentsQueue.add(newS2);
					found = true;
					break;
				}
			}

			if (!found)
				ret.add(s1);
		}

		return ret;
	}

}
