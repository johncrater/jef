package jef.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class League 
{
	private String name;
	private Map<String, Conference> conferences = new HashMap<>();

	public League(String name)
	{
		super();
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}

	public Collection<Conference> conferences()
	{
		return this.conferences.values();
	}
	
	public Collection<Division> divisions()
	{
		return this.conferences().stream().flatMap(c -> c.divisions().stream()).toList();
	}

	public Collection<Team> teams()
	{
		return this.divisions().stream().flatMap(d -> d.teams().stream()).toList();
	}
	
	public Collection<Player> players()
	{
		return this.teams().stream().flatMap(t -> t.players().stream()).toList();
	}
	
	public void addConference(Conference conference)
	{
		conferences.put(conference.getName(), conference);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(name);
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
		League other = (League) obj;
		return Objects.equals(this.name, other.name);
	}

	@Override
	public String toString()
	{
		return "League [name=" + this.name + "]";
	}
}
