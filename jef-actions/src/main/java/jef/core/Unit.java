package jef.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class Unit
{
	private Team team;
	private String name;
	
	private Map<String, Squad> squads = new HashMap<>();

	public Unit(Team team, String name)
	{
		super();
		this.team = team;
		this.name = name;
	}

	public void addSquad(Squad squad)
	{
		this.squads.put(squad.getName(), squad);
	}
	
	public Team getTeam()
	{
		return this.team;
	}

	public String getName()
	{
		return this.name;
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
