package jef.core.pathfinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;

import jef.core.Conversions;
import jef.core.Field;
import jef.core.Geometry;
import jef.core.Player;
import jef.core.movement.DefaultLocation;
import jef.core.movement.Location;
import jef.core.movement.player.DefaultPath;
import jef.core.movement.player.Path;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;

public class EvadePlayer implements Pathfinder
{
	private Set<LineSegment> boundingSegments;
	private Set<Location> locationsOfIntersection;

	private final Player target;
	private final List<Player> interceptors;
	private final List<Player> blockers;
	private final Direction direction;

	public EvadePlayer(final Player target, final List<Player> interceptors, final List<Player> blockers,
			final Direction direction)
	{
		this.target = target;
		this.interceptors = interceptors;
		this.blockers = blockers;
		this.direction = direction;

		final var segments = this.getBoundingSegments(target, interceptors);
		segments.addAll(
				Arrays.asList(Field.EAST_END_ZONE, Field.WEST_END_ZONE, Field.NORTH_SIDELINE, Field.SOUTH_SIDELINE));

		for (final Player interceptor : this.interceptors)
		{
			segments.addAll(this.getBoundingSegments(interceptor, this.blockers));
		}

		this.boundingSegments = Collections.unmodifiableSet(this.splitSegments(segments));

		this.locationsOfIntersection = Collections.unmodifiableSet(this.getPointsOfIntersection(this.boundingSegments));

	}

	@Override
	public Path findPath()
	{
		return this.findPath(Double.MAX_VALUE);
	}

	@Override
	public Path findPath(final double maximumTimeToSpend)
	{
		List<Location> candidateLocations = new ArrayList<>();
		Location destination = null;

		final Set<Location> interceptorReachableLocations = new HashSet<>();
		for (final Player interceptor : this.interceptors)
			interceptorReachableLocations
					.addAll(this.getReachableLocations(interceptor, this.locationsOfIntersection, this.boundingSegments));

		final var commonReachableSegments = this.removeObsoleteSegments(interceptorReachableLocations,
				this.boundingSegments);
		final var commonReachableLocations = this.getReachableLocations(this.target, interceptorReachableLocations,
				commonReachableSegments);

		candidateLocations = this.rateReachableLocations(this.target, commonReachableLocations);
		if (candidateLocations.size() > 0)
			destination = candidateLocations.get(0);


		final var targetReachableLocations = this.getReachableLocations(this.target, this.locationsOfIntersection,
				this.boundingSegments);

		return new DefaultPath(new Waypoint(destination, this.target.getMaxSpeed(), DestinationAction.noStop));
	}

	private List<Location> rateReachableLocations(final Player target, final Set<Location> locations)
	{
		return locations.stream().sorted((l1, l2) ->
		{
			var ret = -1 * Double.compare(l1.getY(), l2.getY());
			if (ret == 0)
				ret = Double.compare(target.getLoc().distanceBetween(l1), target.getLoc().distanceBetween(l2));
			return ret;
		}).toList();
	}

	private Set<LineSegment> removeObsoleteSegments(final Set<Location> reachableLocations,
			final Set<LineSegment> boundingSegments)
	{
		return new HashSet<>(boundingSegments.stream()
				.filter(s -> reachableLocations.contains(s.p0) && reachableLocations.contains(s.p1)).toList());
	}

	private Set<Location> getReachableLocations(final Player target, final Set<Location> locationsOfIntersection,
			final Set<LineSegment> boundingSegments)
	{
		final var ret = new HashSet<Location>();

		for (final Location l : locationsOfIntersection)
		{
			final var lsToPoi = new LineSegment(target.getLoc().toCoordinate(), l.toCoordinate());
			for (final LineSegment ls : boundingSegments)
			{
				final Coordinate poi = Geometry.calculateIntersection(lsToPoi, ls);
				if ((poi == null) || new DefaultLocation(poi).closeEnoughTo(l))
					continue;

				ret.add(l);
				break;
			}
		}

		final var tmp = new HashSet<>(locationsOfIntersection);
		tmp.removeAll(ret);
		return tmp;
	}

	private HashSet<Location> getPointsOfIntersection(final Set<LineSegment> boundingSegments)
	{
		final var ret = new HashSet<Location>();

		for (final LineSegment s : boundingSegments)
		{
			ret.add(new DefaultLocation(s.p0));
			ret.add(new DefaultLocation(s.p1));
		}

		return ret;
	}

	private HashSet<LineSegment> getBoundingSegments(Player p1, List<Player> p2)
	{
		return new HashSet<>(p2.stream().map(p ->
		{
			double ratio = .5;
			final double denom = this.target.getLV().getSpeed() + p.getLV().getSpeed();
			if (denom != 0)
			{
				ratio = this.target.getLV().getSpeed() / denom;
			}

			final LineSegment seg = Conversions.toLineSegment(p1.getLoc(), p.getLoc());
			return new LineSegment(seg.pointAlong(ratio), seg.p1);
		}).toList());
	}

	private Set<LineSegment> splitSegments(final Set<LineSegment> segments)
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

				final Coordinate intersection = Geometry.calculateIntersection(s1, s2);
				if (intersection != null)
				{
					if (intersection.equals(s1.p0) || intersection.equals(s1.p1))
						continue;

					final var newS1 = new LineSegment(s1.p0, intersection);
					final var newS2 = new LineSegment(s1.p1, intersection);

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
