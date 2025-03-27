package jef.movement.player;

import jef.core.Player;
import jef.geometry.AngularVelocity;
import jef.geometry.LineSegment;
import jef.geometry.LinearVelocity;
import jef.geometry.Location;
import jef.movement.Tracker;

public class PlayerTracker extends Tracker
{
	private PlayerState currentState;
	private PlayerState startingState;
	private Path currentPath;

	public PlayerTracker(final PlayerState playerState, final Path currentPath, final double timeInterval)
	{
		super(timeInterval);

		assert currentPath != null;

		this.startingState = this.currentState = playerState;
		this.currentPath = currentPath;
	}

	public PlayerTracker(final PlayerTracker tracker)
	{
		super(tracker);
		this.currentState = tracker.currentState;
		this.startingState = tracker.startingState;
	}

	public void advance()
	{
		this.startingState = this.currentState;
		this.setPctRemaining(1.0);
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

	public boolean finalDestinationReached()
	{
		return this.getLoc().closeEnoughTo(this.getPath().getDestination());
	}

	@Override
	public AngularVelocity getAV()
	{
		return this.currentState.getAV();
	}

	@Override
	public Location getLoc()
	{
		return this.currentState.getLoc();
	}

	@Override
	public LinearVelocity getLV()
	{
		return this.currentState.getLV();
	}

	public Path getPath()
	{
		return this.currentPath;
	}

	public Player getPlayer()
	{
		return this.currentState.getPlayer();
	}

	public PlayerState.Posture getPosture()
	{
		return this.currentState.getPosture();
	}

	public PlayerState getState()
	{
		return this.currentState;
	}

	public boolean hasPastFinalDestination()
	{
		final Location origin = this.startingState.getLoc();
		final Location dest = this.getPath().getDestination();

		final LineSegment line = new LineSegment(origin, dest);
		final LineSegment perpLine = line.getPerpendicularLine(dest, line.getLength());

		final LineSegment currentLine = new LineSegment(this.startingState.getLoc(), this.getLoc());
		final Location intersection = perpLine.xyIntersection(currentLine);

		return intersection != null;
	}

	public boolean hasPastWaypointDestination()
	{
		final Location origin = this.startingState.getLoc();
		final Location dest = this.getPath().getCurrentWaypoint().getWaypointDestination();

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

		this.updatePath();
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

		this.updatePath();
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

		this.updatePath();
	}

	public void reset()
	{
		this.currentState = this.startingState;
		this.setPctRemaining(1.0);
	}

	@Override
	public void setAV(final AngularVelocity av)
	{
		this.currentState = this.currentState.newFrom(null, null, av, null);
	}

	@Override
	public void setLoc(final Location loc)
	{
		this.currentState = this.currentState.newFrom(null, loc, null, null);
	}

	@Override
	public void setLV(final LinearVelocity lv)
	{
		this.currentState = this.currentState.newFrom(lv, null, null, null);
	}

	public void setPath(final Path path)
	{
		assert path != null;
		this.currentPath = path;
	}

	public void setPosture(final PlayerState.Posture posture)
	{
		this.currentState = this.currentState.newFrom(null, null, null, posture);
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

	public boolean waypointDestinationReached()
	{
		return this.getLoc().closeEnoughTo(this.getPath().getCurrentWaypoint().getWaypointDestination());
	}

	private void updatePath()
	{
		if (this.finalDestinationReached() || this.hasPastFinalDestination())
		{
			// if we have reached out destination, stop and orient
			final Double destinationOrientation = this.getPath().getDestinationOrientation();
			this.setLV(this.getLV().newFrom(destinationOrientation, null, 0.0));
			this.setLoc(this.getPath().getDestination());

			while (!this.getPath().isComplete())
			{
				this.setPath(Path.removeCurrentWaypoint(this.getPath()));
			}
		}
		else if (this.waypointDestinationReached() || this.hasPastWaypointDestination())
		{
			this.setPath(Path.removeCurrentWaypoint(this.getPath()));
		}

	}

}
