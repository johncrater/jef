package jef.core;

import jef.core.movement.AngularVelocity;
import jef.core.movement.DefaultAngularVelocity;
import jef.core.movement.DefaultLinearVelocity;
import jef.core.movement.DefaultLocation;
import jef.core.movement.LinearVelocity;
import jef.core.movement.Location;

public class DefaultFootball implements Football
{
	private Location loc;
	private LinearVelocity lv;
	private AngularVelocity av;
	private Player playerInPossession;
	
	public DefaultFootball()
	{
		this.loc = new DefaultLocation(Field.MIDFIELD_X, Field.MIDFIELD_Y, 0);
		this.lv = new DefaultLinearVelocity();
		this.av = new DefaultAngularVelocity();
	}

	public DefaultFootball(Football football)
	{
		this.loc = football.getLoc();
		this.lv = football.getLV();
		this.av = football.getAV();
		this.playerInPossession = football.getPlayerInPossession();
	}
	
	@Override
	public AngularVelocity getAV()
	{
		return this.av;
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
	public Player getPlayerInPossession()
	{
		return this.playerInPossession;
	}

	@Override
	public void setPlayerInPossession(Player player)
	{
		if (this.playerInPossession != null)
			this.playerInPossession.setHasBall(false);

		this.playerInPossession = player;
		if (this.playerInPossession != null)
			this.playerInPossession.setHasBall(true);
	}

}
