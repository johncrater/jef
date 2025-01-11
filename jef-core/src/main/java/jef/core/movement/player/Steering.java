package jef.core.movement.player;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.util.MathUtils;

import jef.core.Player;
import jef.core.movement.Collision;
import jef.core.movement.DefaultLinearVelocity;
import jef.core.movement.LinearVelocity;
import jef.core.movement.Location;
import jef.core.movement.player.Waypoint.DestinationAction;

public class Steering
{
	private static double[] pctTimes =
	{ .333f, .600f, 1.10f, 1.66f, 2.33f, 6.00f };

	private final Steerable steerable;

	private ByteArrayOutputStream baos;
	private PrintStream out;

	public Steering(final Steerable steerable)
	{
		this.steerable = steerable;
	}

	public void next(final PlayerTracker tracker, List<Collision> collisions)
	{
		if ((tracker.getPath().getCurrentWaypoint() == null
				|| tracker.getLoc().closeEnoughTo(this.getDestination())) && tracker.getLV().isNotMoving())
		{
			tracker.setLV(new DefaultLinearVelocity());
			return;
		}
		
		if (collisions == null)
			collisions = Collections.emptyList();
		
		this.baos = new ByteArrayOutputStream();
		this.out = new PrintStream(this.baos);

		this.buildMessage(String.format("%-25s: %s", "Initial",
				String.format("%3.2f %s %s", tracker.getPctRemaining(), tracker.getLV(), tracker.getLoc())));

		if (this.getWaypoints().size() == 0)
		{
			// coasting to a stop
			this.coastToAStop(tracker);
			return;
		}

		final double startingSpeed = tracker.getLV().getSpeed();
		if (startingSpeed > this.getMaxSpeed())
		{
			// too fast
			this.outOfControl(tracker);
			return;
		}

		// do we need to decelerate?
		double speedAdjustment = this.calculateDecelerationAdjustment(tracker);

		final double angleAdjustment = this.calculateAngleOfTurn(tracker.getLoc(), this.getDestination(),
				tracker.getLV().getAzimuth());
		this.buildMessage(String.format("%-25s: \t\t%4f\u00B0", "Angle Adjustment", Math.toDegrees(angleAdjustment)));

		final double newAngle = angleAdjustment + tracker.getLV().getAzimuth();

		this.performTurn(tracker, angleAdjustment, newAngle, speedAdjustment);

		if (startingSpeed <= (tracker.getLV().getSpeed() + speedAdjustment))
		{
			// calculate where we are in the acceleration cycle
			double elapsedTime = this
					.calculateElapsedTime((tracker.getLV().getSpeed() + speedAdjustment) / this.getMaxSpeed());
			elapsedTime += tracker.getRemainingTime() * tracker.getAccelerationCoefficient();
			double newSpeed = this.calculateSpeed(elapsedTime);
			newSpeed *= this.getMaxSpeed();
			newSpeed = Math.min(newSpeed, this.steerable.getDesiredSpeed());
			this.buildMessage(String.format("%-25s: \t%4f", "New Speed", newSpeed));

			speedAdjustment += (newSpeed - startingSpeed) / tracker.getRemainingTime();
		}

		tracker.move(new DefaultLinearVelocity(0.0, newAngle, speedAdjustment), null);

		if (this.destinationReached(tracker) || (tracker.hasPastDestination()
				&& tracker.getPath().getCurrentWaypoint().getDestinationAction() == DestinationAction.noStop))
		{
			tracker.getPath().removeWaypoint(tracker.getPath().getCurrentWaypoint());

			if (this.getWaypoints().size() > 1)
			{
				this.buildMessage(String.format("%-25s: %s", "Destination Reached",
						String.format("%3.2f %s %s", tracker.getPctRemaining(), tracker.getLV(), tracker.getLoc())));
				this.next(tracker, collisions);
			}
			else
			{
				this.buildMessage(String.format("%-25s: %s", "Waypoint Reached",
						String.format("%3.2f %s %s", tracker.getPctRemaining(), tracker.getLV(), tracker.getLoc())));
			}
		}

		this.buildMessage(String.format("%-25s: %s", "Final",
				String.format("%3.2f %s %s", tracker.getPctRemaining(), tracker.getLV(), tracker.getLoc())));
	}

	private void buildMessage(final String msg)
	{
		System.out.println(msg);
		this.out.println(msg);
	}

	private double calculateAngleOfTurn(final Location loc, final Location destination, final double currentAngle)
	{
		final double desiredAngle = loc.angleTo(destination);
		final double angularDiff = desiredAngle - currentAngle;
		return MathUtils.normalizeAngle(angularDiff, 0.0);
	}

	private double calculateDecelerationAdjustment(final PlayerTracker tracker)
	{
		final double distanceRemainingToDestination = tracker.getLoc().distanceBetween(this.getDestination());

		double decelerationAdjustment = this.decelerate(tracker, 0, distanceRemainingToDestination);
		if (decelerationAdjustment != 0)
		{
			this.buildMessage(String.format("%-25s: \t\t%4f", "Deceleration Adjustment", decelerationAdjustment));
		}

		return decelerationAdjustment;
	}

