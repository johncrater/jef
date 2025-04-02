package jef.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Unit
{
	private Team team;
	private String name;
	private UnitType unitType;
	
	private Map<String, Squad> squads = new HashMap<>();
	private Map<String, Formation> formations = new HashMap<>();
	private Map<String, Play> plays = new HashMap<>();
	private Set<Player> players = new HashSet<>();
		
	public Unit(Team team, UnitType unitType, String name)
	{
		super();
		this.team = team;
		this.unitType = unitType;
		this.name = name;
		
		this.team.addUnit(this);
	}

	public UnitType getUnitType()
	{
		return this.unitType;
	}

	public Team getTeam()
	{
		return this.team;
	}

	public String getName()
	{
		return this.name;
	}
	
	// only for use by Squad constructor
	void addSquad(Squad squad)
	{
		this.squads.put(squad.getName(), squad);
	}
	
	// only for use by Formation constructor 
	void addFormation(Formation formation)
	{
		assert this.unitType == formation.getUnitType();
		this.formations.put(formation.getName(), formation);
	}
	
	// only for use by Play constructor 
	void addPlay(Play play)
	{
		assert this.unitType == play.getUnitType();
		this.plays.put(play.getName(), play);
	}
	
	public void addPlayer(Player player)
	{
		this.players.add(player);
	}
	
	public Collection<Formation> formations()
	{
		return this.formations.values();
	}
	
	public Collection<Play> plays()
	{
		return this.plays.values();
	}
	
	public Collection<Player> players()
	{
		return Collections.unmodifiableCollection(this.players);
	}
	
	public Collection<Squad> squads()
	{
		return this.squads.values();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(name, team);
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
		Unit other = (Unit) obj;
		return Objects.equals(this.name, other.name) && Objects.equals(this.team, other.team);
	}

	@Override
	public String toString()
	{
		return "Unit [team=" + this.team + ", name=" + this.name + "]";
	}
}
