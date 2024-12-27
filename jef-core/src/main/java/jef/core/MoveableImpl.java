package jef.core;

import jef.core.steering.Moveable;
import jef.core.units.AngularVelocity;
import jef.core.units.LinearVelocity;
import jef.core.units.Location;

public class MoveableImpl implements Moveable
{
	private AngularVelocity angularVelocity;
	private LinearVelocity linearVelocity;
	private Location location;
	
	public MoveableImpl(Location location, LinearVelocity linearVelocity, AngularVelocity angularVelocity)
	{
		this.location = location;
		this.linearVelocity = linearVelocity;
		this.angularVelocity = angularVelocity;
	}

	public MoveableImpl()
	{
		this.location = new Location();
		this.linearVelocity = new LinearVelocity();
		this.angularVelocity = new AngularVelocity();
	}

	public MoveableImpl(Moveable moveable)
	{
		this.location = moveable.getLocation();
		this.linearVelocity = moveable.getLinearVelocity();
		this.angularVelocity = moveable.getAngularVelocity();
	}

	@Override
	public void adjustAngularVelocity(Double currentAngleInDegrees, Double radiansPerSecond)
	{
		this.angularVelocity = this.getAngularVelocity().adjust(currentAngleInDegrees, radiansPerSecond);
	}

	@Override
	public void adjustLinearVelocity(Double x, Double y, Double z)
	{
		this.linearVelocity = this.linearVelocity.adjust(x, y, z);
	}

	@Override
	public void adjustLocation(Double x, Double y, Double z)
	{
		this.location = this.location.adjust(x, y, z);
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
	public void setAngularVelocity(Double currentAngleInRadians, Double radiansPerSecond)
	{
		if (currentAngleInRadians == null)
			currentAngleInRadians = this.angularVelocity.getCurrentAngleInRadians();
		
		if (radiansPerSecond == null)
			radiansPerSecond = this.angularVelocity.getRadiansPerSecond();
		
		this.angularVelocity = new AngularVelocity(currentAngleInRadians, radiansPerSecond);
	}

	@Override
	public void setLinearVelocity(Double x, Double y, Double z)
	{
		if (x == null)
			x = this.linearVelocity.getX();
		
		if (y == null)
			y = this.linearVelocity.getY();
		
		if (z == null)
			z = this.linearVelocity.getZ();
		
		this.linearVelocity = new LinearVelocity(x, y, z);
	}

	@Override
	public void setLocation(Double x, Double y, Double z)
	{
		if (x == null)
			x = this.location.getX();
		
		if (y == null)
			y = this.location.getY();
		
		if (z == null)
			z = this.location.getZ();
		
		this.location = new Location(x, y, z);
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
}
