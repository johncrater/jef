package jef.core.movement.player;

import jef.core.Player;
import jef.core.geometry.Line;
import jef.core.movement.DefaultAngularVelocity;
import jef.core.movement.LinearVelocity;
import jef.core.movement.Location;
import jef.core.movement.Posture;
import jef.core.movement.Tracker;

public class PlayerTracker extends Tracker implements Player
{
	private final Player player;

	public PlayerTracker(final Player player, final double timeInterval)
	{
		super(player, timeInterval);
		this.player = player;
	}

	public PlayerTracker(final PlayerTracker tracker)
	{
		super(tracker);
		this.player = tracker.getPlayer();
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

	@Override
	public double getAccelerationCoefficient()
	{
		return this.player.getAccelerationCoefficient();
	}

	@Override
	public double getDesiredSpeed()
	{
		final Waypoint wp = this.player.getPath().getCurrentWaypoint();
		if (wp == null)
			return 0;

		return wp.getMaxSpeed();
	}

	@Override
	public String getFirstName()
	{
		return this.player.getFirstName();
	}

	@Override
	public double getHeightInMeters()
	{
		return this.player.getHeightInMeters();
	}

	@Override
	public String getId()
	{
		return this.player.getId();
	}

	@Override
	public String getLastName()
	{
		return this.player.getLastName();
	}

	@Override
	public double getMassInKilograms()
	{
		return this.player.getMassInKilograms();
	}

	@Override
	public double getMaxSpeed()
	{
		return this.player.getMaxSpeed();
	}

	@Override
	public Path getPath()
	{
		return this.player.getPath();
	}

	public Player getPlayer()
	{
		return this.player;
	}

	@Override
	public Posture getPosture()
	{
		return this.player.getPosture();
	}

	@Override
	public double getSpeed(final Type type)
	{
		return this.player.getSpeed(type);
	}

	public boolean hasPastDestination()
	{
		final Location origin = this.getStartingLoc();
		final Location dest = this.getPath().getCurrentWaypoint().getDestination();

		Line line = new Line(origin, dest);
		Line perpLine = line.getPerpendicularLine(dest);
		
		final Line currentLine = new Line(this.getStartingLoc(), this.getLoc());
		final Location intersection = perpLine.intersects(currentLine);

		return intersection != null;
	}

	@Override
	public void setPath(final Path path)
	{
		this.player.setPath(path);
	}

	@Override
	public void setPosture(final Posture posture)
	{
		this.player.setPosture(posture);
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
		this.setAV(new DefaultAngularVelocity(this.getLV().getAzimuth(), 0, 0));
	}

	@Override
	public double move()
	{
		double ret = super.move();
		this.setAV(new DefaultAngularVelocity(this.getLV().getAzimuth(), 0, 0));
		return ret;
	}

	@Override
	public double move(LinearVelocity lvAdjustment, Double maximumDistance)
	{
		double ret = super.move(lvAdjustment, maximumDistance);
		this.setAV(new DefaultAngularVelocity(this.getLV().getAzimuth(), 0, 0));
		return ret;
	}

	@Override
	public void moveRemaining(double speedAdjustment)
	{
		super.moveRemaining(speedAdjustment);
		this.setAV(new DefaultAngularVelocity(this.getLV().getAzimuth(), 0, 0));
	}

}
