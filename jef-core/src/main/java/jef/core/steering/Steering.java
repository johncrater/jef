package jef.core.steering;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.apache.commons.math3.util.MathUtils;

import jef.core.Location;
import jef.core.Player;
import jef.core.Tracker;
import jef.core.units.DefaultLinearVelocity;

public class Steering
{
	private static double[] pctTimes =
	{ .333f, .600f, 1.10f, 1.66f, 2.33f, 6.00f };

	private final Steerable steerable;
	private List<Waypoint> waypoints;

	private ByteArrayOutputStream baos;
	private PrintStream out;

	public Steering(final Steerable steerable)
	{
		this.steerable = steerable;
		this.waypoints = steerable.getPath().getWaypoints();
	}

	public List<Waypoint> next(final Tracker tracker)
	{
		this.baos = new ByteArrayOutputStream();
		this.out = new PrintStream(this.baos);

		this.buildMessage(String.format("%-25s: %s", "Initial", this.printDetails(tracker)));

		if (this.getWaypoints().size() == 0)
		{
			this.coastToAStop(tracker);
			return this.waypoints;
		}

		final double startingSpeed = tracker.getLV().getDistance();
		if (startingSpeed > this.getMaxSpeed())
		{
			this.outOfControl(tracker);
			return this.waypoints;
		}

		final double newAngle = this.calculateAngleOfTurn(tracker.getLoc(), this.steerable.getDestination(),
				tracker.getLV().getAzimuth());

		if (DefaultLinearVelocity.withinEpsilon(0, startingSpeed))
		{
			// if we are just standing there like a rock we need to at least point ourselves
			// in the right direction..
			tracker.turn(newAngle);
			this.buildMessage(String.format("%-25s: %s", "No Velocity Turn", this.printDetails(tracker)));
		}
		else
		{

			// turns of any significance only happen at the beginning of a waypoint.
			// if the current waypoint is the last one, then we use the current waypoint's
			// turning speed to decelerate
			// prior to reaching the waypoint.
			// if the current waypoint is not the last one, use the next waypoint's turning
			// speed to decelerate
			// prior to reaching the waypoint. The next waypoint will be able to accelerate
			// out of the turn and minimize
			// any distance.

			final double distanceRemainingToDestination = tracker.getLoc().distanceBetween(this.getDestination());
			double accumulatedSpeedChanges = 0;

			double decelerationAdjustment = 0;
			final List<Waypoint> waypoints = this.getWaypoints();
			final Waypoint currentWaypoint = this.getCurrentWaypoint();

			if ((this.getWaypoints().size() == 1)
					|| (currentWaypoint.getDestinationAction() == Waypoint.DestinationAction.hardStop))
			{
				decelerationAdjustment = this.decelerate(tracker, 0, distanceRemainingToDestination);
			}
			else if (waypoints.size() > 1)
			{
				decelerationAdjustment = this.decelerate(tracker, this.steerable.getTurningSpeed(),
						distanceRemainingToDestination);
			}

			if (decelerationAdjustment != 0)
			{
				accumulatedSpeedChanges += decelerationAdjustment;
				this.buildMessage(String.format("%-25s: %s", "Deceleration", this.printDetails(tracker)));
			}

			final double minTurnRadius = this.calculateTightestRadiusTurnForAtSpeed(tracker.getLV().getDistance(),
					this.getMaxSpeed());
			double distanceNeededToCompleteTurn = Math.abs(minTurnRadius * newAngle);

			if ((distanceNeededToCompleteTurn > 0) && (distanceNeededToCompleteTurn > tracker.getLoc()
					.distanceBetween(currentWaypoint.getDestination())))
			{
				double turnSpeedAdjustment = this.calculateTurnSpeedAdjustment(tracker,
						this.steerable.getTurningSpeed(), this.steerable.getDesiredSpeed());

				// if we still can't make the turn given the limitations of the waypoint's
				// turnSpeed
				// we will just have to try harder. Otherwise, we could loop for ever in a
				// perpetual holding pattern
				final double maxSpeedToMakeTurn = this.calculateMaximumSpeedForRadiusTurn(minTurnRadius,
						this.getMaxSpeed());

				// I had a limitation on the angle (see above). But I'm not sure why. Perhaps I
				// will
				// come back around and fix it. But for now...
				if (maxSpeedToMakeTurn < (tracker.getLV().getDistance() - turnSpeedAdjustment))
				{
					turnSpeedAdjustment = Math.max(
							maxSpeedToMakeTurn - (tracker.getLV().getDistance() - turnSpeedAdjustment),
							Player.maximumDecelerationRate);
				}

				if (turnSpeedAdjustment != 0)
				{
					accumulatedSpeedChanges += turnSpeedAdjustment;
					this.buildMessage(String.format("%-25s: %s", "Slowing for long turn", this.printDetails(tracker)));
					this.buildMessage(String.format("\tminTurnRadius. %.5f", minTurnRadius));
					this.buildMessage(
							String.format("\tdistanceNeededToCompleteTurn. %.5f", distanceNeededToCompleteTurn));
					this.buildMessage(String.format("\tturnSpeedAdjustment. %.5f", turnSpeedAdjustment));
					this.buildMessage(String.format("\tmaxSpeedToMakeTurn. %.5f", maxSpeedToMakeTurn));
				}

				distanceNeededToCompleteTurn = this.calculateDistanceNeededToCompleteTurn(newAngle,
						tracker.getLV().getDistance(), this.getMaxSpeed());
			}

			if (distanceNeededToCompleteTurn > 0)
			{
				// hold on boys, we're turning
				double distanceRemaining = tracker
						.calculateTraverableDistance(tracker.getLV().getDistance() + accumulatedSpeedChanges);

				final double distanceUsed = Math.min(tracker.getLoc().distanceBetween(currentWaypoint.getDestination()),
						Math.min(distanceRemaining, distanceNeededToCompleteTurn));

				tracker.move(
						new DefaultLinearVelocity(tracker.getLV().getElevation(),
								(newAngle * distanceUsed) / distanceNeededToCompleteTurn, accumulatedSpeedChanges),
						distanceNeededToCompleteTurn);
				this.buildMessage(String.format("%-25s: %s", "Turning", this.printDetails(tracker)));
				this.buildMessage(String.format("\tdistanceNeededToCompleteTurn. %.5f", distanceNeededToCompleteTurn));
				this.buildMessage(String.format("\tdistanceUsed. %.5f", distanceUsed));
			}
		}

		// if the speed has not changed we can accelerate
		double accumulatedSpeedChanges = 0;
		if (tracker.getLV().getDistance() >= startingSpeed)
		{
			// calculate where we are in the acceleration cycle
			double elapsedTime = this.calculateElapsedTime(tracker.getLV().getDistance() / this.getMaxSpeed());
			elapsedTime += tracker.getRemainingTime();
			double newSpeed = this.calculateSpeed(elapsedTime);
			newSpeed *= this.getMaxSpeed();
			newSpeed = Math.min(newSpeed, this.steerable.getDesiredSpeed());

			if ((newSpeed - tracker.getLV().getDistance()) != 0)
			{
				accumulatedSpeedChanges = newSpeed - tracker.getLV().getDistance();
				this.buildMessage(String.format("%-25s: %s", "Accelerating", this.printDetails(tracker)));
			}
		}

		tracker.moveRemaining(accumulatedSpeedChanges);
		this.buildMessage(String.format("%-25s: %s", "Moved", this.printDetails(tracker)));

		if (this.destinationReached(tracker))
		{
			if (this.getWaypoints().size() > 1)
			{
				waypoints.removeFirst();
				this.buildMessage(String.format("%-25s: %s", "Destination Reached", this.printDetails(tracker)));
				return this.next(tracker);
			}

			waypoints.removeFirst();
			this.buildMessage(String.format("%-25s: %s", "Waypoint Reached", this.printDetails(tracker)));
		}

		this.buildMessage(String.format("%-25s: %s", "Final", this.printDetails(tracker)));
		return this.waypoints;
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

	private double calculateDecelerationDistanceToStop(Tracker tracker, final double finalVelocity,
			final double decelerationRate)
	{
		// using Velocity.div(Velocity) is AWFUL! It assumes they are of the same unit
		// and does not first convert
		// NEVER use it

		return tracker.calculateDistanceToReachSpeed(decelerationRate, finalVelocity);
	}

	private double calculateDistanceNeededToCompleteTurn(final double angle, final double speed,
			final double maximumSpeed)
	{
		// angle could be negative. But we are only interested in abs value
		final double minTurnRadius = this.calculateTightestRadiusTurnForAtSpeed(speed, maximumSpeed);
		return Math.abs(minTurnRadius * angle);
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
	private double calculateMaximumSpeedForRadiusTurn(final double radiusInYards, final double maximumSpeed)
	{
		double ret = 1;

		if (radiusInYards <= 1.1)
		{
			ret = (radiusInYards / 1.1) * .4;
		}
		else if (radiusInYards <= 2.2)
		{
			ret = .4 + ((radiusInYards - 1.1) * .1);
		}
		else if (radiusInYards <= 3.3)
		{
			ret = .5 + ((radiusInYards - 2.2) * .1);
		}
		else if (radiusInYards <= 4.4)
		{
			ret = .6 + ((radiusInYards - 3.3) * .1);
		}
		else if (radiusInYards <= 5.5)
		{
			ret = .65 + ((radiusInYards - 4.4) * .05);
		}
		else if (radiusInYards <= 6.6)
		{
			ret = .70 + ((radiusInYards - 5.5) * .05);
		}
		else if (radiusInYards <= 7.7)
		{
			ret = .75 + ((radiusInYards - 6.6) * .05);
		}
		else if (radiusInYards <= 8.8)
		{
			ret = .80 + ((radiusInYards - 7.7) * .05);
		}
		else if (radiusInYards <= 9.9)
		{
			ret = .85 + ((radiusInYards - 8.8) * .05);
		}
		else if (radiusInYards <= 11)
		{
			ret = .90 + ((radiusInYards - 9.9) * .05);
		}
		else if (radiusInYards <= 12.1)
		{
			ret = .95 + ((radiusInYards - 11) * .05);
		}

		return ret * maximumSpeed;
	}

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
			return (1.0f * pctOfMaximumSpeed) / .40f;

		if (pctOfMaximumSpeed < .5f)
			return 2.0f + ((pctOfMaximumSpeed - .40f) / .1f);

		if (pctOfMaximumSpeed < .6f)
			return 3.0f + ((pctOfMaximumSpeed - .50f) / .1f);

		if (pctOfMaximumSpeed < .65f)
			return 4.0f + ((pctOfMaximumSpeed - .60f) / .5f);

		if (pctOfMaximumSpeed < .70f)
			return 5.0f + ((pctOfMaximumSpeed - .65f) / .5f);

		if (pctOfMaximumSpeed < .75f)
			return 6.0f + ((pctOfMaximumSpeed - .70f) / .5f);

		if (pctOfMaximumSpeed < .80f)
			return 7.0f + ((pctOfMaximumSpeed - .75f) / .5f);

		if (pctOfMaximumSpeed < .85f)
			return 8.0f + ((pctOfMaximumSpeed - .80f) / .5f);

		if (pctOfMaximumSpeed < .90f)
			return 9.0f + ((pctOfMaximumSpeed - .85f) / .5f);

		if (pctOfMaximumSpeed < .95f)
			return 10.0f + ((pctOfMaximumSpeed - .90f) / .5f);

		if (pctOfMaximumSpeed < 1.0f)
			return 11.0f + ((pctOfMaximumSpeed - .95f) / .5f);

		return 12.0f;
	}

	private double calculateTurnSpeedAdjustment(Tracker tracker, final double minimumTurnSpeed,
			final double maximumSpeed)
	{
		double currentSpeed = tracker.getLV().getDistance();

		if (currentSpeed < minimumTurnSpeed)
			return 0;

		if (currentSpeed > maximumSpeed)
			return Math.max(maximumSpeed, currentSpeed + tracker.calculateAdjustedSpeed(Player.maximumDecelerationRate))
					- currentSpeed;

		return (Math.max(minimumTurnSpeed, currentSpeed)
				+ tracker.calculateAdjustedSpeed(Player.maximumDecelerationRate)) - currentSpeed;
	}

	private void coastToAStop(final Tracker tracker)
	{
		// coast to a stop
		tracker.moveRemaining(Player.normalDecelerationRate);
		this.buildMessage(String.format("%-25s: %s", "Coasting To Stop", this.printDetails(tracker)));
	}

	private double decelerate(Tracker tracker, final double finalVelocity, final double distanceRemaining)
	{
		double decelerationRate = Player.maximumDecelerationRate;
		final double distanceNeededToDecelerate = tracker.calculateDistanceToReachSpeed(decelerationRate,
				finalVelocity);

		if (distanceRemaining < distanceNeededToDecelerate)
			return Math.min(decelerationRate, 0);

		return 0;
	}

	private boolean destinationReached(Tracker tracker)
	{
		final Waypoint waypoint = this.getCurrentWaypoint();
		if (waypoint == null)
			return true;

		return tracker.getLoc().closeEnoughTo(this.getDestination());
	}

	private Waypoint getCurrentWaypoint()
	{
		return this.steerable.getPath().getCurrentWaypoint();
	}

	private Location getDestination()
	{
		return this.steerable.getPath().getDestination();
	}

	private double getMaxSpeed()
	{
		return this.steerable.getMaxSpeed();
	}

	private List<Waypoint> getWaypoints()
	{
		return this.steerable.getPath().getWaypoints();
	}

	private void outOfControl(final Tracker tracker)
	{
		// we are out of control and will fall if we can't decelerate below
		// getMaxSpeed()
		// immediately no controlled action can be taken, so we return immediately

		double adjustment = Player.normalDecelerationRate;
		final double adjustedSpeed = tracker.calculateAdjustedSpeed(adjustment);
		if (adjustedSpeed > this.getMaxSpeed())
		{
			adjustment = this.getMaxSpeed() - adjustedSpeed;
		}

		tracker.moveRemaining(adjustment);
		this.buildMessage(String.format("%-25s: %s", "Out of Control", this.printDetails(tracker)));
	}

	private String printDetails(final Tracker tracker)
	{
		return String.format("%s %s", tracker.getLV(), tracker.getLoc());
	}

}
