package jef.core;

import com.synerset.unitility.unitsystem.common.Distance;

import jef.core.movement.DUnits;

public class Football
{
	public static final double lengthOfTheMajorAxis = Distance.ofInches(11.25f).getInUnit(DUnits.YARD);
	public static final double lengthOfTheMinorAxis = Distance.ofInches(6.70f).getInUnit(DUnits.YARD);
	
	public static final Football theFootball = new Football();

	private LinearVelocity lv;
	private Location loc;
	private AngularVelocity av;
	private Player playerInPossession;

	public Football()
	{
		this(null, null, null);
	}

	public Football(LinearVelocity lv, Location loc, AngularVelocity av)
	{
		if (lv == null)
			lv = new LinearVelocity();
		
		if (loc == null)
			loc = new Location();
		
		if (av == null)
			av = new AngularVelocity();
		
		this.lv = lv;
		this.loc = loc;
		this.av = av;
	}

	public Player getPlayerInPossession()
	{
		return this.playerInPossession;
	}

	public void setPlayerInPossession(Player player)
	{
		this.playerInPossession = player;
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

	public Football newFrom(LinearVelocity lv, Location loc, AngularVelocity av)
	{
		if (lv == null)
			lv = this.lv;
		
		if (loc == null)
			loc = this.loc;
		
		if (av == null)
			av = this.av;
		
		return new Football(lv, loc, av);
	}
}
