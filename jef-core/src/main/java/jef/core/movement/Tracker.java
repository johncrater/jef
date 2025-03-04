package jef.core.movement;

import jef.core.AngularVelocity;
import jef.core.Conversions;
import jef.core.DefaultAngularVelocity;
import jef.core.DefaultLinearVelocity;
import jef.core.Location;
import jef.core.LinearVelocity;
import jef.core.Location;

public class Tracker extends DefaultMoveable
{
	private LinearVelocity startingLv;
	private Location startingLoc;
	private AngularVelocity startingAv;

	private double pctRemaining;
	private final double timeInterval;

	public Tracker(Tracker tracker)
	{
		this(tracker.getLV(), tracker.getLoc(), tracker.getAV(), tracker.getTimeInterval());
	}
	
	public Tracker(final double timeInterval)
	{
		this(new DefaultLinearVelocity(), new Location(), new DefaultAngularVelocity(), timeInterval);
	}

	public Tracker(final LinearVelocity lv, final Location loc, final AngularVelocity av, final double timeInterval)
	{
		super(lv, loc, av);
		this.pctRemaining = 1.0;
		this.timeInterval = timeInterval;
		
		this.startingLv = lv;
		this.startingLoc = loc;
		this.startingAv = av;
	}

	public Tracker(final Moveable moveable, final double timeInterval)
	{
		this(moveable.getLV(), moveable.getLoc(), moveable.getAV(), timeInterval);
	}

	public LinearVelocity getStartingLv()
	{
		return this.startingLv;
	}

	public Location getStartingLoc()
	{
		return this.startingLoc;
	}

	public AngularVelocity getStartingAv()
	{
		return this.startingAv;
	}

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

	public void reset()
	{
		this.setLV(this.startingLv);
		this.setAV(this.startingAv);
		this.setLoc(this.startingLoc);
		this.pctRemaining = 1.0;
	}
	
	public void advance()
	{
		this.startingLv = this.getLV();
		this.startingAv = this.getAV();
		this.startingLoc = this.getLoc();
		this.pctRemaining = 1.0;
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

		this.setAV(new DefaultAngularVelocity(spin, this.getAV().getRotation(), this.getAV().getSpiralVelocity()));
	}

}
