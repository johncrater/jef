package jef.core.movement.ball;


import jef.core.AngularVelocity;
import jef.core.AngularVelocity;
import jef.core.LinearVelocity;
import jef.core.Location;
import jef.core.geometry.LineSegment;
import jef.core.geometry.Plane;
import jef.core.movement.Moveable;
import jef.core.movement.Tracker;

public class BallTracker extends Tracker
{
	public BallTracker(double timeInterval)
	{
		super(timeInterval);
		// TODO Auto-generated constructor stub
	}

	public BallTracker(LinearVelocity lv, Location loc, AngularVelocity av, double timeInterval)
	{
		super(lv, loc, av, timeInterval);
		// TODO Auto-generated constructor stub
	}

	public BallTracker(Moveable moveable, double timeInterval)
	{
		super(moveable, timeInterval);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Moves the tracker until getLoc().getZ() is 0 or as far as it can go with the
	 * given linear velocity adjusted by the lv argument, starting location, time
	 * remaining. There is no guarantee that the tracker can reach the ground given
	 * the current state of the tracker
	 * 
	 * @param lvAdjustment the adjustment to the linear velocity done before the
	 *                     movement is initiated.
	 * @return true if the ground was reached, false otherwise
	 */
	public boolean moveToGround(LinearVelocity lvAdjustment)
	{
		// the location if the full remaining time is taken
		LinearVelocity tmpLV = getLV().add(lvAdjustment.multiply(getRemainingTime()));

		Location locTmp = this.getLoc().add(tmpLV.multiply(getRemainingTime()));
		if (locTmp.getZ() < 0)
		{
			double zSpeed = getLoc().distanceBetween(locTmp);
			LineSegment line = new LineSegment(getLoc(), locTmp);
			Location intersection = line.intersects(Plane.THE_FIELD);

			// make sure we are at absolute 0
			intersection = intersection.newFrom(null, null, 0.0);

			double distanceAboveGround = getLoc().distanceBetween(intersection);
			double pctAboveGround = distanceAboveGround / zSpeed;

			this.setLV(this.getLV().add(lvAdjustment.multiply(getRemainingTime()).multiply(pctAboveGround)));

			this.setLoc(intersection);
			this.setPctRemaining(1 - pctAboveGround);

			this.applyAngularVelocity();

			return true;
		}
		else
		{
			this.move(lvAdjustment, null);
			return false;
		}
	}

	/**
	 * Makes the location, velocity and orientation of the ball make physical sense
	 * 
	 * @return
	 */
	public void rationalize()
	{
		// if we are still at or below ground that means we have no rebounded any. Due to rounding
		// it may be possible to have a very small slow upward trajectory. Let's set that elevation to zero
		if (getLoc().getZ() <= 0)
		{
			setLoc(getLoc().newFrom(null, null, 0.0));
			setLV(getLV().newFrom(null, 0.0, null));
			setAV(new AngularVelocity());
		}
		
		// if the ball is not moving and it is sitting within EPSILON of the ground, we
		// consider it stopped. It might be not moving but above ground when it reaches
		// apogee. That is why we check for close to the ground
		if (Location.closeEnoughTo(0, getLoc().getZ()) && getLV().isNotMoving())
		{
			setLV(getLV().newFrom(null, 0.0, null));
			setAV(new AngularVelocity());
		}

		if (getLoc().getZ() == 0 && getLV().getElevation() < 0)
		{
			setLV(getLV().newFrom(null, 0.0, null));
			setAV(new AngularVelocity());
		}
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
