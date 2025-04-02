package jef.movement.player;

import java.util.Objects;

import jef.geometry.AngularVelocity;
import jef.geometry.LinearVelocity;
import jef.geometry.Location;
import jef.movement.PlayerId;

public class PlayerState 
{
	private LinearVelocity lv;
	private Location loc;
	private AngularVelocity av;
	private Posture posture;
	private ISpeedMatrix speedMatrix;
	private PlayerId playerId;

	public PlayerState(PlayerId playerId, ISpeedMatrix speedMatrix, LinearVelocity lv, Location loc, AngularVelocity av, Posture posture)
	{
		assert playerId != null;
		assert speedMatrix != null;
		
		this.playerId = playerId;
		this.speedMatrix = speedMatrix;
		
		if (lv == null)
			lv = new LinearVelocity();

		if (loc == null)
			loc = new Location();

		if (av == null)
			av = new AngularVelocity();

		if (posture == null)
			posture = Posture.upright;

		this.lv = lv;
		this.loc = loc;
		this.av = av;
		this.posture = posture;
	}

	public PlayerId getPlayerId()
	{
		return this.playerId;
	}

	public ISpeedMatrix getSpeedMatrix()
	{
		return this.speedMatrix;
	}

	public double getMaxSpeed()
	{
		return this.speedMatrix.getSprintingSpeed();
	}
	
	public LinearVelocity getLV()
	{
		return this.lv;
	}

	public Location getLoc()
	{
		return this.loc;
	}

	public AngularVelocity getAV()
	{
		return this.av;
	}

	public Posture getPosture()
	{
		return this.posture;
	}

	public PlayerState newFrom(LinearVelocity lv, Location loc, AngularVelocity av, Posture posture)
	{
		if (lv == null)
			lv = this.lv;

		if (loc == null)
			loc = this.loc;

		if (av == null)
			av = this.av;

		if (posture == null)
			posture = this.posture;

		return new PlayerState(this.playerId, this.speedMatrix, lv, loc, av, posture);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(av, loc, lv, posture);
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
		PlayerState other = (PlayerState) obj;
		return Objects.equals(this.av, other.av) && Objects.equals(this.loc, other.loc)
				&& Objects.equals(this.lv, other.lv) && this.posture == other.posture;
	}

	@Override
	public String toString()
	{
		return this.lv + ", " + this.loc + ", " + this.av + ", " + this.posture;
	}

}
