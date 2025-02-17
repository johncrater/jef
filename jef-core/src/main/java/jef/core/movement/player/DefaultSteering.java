package jef.core.movement.player;

import java.util.List;

import jef.core.Conversions;
import jef.core.Player.DecelerationRate;
import jef.core.movement.DefaultLinearVelocity;
import jef.core.movement.Location;
import jef.core.movement.player.Waypoint.DestinationAction;

public class DefaultSteering implements Steering
{
	private static final boolean SHOW_MESSAGES = false;

	public static int calculateTicks(PlayerTracker tracker)
	{
		int count = 0;
		Steering steering = new DefaultSteering();
		while (steering.next(tracker) == false)
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

		final double startingSpeed = tracker.getLV().getSpeed();

		if (startingSpeed != tracker.getMaxSpeed())
			tracker.setLV(tracker.getLV().newFrom(null, null, tracker.getMaxSpeed()));

		switch (tracker.getPosture())
		{
			case fallingDown:
				if (tracker.getLV().isNotMoving())
					tracker.setPosture(tracker.getPosture().adjustDown());
				else
					tracker.moveRemaining(
							DecelerationRate.MAXIMUM.getRate() * tracker.getAccelerationCoefficient());
				return false;
			case onTheGround:
				tracker.setPosture(tracker.getPosture().adjustUp());
				return false;
			case standingUp:
				tracker.setPosture(tracker.getPosture().adjustUp());
				return false;
			case stumbling:
				tracker.setPosture(tracker.getPosture().adjustUp());
				return false;
			default:
				break;
		}

		final double angleAdjustment = this.calculateAngleOfTurn(tracker.getLoc(), this.getDestination(tracker),
				tracker.getLV().getAzimuth());

		if (SHOW_MESSAGES)
			this.buildMessage(
					String.format("%-25s: \t\t%4f\u00B0", "Angle Adjustment", Math.toDegrees(angleAdjustment)));

		final double newAngle = angleAdjustment + tracker.getLV().getAzimuth();

		tracker.turn(angleAdjustment);
		tracker.move(new DefaultLinearVelocity(newAngle, 0.0, tracker.getMaxSpeed() - tracker.getLV().getSpeed()), null);

		if (this.destinationReached(tracker) || tracker.hasPastDestination())
		{
			List<Waypoint> waypoints = tracker.getPath().getWaypoints();
			waypoints.remove(0);
			tracker.setPath(new DefaultPath(waypoints.toArray(new Waypoint[waypoints.size()])));

			if (tracker.getPath().getWaypoints().size() > 1)
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

	private double calculateDecelerationAdjustment(final PlayerTracker tracker)
	{
		final double distanceRemainingToDestination = tracker.getLoc().distanceBetween(this.getDestination(tracker));

		double decelerationAdjustment = this.decelerate(tracker, 0, distanceRemainingToDestination);
		if (decelerationAdjustment != 0)
		{
			if (SHOW_MESSAGES)
				this.buildMessage(String.format("%-25s: \t\t%4f", "Deceleration Adjustment", decelerationAdjustment));
		}

		return decelerationAdjustment;
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

	private double decelerate(final PlayerTracker tracker, final double finalVelocity, final double distanceRemaining)
	{
		DecelerationRate maxDecelerationRate = switch (tracker.getPath().getCurrentWaypoint().getDestinationAction())
		{
			case instant -> DecelerationRate.INSTANT;
			case fastStop -> DecelerationRate.INSTANT;
			case normalStop -> DecelerationRate.INSTANT;
			case slowStop -> DecelerationRate.INSTANT;
			case noStop -> DecelerationRate.NONE;
			case rounded -> DecelerationRate.NONE;
		};

		if (maxDecelerationRate == DecelerationRate.NONE)
			return maxDecelerationRate.getRate();

		/*
		 * This can be less than distanceRemaining because the previous turn started
		 * when distanceNeededToDecelerate was > distanceRemaining and extended past the
		 * point of equilibrium.
		 */

		DecelerationRate minDecelerationRate = DecelerationRate.values()[maxDecelerationRate.ordinal() + 1];
		if (minDecelerationRate == DecelerationRate.NONE)
			minDecelerationRate = DecelerationRate.INSTANT;

		final double maxDistanceNeededToDecelerate = tracker
				.calculateDistanceToReachSpeed(minDecelerationRate.getRate(), finalVelocity);

		if (distanceRemaining >= maxDistanceNeededToDecelerate)
			return 0;

		return Math.min(minDecelerationRate.getRate() * maxDistanceNeededToDecelerate / distanceRemaining,
				maxDecelerationRate.getRate());
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
