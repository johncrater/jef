package jef.core.movement;


import jef.core.AngularVelocity;
import jef.core.AngularVelocity;
import jef.core.LinearVelocity;
import jef.core.Location;
import jef.core.LinearVelocity;
import jef.core.Location;
import jef.core.movement.Moveable;

public class TestMoveable implements Moveable
{
	private AngularVelocity angularVelocity;
	private LinearVelocity linearVelocity;
	private Location location;

	public TestMoveable()
	{
		this.location = new Location();
		this.linearVelocity = new LinearVelocity();
		this.angularVelocity = new AngularVelocity();
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

	public void setAV(final AngularVelocity angularVelocity)
	{
		this.angularVelocity = angularVelocity;
	}

	public void setLV(final LinearVelocity lv)
	{
		this.linearVelocity = lv;
	}

	public void setLoc(final Location location)
	{
		this.location = location;
	}
}
