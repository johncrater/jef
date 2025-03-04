package jef.actions.pathfinding.runners;

import jef.core.Direction;
import jef.core.Field;
import jef.core.LinearVelocity;
import jef.core.Location;
import jef.core.geometry.LineSegment;
import jef.core.movement.DefaultLocation;
import jef.core.movement.player.DefaultPath;
import jef.core.movement.player.Path;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;

public class RunnerPathUtils
{
	/**
	 * This path is for runners only. It will return a path that extends the
	 * runner's current vector until he runs out of bounds or reaches the ends zone.
	 * If his path runs out of bounds, it will turn towards the end zone one yard
	 * short of running out of bounds and the path will then continue on to the end
	 * zone resulting in 2 waypoints.
	 * 
	 * @param runner
	 * @param startingLocation
	 * @param direction
	 * @return
	 */
	public static Path calculateRunnerEstimatedPath(Location startingLocation, LinearVelocity lv, Direction direction)
	{
		if (lv.isNotMoving())
			return calculateRunForGloryPath(startingLocation, lv, direction);

		Location endLoc = calculateRunnerEndLocation(startingLocation, lv);
		if (endLoc.isInEndZone(direction))
			return new DefaultPath(new Waypoint(endLoc, lv.getSpeed(), lv.getSpeed(), DestinationAction.noStop));

		return new DefaultPath(new Waypoint(endLoc, lv.getSpeed(), lv.getSpeed(), DestinationAction.noStop),
				calculateRunForGloryPath(startingLocation, lv, direction).getCurrentWaypoint());
	}

	/**
	 * Calculates a line segment starting at <i>startingLocation</i> and ending
	 * either out of bounds or in the end zone then returns the end location of that
	 * line segment
	 * 
	 * @param runner
	 * @param startingLocation
	 * @param direction
	 * @return
	 */
	public static Location calculateRunnerEndLocation(Location startingLocation, LinearVelocity lv)
	{
		return new LineSegment(startingLocation, startingLocation.add(lv.newFrom(null, null, 500.0)))
				.restrictToBetweenEndZones(true).getLoc2();
	}

	/**
	 * Calculates a path from the current location directly to the end zone. Current
	 * linear velocity is not considered.
	 * 
	 * @param runner
	 * @param startingLocation
	 * @param direction
	 * @return
	 */
	public static Path calculateRunForGloryPath(Location startingLocation, LinearVelocity lv, Direction direction)
	{
		return new DefaultPath(new Waypoint(new DefaultLocation(getEndZone(direction), startingLocation.getY(), 0),
				lv.getSpeed(), lv.getSpeed(), DestinationAction.noStop));
	}

	public static double getEndZone(Direction direction)
	{
		if (direction == Direction.east)
			return Field.EAST_END_ZONE_X;
		else
			return Field.WEST_END_ZONE_X;
	}
}
