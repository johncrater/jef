package jef.core.movement.player;

import jef.core.movement.AngularVelocity;
import jef.core.movement.LinearVelocity;
import jef.core.movement.Location;
import jef.core.movement.Moveable;
import jef.core.movement.Tracker;

public class PlayerTracker extends Tracker
{
	private Path path;
	
	public PlayerTracker(Path path, double timeInterval)
	{
		super(timeInterval);
		this.path = path;
	}

	public PlayerTracker(Path path, LinearVelocity lv, Location loc, AngularVelocity av, double timeInterval)
	{
		super(lv, loc, av, timeInterval);
		this.path = path;
	}

	public PlayerTracker(Path path, Moveable moveable, double timeInterval)
	{
		super(moveable, timeInterval);
		this.path = path;
	}

	public Path getPath()
	{
		return this.path;
	}

	/**
	 * Simply adjust the linear velocity to point in the direction created by adding
	 * the adjustment to the current velocity
	 *
	 * @param angleAdjustment
	 */
	public void turn(final double angleAdjustment)
	{
		this.setLV(this.getLV().newFrom(null, this.getLV().getAzimuth() + angleAdjustment, null));
	}

	/**
	 * Calculates the distance necessary to reach the desired speed using the
	 * current direction.
	 *
	 * @param accelerationRate
	 * @param desiredSpeed
	 * @return a positive value to indicate the distance or a negative value meaning
	 *         the desiredSpeed cannot be reached using the acceleration rate
	 *         argument. This will happen if the acceleration rate is negative and
	 *         the desired speed is greater that the current speed or if the
	 *         acceleration rate is positive and the desired speed is less than the
	 *         current speed
	 */
	public double calculateDistanceToReachSpeed(final double accelerationRate, final double desiredSpeed)
	{
		return (Math.pow(desiredSpeed, 2) - Math.pow(this.getLV().getSpeed(), 2)) / (2 * accelerationRate);
	}

	/**
	 * Calculates the speed after the adjust taking into consideration of remaining
	 * time and the lower limit of zero
	 *
	 * @param speedAdjustment y/s speed adjustment
	 * @return The adjusted speed. Does not consider any limitations on maximum
	 *         speed.
	 */
	public double calculateAdjustedSpeed(double speedAdjustment)
	{
		speedAdjustment = speedAdjustment * this.getRemainingTime();
		return Math.max(0, this.getLV().getSpeed() + speedAdjustment);
	}

	
}
