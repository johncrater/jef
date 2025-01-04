package jef.core;

import jef.core.units.DefaultAngularVelocity;
import jef.core.units.DefaultLinearVelocity;
import jef.core.units.DefaultLocation;

public class Tracker implements Moveable
{
	private LinearVelocity lv;
	private Location loc;
	private AngularVelocity av;
	
	private LinearVelocity originalLv;
	private Location originalLoc;
	private AngularVelocity originalAv;

	private double pctRemaining;
	private final double timeInterval;

	public Tracker(final double timeInterval)
	{
		this(new DefaultLinearVelocity(), new DefaultLocation(), new DefaultAngularVelocity(), timeInterval);
	}

	public Tracker(final LinearVelocity lv, final Location loc, final AngularVelocity av,
			final double timeInterval)
	{
		this.lv = this.originalLv = lv;
		this.loc = this.originalLoc = loc;
		this.av = this.originalAv = av;
		this.pctRemaining = 1.0;
		this.timeInterval = timeInterval;
	}

	public Tracker(final Moveable moveable, final double timeInterval)
	{
		this(moveable.getLV(), moveable.getLoc(), moveable.getAV(), timeInterval);
	}

	public void reset()
	{
		this.lv = originalLv;
		this.av = originalAv;
		this.loc = originalLoc;
		this.pctRemaining = 1.0;
	}
	
	@Override
	public AngularVelocity getAV()
	{
		return this.av;
	}

	@Override
	public LinearVelocity getLV()
	{
		return this.lv;
	}

	@Override
	public Location getLoc()
	{
		return this.loc;
	}

	public double getPctRemaining()
	{
		return this.pctRemaining;
	}

	public double getTimeInterval()
	{
		return this.timeInterval;
	}

	@Override
	public void setAV(final AngularVelocity angularVelocity)
	{
		this.av = angularVelocity;
	}

	@Override
	public void setLV(final LinearVelocity lv)
	{
		this.lv = lv;
	}

	@Override
	public void setLoc(final Location location)
	{
		this.loc = location;
	}

	public void setPctRemaining(final double pctRemaining)
	{
		this.pctRemaining = pctRemaining;
	}

	/**
	 * Moves whatever remaining distance is left using the current velocity adjusted by the speedAdjustment argument. 
	 * The remaining time is expended regardless if it is used or not. This may happen when coming to a stop. The linear velocity
	 * may be reduced to zero, but never below
	 * @param speedAdjustment y/s adjustment to current linear velocity
	 */
	public void moveRemaining(double speedAdjustment)
	{
		this.lv = this.lv.add(speedAdjustment * this.getRemainingTime());
		this.loc = this.loc.add(this.lv.multiply(this.getRemainingTime()));
		this.pctRemaining = 0;
		this.applyAngularVelocity();
	}
	
	/**
	 * Calculates the speed after the adjust taking into consideration of remaining time and the lower limit of zero
	 * @param speedAdjustment y/s speed adjustment
	 * @return The adjusted speed. Does not consider any limitations on maximum speed.
	 */
	public double calculateAdjustedSpeed(double speedAdjustment)
	{
		speedAdjustment = speedAdjustment * getRemainingTime();
		return Math.max(0, lv.getDistance() + speedAdjustment);
	}
	
	/**
	 * Simply adjust the linear velocity to point in the direction created by adding the adjustment to the current velocity
	 * @param angleAdjustment
	 */
	public void turn(double angleAdjustment)
	{
		this.lv = this.lv.add(0, angleAdjustment, 0);
	}

	/**
	 * @param speed null to use the current speed
	 * @return the distance that can be traveled at the given speed with the given time remaining in the turn
	 */
	public double calculateTraverableDistance(Double speed)
	{
		if (speed == null)
			speed = this.lv.getDistance();
		
		return speed * getRemainingTime();
	}
	
	/**
	 * Changes direction and velocity according to lvAdjustment argument and moves up to maximumDistance yards in the new direction.
	 * @param lvAdjustment
	 * @param maximumDistance
	 * @return the distance traveled which will be between 0 and maximumDistance inclusive
	 */
	public double move(LinearVelocity lvAdjustment, Double maximumDistance)
	{
		double remainingTime = this.getRemainingTime();
		if (remainingTime == 0)
			return 0;
		
		this.applyAngularVelocity();

		if (lvAdjustment != null)
			lv = lv.add(lvAdjustment.multiply(remainingTime));
		
		double traversableDistance = calculateTraverableDistance(lv.getDistance());
		if (traversableDistance == 0)
			return 0;
		
		if (maximumDistance == null)
			maximumDistance = traversableDistance;
		
		maximumDistance = Math.min(traversableDistance, maximumDistance);
		double ratio = maximumDistance / traversableDistance;

		Location oldLocation = this.loc;
		this.loc = this.loc.add(lv.multiply(ratio * remainingTime));
		double ret = this.loc.distanceBetween(oldLocation);
		
		this.pctRemaining -= this.pctRemaining * ratio;
		return ret;
	}
	
	/**
	 * Moves the distance corresponding to the current linear velocity from the current location in the time remaining.
	 * This is the same as move(null, null)
	 * @return the distance traveled
	 */
	public double move()
	{
		return this.move(null, null);
	}
	
	/**
	 * Calculates the distance necessary to reach the desired speed using the current direction. 
	 * @param accelerationRate
	 * @param desiredSpeed
	 * @return a positive value to indicate the distance or a negative value meaning the desiredSpeed cannot be reached using the acceleration rate argument. 
	 * This will happen if the acceleration rate is negative and the desired speed is greater that the current speed or if the acceleration rate is positive
	 * and the desired speed is less than the current speed
	 */
	public double calculateDistanceToReachSpeed(double accelerationRate, double desiredSpeed)
	{
		return (Math.pow(desiredSpeed, 2) - Math.pow(lv.getDistance(), 2)) / (2 * accelerationRate);
	}
	
	public double getRemainingTime()
	{
		return this.timeInterval * this.pctRemaining;
	}
	
	public void setRotation(double rotation)
	{
		this.av = this.av.newFrom(null, rotation, null);
	}
	
	@Override
	public String toString()
	{
		return String.format("%s %s %s %.2f", this.loc, this.lv, this.av, this.pctRemaining);
	}

	protected void applyAngularVelocity()
	{
		double spin = 0;
		spin = Conversions.normalizeAngle(av.getOrientation() + av.getRotation() * this.getRemainingTime());

		if (av.getRotation() == 0 && av.getSpiralVelocity() > 0)
		{
			spin = lv.getElevation();
		}

		this.av = new DefaultAngularVelocity(spin, av.getRotation(), av.getSpiralVelocity());
	}

}
