package jef.core.movement.player;

import jef.core.AngularVelocity;
import jef.core.LinearVelocity;
import jef.core.Location;
import jef.core.Player;
import jef.core.PlayerState;
import jef.core.geometry.LineSegment;
import jef.core.movement.Posture;
import jef.core.movement.Tracker;

public class PlayerTracker extends Tracker
{
	private PlayerState currentState;
	private PlayerState startingState;
	private Path currentPath;
	
	public PlayerTracker(PlayerState playerState, Path currentPath, final double timeInterval)
	{
		super(timeInterval);
		this.startingState = this.currentState = playerState;
		this.currentPath = currentPath;
	}

	public PlayerTracker(final PlayerTracker tracker)
	{
		super(tracker);
		this.currentState = tracker.currentState;
		this.startingState = tracker.startingState;
	}

	public PlayerState getState()
	{
		return this.currentState;
	}
	
	public void setLV(LinearVelocity lv)
	{
		this.currentState = this.currentState.newFrom(lv, null, null, null);
	}
	
	public void setLoc(Location loc)
	{
		this.currentState = this.currentState.newFrom(null, loc, null, null);
	}
	
	public void setAV(AngularVelocity av)
	{
		this.currentState = this.currentState.newFrom(null, null, av, null);
	}
	
	public LinearVelocity getLV()
	{
		return this.currentState.getLV();
	}

	public AngularVelocity getAV()
	{
		return this.currentState.getAV();
	}

	public void reset()
	{
		this.currentState = this.startingState;
		this.setPctRemaining(1.0);
	}
	
	public void advance()
	{
		this.startingState = this.currentState;
		this.setPctRemaining(1.0);
	}

	public void setPosture(Posture posture)
	{
		this.currentState = this.currentState.newFrom(null, null, null, posture);
	}

	public void setPath(Path path)
	{
		this.currentPath = path;
	}

	public Location getLoc()
	{
		return this.currentState.getLoc();
	}

	public Path getPath()
	{
		return this.currentPath;
	}

	public Posture getPosture()
	{
		return this.currentState.getPosture();
	}

	public Player getPlayer()
	{
		return this.currentState.getPlayer();
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

	public boolean destinationReached()
	{
		final Waypoint waypoint = getPath().getCurrentWaypoint();
		if (waypoint == null)
			return true;

		return getLoc().closeEnoughTo(this.getPath().getDestination());
	}

	public boolean hasPastDestination()
	{
		final Location origin = this.startingState.getLoc();
		final Location dest = this.getPath().getCurrentWaypoint().getDestination();

		final LineSegment line = new LineSegment(origin, dest);
		final LineSegment perpLine = line.getPerpendicularLine(dest, line.getLength());

		final LineSegment currentLine = new LineSegment(this.startingState.getLoc(), this.getLoc());
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
			this.setAV(new AngularVelocity(this.getLV().getAzimuth(), 0, 0));
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
			this.setAV(new AngularVelocity(this.getLV().getAzimuth(), 0, 0));
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
			this.setAV(new AngularVelocity(this.getLV().getAzimuth(), 0, 0));
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
			this.setAV(new AngularVelocity(this.getLV().getAzimuth(), 0, 0));
		}
	}

}
