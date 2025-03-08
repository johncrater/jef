package jef.core.movement.player;

import java.util.List;

import jef.core.Conversions;
import jef.core.LinearVelocity;
import jef.core.LinearVelocity;
import jef.core.Location;
import jef.core.Player.DecelerationRate;
import jef.core.movement.Posture;
import jef.core.movement.player.Waypoint.DestinationAction;

public class AdvancedSteering implements Steering
{
	private static final boolean SHOW_MESSAGES = false;

	private static double[] pctTimes =
	{ .333f, .600f, 1.10f, 1.66f, 2.33f, 6.00f };

	public static final int USE_ALL = 0x8FFFFFFF;
	public static final int USE_OUT_OF_CONTROL = 0x00000001;
	public static final int USE_POSTURE = 0x00000002;
	public static final int USE_COAST_TO_STOP = 0x00000004;
	public static final int USE_DECELERATION = 0x00000008;
	public static final int USE_TURN_SPEED_ADJUSTMENT = 0x00000010;
	public static final int USE_TURN_CALCULATION = 0x00000020;
	public static final int USE_ACCELERATION = 0x00000040;

	private final int options;

	private AdvancedSteering()
	{
		options = USE_ALL;
	}

	private AdvancedSteering(int options)
	{
		this.options = options;
	}

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

