package football.jef.core;

import football.jef.core.units.AngularVelocity;
import football.jef.core.units.LinearVelocity;
import football.jef.core.units.Location;

public class TestBall implements Football
{
	private AngularVelocity angularVelocity;
	private LinearVelocity linearVelocity;
	private Location location;
	
	public TestBall(Location location, LinearVelocity linearVelocity, AngularVelocity angularVelocity)
	{
		this.location = location;
		this.linearVelocity = linearVelocity;
		this.angularVelocity = angularVelocity;
	}

	public TestBall()
	{
		this.location = new Location();
		this.linearVelocity = new LinearVelocity();
		this.angularVelocity = new AngularVelocity();
	}

	@Override
	public Football adjustAngularVelocity(Double currentAngleInDegrees, Double radiansPerSecond)
	{
		this.angularVelocity = this.getAngularVelocity().adjust(currentAngleInDegrees, radiansPerSecond);
		return this;
	}

	@Override
	public Football adjustLinearVelocity(Double x, Double y, Double z)
	{
		this.linearVelocity = this.linearVelocity.adjust(x, y, z);
		return this;
	}

	@Override
	public Football adjustLocation(Double x, Double y, Double z)
	{
		this.location = this.location.adjust(x, y, z);
		return this;
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
	public Football setAngularVelocity(AngularVelocity angularVelocity)
	{
		this.angularVelocity = angularVelocity;
		return this;
	}

	@Override
	public Football setAngularVelocity(Double currentAngleInRadians, Double radiansPerSecond)
	{
		if (currentAngleInRadians == null)
			currentAngleInRadians = this.angularVelocity.getCurrentAngleInRadians();
		
		if (radiansPerSecond == null)
			radiansPerSecond = this.angularVelocity.getRadiansPerSecond();
		
		this.angularVelocity = new AngularVelocity(currentAngleInRadians, radiansPerSecond);
		return this;
	}

	@Override
	public Football setLinearVelocity(Double x, Double y, Double z)
	{
		if (x == null)
			x = this.linearVelocity.getX();
		
		if (y == null)
			y = this.linearVelocity.getY();
		
		if (z == null)
			z = this.linearVelocity.getZ();
		
		this.linearVelocity = new LinearVelocity(x, y, z);
		return this;
	}

	@Override
	public Football setLocation(Double x, Double y, Double z)
	{
		if (x == null)
			x = this.location.getX();
		
		if (y == null)
			y = this.location.getY();
		
		if (z == null)
			z = this.location.getZ();
		
		this.location = new Location(x, y, z);
		return this;
	}

	@Override
	public Football setLinearVelocity(LinearVelocity lv)
	{
		this.linearVelocity = lv;
		return this;
	}

	@Override
	public Football setLocation(Location location)
	{
		this.location = location;
		return this;
	}
}