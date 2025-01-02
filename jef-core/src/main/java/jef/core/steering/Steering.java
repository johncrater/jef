package jef.core.steering;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.util.MathUtils;

import jef.core.Player;
import jef.core.units.AngularVelocity;
import jef.core.units.LinearVelocity;
import jef.core.units.Location;

public class Steering implements Iterable<Steerable>, Iterator<Steerable>
{
	private static double[] pctTimes =
	{ .333f, .600f, 1.10f, 1.66f, 2.33f, 6.00f };

	private final Steerable steerable;
	private final double timeInterval;
	private double remainingTime;

	public Steering(final Steerable steerable, final double timeInterval)
	{
		this.steerable = steerable;
		this.timeInterval = timeInterval;
	}

	@Override
	public boolean hasNext()
	{
		return !((getWaypoints().size() == 0)
				&& this.steerable.getLinearVelocity().isCloseToZero());
	}

	@Override
	public Iterator<Steerable> iterator()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Steerable next()
	{
		this.remainingTime = this.timeInterval;
		if (getWaypoints().size() == 0)
		{
			this.coastToAStop();
			return this.steerable;
		}

		final double startingSpeed = getSpeed();
		if (startingSpeed > getMaxSpeed())
		{
			this.outOfControl(startingSpeed);
			return this.steerable;
		}

		final double newAngle = this.calculateAngleOfTurn(this.steerable.getLocation(), this.steerable.getDestination(),
				this.steerable.getLinearVelocity().getAzimuth());

		if (LinearVelocity.withinEpsilon(0, startingSpeed))
		{
			// if we are just standing there like a rock we need to at least point ourselves
			// in the right direction..
			this.steerable.turn(newAngle);
		}
		else
		{
	
			final LinearVelocity velocity = this.steerable.getLinearVelocity();
	
			// turns of any significance only happen at the beginning of a waypoint.
			// if the current waypoint is the last one, then we use the current waypoint's
			// turning speed to decelerate
			// prior to reaching the waypoint.
			// if the current waypoint is not the last one, use the next waypoint's turning
			// speed to decelerate
			// prior to reaching the waypoint. The next waypoint will be able to accelerate
			// out of the turn and minimize
			// any distance.
	
			final double distanceRemainingToDestination = this.steerable.getLocation()
					.distanceBetween(getDestination());
			double decelerationAdjustment = 0;
			final List<Waypoint> waypoints = getWaypoints();
			final Waypoint currentWaypoint = getCurrentWaypoint();
	
			if ((getWaypoints().size() == 1)
					&& (currentWaypoint.getDestinationAction() == Waypoint.DestinationAction.hardStop))
				decelerationAdjustment = this.decelerate(velocity, 0, distanceRemainingToDestination,
						this.remainingTime);
			else if (waypoints.size() > 1)
				decelerationAdjustment = this.decelerate(velocity, this.steerable.getTurningSpeed(),
						distanceRemainingToDestination, this.remainingTime);
	
			if (decelerationAdjustment != 0)
				this.steerable.adjustSpeed(decelerationAdjustment);
	
			final double minTurnRadius = this.calculateTightestRadiusTurnForAtSpeed(
					getSpeed(), getMaxSpeed());
			double distanceNeededToCompleteTurn = Math.abs(minTurnRadius * newAngle);
	
			if ((distanceNeededToCompleteTurn > 0) && (distanceNeededToCompleteTurn > this.steerable.getLocation()
					.distanceBetween(currentWaypoint.getDestination())))
			{
				double turnSpeedAdjustment = this.calculateTurnSpeedAdjustment(
						getSpeed(), this.steerable.getTurningSpeed(),
						this.steerable.getDesiredSpeed(), this.remainingTime);
	
				// if we still can't make the turn given the limitations of the waypoint's
				// turnSpeed
				// we will just have to try harder. Otherwise, we could loop for ever in a
				// perpetual holding pattern
				final double maxSpeedToMakeTurn = this.calculateMaximumSpeedForRadiusTurn(minTurnRadius,
						getMaxSpeed());
	//				if (maxSpeedToMakeTurn > ray.getSpeed() - turnSpeedAdjustment
	//						&& Utils.normalizeAngleInDegrees(angleAdjustment) >= 45)
	
				// I had a limitation on the angle (see above). But I'm not sure why. Perhaps I
				// will
				// come back around and fix it. But for now...
				if (maxSpeedToMakeTurn < (getSpeed() - turnSpeedAdjustment))
					turnSpeedAdjustment = Math.max(
							maxSpeedToMakeTurn
									- (getSpeed() - turnSpeedAdjustment),
							Player.maximumDecelerationRate * this.remainingTime);
	
				if (turnSpeedAdjustment != 0)
					this.steerable.adjustSpeed(turnSpeedAdjustment);
	
				distanceNeededToCompleteTurn = this.calculateDistanceNeededToCompleteTurn(newAngle,
						getSpeed(), getMaxSpeed());
			}
	
			if (distanceNeededToCompleteTurn > 0)
			{
				// hold on boys, we're turning
	
				final double distanceUsed = Math.min(
						this.steerable.getLocation().distanceBetween(currentWaypoint.getDestination()),
						Math.min(getSpeed(), distanceNeededToCompleteTurn));
	
				this.steerable.turn((newAngle * distanceUsed) / distanceNeededToCompleteTurn);
				this.steerable.move(distanceUsed);
			}
		}
		
		// if the speed has not changed we can accelerate
		if (this.steerable.getSpeed() >= startingSpeed)
		{
			// calculate where we are in the acceleration cycle
			double elapsedTime = this.calculateElapsedTime(this.steerable.getSpeed() / getMaxSpeed());
			elapsedTime += this.remainingTime;
			double newSpeed = this.calculateSpeed(elapsedTime);
			newSpeed *= getMaxSpeed();
			newSpeed = Math.min(newSpeed, this.steerable.getDesiredSpeed());

			if ((newSpeed - getSpeed()) != 0)
				this.steerable.adjustSpeed(newSpeed - getSpeed());
		}

		this.steerable.move(this.steerable.getLinearVelocity().multiply(remainingTime));

		if (this.destinationReached())
		{
			if (getWaypoints().size() > 1)
			{
				this.switchWaypoints();
				return this.next();
			}
			final double distance = this.steerable.getLocation()
					.distanceBetween(getDestination());
			final Location nextTickLocationEstimate = this.steerable.getLocation()
					.adjust(this.steerable.getLinearVelocity());
			if (nextTickLocationEstimate
					.distanceBetween(getDestination()) >= distance)
				this.steerable.setLocation(getDestination());

			this.switchWaypoints();
		}

		return this.steerable;
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

	private double getSpeed()
	{
		return this.steerable.getLinearVelocity().getXYDistance();
	}

	private List<Waypoint> getWaypoints()
	{
		return this.steerable.getPath().getWaypoints();
	}

	private double calculateAngleOfTurn(final Location loc, final Location destination, final double currentAngle)
	{
		final double desiredAngle = loc.angleTo(destination);
		final double angularDiff = desiredAngle - currentAngle;
		return MathUtils.normalizeAngle(angularDiff, 0.0);
	}

	private double calculateDecelerationDistanceToStop(final double initialVelocity, final double finalVelocity,
			final double decelerationRate)
	{
		// using Velocity.div(Velocity) is AWFUL! It assumes they are of the same unit
		// and does not first convert
		// NEVER use it

		return (Math.pow(finalVelocity, 2) - Math.pow(initialVelocity, 2)) / (2 * decelerationRate);
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
			ret = (radiusInYards / 1.1) * .4;
		else if (radiusInYards <= 2.2)
			ret = .4 + ((radiusInYards - 1.1) * .1);
		else if (radiusInYards <= 3.3)
			ret = .5 + ((radiusInYards - 2.2) * .1);
		else if (radiusInYards <= 4.4)
			ret = .6 + ((radiusInYards - 3.3) * .1);
		else if (radiusInYards <= 5.5)
			ret = .65 + ((radiusInYards - 4.4) * .05);
		else if (radiusInYards <= 6.6)
			ret = .70 + ((radiusInYards - 5.5) * .05);
		else if (radiusInYards <= 7.7)
			ret = .75 + ((radiusInYards - 6.6) * .05);
		else if (radiusInYards <= 8.8)
			ret = .80 + ((radiusInYards - 7.7) * .05);
		else if (radiusInYards <= 9.9)
			ret = .85 + ((radiusInYards - 8.8) * .05);
		else if (radiusInYards <= 11)
			ret = .90 + ((radiusInYards - 9.9) * .05);
		else if (radiusInYards <= 12.1)
			ret = .95 + ((radiusInYards - 11) * .05);

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

	private double calculateTurnSpeedAdjustment(final double currentSpeed, final double minimumTurnSpeed,
			final double maximumSpeed, final double deltaTime)
	{
		if (currentSpeed < minimumTurnSpeed)
			// final double elapsedTime = PlayerMover.calculateElapsedTime(currentSpeed /
			// maximumSpeed);
//			return Math.min(PlayerMover.calculateSpeed(elapsedTime) * maximumSpeed, maximumSpeed) - currentSpeed;
			return 0;

		if (currentSpeed > maximumSpeed)
			return Math.max(maximumSpeed, currentSpeed + (Player.maximumDecelerationRate * deltaTime)) - currentSpeed;

		return (Math.max(minimumTurnSpeed, currentSpeed) + (Player.maximumDecelerationRate * deltaTime)) - currentSpeed;
	}

	private void coastToAStop()
	{
		// coast to a stop
		double currentSpeed = getSpeed();
		currentSpeed += Player.normalDecelerationRate * this.remainingTime;
		currentSpeed = Math.max(0, currentSpeed);
		this.steerable.setSpeed(currentSpeed);
		this.steerable.move(this.steerable.getLinearVelocity());
		this.remainingTime = 0;
	}

	private double decelerate(final LinearVelocity initialVector, final double finalVelocity,
			final double distanceRemaining, final double deltaTime)
	{
		// otherwise we see if we need to slow down
		final double distanceNeededToDecelerate = this.calculateDecelerationDistanceToStop(finalVelocity,
				initialVector.getXYDistance(), Player.maximumDecelerationRate);

		// technically this should be distanceRemaining < distanceNeededToStop
		// but if distanceRemaining gets less than distanceNeededToStop then
		// it may be hard to catch up. So, we have a buffer of decelerationRate
		// have to include EPSILON as well or our calc is off
		if (distanceRemaining < (distanceNeededToDecelerate
				+ ((Player.maximumDecelerationRate + LinearVelocity.EPSILON) * deltaTime)))
			return Math.min(Player.maximumDecelerationRate * deltaTime, 0);

		return 0;
	}

	private boolean destinationReached()
	{
		final Waypoint waypoint = getCurrentWaypoint();
		if (waypoint == null)
			return true;

		return this.steerable.getLocation()
				.closeEnoughTo(getDestination());
	}

	private void outOfControl(final double startingSpeed)
	{
		// we are out of control and will fall if we can't decelerate below
		// getMaxSpeed()
		// immediately no controlled action can be taken, so we return immediately
		double adjustment = Player.normalDecelerationRate * this.remainingTime;
		if ((startingSpeed + adjustment) > getMaxSpeed())
			adjustment = -startingSpeed;

		this.steerable.adjustSpeed(adjustment);
		this.steerable.move(this.steerable.getLinearVelocity());
	}

	private void switchWaypoints()
	{
		if (getWaypoints().size() == 0)
			return;

		this.steerable.getPath().removeWaypoint(getCurrentWaypoint());
	}

}
