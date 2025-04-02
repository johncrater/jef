package jef.testapps;

import java.util.Objects;

import jef.formations.IFormationPlayer;
import jef.formations.PlayerPosition;
import jef.formations.SpeedMatrix;
import jef.geometry.Conversions;
import jef.movement.PlayerId;
import jef.movement.TeamId;
import jef.movement.player.ISpeedMatrix;

public class TestViewerPlayer implements IFormationPlayer
{
	private int weight;
	private double height;
	private ISpeedMatrix speedMatrix;
	private TeamId teamId;
	private String firstName;
	private String lastName;
	private PlayerPosition playerPosition;
	
	public TestViewerPlayer(PlayerPosition playerPosition)
	{
		super();
		this.playerPosition = playerPosition;
		this.speedMatrix = new SpeedMatrix(playerPosition);
		this.height = Conversions.inchesToYards(74.0);  // 6'2"
	}

	public double getHeight()
	{
		return this.height;
	}

	public void setHeight(double height)
	{
		this.height = height;
	}

	public PlayerPosition getPlayerPosition()
	{
		return this.playerPosition;
	}

	public void setPlayerPosition(PlayerPosition playerPosition)
	{
		this.playerPosition = playerPosition;
	}

	@Override
	public int getWeight()
	{
		return weight;
	}

	@Override
	public ISpeedMatrix getSpeedMatrix()
	{
		return this.speedMatrix;
	}

	@Override
	public PlayerId getId()
	{
		return PlayerId.getId(this.getLastName() + "/" + this.getFirstName());
	}

	@Override
	public TeamId getTeamId()
	{
		return this.teamId;
	}

	public String getFirstName()
	{
		return this.firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return this.lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public void setWeight(int weight)
	{
		this.weight = weight;
	}

	public void setSpeedMatrix(ISpeedMatrix speedMatrix)
	{
		this.speedMatrix = speedMatrix;
	}

	public void setTeamId(TeamId teamId)
	{
		this.teamId = teamId;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(firstName, lastName);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestViewerPlayer other = (TestViewerPlayer) obj;
		return Objects.equals(this.firstName, other.firstName) && Objects.equals(this.lastName, other.lastName);
	}

	@Override
	public String toString()
	{
		return "TestViewerPlayer [getId()=" + this.getId() + "]";
	}
	
}
