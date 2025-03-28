package jef.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Division
{
	private String name;
	private Conference conference;
	private Map<String, Team> teams = new HashMap<>();
	
	public Division(Conference conference, String name)
	{
		super();
		this.conference = conference;
		this.name = name;
		this.conference.addDivision(this);
	}

	public void addTeam(Team team)
	{
		this.teams.put(team.getFullName(), team);
	}

	public String getName()
	{
		return this.name;
	}

	public Conference getConference()
	{
		return this.conference;
	}

	public Collection<Team> teams()
	{
		return teams.values();
	}

	public Collection<Player> players()
	{
		return this.teams().stream().flatMap(t -> t.players().stream()).toList();
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(conference, name);
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
		Division other = (Division) obj;
		return Objects.equals(this.conference, other.conference) && Objects.equals(this.name, other.name);
	}

	@Override
	public String toString()
	{
		return "Division [conference=" + this.conference + ", name=" + this.name + "]";
	}


}
