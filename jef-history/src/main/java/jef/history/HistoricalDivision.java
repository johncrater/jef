package jef.history;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoricalDivision
{
	private final HistoricalConference conference;
	private final Map<String, HistoricalTeam> teams = new HashMap<>();
	private final String name;

	public HistoricalDivision(final HistoricalConference conference, final String name)
	{
		this.conference = conference;
		this.name = name;
	}

	public void addTeam(final HistoricalTeam team)
	{
		this.teams.put(team.getAbbreviation().toLowerCase(), team);
	}

	public List<HistoricalTeam> getAllTeams()
	{
		return this.teams.values().stream().toList();
	}

	public HistoricalConference getConference()
	{
		return this.conference;
	}

	public String getName()
	{
		return this.name;
	}

	public HistoricalTeam getTeam(final String teamAbbreviation)
	{
		return this.teams.get(teamAbbreviation.toLowerCase());
	}

	@Override
	public String toString()
	{
		return "" + this.getConference().toString() + " " + this.getName();
	}
}
