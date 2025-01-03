package jef.core;

import jef.core.steering.Moveable;
import jef.core.units.AngularVelocity;
import jef.core.units.DefaultAngularVelocity;
import jef.core.units.DefaultLinearVelocity;
import jef.core.units.LinearVelocity;
import jef.core.units.Location;
import jef.core.units.DefaultLocation;

public class MovementTracker implements Moveable
{
	private LinearVelocity lv;
	private Location loc;
	private AngularVelocity av;
	private double pctRemaining;
	
	public MovementTracker()
	{
		this(new DefaultLinearVelocity(), new DefaultLocation(), new DefaultAngularVelocity());
	}

	public MovementTracker(LinearVelocity lv, Location loc, AngularVelocity av)
	{
		super();
		this.lv = lv;
		this.loc = loc;
		this.av = av;
		this.pctRemaining = 1.0;
	}

	public MovementTracker(Moveable moveable)
	{
		this(moveable.getLinearVelocity(), moveable.getLocation(), moveable.getAngularVelocity());
	}

	@Override
	public AngularVelocity getAngularVelocity()
	{
		return this.av;
	}

	@Override
	public void setAngularVelocity(AngularVelocity angularVelocity)
	{
		this.av = angularVelocity;
	}

	@Override
	public Location getLocation()
	{
		return this.loc;
	}

	@Override
	public void setLocation(Location location)
	{
		this.loc = location;
	}

	@Override
	public LinearVelocity getLinearVelocity()
	{
		return this.lv;
	}

	@Override
	public void setLinearVelocity(LinearVelocity lv)
	{
		this.lv = lv;
	}

	public double getPctRemaining()
	{
		return this.pctRemaining;
	}

	public void setPctRemaining(double pctRemaining)
	{
		this.pctRemaining = pctRemaining;
	}

	@Override
	public String toString()
	{
		return String.format("%s %s %s %.2f", this.loc, this.lv, this.av, this.pctRemaining);
	}
}
