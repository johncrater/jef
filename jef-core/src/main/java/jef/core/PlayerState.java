package jef.core;

import java.util.Objects;

import jef.core.movement.Posture;
import jef.core.movement.player.Path;
import jef.core.movement.player.SpeedMatrix;

public class PlayerState
{
	private LinearVelocity lv;
	private Location loc;
	private AngularVelocity av;
	private Posture posture;
	private Player player;
	
	public PlayerState(Player player)
	{
		this(player, null, null, null, null);
	}
	
	public PlayerState(Player player, LinearVelocity lv, Location loc, AngularVelocity av, Posture posture)
	{
		assert player != null;
		this.player = player;
		
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

	public Player getPlayer()
	{
		return this.player;
	}

	public double getMaxSpeed()
	{
		return this.player.getSpeedMatrix().getSprintingSpeed();
	}
	
	public SpeedMatrix getSpeedMatrix()
	{
		return player.getSpeedMatrix();
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
		
		return new PlayerState(this.player, lv, loc, av, posture);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(av, loc, lv, player, posture);
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
				&& Objects.equals(this.lv, other.lv) 
				&& Objects.equals(this.player, other.player) && this.posture == other.posture;
	}

	@Override
	public String toString()
	{
		return this.player + "" + this.lv + ", " + this.loc + ", " + this.av
				+ ", " + this.posture;
	}
	
	
}
