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
		this.linearVelocity = linearVelocity.set(null, angle, null);
	}

	@Override
	public void move(double distance)
	{
		this.location = this.location.adjust(this.linearVelocity.set(null, null, distance));
	}

	@Override
	public void move(LinearVelocity lv)
	{
		this.location = this.location.adjust(lv);
	}

	@Override
	public void adjustSpeed(double speedDelta)
	{
		this.linearVelocity = this.linearVelocity.add(0, 0, speedDelta);
	}

	@Override
	public void setSpeed(double newSpeed)
	{
		this.linearVelocity = this.linearVelocity.set(null, null, newSpeed);
	}

	@Override
	public double getSpeed()
	{
		return this.linearVelocity.getDistance();
	}
}
