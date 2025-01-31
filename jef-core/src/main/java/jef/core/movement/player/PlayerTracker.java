package jef.core.movement.player;

import jef.core.Player;
import jef.core.geometry.LineSegment;
import jef.core.movement.DefaultAngularVelocity;
import jef.core.movement.LinearVelocity;
import jef.core.movement.Location;
import jef.core.movement.Posture;
import jef.core.movement.Tracker;

public class PlayerTracker extends Tracker
{
	private final Steerable steerable;
	private Path path;
	private Posture posture;

	public PlayerTracker(Steerable steerable, final double timeInterval)
	{
		super(steerable.getLV(), steerable.getLoc(), steerable.getAV(), timeInterval);
		this.steerable = steerable;
		this.path = steerable.getPath();
		this.posture = steerable.getPosture();
	}

	public PlayerTracker(final PlayerTracker tracker)
	{
		super(tracker);
		this.steerable = tracker.steerable;
		this.path = tracker.path;
		this.posture = tracker.getPosture();
	}

	public Posture getPosture()
	{
		return this.posture;
	}

	public void setPosture(Posture posture)
	{
		this.posture = posture;
	}

	public double getAccelerationCoefficient()
	{
		return this.steerable.getAccelerationCoefficient();
	}
	
	public double getDesiredSpeed()
	{
		return steerable.getDesiredSpeed();
	}
	
	public Path getPath()
	{
		return this.path;
	}

	public double getMaxSpeed()
	{
		return this.steerable.getMaxSpeed();
	}
	
	public void setPath(Path path)
	{
		this.path = path;
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

	public boolean hasPastDestination()
	{
		final Location origin = this.getStartingLoc();
		final Location dest = this.steerable.getPath().getCurrentWaypoint().getDestination();

		final LineSegment line = new LineSegment(origin, dest);
		final LineSegment perpLine = line.getPerpendicularLine(dest, line.getLength());

		final LineSegment currentLine = new LineSegment(this.getStartingLoc(), this.getLoc());
		final Location intersection = perpLine.xyIntersection(currentLine);

		return intersection != null;
	}

	@Override
	public double move()
	{
		final double ret = super.move();

		// when LV = 0, azimuth becomes 0 also and we don't want to change AV
		// orientation in that case
		if (this.getLV().getSpeed() > 0)
		{
			this.setAV(new DefaultAngularVelocity(this.getLV().getAzimuth(), 0, 0));
		}

		return ret;
	}

	@Override
	public double move(final LinearVelocity lvAdjustment, final Double maximumDistance)
	{
		final double ret = super.move(lvAdjustment, maximumDistance);

		// when LV = 0, azimuth becomes 0 also and we don't want to change AV
		// orientation in that case
		if (this.getLV().getSpeed() > 0)
		{
			this.setAV(new DefaultAngularVelocity(this.getLV().getAzimuth(), 0, 0));
		}

		return ret;
	}

	@Override
	public void moveRemaining(final double speedAdjustment)
	{
		super.moveRemaining(speedAdjustment);

		// when LV = 0, azimuth becomes 0 also and we don't want to change AV
		// orientation in that case
		if (this.getLV().getSpeed() > 0)
		{
			this.setAV(new DefaultAngularVelocity(this.getLV().getAzimuth(), 0, 0));
		}
	}

	/**
	 * Simply adjust the linear velocity to point in the direction created by adding
	 * the adjustment to the current velocity
	 *
	 * @param angleAdjustment
	 */
	public void turn(final double angleAdjustment)
	{
		this.setLV(this.getLV().newFrom(this.getLV().getAzimuth() + angleAdjustment, null, null));

		// when LV = 0, azimuth becomes 0 also and we don't want to change AV
		// orientation in that case
		if (this.getLV().getSpeed() > 0)
		{
			this.setAV(new DefaultAngularVelocity(this.getLV().getAzimuth(), 0, 0));
		}
	}

}
