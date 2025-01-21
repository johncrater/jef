package jef.core.movement.player;

import java.util.List;

import jef.core.Player;
import jef.core.PlayerPosition;
import jef.core.PlayerRatings;
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
	public int getAge()
	{
		return this.player.getAge();
	}

	@Override
	public PlayerPosition getCurrentPosition()
	{
		return this.player.getCurrentPosition();
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
	public int getHeight()
	{
		return this.player.getHeight();
	}

	@Override
	public String getLastName()
	{
		return this.player.getLastName();
	}

	@Override
	public double getMaxSpeed()
	{
		return this.player.getMaxSpeed();
	}

	@Override
	public int getNumber()
	{
		return this.player.getNumber();
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
	public String getPlayerID()
	{
		return this.player.getPlayerID();
	}

	@Override
	public List<PlayerPosition> getPositions()
	{
		return this.player.getPositions();
	}

	@Override
	public Posture getPosture()
	{
		return this.player.getPosture();
	}

	@Override
	public PlayerPosition getPrimaryPosition()
	{
		return this.player.getPrimaryPosition();
	}

	@Override
	public PlayerRatings getRatings()
	{
		return this.player.getRatings();
	}

	@Override
	public PlayerPosition getSecondaryPosition()
	{
		return this.player.getSecondaryPosition();
	}

	@Override
	public SpeedMatrix getSpeedMatrix()
	{
		return this.player.getSpeedMatrix();
	}

	@Override
	public PlayerPosition getTertiaryPosition()
	{
		return this.player.getTertiaryPosition();
	}

	@Override
	public int getWeight()
	{
		return this.player.getWeight();
	}

	@Override
	public boolean hasBall()
	{
		return this.player.hasBall();
	}

	public boolean hasPastDestination()
	{
		final Location origin = this.getStartingLoc();
		final Location dest = this.getPath().getCurrentWaypoint().getDestination();

		final Line line = new Line(origin, dest);
		final Line perpLine = line.getPerpendicularLine(dest);

		final Line currentLine = new Line(this.getStartingLoc(), this.getLoc());
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

	@Override
	public void setAge(final int age)
	{
		this.player.setAge(age);
	}

	@Override
	public void setFirstName(final String firstName)
	{
		this.player.setFirstName(firstName);
	}

	@Override
	public void setHasBall(final boolean hasBall)
	{
		this.player.setHasBall(hasBall);
	}

	@Override
	public void setHeight(final int height)
	{
		this.player.setHeight(height);
	}

	@Override
	public void setLastName(final String lastName)
	{
		this.player.setLastName(lastName);
	}

	@Override
	public void setNumber(final int number)
	{
		this.player.setNumber(number);
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

	@Override
	public void setSpeedMatrix(final SpeedMatrix matrix)
	{
		this.player.setSpeedMatrix(matrix);
	}

	@Override
	public void setWeight(final int weight)
	{
		this.player.setWeight(weight);
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
