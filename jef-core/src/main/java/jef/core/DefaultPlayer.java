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
import jef.core.movement.player.Path;
import jef.core.movement.player.SpeedMatrix;
import jef.core.movement.player.Waypoint;

public class DefaultPlayer implements Player
{
	private Location loc;
	private LinearVelocity lv;
	private AngularVelocity av;
	private boolean hasBall;
	private Path path;
	private Posture posture;
	private int weight;
	private String id;
	private String firstName;
	private String lastName;
	private int height;
	private int age;
	private int number;

	private SpeedMatrix speedMatrix;
	private PlayerRatings ratings;
	private PlayerPosition currentPosition;
	private final List<PlayerPosition> positions = new ArrayList<>();

	public DefaultPlayer(final Player player)
	{
		this.loc = player.getLoc();
		this.lv = player.getLV();
		this.av = player.getAV();
		this.hasBall = player.hasBall();

		this.speedMatrix = new SpeedMatrix(player.getSpeedMatrix());

		this.path = player.getPath();
		this.posture = player.getPosture();
		this.weight = player.getWeight();
		this.firstName = player.getFirstName();
		this.lastName = player.getLastName();
		this.height = player.getHeight();
		this.age = player.getAge();
		this.number = player.getNumber();
		this.ratings = player.getRatings();
		this.currentPosition = player.getCurrentPosition();
		this.positions.clear();
		this.positions.addAll(player.getPositions());
	}

	public DefaultPlayer()
	{
		this.loc = new DefaultLocation(0, 0, 0);
		this.lv = new DefaultLinearVelocity();
		this.av = new DefaultAngularVelocity();
		this.hasBall = false;
		this.posture = Posture.upright;
	}

	@Override
	public double getAccelerationCoefficient()
	{
		return 1.0;
	}

	@Override
	public void setSpeedMatrix(SpeedMatrix matrix)
	{
		this.speedMatrix = new SpeedMatrix(matrix);
	}

	@Override
	public int getAge()
	{
		return this.age;
	}

	@Override
	public AngularVelocity getAV()
	{
		return this.av;
	}

	@Override
	public PlayerPosition getCurrentPosition()
	{
		return this.currentPosition;
	}

	@Override
	public double getDesiredSpeed()
	{
		if (this.path != null)
		{
			final Waypoint wp = this.path.getCurrentWaypoint();
			if (wp != null)
				return wp.getMaxSpeed();
		}

		return this.speedMatrix.getJoggingSpeed();
	}

	@Override
	public SpeedMatrix getSpeedMatrix()
	{
		return this.speedMatrix;
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
	public Location getLoc()
	{
		return this.loc;
	}

	@Override
	public LinearVelocity getLV()
	{
		return this.lv;
	}

	@Override
	public double getMaxSpeed()
	{
		return this.speedMatrix.getSprintingSpeed();
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
	public Posture getPosture()
	{
		return this.posture;
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
	public boolean hasBall()
	{
		return this.hasBall;
	}

	@Override
	public void setAge(final int age)
	{
		this.age = age;
	}

	@Override
	public void setAV(final AngularVelocity angularVelocity)
	{
		this.av = angularVelocity;
	}

	public void setCurrentPosition(final PlayerPosition currentPosition)
	{
		this.currentPosition = currentPosition;
	}

	@Override
	public void setFirstName(final String firstName)
	{
		this.firstName = firstName;
	}

	@Override
	public void setHasBall(final boolean hasBall)
	{
		this.hasBall = hasBall;
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
	public void setLoc(final Location location)
	{
		this.loc = location;
	}

	@Override
	public void setLV(final LinearVelocity lv)
	{
		this.lv = lv;
	}

	@Override
	public void setNumber(final int number)
	{
		this.number = number;
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

	public void setRatings(final PlayerRatings ratings)
	{
		this.ratings = ratings;
	}

	@Override
	public void setWeight(final int weight)
	{
		this.weight = weight;
	}
}
