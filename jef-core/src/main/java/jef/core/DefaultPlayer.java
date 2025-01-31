package jef.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jef.core.movement.AngularVelocity;
import jef.core.movement.DefaultAngularVelocity;
import jef.core.movement.DefaultLinearVelocity;
import jef.core.movement.DefaultLocation;
import jef.core.movement.LinearVelocity;
import jef.core.movement.Location;
import jef.core.movement.Posture;
import jef.core.movement.player.DefaultPath;
import jef.core.movement.player.Path;
import jef.core.movement.player.SpeedMatrix;

public class DefaultPlayer implements Player
{
	private LinearVelocity lv;
	private Location loc;
	private AngularVelocity av;

	private SpeedMatrix speedMatrix;
	private Path path;
	private Posture posture;
	private double accelerationCoefficient;

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
		this.av = new DefaultAngularVelocity();
		this.lv = new DefaultLinearVelocity();
		this.loc = new DefaultLocation();

		this.posture = Posture.upright;
		this.path = new DefaultPath();
		this.currentPosition = currentPosition;
		this.speedMatrix = new SpeedMatrix(currentPosition);
		this.accelerationCoefficient = 1.0;
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
	public AngularVelocity getAV()
	{
		return av;
	}

	@Override
	public void setAV(AngularVelocity angularVelocity)
	{
		this.av = angularVelocity;
	}

	@Override
	public Location getLoc()
	{
		return this.loc;
	}

	@Override
	public void setLoc(Location location)
	{
		this.loc = location;
	}

	@Override
	public LinearVelocity getLV()
	{
		return this.lv;
	}

	@Override
	public void setLV(LinearVelocity lv)
	{
		this.lv = lv;
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
	public double getAccelerationCoefficient()
	{
		return this.accelerationCoefficient;
	}

	@Override
	public double getMaxSpeed()
	{
		return this.speedMatrix.getSprintingSpeed();
	}

	@Override
	public Path getPath()
	{
		return this.path;
	}

	@Override
	public Posture getPosture()
	{
		return this.posture;
	}

	@Override
	public SpeedMatrix getSpeedMatrix()
	{
		return this.speedMatrix;
	}

	@Override
	public void setAccelerationCoefficient(final double coefficient)
	{
		this.accelerationCoefficient = coefficient;
	}

	@Override
	public void setPath(final Path path)
	{
		this.path = path;
	}

	@Override
	public void setPosture(final Posture posture)
	{
		this.posture = posture;
	}

	@Override
	public void setSpeedMatrix(final SpeedMatrix matrix)
	{
		this.speedMatrix = matrix;
	}
}
