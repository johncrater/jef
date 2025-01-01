package jef.core;

import jef.core.steering.Moveable;
import jef.core.units.AngularVelocity;
import jef.core.units.LinearVelocity;
import jef.core.units.Location;

public class TestMoveable implements Moveable
{
	private AngularVelocity angularVelocity;
	private LinearVelocity linearVelocity;
	private Location location;
	
	public TestMoveable(Location location, LinearVelocity linearVelocity, AngularVelocity angularVelocity)
	{
		this.location = location;
		this.linearVelocity = linearVelocity;
		this.angularVelocity = angularVelocity;
	}

	public TestMoveable()
	{
		this.location = new Location();
		this.linearVelocity = new LinearVelocity();
		this.angularVelocity = new AngularVelocity();
	}

	public TestMoveable(Moveable moveable)
	{
		this.location = moveable.getLocation();
		this.linearVelocity = moveable.getLinearVelocity();
		this.angularVelocity = moveable.getAngularVelocity();
	}

	@Override
	public AngularVelocity getAngularVelocity()
	{
		return this.angularVelocity;
	}

	@Override
	public LinearVelocity getLinearVelocity()
	{
		return this.linearVelocity;
	}

	@Override
	public Location getLocation()
	{
		return this.location;
	}

	@Override
	public void setAngularVelocity(AngularVelocity angularVelocity)
	{
		this.angularVelocity = angularVelocity;
	}

	@Override
	public void setLinearVelocity(LinearVelocity lv)
	{
		this.linearVelocity = lv;
	}

	@Override
	public void setLocation(Location location)
	{
		this.location = location;
	}

	@Override
	public void turn(double angle)
	{
		this.linearVelocity = linearVelocity.turn(angle);
	}

	@Override
	public void move(double distance)
	{
		this.location = this.location.adjust(new LinearVelocity(this.linearVelocity.calculateXYAngle(), distance));
	}

	@Override
	public void move(LinearVelocity lv)
	{
		this.location = this.location.adjust(lv);
	}

	@Override
	public void adjustSpeed(double speedDelta)
	{
		if (this.linearVelocity.magnitude() == 0)
		{
			this.linearVelocity = new LinearVelocity(this.angularVelocity.getCurrentAngleInRadians(), speedDelta);
		}
		else
		{
			double factor = speedDelta / this.linearVelocity.magnitude();
			this.linearVelocity = this.linearVelocity.add(this.linearVelocity.getX() * factor, this.linearVelocity.getY() * factor, 0);
		}
	}

	@Override
	public void setSpeed(double newSpeed)
	{
		double x = Math.sqrt(newSpeed * newSpeed / 2);
		if (this.linearVelocity.magnitude() == 0)
		{
			this.linearVelocity = new LinearVelocity(this.angularVelocity.getCurrentAngleInRadians(), x);
		}
		else
		{
			this.linearVelocity = new LinearVelocity(this.linearVelocity.calculateXYAngle(), newSpeed);
		}
	}

	@Override
	public double getSpeed()
	{
		return this.linearVelocity.getXYSpeed();
	}
}
