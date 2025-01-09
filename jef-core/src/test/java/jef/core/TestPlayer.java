package jef.core;

import jef.core.movement.Moveable;
import jef.core.movement.player.DefaultPath;
import jef.core.movement.player.Path;
import jef.core.movement.player.Steerable;

public class TestPlayer extends TestMoveable implements Player, Steerable
{
	private String id;
	private String firstName;
	private String lastName;

	private final double massInKilograms = 100;
	private double heightInMeters;

	private Path path = new DefaultPath();

	public TestPlayer(String firstName, String lastName)
	{
		this.firstName = firstName;
		this.lastName = lastName;
		this.id = firstName + lastName;
	}

	public void update(Moveable tracker)
	{
		this.setAV(tracker.getAV());
		this.setLV(tracker.getLV());
		this.setLoc(tracker.getLoc());
	}
	
	@Override
	public String getFirstName()
	{
		return this.firstName;
	}

	@Override
	public double getHeightInMeters()
	{
		return this.heightInMeters;
	}

	@Override
	public String getId()
	{
		return this.id;
	}

	@Override
	public String getLastName()
	{
		return this.lastName;
	}

	@Override
	public double getMassInKilograms()
	{
		return this.massInKilograms;
	}

	@Override
	public double getMaxSpeed()
	{
		return 15;
	}

	@Override
	public Path getPath()
	{
		return this.path;
	}

	public void setFirstName(final String firstName)
	{
		this.firstName = firstName;
	}

	public void setHeightInMeters(final double heightInMeters)
	{
		this.heightInMeters = heightInMeters;
	}

	public void setId(final String id)
	{
		this.id = id;
	}

	public void setLastName(final String lastName)
	{
		this.lastName = lastName;
	}

	public void setPath(final Path path)
	{
		this.path = path;
	}

	@Override
	public double getDesiredSpeed()
	{
		return 10;
	}
}
