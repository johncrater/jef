package jef.core.movement;

import jef.core.AngularVelocity;
import jef.core.Conversions;
import jef.core.LinearVelocity;
import jef.core.Location;

public abstract class Tracker
{
	private double pctRemaining;
	private final double timeInterval;

	public Tracker(double timeInterval)
	{
		this.pctRemaining = 1.0;
		this.timeInterval = timeInterval;
	}
	
	public Tracker(Tracker tracker)
	{
		this.pctRemaining = tracker.pctRemaining;
		this.timeInterval = tracker.timeInterval;
	}
	
	public abstract LinearVelocity getLV();
	public abstract Location getLoc();
	public abstract AngularVelocity getAV();
	
	public abstract void setLV(LinearVelocity lv);
	public abstract void setLoc(Location loc);
	public abstract void setAV(AngularVelocity av);
	
	/**
	 * @param speed null to use the current speed
	 * @return the distance that can be traveled at the given speed with the given
	 *         time remaining in the turn
	 */
	public double calculateTraversableDistance(Double speed)
	{
		if (speed == null)
		{
			speed = this.getLV().getSpeed();
		}

		return speed * this.getRemainingTime();
	}

	public double getPctRemaining()
	{
		return this.pctRemaining;
	}

	public double getRemainingTime()
	{
		return this.timeInterval * this.pctRemaining;
	}

	public double getTimeInterval()
	{
		return this.timeInterval;
	}
	
	/**
	 * Moves the distance corresponding to the current linear velocity from the
	 * current location in the time remaining. This is the same as move(null, null)
	 *
	 * @return the distance traveled
	 */
	public double move()
	{
		return this.move(null, null);
	}

	/**
	 * Changes direction and velocity by adding the lvAdjustment argument to the current LV and moves
	 * up to maximumDistance yards in the new direction.
	 *
	 * @param lvAdjustment
	 * @param maximumDistance
	 * @return the distance traveled which will be between 0 and maximumDistance
	 *         inclusive
	 */
	public double move(final LinearVelocity lvAdjustment, Double maximumDistance)
	{
		final double remainingTime = this.getRemainingTime();
		if (remainingTime == 0)
			return 0;

		this.applyAngularVelocity();

		if (lvAdjustment != null)
		{
			this.setLV(this.getLV().add(lvAdjustment.multiply(remainingTime)));
		}

		final double traversableDistance = this.calculateTraversableDistance(this.getLV().getSpeed());
		if (traversableDistance == 0)
			return 0;

		if (maximumDistance == null)
		{
			maximumDistance = traversableDistance;
		}

		maximumDistance = Math.min(traversableDistance, maximumDistance);
		final double ratio = maximumDistance / traversableDistance;

		final Location oldLocation = this.getLoc();
		this.setLoc(this.getLoc().add(this.getLV().multiply(ratio * remainingTime)));
		final double ret = this.getLoc().distanceBetween(oldLocation);

		this.pctRemaining -= this.pctRemaining * ratio;
		return ret;
	}

	/**
	 * Moves whatever remaining distance is left using the current velocity adjusted
	 * by the speedAdjustment argument. The remaining time is expended regardless if
	 * it is used or not. This may happen when coming to a stop. The linear velocity
	 * may be reduced to zero, but never below
	 *
	 * @param speedAdjustment y/s adjustment to current linear velocity
	 */
	public void moveRemaining(final double speedAdjustment)
	{
		double adjustedLV = this.getLV().getSpeed() + speedAdjustment * this.getRemainingTime();
		adjustedLV = Math.max(0, adjustedLV);
		
		this.setLV(this.getLV().newFrom(null,  null, adjustedLV));
		this.setLoc(this.getLoc().add(this.getLV().multiply(this.getRemainingTime())));
		this.pctRemaining = 0;
		this.applyAngularVelocity();
	}

	public void setPctRemaining(final double pctRemaining)
	{
		this.pctRemaining = pctRemaining;
	}

	public void setRotation(final double rotation)
	{
		this.setAV(this.getAV().newFrom(null, rotation, null));
	}

	@Override
	public String toString()
	{
		return String.format("%s %s %s %.2f", this.getLoc(), this.getLV(), this.getAV(), this.pctRemaining);
	}

	protected void applyAngularVelocity()
	{
		double spin = 0;
		spin = Conversions.normalizeAngle(this.getAV().getOrientation() + (this.getAV().getRotation() * this.getRemainingTime()));

		if ((this.getAV().getRotation() == 0) && (this.getAV().getSpiralVelocity() > 0))
			spin = this.getLV().getElevation();

		this.setAV(new AngularVelocity(spin, this.getAV().getRotation(), this.getAV().getSpiralVelocity()));
	}

}
