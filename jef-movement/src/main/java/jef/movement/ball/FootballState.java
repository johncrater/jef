package jef.movement.ball;

import com.synerset.unitility.unitsystem.common.Distance;

import jef.geometry.AngularVelocity;
import jef.geometry.DUnits;
import jef.geometry.Direction;
import jef.geometry.Field;
import jef.geometry.LinearVelocity;
import jef.geometry.Location;
import jef.movement.PlayerId;
import jef.movement.TeamId;

public class FootballState
{
	public static FootballState beginGame()
	{
		return new FootballState();
	}
	
	public static final double lengthOfTheMajorAxis = Distance.ofInches(11.25f).getInUnit(DUnits.YARD);
	public static final double lengthOfTheMinorAxis = Distance.ofInches(6.70f).getInUnit(DUnits.YARD);

	private final LinearVelocity lv;
	private final Location loc;
	private final AngularVelocity av;
	private final PlayerId playerInPossession;
	private final TeamId teamInPossession;
	private final Direction currentOffenseDirection;
	private final Location pointOfScrimmage;

	private FootballState()
	{
		this(TeamId.NONE, PlayerId.NONE, null, Field.MIDFIELD, new LinearVelocity(), Field.MIDFIELD, new AngularVelocity());
	}
	
	private FootballState(TeamId teamInPossession, PlayerId playerInPossession, 
			final Direction currentOffenseDirection, final Location pointOfScrimmage, final LinearVelocity lv,
			final Location loc, final AngularVelocity av)
	{
		if (teamInPossession == null)
			teamInPossession = TeamId.NONE;
		
		if (playerInPossession == null)
			playerInPossession = PlayerId.NONE;
		
		this.teamInPossession = teamInPossession;
		this.playerInPossession = playerInPossession;
		
		this.currentOffenseDirection = currentOffenseDirection;
		this.pointOfScrimmage = pointOfScrimmage;
		this.lv = lv;
		this.loc = loc;
		this.av = av;
	}

//	/**
//	 * Beginning of a game
//	 * @param teamInPossession
//	 * @param playerInPossession
//	 * @param currentOffenseDirection
//	 * @param pointOfScrimmage
//	 */
//	public FootballState(TeamId teamInPossession, PlayerId playerInPossession, 
//			final Direction currentOffenseDirection, final Location pointOfScrimmage)
//	{
//		this(teamInPossession, playerInPossession, currentOffenseDirection, pointOfScrimmage, new LinearVelocity(), pointOfScrimmage, new AngularVelocity());
//	}
//
	/**
	 * Marks a change in possession within the same team such as a snap, handoff, or pass
	 * @param playerInPossession
	 * @return
	 */
	public FootballState changePossession(PlayerId playerInPossession)
	{
		if (playerInPossession == null)
			playerInPossession = PlayerId.NONE;
		
		return newFrom(null, playerInPossession, null, null, null, null, null);
	}
	
	/**
	 * Change of possession
	 * @param teamInPossession
	 * @param direction
	 * @return
	 */
//	public FootballState newFrom(TeamId teamInPossession, Direction direction)
//	{
//		return newFrom(teamInPossession, null, null, null, null, null, null);
//	}
//	
	/**
	 * Useful for a turnover
	 * @param teamInPossession
	 * @param playerInPossession
	 * @return
	 */
	public FootballState turnover(TeamId teamInPossession, PlayerId playerInPossession, Direction direction)
	{
		return newFrom(teamInPossession, playerInPossession, direction, null, null, null, null);
	}
	
	private FootballState newFrom(TeamId teamInPossession, PlayerId playerInPossession, 
			Direction currentOffenseDirection, Location pointOfScrimmage, LinearVelocity lv,
			Location loc, AngularVelocity av)
	{
		if (teamInPossession == null)
			teamInPossession = this.teamInPossession;
		
		if (playerInPossession == null)
			playerInPossession = this.playerInPossession;
		
		if (currentOffenseDirection == null)
			currentOffenseDirection = this.currentOffenseDirection;
	
		if (pointOfScrimmage == null)
			pointOfScrimmage = this.pointOfScrimmage;
		
		if (lv == null)
			lv = this.lv;
		
		if (loc == null)
			loc = this.loc;
		
		if (av == null)
			av = this.av;
		
		return new FootballState(teamInPossession, playerInPossession, currentOffenseDirection, pointOfScrimmage, lv, loc, av);
	}
	
	
	public FootballState spot(Location loc)
	{
		return this.newFrom(null, null, null, loc, null, loc, null);
	}
	/**
	 * change the location, LV and AV without changing any of the possession attributes
	 * @param lv
	 * @param loc
	 * @param av
	 * @return
	 */
	public FootballState move(LinearVelocity lv, Location loc, AngularVelocity av)
	{
		if (lv == null)
			lv = this.lv;
		
		if (loc == null)
			loc = this.loc;
		
		if (av == null)
			av = this.av;
		
		return newFrom(teamInPossession, null, null, null, lv, loc, av);
	}
	
	public AngularVelocity getAV()
	{
		return this.av;
	}

	public Direction getCurrentOffenseDirection()
	{
		return this.currentOffenseDirection;
	}

	public Location getLoc()
	{
		return this.loc;
	}

	public LinearVelocity getLV()
	{
		return this.lv;
	}

	public PlayerId getPlayerInPossession()
	{
		return this.playerInPossession;
	}

	public Location getPointOfScrimmage()
	{
		return this.pointOfScrimmage;
	}

	public TeamId getTeamInPossession()
	{
		return this.teamInPossession;
	}

}