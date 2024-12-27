package jef.core;

import jef.core.steering.Path;
import jef.core.steering.Steerable;

public class TestPlayer extends MoveableImpl implements Player, Steerable
{
	private String id;
	private String firstName;
	private String lastName;
	private int number;
	
	private double massInKilograms = 100;
	private double heightInMeters;
	
	private Path path;
	
	public TestPlayer()
	{
	}

	@Override
	public double getMassInKilograms()
	{
		return massInKilograms;
	}

	public Path getPath()
	{
		return this.path;
	}

	public void setPath(Path path)
	{
		this.path = path;
	}

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public double getMaxSpeed()
	{
		return 10;
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

	public void setNumber(int number)
	{
		this.number = number;
	}

}
