package jef.core;

import jef.core.units.DefaultAngularVelocity;
import jef.core.units.DefaultLinearVelocity;
import jef.core.units.DefaultLocation;

public class TestMoveable implements Moveable
{
	private AngularVelocity angularVelocity;
	private LinearVelocity linearVelocity;
	private Location location;

	public TestMoveable()
	{
		this.location = new DefaultLocation();
		this.linearVelocity = new DefaultLinearVelocity();
		this.angularVelocity = new DefaultAngularVelocity();
	}

	public TestMoveable(final Location location, final LinearVelocity linearVelocity,
			final AngularVelocity angularVelocity)
	{
		this.location = location;
		this.linearVelocity = linearVelocity;
		this.angularVelocity = angularVelocity;
	}

	public TestMoveable(final Moveable moveable)
	{
		this.location = moveable.getLoc();
		this.linearVelocity = moveable.getLV();
		this.angularVelocity = moveable.getAV();
	}

	@Override
	public void adjustSpeed(final double speedDelta)
	{
		this.linearVelocity = this.linearVelocity.add(speedDelta);
	}

	@Override
	public AngularVelocity getAV()
	{
		return this.angularVelocity;
	}

	@Override
	public LinearVelocity getLV()
	{
		return this.linearVelocity;
	}

	@Override
	public Location getLoc()
	{
		return this.location;
	}

	@Override
	public double getSpeed()
	{
		return this.linearVelocity.getDistance();
	}

	@Override
	public void move(final double distance)
	{
		this.location = this.location.add(this.linearVelocity.newFrom(null, null, distance));
	}

	@Override
	public void move(final LinearVelocity lv)
	{
		this.location = this.location.add(lv);
	}

	@Override
	public void setAV(final AngularVelocity angularVelocity)
	{
		this.angularVelocity = angularVelocity;
	}

	@Override
	public void setLV(final LinearVelocity lv)
	{
		this.linearVelocity = lv;
	}

	@Override
	public void setLoc(final Location location)
	{
		this.location = location;
	}

	@Override
	public void setSpeed(final double newSpeed)
	{
		this.linearVelocity = this.linearVelocity.newFrom(null, null, newSpeed);
	}

	@Override
	public void turn(final double angle)
	{
		this.linearVelocity = this.linearVelocity.newFrom(null, angle, null);
	}
}
