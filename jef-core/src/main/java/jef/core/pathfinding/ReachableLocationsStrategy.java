package jef.core.pathfinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jfg.game.Player;
import jfg.game.ai.LineSegment;
import jfg.game.ai.Location;
import jfg.game.utils.Comparisons;
import jfg.game.utils.Utils;

public class ReachableLocationsStrategy
{
	private final Set<Segment> boundingSegments;
	private final Set<Location> locationsOfIntersection;
	private final Player runner;
	private final Collection<Player> defenders;

	public ReachableLocationsStrategy(final Player runner, final Collection<Player> defenders,
			final Collection<Player> blockers)
	{
		this.runner = runner;
		this.defenders = defenders;

		final var segments = this.getBoundingSegments(runner, defenders);
		segments.addAll(Arrays.asList(LineSegment.northEndZone, LineSegment.southEndZone, LineSegment.eastSideline,
				LineSegment.westSideline));

		for (final Player defender : defenders)
			segments.addAll(this.getBoundingSegments(defender, blockers));

		this.boundingSegments = Collections.unmodifiableSet(this.splitSegments(segments));

		this.locationsOfIntersection = Collections.unmodifiableSet(this.getPointsOfIntersection(this.boundingSegments));
	}

	public Location getRunnerDestination()
	{
		List<Location> candidateLocations = new ArrayList<>();
		Location destination = null;

		final Set<Location> defenderReachableLocations = new HashSet<>();
		for (final Player defender : this.defenders)
			defenderReachableLocations
					.addAll(this.getReachableLocations(defender, this.locationsOfIntersection, this.boundingSegments));

		final var commonReachableSegments = this.removeObsoleteSegments(defenderReachableLocations,
				this.boundingSegments);
		final var commonReachableLocations = this.getReachableLocations(this.runner, defenderReachableLocations,
				commonReachableSegments);

		candidateLocations = this.rateReachableLocations(this.runner, commonReachableLocations);
		if (candidateLocations.size() > 0)
			destination = candidateLocations.get(0);


		final var runnerReachableLocations = this.getReachableLocations(this.runner, this.locationsOfIntersection,
				this.boundingSegments);

		return destination;
	}

	private HashSet<LineSegment> getBoundingSegments(final Player target, final Collection<Player> interceptors)
	{
		return new HashSet<>(interceptors.stream().map(p -> 
		{
			var ratio = .5;
			final var denom = target.getLinearVelocity().getSpeed() + p.getLinearVelocity().getSpeed();
			if (denom != 0)
				ratio = target.getLinearVelocity().getSpeed() / denom;
			
			final var seg = new LineSegment(target.getLocation(), p.getLocation());
			return seg.getPerpendicularSegment(seg.bisect((float)ratio));
		}).toList());
	}

	private HashSet<Location> getPointsOfIntersection(final Set<LineSegment> boundingSegments)
	{
		final var ret = new HashSet<Location>();

		for (final LineSegment s : boundingSegments)
		{
			ret.add(s.a);
			ret.add(s.b);
		}

		return ret;
	}

	private Set<Location> getReachableLocations(final Player target, final Set<Location> locationsOfIntersection,
			final Set<LineSegment> boundingSegments)
	{
		final var ret = new HashSet<Location>();

		for (final Location l : locationsOfIntersection)
		{
			final var lsToPoi = new LineSegment(target.getLocation(), l);
			for (final LineSegment ls : boundingSegments)
			{
				final var poi = Utils.calculateIntersection(lsToPoi, ls);
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

	private List<Location> rateReachableLocations(final Player target, final Set<Location> locations)
	{
		return locations.stream().sorted((l1, l2) ->
		{
			var ret = -1 * Double.compare(l1.getY(), l2.getY());
			if (ret == 0)
				ret = Double.compare(Comparisons.distanceFrom(target, l1), Comparisons.distanceFrom(target, l2));
			return ret;
		}).toList();
	}

	private Set<LineSegment> removeObsoleteSegments(final Set<Location> reachableLocations,
			final Set<LineSegment> boundingSegments)
	{
		return new HashSet<>(boundingSegments.stream()
				.filter(s -> reachableLocations.contains(s.a) && reachableLocations.contains(s.b)).toList());
	}

	private Set<LineSegment> splitSegments(final Set<LineSegment> segments)
	{
		final var segmentsQueue = new HashSet<>(segments);
		final var ret = new HashSet<LineSegment>();

		while (segmentsQueue.size() > 0)
		{
			final var s1 = segmentsQueue.iterator().next();
			segmentsQueue.remove(s1);

			var found = false;
			for (final LineSegment s2 : segments)
			{
				if (s1.equals(s2))
					continue;

				final var intersection = Utils.calculateIntersection(s1, s2);
				if (intersection != null)
				{
					if (intersection.equals(s1.a) || intersection.equals(s1.b))
						continue;

//					log.debug(String.format("intersection: %s s1: %s s2: %s", intersection, s1, s2));
					Utils.calculateIntersection(s1, s2);

					final var newS1 = new LineSegment(s1.a, intersection);
					final var newS2 = new LineSegment(s1.b, intersection);

					segmentsQueue.add(newS1);
					segmentsQueue.add(newS2);
					found = true;
					break;
				}
			}

			if (!found)
				// log.debug(String.format("adding: %s", s1));
				ret.add(s1);
		}

		return ret;
	}

}
