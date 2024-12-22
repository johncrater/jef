package football.jef.core;

import football.jef.core.units.AngularVelocity;
import football.jef.core.units.LinearVelocity;
import football.jef.core.units.Location;

public class TestPlayer implements Player
{
	private String id;
	private String firstName;
	private String lastName;
	private int number;
	
	private double massInKilograms;
	private double heightInMeters;
	
	private AngularVelocity angularVelocity;
	private LinearVelocity linearVelocity;
	private Location location;
	
	public TestPlayer()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getMassInKilograms()
	{
		return massInKilograms;
	}

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public String getFirstName()
	{
		return firstName;
	}

	@Override
	public String getLastName()
	{
		return lastName;
	}

	@Override
	public double getHeightInMeters()
	{
		return heightInMeters;
	}

	@Override
	public int getNumber()
	{
		return number;
	}

	@Override
	public Player adjustAngularVelocity(Double currentAngleInDegrees, Double radiansPerSecond)
	{
		this.angularVelocity = angularVelocity.adjust(currentAngleInDegrees, radiansPerSecond);
		return this;
	}

	@Override
	public Player adjustLinearVelocity(Double x, Double y, Double z)
	{
		this.linearVelocity = this.linearVelocity.adjust(x, y, z);
		return this;
	}

	@Override
	public Player adjustLocation(Double x, Double y, Double z)
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
	public Player setAngularVelocity(AngularVelocity angularVelocity)
	{
		this.angularVelocity = angularVelocity;
		return this;
	}

	@Override
	public Player setAngularVelocity(Double currentAngleInRadians, Double radiansPerSecond)
	{
		if (currentAngleInRadians == null)
			currentAngleInRadians = this.angularVelocity.getCurrentAngleInRadians();
		
		if (radiansPerSecond == null)
			radiansPerSecond = this.angularVelocity.getRadiansPerSecond();
		
		this.angularVelocity = new AngularVelocity(currentAngleInRadians, radiansPerSecond);
		return this;
	}

	@Override
	public Player setLinearVelocity(Double x, Double y, Double z)
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
	public Player setLocation(Double x, Double y, Double z)
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
	public Player setLinearVelocity(LinearVelocity lv)
	{
		this.linearVelocity = lv;
		return this;
	}

	@Override
	public Player setLocation(Location location)
	{
		this.location = location;
		return this;
	}

	public void setNumber(int number)
	{
		this.number = number;
	}

}
