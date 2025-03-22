package jef.pathfinding.runners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.actions.pathfinding.PathfinderBase;
import jef.actions.pathfinding.PlayerStates;
import jef.core.Direction;
import jef.core.Field;
import jef.core.LinearVelocity;
import jef.core.Location;
import jef.core.Player;
import jef.core.PlayerState;
import jef.core.events.DebugShape;
import jef.core.events.Messages;
import jef.core.geometry.Angle;
import jef.core.geometry.LineSegment;
import jef.core.geometry.Vector;
import jef.core.movement.player.AdvancedSteering;
import jef.core.movement.player.Path;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;

public class AdvancedEvadeInterceptors extends PathfinderBase implements RunnerPathfinder
{
	public AdvancedEvadeInterceptors(PlayerStates players, final Player player, final Direction direction)
	{
		super(players, player, direction);
	}

	@Override
	public void calculate(long deltaNanos)
	{
		long nanos = System.nanoTime();

		List<PlayerState> interceptorPlayers = getPlayerStates().getDefenderStates();

		List<PlayerState> tmpPlayers = new ArrayList<PlayerState>();
		tmpPlayers.add(getPlayerState());
		tmpPlayers.addAll(interceptorPlayers);
		tmpPlayers = tmpPlayers.stream().sorted((p1, p2) -> (getDirection() == Direction.west ? 1 : -1)
				* Double.compare(p1.getLoc().getX(), p2.getLoc().getX())).toList();
		if (tmpPlayers.getFirst() == getPlayerState())
		{
			// if there are no defenders between the runner and the end zone
			Location endZone = new Location(
					(getDirection() == Direction.west) ? Field.WEST_END_ZONE_X : Field.EAST_END_ZONE_X,
					getPlayerState().getLoc().getY());
			MessageManager.getInstance().dispatchMessage(Messages.drawRunnerDestination, endZone);
			MessageManager.getInstance().dispatchMessage(Messages.drawRunnerPath,
					new LineSegment(getPlayerState().getLoc(), endZone));
			setPath(new Path(new Waypoint(endZone, this.getPlayerState().getSpeedMatrix().getJoggingSpeed(), this.getPlayerState().getMaxSpeed(), DestinationAction.noStop)));
		}
		else
		{
			Set<LineSegment> segments = new HashSet<>(
					Arrays.asList(Field.GOAL_LINE_EAST, Field.GOAL_LINE_WEST, Field.SIDELINE_NORTH, Field.SIDELINE_SOUTH));
	
	 		final HashSet<LineSegment> runnerInterceptorSegments = this.getBoundingLines(getPlayerState(), interceptorPlayers);
			runnerInterceptorSegments.stream().forEach(
					s -> MessageManager.getInstance().dispatchMessage(Messages.drawRunnerInterceptorBoundingSegments, s));
			
			segments.addAll(runnerInterceptorSegments);
	
			final HashSet<LineSegment> blockerInterceptorSegments = new HashSet<>();
			for (final PlayerState interceptor : interceptorPlayers)
				blockerInterceptorSegments.addAll(this.getBoundingLines(interceptor, getPlayerStates().getBlockerStates()));
	
			blockerInterceptorSegments.stream().forEach(
					s -> MessageManager.getInstance().dispatchMessage(Messages.drawBlockerInterceptorBoundingSegments, s));
	
			segments.addAll(blockerInterceptorSegments);
			
			segments = Collections.unmodifiableSet(this.splitLines(segments));
	
			Set<Location> locationsOfIntersection = Collections
					.unmodifiableSet(this.getPointsOfIntersection(segments));
	
			final Set<Location> interceptorReachableLocations = new HashSet<>();
			for (final PlayerState interceptor : interceptorPlayers)
				interceptorReachableLocations
						.addAll(this.getReachableLocations(interceptor, locationsOfIntersection, segments));
	
			var commonReachableLines = this.removeObsoleteLines(interceptorReachableLocations, segments);
	//		commonReachableLines = this.removeObsoleteLines(this.getReachableLocations(runner.getPlayerState(), locationsOfIntersection, segments), commonReachableLines);
			final var commonReachableLocations = this.getReachableLocations(this.getPlayerState(), interceptorReachableLocations,
					commonReachableLines);
	
			Location destination = this.getFarthestReachableLocation(this.getPlayerState(), commonReachableLocations);
	
			if (destination.isInEndZone(getDirection()))
			{
				MessageManager.getInstance().dispatchMessage(Messages.drawRunnerDestination, destination);
				MessageManager.getInstance().dispatchMessage(Messages.drawRunnerPath,
						new LineSegment(getPlayerState().getLoc(), destination));
	
				Waypoint wp1 = new Waypoint(destination, getPlayerState().getSpeedMatrix().getJoggingSpeed(), this.getPlayerState().getMaxSpeed(), DestinationAction.noStop);
				setPath(new Path(wp1));
			}
			else
			{
				Location endZone = new Location(
						(getDirection() == Direction.west) ? Field.WEST_END_ZONE_X : Field.EAST_END_ZONE_X,
						destination.getY());
	
				Angle angle = new Angle(destination, Vector.fromPolarCoordinates(destination.angleTo(this.getPlayerState().getLoc()), 0, destination.distanceBetween(this.getPlayerState().getLoc())), 
						Vector.fromPolarCoordinates(destination.angleTo(endZone), 0, destination.distanceBetween(endZone)));
				double tightestRadiusAtSpeed = AdvancedSteering.calculateTightestRadiusTurnAtSpeed(this.getPlayerState().getLV().getSpeed(), this.getPlayerState().getMaxSpeed());
				
				double adjacentSide = tightestRadiusAtSpeed / Math.tan(angle.getAngle() / 2);
				double hypotenuse = Math.sqrt(tightestRadiusAtSpeed * tightestRadiusAtSpeed + adjacentSide * adjacentSide);
				Vector bisection = angle.bisect();
				bisection = Vector.fromPolarCoordinates(bisection.getAzimuth(), bisection.getElevation(), hypotenuse);
				Location vertex = destination.add(new LinearVelocity(bisection));
				MessageManager.getInstance().dispatchMessage(Messages.drawDebugShape, DebugShape.drawCircle(vertex, "#00000000", tightestRadiusAtSpeed));
	
				Location pivotLocation = destination;
				Location wp1Location = pivotLocation.add(new LinearVelocity(angle.getVector1()).newFrom(null, null, adjacentSide));
				Location wp2Location = pivotLocation.add(new LinearVelocity(angle.getVector2()).newFrom(null, null, adjacentSide));
				
				Waypoint wp1 = new Waypoint(wp1Location, this.getPlayerState().getMaxSpeed(), this.getPlayerState().getMaxSpeed(), DestinationAction.noStop);
				Waypoint wp2 = new Waypoint(wp2Location, this.getPlayerState().getMaxSpeed(), this.getPlayerState().getMaxSpeed(), DestinationAction.noStop);
				Waypoint wp3 = new Waypoint(endZone, this.getPlayerState().getMaxSpeed(), this.getPlayerState().getMaxSpeed(), DestinationAction.noStop);
	
				double distance = wp1.getDestination().distanceBetween(this.getPlayerState().getLoc());
				System.out.println(distance);
				
				double distanceToPivot = this.getPlayerState().getLoc().distanceBetween(pivotLocation);
				double distanceBetweenWp1AndWp2 = wp1.getDestination().distanceBetween(pivotLocation);
				
				if (distance < 1 || distanceToPivot < distanceBetweenWp1AndWp2)
				{
					setPath(new Path(wp2, wp3));
	
					MessageManager.getInstance().dispatchMessage(Messages.drawRunnerDestination, wp2.getDestination());
					MessageManager.getInstance().dispatchMessage(Messages.drawRunnerPath,
							new LineSegment(this.getPlayerState().getLoc(), wp2.getDestination()));
	
					MessageManager.getInstance().dispatchMessage(Messages.drawRunnerDestination, wp3.getDestination());
					MessageManager.getInstance().dispatchMessage(Messages.drawRunnerPath,
							new LineSegment(wp2.getDestination(), wp3.getDestination()));
				}
				else
				{
					setPath(new Path(wp1, wp2, wp3));
	
					MessageManager.getInstance().dispatchMessage(Messages.drawRunnerDestination, wp1.getDestination());
					MessageManager.getInstance().dispatchMessage(Messages.drawRunnerPath,
							new LineSegment(getPlayerState().getLoc(), wp1.getDestination()));
	
					MessageManager.getInstance().dispatchMessage(Messages.drawRunnerDestination, wp2.getDestination());
					MessageManager.getInstance().dispatchMessage(Messages.drawRunnerPath,
							new LineSegment(wp1.getDestination(), wp2.getDestination()));
	
					MessageManager.getInstance().dispatchMessage(Messages.drawRunnerDestination, wp3.getDestination());
					MessageManager.getInstance().dispatchMessage(Messages.drawRunnerPath,
							new LineSegment(wp2.getDestination(), wp3.getDestination()));
				}
				
	
				
			}
	//		else
	//		{
	//			Waypoint wp1 = new Waypoint(destination, getPlayerState().getSpeedMatrix().getJoggingSpeed(), this.getPlayerState().getMaxSpeed(), DestinationAction.noStop);
	//
	//			Location endZone = new DefaultLocation(
	//					(getDirection() == Direction.west) ? Field.WEST_END_ZONE_X : Field.EAST_END_ZONE_X,
	//					destination.getY());
	//
	//			double angle = Conversions.normalizeAngle(wp1.getDestination().angleTo(endZone) - this.getPlayerState().getLoc().angleTo(wp1.getDestination()));
	//			double tightestRadiusAtSpeed = Steering.calculateTightestRadiusTurnAtSpeed(this.getPlayerState().getLV().getSpeed(), this.getPlayerState().getMaxSpeed());
	//			double adjacentSide = tightestRadiusAtSpeed / Math.tan(angle / 2);
	//			
	//			Waypoint wp2 = new Waypoint(wp1.getDestination().add(new DefaultLinearVelocity(wp1.getDestination(), endZone).newFrom(null, null, adjacentSide)), 
	//					this.getPlayerState().getMaxSpeed(), this.getPlayerState().getMaxSpeed(), DestinationAction.noStop);
	//
	//			double hypotenuse = Math.sqrt(tightestRadiusAtSpeed * tightestRadiusAtSpeed + adjacentSide * adjacentSide);
	//			Location vertex = wp2.getDestination().add(new DefaultLinearVelocity(0, angle / 2, hypotenuse));
	//			MessageManager.getInstance().dispatchMessage(Messages.drawDebugShape, DebugShape.drawCircle(vertex, "#00000000", tightestRadiusAtSpeed));
	//			
	//			wp1 = new Waypoint(new LineSegment(this.getPlayerState().getLoc(), wp1.getDestination()).addLength(-adjacentSide).getLoc2(), this.getPlayerState().getMaxSpeed(), this.getPlayerState().getMaxSpeed(), DestinationAction.noStop);
	//			Waypoint wp3 = new Waypoint(endZone, this.getPlayerState().getMaxSpeed(), this.getPlayerState().getMaxSpeed(), DestinationAction.noStop);
	//
	//			if (this.getPlayerState().getLoc().distanceBetween(wp2.getDestination()) < wp1.getDestination().distanceBetween(wp2.getDestination()))
	//			{
	//				setPath(new DefaultPath(wp2, wp3));
	//			}
	//			else
	//			{
	//				setPath(new DefaultPath(wp1, wp2, wp3));
	//
	//				MessageManager.getInstance().dispatchMessage(Messages.drawRunnerDestination, wp1.getDestination());
	//				MessageManager.getInstance().dispatchMessage(Messages.drawRunnerPath,
	//						new LineSegment(getPlayerState().getLoc(), wp1.getDestination()));
	//			}
	//
	//			MessageManager.getInstance().dispatchMessage(Messages.drawRunnerDestination, wp2.getDestination());
	//			MessageManager.getInstance().dispatchMessage(Messages.drawRunnerPath,
	//					new LineSegment(wp1.getDestination(), wp2.getDestination()));
	//
	//			MessageManager.getInstance().dispatchMessage(Messages.drawRunnerDestination, wp3.getDestination());
	//			MessageManager.getInstance().dispatchMessage(Messages.drawRunnerPath,
	//					new LineSegment(wp2.getDestination(), wp3.getDestination()));
	//		}
		}
	}

