package jef.core;

import jef.core.movement.AngularVelocity;
import jef.core.movement.DefaultAngularVelocity;
import jef.core.movement.DefaultLinearVelocity;
import jef.core.movement.DefaultLocation;
import jef.core.movement.LinearVelocity;
import jef.core.movement.Location;

public class TestBall implements Football
{
	private AngularVelocity angularVelocity;
	private LinearVelocity linearVelocity;
	private Location location;
	
	public TestBall(Location location, LinearVelocity linearVelocity, AngularVelocity angularVelocity)
	{
		this.location = location;
		this.linearVelocity = linearVelocity;
		this.angularVelocity = angularVelocity;
	}

	public TestBall()
	{
		this.location = new DefaultLocation();
		this.linearVelocity = new DefaultLinearVelocity();
		this.angularVelocity = new DefaultAngularVelocity();
	}

	@Override
	public AngularVelocity getAV()
	{
		return this.angularVelocity;
	}

	@Override
	public LinearVelocity getLV()
	{
		return this.linearVelocity;
	}

	@Override
	public Location getLoc()
	{
		return this.location;
	}

	@Override
	public void setAV(AngularVelocity angularVelocity)
	{
		this.angularVelocity = angularVelocity;
	}

	@Override
	public void setLV(LinearVelocity lv)
	{
		this.linearVelocity = lv;
	}

	@Override
	public void setLoc(Location location)
	{
		this.location = location;
	}

	@Override
	public Player getPlayerInPossession()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPlayerInPossession()
	{
		// TODO Auto-generated method stub
		
	}

}