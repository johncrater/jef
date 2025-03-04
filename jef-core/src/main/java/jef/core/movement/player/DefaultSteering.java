package jef.core.movement.player;

import java.util.List;

import jef.core.Conversions;
import jef.core.Location;

public class DefaultSteering implements Steering
{
	private static final boolean SHOW_MESSAGES = false;

	public int calculateTicks(PlayerTracker tracker)
	{
		int count = 0;
		while (next(tracker) == false)
		{
			count += 1;
			tracker.advance();
		}

		return count;
	}

	DefaultSteering()
	{
	}

	
	@Override
	public boolean next(final PlayerTracker tracker)
	{
		if (tracker.getPath() != null && tracker.getPath().getCurrentWaypoint() != null
				&& tracker.getLoc().closeEnoughTo(this.getDestination(tracker)) && tracker.getLV().isNotMoving())
		{
			List<Waypoint> waypoints = tracker.getPath().getWaypoints();
			waypoints.remove(0);
			tracker.setPath(new DefaultPath(waypoints.toArray(new Waypoint[waypoints.size()])));
		}

		if (tracker.getPath() == null || tracker.getPath().getWaypoints().size() == 0)
		{
			tracker.setLV(tracker.getLV().newFrom(null, null, 0.0));
			return true;
		}
		
		if (SHOW_MESSAGES)
			this.buildMessage(String.format("%-25s: %s", "Initial",
					String.format("%3.2f %s %s", tracker.getPctRemaining(), tracker.getLV(), tracker.getLoc())));

		switch (tracker.getPosture())
		{
			case fallingDown:
				tracker.setPosture(tracker.getPosture().adjustDown());
				break;
			case onTheGround:
				tracker.setPosture(tracker.getPosture().adjustUp());
				break;
			case standingUp:
				tracker.setPosture(tracker.getPosture().adjustUp());
				break;
			case stumbling:
				tracker.setPosture(tracker.getPosture().adjustUp());
				break;
			default:
				tracker.setLV(tracker.getLV().newFrom(null, null, tracker.getMaxSpeed()));
				break;
		}

		final double angleAdjustment = this.calculateAngleOfTurn(tracker.getLoc(), this.getDestination(tracker),
				tracker.getLV().getAzimuth());

		if (SHOW_MESSAGES)
			this.buildMessage(
					String.format("%-25s: \t\t%4f\u00B0", "Angle Adjustment", Math.toDegrees(angleAdjustment)));

		tracker.turn(angleAdjustment);
		tracker.move();

		if (this.destinationReached(tracker) || tracker.hasPastDestination())
		{
			List<Waypoint> waypoints = tracker.getPath().getWaypoints();
			waypoints.remove(0);
			tracker.setPath(new DefaultPath(waypoints.toArray(new Waypoint[waypoints.size()])));

			if (tracker.getPath().getWaypoints().size() > 0)
			{
				if (SHOW_MESSAGES)
					this.buildMessage(String.format("%-25s: %s", "Destination Reached", String.format("%3.2f %s %s",
							tracker.getPctRemaining(), tracker.getLV(), tracker.getLoc())));
				return this.next(tracker);
			}
			else
			{
				if (SHOW_MESSAGES)
					this.buildMessage(String.format("%-25s: %s", "Waypoint Reached", String.format("%3.2f %s %s",
							tracker.getPctRemaining(), tracker.getLV(), tracker.getLoc())));
				return true;
			}
		}

		if (SHOW_MESSAGES)
			this.buildMessage(String.format("%-25s: %s", "Final",
					String.format("%3.2f %s %s", tracker.getPctRemaining(), tracker.getLV(), tracker.getLoc())));

		return false;
	}

	private void buildMessage(final String msg)
	{
		System.out.println(msg);
	}

	private double calculateAngleOfTurn(final Location loc, final Location destination, final double currentAngle)
	{
		final double desiredAngle = loc.angleTo(destination);
		final double angularDiff = desiredAngle - currentAngle;
		return Conversions.normalizeAngle(angularDiff);
	}


	/**
	 * @formatter:off
	 * 1m radius velocity at 40%
	 * 2m radius velocity at 50%
	 * 3m radius velocity at 60%
	 * 4m radius velocity at 65%
	 * 5m radius velocity at 70%
	 * 6m radius velocity at 75%
	 * 7m radius velocity at 80%
	 * 8m radius velocity at 85%
	 * 9m radius velocity at 90%
	 * 10m+ radius velocity at 95%
	 * @formatter:on

	 * @param currentSpeed
	 * @return
	 */
	public static double calculateTightestRadiusTurnAtSpeed(final double currentSpeed, final double maximumSpeed)
	{
		final double pctOfMaximumSpeed = currentSpeed / maximumSpeed;

		if (pctOfMaximumSpeed < .4f)
			return (1.1f * pctOfMaximumSpeed) / .40f;

		if (pctOfMaximumSpeed < .5f)
			return 2.2f + ((pctOfMaximumSpeed - .40f) / .1f);

		if (pctOfMaximumSpeed < .6f)
			return 3.3f + ((pctOfMaximumSpeed - .50f) / .1f);

		if (pctOfMaximumSpeed < .65f)
			return 4.4f + ((pctOfMaximumSpeed - .60f) / .5f);

		if (pctOfMaximumSpeed < .70f)
			return 5.5f + ((pctOfMaximumSpeed - .65f) / .5f);

		if (pctOfMaximumSpeed < .75f)
			return 6.6f + ((pctOfMaximumSpeed - .70f) / .5f);

		if (pctOfMaximumSpeed < .80f)
			return 7.7f + ((pctOfMaximumSpeed - .75f) / .5f);

		if (pctOfMaximumSpeed < .85f)
			return 8.8f + ((pctOfMaximumSpeed - .80f) / .5f);

		if (pctOfMaximumSpeed < .90f)
			return 9.9f + ((pctOfMaximumSpeed - .85f) / .5f);

		if (pctOfMaximumSpeed < .95f)
			return 11.0f + ((pctOfMaximumSpeed - .90f) / .5f);

		if (pctOfMaximumSpeed < 1.0f)
			return 12.1f + ((pctOfMaximumSpeed - .95f) / .5f);

		return 12.1f;
	}

	private boolean destinationReached(final PlayerTracker tracker)
	{
		final Waypoint waypoint = tracker.getPath().getCurrentWaypoint();
		if (waypoint == null)
			return true;

		return tracker.getLoc().closeEnoughTo(this.getDestination(tracker));
	}

	private Location getDestination(PlayerTracker tracker)
	{
		return tracker.getPath().getCurrentWaypoint().getDestination();
	}

}
