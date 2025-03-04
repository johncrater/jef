package jef.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jef.core.movement.Posture;
import jef.core.movement.player.DefaultPath;
import jef.core.movement.player.DefaultSteerable;
import jef.core.movement.player.SpeedMatrix;

public class DefaultPlayer extends DefaultSteerable implements Player
{
	private int weight;
	private String id;
	private String firstName;
	private String lastName;
	private int height;
	private int age;
	private int number;

	private PlayerRatings ratings;

	private PlayerPosition currentPosition;
	private final List<PlayerPosition> positions = new ArrayList<>();

	public DefaultPlayer(PlayerPosition currentPosition)
	{
		this.setAV(new DefaultAngularVelocity());
		this.setLV(new LinearVelocity());
		this.setLoc(new Location());

		this.setPosture(Posture.upright);
		this.setPath(new DefaultPath());
		this.setCurrentPosition(currentPosition);
		this.setSpeedMatrix(new SpeedMatrix(currentPosition));
		this.setAccelerationCoefficient(1.0);
	}

	public DefaultPlayer(Player player)
	{
		super(player);
		this.weight = player.getWeight();
		this.id = player.getPlayerID();
		this.firstName = player.getFirstName();
		this.lastName = player.getLastName();
		this.height = player.getHeight();
		this.age = player.getAge();
		this.number = player.getNumber();
	}
	@Override
	public double getDesiredSpeed()
	{
		if (this.getPath() == null)
			return this.getSpeedMatrix().getSprintingSpeed();
		
		if (this.getPath().getCurrentWaypoint() == null)
			return this.getSpeedMatrix().getSprintingSpeed();
		
		return this.getPath().getCurrentWaypoint().getMaxSpeed();
	}

	@Override
	public int getAge()
	{
		return this.age;
	}

	@Override
	public String getFirstName()
	{
		return this.firstName;
	}

	@Override
	public int getHeight()
	{
		return this.height;
	}

	@Override
	public String getLastName()
	{
		return this.lastName;
	}

	@Override
	public int getNumber()
	{
		return this.number;
	}

	@Override
	public String getPlayerID()
	{
		if (this.id == null)
			return lastName + firstName;
		
		return this.id;
	}

	@Override
	public List<PlayerPosition> getPositions()
	{
		return Collections.unmodifiableList(this.positions);
	}

	@Override
	public PlayerPosition getPrimaryPosition()
	{
		if (this.positions.size() > 0)
			return this.positions.getFirst();

		return null;
	}

	@Override
	public PlayerRatings getRatings()
	{
		return this.ratings;
	}

	@Override
	public PlayerPosition getSecondaryPosition()
	{
		if (this.positions.size() > 1)
			return this.positions.get(1);

		return null;
	}

	@Override
	public PlayerPosition getTertiaryPosition()
	{
		if (this.positions.size() > 2)
			return this.positions.get(2);

		return null;
	}

	@Override
	public int getWeight()
	{
		return this.weight;
	}

	@Override
	public void setAge(final int age)
	{
		this.age = age;
	}

	@Override
	public void setFirstName(final String firstName)
	{
		this.firstName = firstName;
	}

	@Override
	public void setHeight(final int height)
	{
		this.height = height;
	}

	public void setId(final String id)
	{
		this.id = id;
	}

	@Override
	public void setLastName(final String lastName)
	{
		this.lastName = lastName;
	}

	@Override
	public void setNumber(final int number)
	{
		this.number = number;
	}

	public void setRatings(final PlayerRatings ratings)
	{
		this.ratings = ratings;
	}

	@Override
	public void setWeight(final int weight)
	{
		this.weight = weight;
	}

	@Override
	public PlayerPosition getCurrentPosition()
	{
		return this.currentPosition;
	}

	@Override
	public void setCurrentPosition(PlayerPosition pos)
	{
		this.currentPosition = pos;
	}

	@Override
	public String toString()
	{
		return this.firstName + " " + this.lastName;
	}

}
