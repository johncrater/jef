package jef.core;

import jef.core.movement.Posture;
import jef.core.movement.player.Path;
import jef.core.movement.player.SpeedMatrix;

public class PlayerState
{
	private LinearVelocity lv;
	private Location loc;
	private AngularVelocity av;
	private Path path;
	private Posture posture;
	private Player player;
	
	public PlayerState(Player player)
	{
		this(player, null, null, null, null, null);
	}
	
	public PlayerState(Player player, LinearVelocity lv, Location loc, AngularVelocity av, Path path, Posture posture)
	{
		assert player != null;
		this.player = player;
		
		if (lv == null)
			lv = new LinearVelocity();
		
		if (loc == null)
			loc = new Location();
		
		if (av == null)
			av = new AngularVelocity();

		if (path == null)
			path = new Path();
		
		if (posture == null)
			posture = Posture.upright;
		
		this.lv = lv;
		this.loc = loc;
		this.av = av;
		this.path = path;
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

	public Path getPath()
	{
		return this.path;
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
	
	public PlayerState newFrom(LinearVelocity lv, Location loc, AngularVelocity av, Path path, Posture posture)
	{
		if (lv == null)
			lv = this.lv;
		
		if (loc == null)
			loc = this.loc;
		
		if (av == null)
			av = this.av;
		
		if (path == null)
			path = this.path;
		
		if (posture == null)
			posture = this.posture;
		
		return new PlayerState(this.player, lv, loc, av, path, posture);
	}
	
	@Override
	public String toString()
	{
		return "PlayerState [player=" + this.player + ", lv=" + this.lv + ", loc=" + this.loc + ", av=" + this.av
				+ ", path=" + this.path + ", posture=" + this.posture + "]";
	}
}