	private double calculateDistanceNeededToCompleteTurn(final PlayerTracker tracker, final double newAngle)
	{
		final double minTurnRadius = this.calculateTightestRadiusTurnForAtSpeed(tracker.getLV().getSpeed(),
				this.getMaxSpeed()) / tracker.getAccelerationCoefficient();
		return  Math.abs(minTurnRadius * newAngle);
	}

	private double calculateElapsedTime(final double pctOfMaxSpeed)
	{
		double elapsedTime = 0;
		final double increment = 1.0 / Steering.pctTimes.length;
		double previousElapsedTime = 0;
		for (int i = 0; i < Steering.pctTimes.length; i++)
		{
			if (pctOfMaxSpeed <= ((i + 1) * increment))
			{
				final double pctOfIncrementRemaining = (pctOfMaxSpeed - (i * increment)) / increment;
				final double secondsToCover = Steering.pctTimes[i] - previousElapsedTime;
				elapsedTime += pctOfIncrementRemaining * secondsToCover;
				break;
			}
			elapsedTime = Steering.pctTimes[i];
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
		final double increment = 1 / (double) Steering.pctTimes.length;
		for (final double pctTime : Steering.pctTimes)
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
	private double calculateTightestRadiusTurnForAtSpeed(final double currentSpeed, final double maximumSpeed)
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
		tracker.moveRemaining(Player.normalDecelerationRate * tracker.getAccelerationCoefficient());
		this.buildMessage(String.format("%-25s: %s", "Coasting To Stop",
				String.format("%3.2f %s %s", tracker.getPctRemaining(), tracker.getLV(), tracker.getLoc())));
	}

	private double decelerate(final PlayerTracker tracker, final double finalVelocity, final double distanceRemaining)
	{
		final double decelerationRate = tracker.getAccelerationCoefficient() * switch (tracker.getPath().getCurrentWaypoint().getDestinationAction())
		{
			case fastStop -> Player.maximumDecelerationRate;
			case normalStop -> Player.normalDecelerationRate;
			case slowStop -> Player.slowDecelerationRate;
			case noStop -> 0;
		};

		final double distanceNeededToDecelerate = tracker.calculateDistanceToReachSpeed(decelerationRate,
				finalVelocity);

		if (distanceRemaining < distanceNeededToDecelerate)
			return Math.min(decelerationRate, 0);

		return 0;
	}

	private boolean destinationReached(final PlayerTracker tracker)
	{
		final Waypoint waypoint = this.steerable.getPath().getCurrentWaypoint();
		if (waypoint == null)
			return true;

		return tracker.getLoc().closeEnoughTo(this.getDestination());
	}

	private Location getDestination()
	{
		return this.steerable.getPath().getCurrentWaypoint().getDestination();
	}

	private double getMaxSpeed()
	{
		return this.steerable.getMaxSpeed();
	}

	private List<Waypoint> getWaypoints()
	{
		return this.steerable.getPath().getWaypoints();
	}

	private void outOfControl(final PlayerTracker tracker)
	{
		// we are out of control and will fall if we can't decelerate below
		// getMaxSpeed()
		// immediately no controlled action can be taken, so we return immediately

		// we use normal deceleration rate since we are out of control and it takes
		// control to decelerate
		// at the fastest rate
		
		
		
		double adjustment = Player.normalDecelerationRate * tracker.getAccelerationCoefficient();
		final double adjustedSpeed = tracker.calculateAdjustedSpeed(adjustment);
		if (adjustedSpeed > this.getMaxSpeed())
		{
			// we are falling. need to improve this
			adjustment = -adjustedSpeed / 2;
		}

		tracker.moveRemaining(adjustment);
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
			this.buildMessage(String.format("%-25s: \t%4f", "Turning Distance", distanceNeededToCompleteTurn));

			double turnSpeedAdjustment = 0; // Math.min(tracker.getLV().getSpeed() - minimumTurnSpeed, this.calculateTurnSpeedAdjustment(tracker));
//			this.buildMessage(String.format("%-25s: \t%4f", "Turn Speed Adjustment", turnSpeedAdjustment));

			final double distanceRemaining = tracker
					.calculateTraversableDistance(tracker.getLV().add(turnSpeedAdjustment).getSpeed());
			this.buildMessage(String.format("%-25s: \t%4f", "Distance Remaining", distanceRemaining));

			final double distanceUsed = Math.min(tracker.getLoc().distanceBetween(this.getDestination()),
					Math.min(distanceRemaining, distanceNeededToCompleteTurn));
			this.buildMessage(String.format("%-25s: \t%4f", "Distance Used", distanceUsed));
			
			if (distanceUsed < distanceNeededToCompleteTurn)
			{
				turnSpeedAdjustment = Player.maximumDecelerationRate * tracker.getAccelerationCoefficient();
				this.buildMessage(String.format("%-25s: \t%4f", "Turn Speed Adjustment", turnSpeedAdjustment));
			}
			
			// we aren't adding the direction, we are setting it explicitly
			tracker.turn(angleAdjustment * distanceUsed / distanceNeededToCompleteTurn);
			tracker.move(tracker.getLV().newFrom(null, null, turnSpeedAdjustment), distanceUsed);
			this.buildMessage(String.format("%-25s: %s", "Turning",
					String.format("%3.2f %s %s", tracker.getPctRemaining(), tracker.getLV(), tracker.getLoc())));

			return true;
		}

		return false;
	}

}