	@Override
	public boolean next(final PlayerTracker tracker)
	{
		if (tracker.getPath() != null && tracker.getPath().getCurrentWaypoint() != null
				&& tracker.getLoc().closeEnoughTo(this.getDestination(tracker)) && tracker.getLV().isNotMoving())
		{
			List<Waypoint> waypoints = tracker.getPath().getWaypoints();
			waypoints.remove(0);
			tracker.setPath(new Path(waypoints.toArray(new Waypoint[waypoints.size()])));
		}

		if (tracker.getPath() == null || tracker.getPath().getWaypoints().size() == 0)
		{
			if ((options & USE_COAST_TO_STOP) > 0)
			{
				// coasting to a stop
				this.coastToAStop(tracker);
				if (tracker.getLV().isNotMoving())
					return true;
			}
			else
			{
				tracker.setLV(tracker.getLV().newFrom(null, null, 0.0));
				return true;
			}

			return false;
		}

		if (SHOW_MESSAGES)
			this.buildMessage(String.format("%-25s: %s", "Initial",
					String.format("%3.2f %s %s", tracker.getPctRemaining(), tracker.getLV(), tracker.getLoc())));

		final double startingSpeed = tracker.getLV().getSpeed();

		if (startingSpeed > tracker.getPlayer().getSpeedMatrix().getSprintingSpeed())
		{
			if ((options & USE_OUT_OF_CONTROL) > 0)
			{
				// too fast
				this.outOfControl(tracker);
			}
			else
			{
				tracker.setLV(tracker.getLV().newFrom(null, null, tracker.getPlayer().getSpeedMatrix().getSprintingSpeed()));
			}

			return false;
		}

		if ((options & USE_POSTURE) > 0)
		{
			switch (tracker.getPosture())
			{
				case fallingDown:
					if (tracker.getLV().isNotMoving())
						tracker.setPosture(tracker.getPosture().adjustDown());
					else
						tracker.moveRemaining(
								DecelerationRate.MAXIMUM.getRate());
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
		}

		double speedAdjustment = 0;

		if ((options & USE_DECELERATION) > 0 || (options & USE_TURN_SPEED_ADJUSTMENT) > 0)
		{
			// do we need to decelerate?
			speedAdjustment = this.calculateDecelerationAdjustment(tracker);
		}

		final double angleAdjustment = this.calculateAngleOfTurn(tracker.getLoc(), this.getDestination(tracker),
				tracker.getLV().getAzimuth());

		if (SHOW_MESSAGES)
			this.buildMessage(
					String.format("%-25s: \t\t%4f\u00B0", "Angle Adjustment", Math.toDegrees(angleAdjustment)));

		final double newAngle = angleAdjustment + tracker.getLV().getAzimuth();

		if ((options & USE_TURN_CALCULATION) > 0)
		{
			this.performTurn(tracker, angleAdjustment, newAngle, speedAdjustment);
		}
		else
		{
			tracker.turn(angleAdjustment);
		}

		if (startingSpeed <= (tracker.getLV().getSpeed() + speedAdjustment))
		{
			if ((options & USE_ACCELERATION) > 0)
			{
				// calculate where we are in the acceleration cycle
				double elapsedTime = this
						.calculateElapsedTime((tracker.getLV().getSpeed() + speedAdjustment) / tracker.getPlayer().getSpeedMatrix().getSprintingSpeed());
				elapsedTime += tracker.getRemainingTime();
				double newSpeed = this.calculateSpeed(elapsedTime);
				newSpeed *= tracker.getPlayer().getSpeedMatrix().getSprintingSpeed();
				newSpeed = Math.min(newSpeed, tracker.getPath().getCurrentWaypoint().getMaxSpeed());

				if (SHOW_MESSAGES)
					this.buildMessage(String.format("%-25s: \t%4f", "New Speed", newSpeed));

				speedAdjustment += (newSpeed - startingSpeed) / tracker.getRemainingTime();
			}
			else
			{
				speedAdjustment = (tracker.getPlayer().getSpeedMatrix().getSprintingSpeed() - tracker.getLV().getSpeed()) / tracker.getRemainingTime();
			}
		}

		tracker.move(new LinearVelocity(newAngle, 0.0, speedAdjustment), null);

		if (this.destinationReached(tracker) || (tracker.hasPastDestination()
				&& tracker.getPath().getCurrentWaypoint().getDestinationAction() == DestinationAction.noStop))
		{
			List<Waypoint> waypoints = tracker.getPath().getWaypoints();
			waypoints.remove(0);
			tracker.setPath(new Path(waypoints.toArray(new Waypoint[waypoints.size()])));

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

	private double calculateDistanceNeededToCompleteTurn(final PlayerTracker tracker, final double newAngle)
	{
		final double minTurnRadius = this.calculateTightestRadiusTurnAtSpeed(tracker.getLV().getSpeed(),
				tracker.getPlayer().getSpeedMatrix().getSprintingSpeed());
		return Math.abs(minTurnRadius * newAngle);
	}

	private double calculateElapsedTime(final double pctOfMaxSpeed)
	{
		double elapsedTime = 0;
		final double increment = 1.0 / AdvancedSteering.pctTimes.length;
		double previousElapsedTime = 0;
		for (int i = 0; i < AdvancedSteering.pctTimes.length; i++)
		{
			if (pctOfMaxSpeed <= ((i + 1) * increment))
			{
				final double pctOfIncrementRemaining = (pctOfMaxSpeed - (i * increment)) / increment;
				final double secondsToCover = AdvancedSteering.pctTimes[i] - previousElapsedTime;
				elapsedTime += pctOfIncrementRemaining * secondsToCover;
				break;
			}
			elapsedTime = AdvancedSteering.pctTimes[i];
			previousElapsedTime = elapsedTime;
		}

		return elapsedTime;
	}

	/**
	 * @formatter:off
	 * 1.1y radius velocity at 40%
	 * 2.2y radius velocity at 50%
	 * 3.3y radius velocity at 60%
	 * 4.4y radius velocity at 65%
	 * 5.5y radius velocity at 70%
	 * 6.6y radius velocity at 75%
	 * 7.7y radius velocity at 80%
	 * 8.8y radius velocity at 85%
	 * 9.9y radius velocity at 90%
	 * 11y+ radius velocity at 95%
	 * @formatter:on
	 *
	 * @param radiusInYards
	 * @return
	 */
//	private double calculateMaximumSpeedForRadiusTurn(final double radiusInYards, final double maximumSpeed)
//	{
//		double ret = 1;
//
//		if (radiusInYards <= 1.1)
//		{
//			ret = (radiusInYards / 1.1) * .4;
//		}
//		else if (radiusInYards <= 2.2)
//		{
//			ret = .4 + ((radiusInYards - 1.1) * .1);
//		}
//		else if (radiusInYards <= 3.3)
//		{
//			ret = .5 + ((radiusInYards - 2.2) * .1);
//		}
//		else if (radiusInYards <= 4.4)
//		{
//			ret = .6 + ((radiusInYards - 3.3) * .1);
//		}
//		else if (radiusInYards <= 5.5)
//		{
//			ret = .65 + ((radiusInYards - 4.4) * .05);
//		}
//		else if (radiusInYards <= 6.6)
//		{
//			ret = .70 + ((radiusInYards - 5.5) * .05);
//		}
//		else if (radiusInYards <= 7.7)
//		{
//			ret = .75 + ((radiusInYards - 6.6) * .05);
//		}
//		else if (radiusInYards <= 8.8)
//		{
//			ret = .80 + ((radiusInYards - 7.7) * .05);
//		}
//		else if (radiusInYards <= 9.9)
//		{
//			ret = .85 + ((radiusInYards - 8.8) * .05);
//		}
//		else if (radiusInYards <= 11)
//		{
//			ret = .90 + ((radiusInYards - 9.9) * .05);
//		}
//		else if (radiusInYards <= 12.1)
//		{
//			ret = .95 + ((radiusInYards - 11) * .05);
//		}
//
//		return ret * maximumSpeed;
//	}
//
	private double calculateSpeed(final double elapsedTime)
	{
		double currentSpeed = 0;
		double previousElapsedTimeMax = 0;
		final double increment = 1 / (double) AdvancedSteering.pctTimes.length;
		for (final double pctTime : AdvancedSteering.pctTimes)
		{
			if (elapsedTime <= pctTime)
			{
				currentSpeed += (increment * (elapsedTime - previousElapsedTimeMax))
						/ (pctTime - previousElapsedTimeMax);
				break;
			}
			currentSpeed += increment;
			previousElapsedTimeMax = pctTime;
		}

		return currentSpeed;
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

//	private double calculateTurnSpeedAdjustment(final PlayerTracker tracker)
//	{
//		double turnSpeedAdjustment = 0;
//		
//		final double minTurnRadius = this.calculateTightestRadiusTurnForAtSpeed(tracker.getLV().getSpeed(),
//				this.getMaxSpeed());
//
//		// can we actually even make a turn at this speed
//		final double maxSpeedToMakeTurn = this.calculateMaximumSpeedForRadiusTurn(minTurnRadius, this.getMaxSpeed());
//		if (maxSpeedToMakeTurn < tracker.getLV().getSpeed())
//			return Math.max(maxSpeedToMakeTurn - (tracker.getLV().getSpeed()), Player.maximumDecelerationRate);
//
//		// if we still can't make the turn given the limitations of the waypoint's
//		// turnSpeed
//		// we will just have to try harder. Otherwise, we could loop for ever in a
//		// perpetual holding pattern
//		
//		return turnSpeedAdjustment;
//	}
//
	private void coastToAStop(final PlayerTracker tracker)
	{
		// coast to a stop
		tracker.moveRemaining(DecelerationRate.NORMAL.getRate());
		if (SHOW_MESSAGES)
			this.buildMessage(String.format("%-25s: %s", "Coasting To Stop",
					String.format("%3.2f %s %s", tracker.getPctRemaining(), tracker.getLV(), tracker.getLoc())));
	}

	private double decelerate(final PlayerTracker tracker, final double finalVelocity, final double distanceRemaining)
	{
		DecelerationRate maxDecelerationRate = switch (tracker.getPath().getCurrentWaypoint().getDestinationAction())
		{
			case instant -> DecelerationRate.INSTANT;
			case fastStop -> DecelerationRate.MAXIMUM;
			case normalStop -> DecelerationRate.NORMAL;
			case slowStop -> DecelerationRate.LEISURELY;
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
			minDecelerationRate = DecelerationRate.MINIMAL;

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

	private void outOfControl(final PlayerTracker tracker)
	{
		// we are out of control and will fall if we can't decelerate below
		// getMaxSpeed()
		// immediately no controlled action can be taken, so we return immediately

		// we use normal deceleration rate since we are out of control and it takes
		// control to decelerate
		// at the fastest rate

		double adjustment = DecelerationRate.NORMAL.getRate();
		final double adjustedSpeed = tracker.calculateAdjustedSpeed(adjustment);
		if (adjustedSpeed > tracker.getPlayer().getSpeedMatrix().getSprintingSpeed())
		{
			// we are falling. need to improve this
			adjustment = -adjustedSpeed / 2;
			tracker.setPosture(tracker.getPosture().adjustDown());
		}

		tracker.moveRemaining(adjustment);

		if (tracker.getPosture() == Posture.onTheGround)
			tracker.setLV(tracker.getLV().newFrom(null, null, 0.0));

		if (SHOW_MESSAGES)
			this.buildMessage(String.format("%-25s: %s", "Out of Control",
					String.format("%3.2f %s %s", tracker.getPctRemaining(), tracker.getLV(), tracker.getLoc())));
	}

	private boolean performTurn(final PlayerTracker tracker, final double angleAdjustment, final double newAngle,
			double minimumTurnSpeed)
	{
		final double distanceNeededToCompleteTurn = this.calculateDistanceNeededToCompleteTurn(tracker,
				angleAdjustment);
		if (!LinearVelocity.closeEnoughTo(0, distanceNeededToCompleteTurn))
		{
			if (SHOW_MESSAGES)
				this.buildMessage(String.format("%-25s: \t%4f", "Turning Distance", distanceNeededToCompleteTurn));

			double speed = tracker.getLV().getSpeed();
			final double traversableDistance = tracker.calculateTraversableDistance(speed);
			if (SHOW_MESSAGES)
				this.buildMessage(String.format("%-25s: \t%4f", "Traversable Distance", traversableDistance));

			final double distanceUsed = Math.min(tracker.getLoc().distanceBetween(this.getDestination(tracker)),
					Math.min(traversableDistance, distanceNeededToCompleteTurn));
			if (SHOW_MESSAGES)
				this.buildMessage(String.format("%-25s: \t%4f", "Distance Used", distanceUsed));

			double turnSpeedAdjustment = 0;
			if (distanceUsed < distanceNeededToCompleteTurn)
			{
//				turnSpeedAdjustment = DecelerationRate.MAXIMUM.getRate() * tracker.getAccelerationCoefficient();
				turnSpeedAdjustment = Math.max(
						DecelerationRate.MAXIMUM.getRate(),
						tracker.getPath().getCurrentWaypoint().getMinTurnSpeed() - speed);
				if (SHOW_MESSAGES)
					this.buildMessage(String.format("%-25s: \t%4f", "Turn Speed Adjustment", turnSpeedAdjustment));
			}

			tracker.turn(angleAdjustment * distanceUsed / distanceNeededToCompleteTurn);
			turnSpeedAdjustment = Math.max(turnSpeedAdjustment, -tracker.getLV().getSpeed());
			tracker.move(tracker.getLV().newFrom(null, null, turnSpeedAdjustment), distanceUsed);
			if (SHOW_MESSAGES)
				this.buildMessage(String.format("%-25s: %s", "Turning",
						String.format("%3.2f %s %s", tracker.getPctRemaining(), tracker.getLV(), tracker.getLoc())));

			return true;
		}

		return false;
	}

}
