package jef.core.pathfinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jef.core.Field;
import jef.core.Player;
import jef.core.geometry.Line;
import jef.core.movement.Location;
import jef.core.movement.player.DefaultPath;
import jef.core.movement.player.Path;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;

public class EvadeInterceptors implements Pathfinder
{
	private final Player player;
	private final List<Player> interceptors;
	private final List<Player> blockers;
	private final Direction direction;

	public EvadeInterceptors(final Player player, final List<Player> interceptors, final List<Player> blockers,
			final Direction direction)
	{
		this.player = player;
		this.interceptors = interceptors;
		this.blockers = blockers;
		this.direction = direction;
	}

	@Override
	public Path findPath()
	{
		return this.findPath(Double.MAX_VALUE);
	}

	@Override
	public Path findPath(final double maximumTimeToSpend)
	{
		final var segments = this.getBoundingLines(player, interceptors);
		segments.addAll(
				Arrays.asList(Field.EAST_END_ZONE, Field.WEST_END_ZONE, Field.NORTH_SIDELINE, Field.SOUTH_SIDELINE));

		for (final Player interceptor : this.interceptors)
		{
			segments.addAll(this.getBoundingLines(interceptor, this.blockers));
		}

		Set<Line> boundingLines = Collections.unmodifiableSet(this.splitLines(segments));
		Set<Location> locationsOfIntersection = Collections.unmodifiableSet(this.getPointsOfIntersection(boundingLines));

		List<Location> candidateLocations = new ArrayList<>();
		Location destination = null;

		final Set<Location> interceptorReachableLocations = new HashSet<>();
		for (final Player interceptor : this.interceptors)
			interceptorReachableLocations
					.addAll(this.getReachableLocations(interceptor, locationsOfIntersection, boundingLines));

		final var commonReachableLines = this.removeObsoleteLines(interceptorReachableLocations,
				boundingLines);
		final var commonReachableLocations = this.getReachableLocations(this.player, interceptorReachableLocations,
				commonReachableLines);

		candidateLocations = this.rateReachableLocations(this.player, commonReachableLocations);
		if (candidateLocations.size() > 0)
			destination = candidateLocations.get(0);

//		final var playerReachableLocations = this.getReachableLocations(this.player, this.locationsOfIntersection,
//				this.boundingLines);

		if (destination == null)
			return null;
			
		return new DefaultPath(new Waypoint(destination, this.player.getMaxSpeed(), DestinationAction.noStop));
	}

	private List<Location> rateReachableLocations(final Player player, final Set<Location> locations)
	{
		return locations.stream().sorted((l1, l2) ->
		{
			var ret = -1 * Double.compare(l1.getY(), l2.getY());
			if (ret == 0)
				ret = Double.compare(player.getLoc().distanceBetween(l1), player.getLoc().distanceBetween(l2));
			return ret;
		}).toList();
	}

	private Set<Line> removeObsoleteLines(final Set<Location> reachableLocations,
			final Set<Line> boundingLines)
	{
		return new HashSet<>(boundingLines.stream()
				.filter(s -> reachableLocations.contains(s.getLoc1()) && reachableLocations.contains(s.getLoc2())).toList());
	}

	private Set<Location> getReachableLocations(final Player player, final Set<Location> locationsOfIntersection,
			final Set<Line> boundingLines)
	{
		final var ret = new HashSet<Location>();

		for (final Location l : locationsOfIntersection)
		{
			final Line lsToPoi = new Line(player.getLoc(), l);
			for (final Line ls : boundingLines)
			{
				final Location poi = lsToPoi.intersects(ls);
				if ((poi == null) || poi.closeEnoughTo(l))
					continue;

				ret.add(l);
				break;
			}
		}

		final var tmp = new HashSet<>(locationsOfIntersection);
		tmp.removeAll(ret);
		return tmp;
	}

	private HashSet<Location> getPointsOfIntersection(final Set<Line> boundingLines)
	{
		final var ret = new HashSet<Location>();

		for (final Line s : boundingLines)
		{
			ret.add(s.getLoc1());
			ret.add(s.getLoc2());
		}

		return ret;
	}

	private HashSet<Line> getBoundingLines(Player p1, List<Player> p2)
	{
		return new HashSet<>(p2.stream().map(p ->
		{
			double ratio = .5;
			final double denom = this.player.getLV().getSpeed() + p.getLV().getSpeed();
			if (denom != 0)
			{
				ratio = this.player.getLV().getSpeed() / denom;
			}

			final Line seg = new Line(p1.getLoc(), p.getLoc());
			Location pointAlong = seg.getPoint(ratio);
			return new Line(pointAlong, seg.getLoc1());
		}).toList());
	}

	private Set<Line> splitLines(final Set<Line> segments)
	{
		final var segmentsQueue = new HashSet<>(segments);
		final var ret = new HashSet<Line>();

		while (segmentsQueue.size() > 0)
		{
			Line s1 = segmentsQueue.iterator().next();
			segmentsQueue.remove(s1);

			boolean found = false;
			for (final Line s2 : segments)
			{
				if (s1.equals(s2))
					continue;

				final Location intersection = s1.intersects(s2);
				if (intersection != null)
				{
					if (intersection.equals(s1.getLoc1()) || intersection.equals(s1.getLoc2()))
						continue;

					final var newS1 = new Line(s1.getLoc1(), intersection);
					final var newS2 = new Line(s1.getLoc2(), intersection);

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