	private Location getFarthestReachableLocation(final PlayerState player, final Set<Location> locations)
	{
		return locations.stream().sorted((l1, l2) ->
		{
			double l1x = l1.getX();
			double l2x = l2.getX();

			var ret = (getDirection() == Direction.east ? -1 : 1) * Double.compare(l1x, l2x);
			if (ret == 0)
				ret = Double.compare(player.getLoc().distanceBetween(l1), player.getLoc().distanceBetween(l2));
			return ret;
		}).toList().getFirst();
	}

	private Set<LineSegment> removeObsoleteLines(final Set<Location> reachableLocations,
			final Set<LineSegment> boundingLines)
	{
		return new HashSet<>(boundingLines.stream()
				.filter(s -> reachableLocations.contains(s.getLoc1()) && reachableLocations.contains(s.getLoc2()))
				.toList());
	}

	private Set<Location> getReachableLocations(final PlayerState player, final Set<Location> locationsOfIntersection,
			final Set<LineSegment> boundingLines)
	{
		final var ret = new HashSet<Location>();

		for (final Location l : locationsOfIntersection)
		{
			final LineSegment lsToPoi = new LineSegment(player.getLoc(), l);
			for (final LineSegment ls : boundingLines)
			{
				final Location poi = lsToPoi.xyIntersection(ls);
				if (poi == null || poi.isInBounds() == false || ls.intersects(poi) == false
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

	private HashSet<LineSegment> getBoundingLines(PlayerState p1, Collection<? extends PlayerState> p2)
	{
		return new HashSet<>(p2.stream().map(p ->
		{
			double ratio = .5;
			final double denom = p1.getLV().getSpeed() + p.getLV().getSpeed();
			if (denom != 0)
			{
				ratio = p1.getLV().getSpeed() / denom;
			}

			final LineSegment seg = new LineSegment(p1.getLoc(), p.getLoc());
			LinearVelocity segLV = seg.getDirection();
			Location pointAlong = seg.getPoint(ratio);

			// make it longer than field distances and then chop it down to size to make
			// sure it fits
			return new LineSegment(pointAlong.add(segLV.add(-Math.PI / 2, 0, 200)),
					pointAlong.add(segLV.add(Math.PI / 2, 0, 200))).restrictToBetweenEndZones(true);
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
