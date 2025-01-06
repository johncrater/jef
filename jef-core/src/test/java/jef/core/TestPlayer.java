package jef.core;

import jef.core.movement.Moveable;
import jef.core.movement.player.DefaultPath;
import jef.core.movement.player.Path;
import jef.core.movement.player.Steerable;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;

public class TestPlayer extends TestMoveable implements Player, Steerable
{
	private String id;
	private String firstName;
	private String lastName;
	private int number;

	private final double massInKilograms = 100;
	private double heightInMeters;

	private Path path;

	private double turningSpeed;

	public TestPlayer()
	{
		this.path = new DefaultPath();
		this.path.addWaypoint(new Waypoint(Field.midfield(), 10, DestinationAction.hardStop));
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
		return 10;
	}

	@Override
	public int getNumber()
	{
		return this.number;
	}

	@Override
	public Path getPath()
	{
		return this.path;
	}

	@Override
	public double getTurningSpeed()
	{
		return 4;
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

	public void setNumber(final int number)
	{
		this.number = number;
	}

	public void setPath(final Path path)
	{
		this.path = path;
	}

	public void setTurningSpeed(final double turningSpeed)
	{
		this.turningSpeed = turningSpeed;
	}

	@Override
	public double getDesiredSpeed()
	{
		Waypoint waypoint = this.getPath().getCurrentWaypoint();
		if (waypoint == null)
			return 0;
		
		return waypoint.getMaxSpeed();
	}
}
