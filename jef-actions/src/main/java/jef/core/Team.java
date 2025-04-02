package jef.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Team
{
	private int year;
	private String placeName;
	private String nickName;
	private Division division;

	private Set<Player> players = new HashSet<>();
	private Map<UnitType, Unit> units = new HashMap<>();
	
	public Team(Division division, int year, String placeName, String nickName)
	{
		super();
		this.division = division;
		this.year = year;
		this.placeName = placeName;
		this.nickName = nickName;
		
		this.division.addTeam(this);
	}

	// only used by unit constructor
	void addUnit(Unit unit)
	{
		this.units.put(unit.getUnitType(), unit);
	}
	
	public Unit getUnit(UnitType unitType)
	{
		return this.units.get(unitType);
	}
	
	public Collection<Player> players()
	{
		return this.players;
	}
	
	public void addPlayer(Player player)
	{
		this.players.add(player);
	}
	
	public String getFullName()
	{
		return "" + year + " " + placeName + " " + nickName;
	}
	
	public int getYear()
	{
		return this.year;
	}

	public String getPlaceName()
	{
		return this.placeName;
	}

	public String getNickName()
	{
		return this.nickName;
	}

	public Division getDivision()
	{
		return this.division;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(division, nickName, placeName, year);
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
		Team other = (Team) obj;
		return Objects.equals(this.division, other.division) && Objects.equals(this.nickName, other.nickName)
				&& Objects.equals(this.placeName, other.placeName) && this.year == other.year;
	}

	@Override
	public String toString()
	{
		return "Team [division=" + this.division + ", getFullName()=" + this.getFullName() + "]";
	}
}
