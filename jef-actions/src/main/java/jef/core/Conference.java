package jef.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Conference
{
	private final String name;
	private final League league;
	private final Map<String, Division> divisions = new HashMap<>();

	public Conference(final League league, final String name)
	{
		this.league = league;
		this.name = name;
		this.league.addConference(this);
	}

	public void addDivision(Division division)
	{
		this.divisions.put(division.getName(), division);
	}
	
	public Collection<Division> divisions()
	{
		return this.divisions.values();
	}

	public Collection<Team> teams()
	{
		return this.divisions().stream().flatMap(d -> d.teams().stream()).toList();
	}
	
	public Collection<Player> players()
	{
		return this.teams().stream().flatMap(t -> t.players().stream()).toList();
	}
	
	public League getLeague()
	{
		return this.league;
	}

	public String getName()
	{
		return this.name;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(league, name);
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
		Conference other = (Conference) obj;
		return Objects.equals(this.league, other.league) && Objects.equals(this.name, other.name);
	}

	@Override
	public String toString()
	{
		return "Conference [league=" + this.league + ", name=" + this.name + "]";
	}

}
