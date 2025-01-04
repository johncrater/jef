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

}
