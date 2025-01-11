package jef.core.pathfinding;

import java.util.HashSet;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Segment;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import jef.core.Player;
import jef.core.movement.Location;
import jef.core.movement.player.Path;

public class EvadePlayer implements Pathfinder
{
	private Player target;
	private List<Player> interceptors;
	private final Direction direction;
	
	public EvadePlayer(Player target, List<Player> interceptors, Direction direction)
	{
		super();
		this.target = target;
		this.interceptors = interceptors;
		this.direction = direction;
	}

	@Override
	public Path findPath()
	{
		return findPath(Double.MAX_VALUE);
	}

	@Override
	public Path findPath(double maximumTimeToSpend)
	{
		final var segments = this.getBoundingSegments();

		return null;
	}

	private HashSet<Segment> getBoundingSegments()
	{
		return new HashSet<>(interceptors.stream().map(p -> 
		{
			var ratio = .5;
			final double denom = target.getLV().getSpeed() + p.getLV().getSpeed();
			if (denom != 0)
				ratio = target.getLV().getSpeed() / denom;
			
			final Segment seg = createSegment(target.getLoc().toVector2D(), p.getLoc().toVector2D);
			return createPerpendicularSegmentAt(seg.bisect((float)ratio));
		}).toList());
	}

	private Segment createSegment(Vector2D loc1, Vector2D loc2)
	{
		return new Segment(loc1, loc2, new Line(loc1, loc2, Location.EPSILON));
	}
	
	private Segment createPerpendicularSegmentAt(Segment segment, Vector2D loc)
	{
		assert segment.getLine().contains(loc);
		double angle = segment.getLine().getAngle() + Math.PI / 2;
		new Vector2D(angle, loc).
		Location loc2 = loc.toVector2D().
		return new Segment(loc1.toVector2D(), loc2.toVector2D(), new Line(loc1.toVector2D(), loc2.toVector2D(), Location.EPSILON));
	}
	
	private Vector2D addMagnitudeToVector(Vector2D vector, double magnitude)
	{
		double angle = vector.
		return new Vector2D(newMagnitude * Math.cos(angle), newMagnitude * Math.sin(angle)); 
	}
}
