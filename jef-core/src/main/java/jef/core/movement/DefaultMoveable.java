package jef.core.movement;

import jef.core.AngularVelocity;
import jef.core.DefaultAngularVelocity;
import jef.core.LinearVelocity;
import jef.core.Location;
import jef.core.LinearVelocity;
import jef.core.Location;

public class DefaultMoveable implements Moveable
{
	private LinearVelocity lv;
	private Location loc;
	private AngularVelocity av;

	public DefaultMoveable(LinearVelocity lv, Location loc, AngularVelocity av)
	{
		super();
		this.lv = lv;
		this.loc = loc;
		this.av = av;
	}

	public DefaultMoveable(Moveable m)
	{
		this.av = m.getAV();
		this.lv = m.getLV();
		this.loc = m.getLoc();
	}
	
	public DefaultMoveable()
	{
		this.av = new DefaultAngularVelocity();
		this.lv = new LinearVelocity();
		this.loc = new Location();
	}

	@Override
	public AngularVelocity getAV()
	{
		return av;
	}

	public void setAV(AngularVelocity angularVelocity)
	{
		this.av = angularVelocity;
	}

	@Override
	public Location getLoc()
	{
		return this.loc;
	}

	public void setLoc(Location location)
	{
		this.loc = location;
	}

	@Override
	public LinearVelocity getLV()
	{
		return this.lv;
	}

	public void setLV(LinearVelocity lv)
	{
		this.lv = lv;
	}

}
