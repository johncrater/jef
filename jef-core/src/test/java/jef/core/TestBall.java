package jef.core;

import jef.core.units.DefaultAngularVelocity;
import jef.core.units.DefaultLinearVelocity;
import jef.core.units.DefaultLocation;

public class TestBall implements Football
{
	private AngularVelocity angularVelocity;
	private LinearVelocity linearVelocity;
	private DefaultLocation location;
	
	public TestBall(DefaultLocation location, LinearVelocity linearVelocity, AngularVelocity angularVelocity)
	{
		this.location = location;
		this.linearVelocity = linearVelocity;
		this.angularVelocity = angularVelocity;
	}

	public TestBall()
	{
		this.location = new DefaultLocation();
		this.linearVelocity = new DefaultLinearVelocity();
		this.angularVelocity = new DefaultAngularVelocity();
	}

	@Override
	public void adjustAngularVelocity(double currentAngleInDegrees, double radiansPerSecond)
	{
		this.angularVelocity = this.getAV().addRotation(currentAngleInDegrees, radiansPerSecond, 0);
	}

	@Override
	public void adjustLinearVelocity(double elevation, double azimuth, double speed)
	{
		this.linearVelocity = this.linearVelocity.add(elevation, azimuth, speed);
	}

	@Override
	public void adjustLocation(double x, double y, double z)
	{
		this.location = this.location.add(x, y, z);
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
	public DefaultLocation getLoc()
	{
		return this.location;
	}

	@Override
	public void setAV(AngularVelocity angularVelocity)
	{
		this.angularVelocity = angularVelocity;
	}

	@Override
	public void setAngularVelocity(Double currentAngleInRadians, Double radiansPerSecond)
	{
		if (currentAngleInRadians == null)
			currentAngleInRadians = this.angularVelocity.getOrientation();
		
		if (radiansPerSecond == null)
			radiansPerSecond = this.angularVelocity.getRotation();
		
		this.angularVelocity = new DefaultAngularVelocity(currentAngleInRadians, radiansPerSecond);
	}

	@Override
	public void setLinearVelocity(Double elevation,  Double azimuth, Double speed)
	{
		this.linearVelocity = this.linearVelocity.newFrom(elevation, azimuth, speed);
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
		
		this.location = new DefaultLocation(x, y, z);
	}

	@Override
	public void setLV(LinearVelocity lv)
	{
		this.linearVelocity = lv;
	}

	@Override
	public void setLocation(DefaultLocation location)
	{
		this.location = location;
	}
}