package jef.history;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HistoricalLeague
{
	private final String name;
	private final String abbreviation;

	private final TreeMap<Integer, Map<String, HistoricalConference>> conferences = new TreeMap<>();

	public HistoricalLeague(final String abbreviation, final String name)
	{
		this.name = name;
		this.abbreviation = abbreviation;
	}

	public void addHistoricalConference(final HistoricalConference conference)
	{
		var conferenceMap = this.conferences.get(conference.getSeason());
		if (conferenceMap == null)
		{
			conferenceMap = new HashMap<>();
			this.conferences.put(conference.getSeason(), conferenceMap);
		}

		conferenceMap.put(conference.getAbbreviation().toLowerCase(), conference);
	}

	public String getAbbreviation()
	{
		return this.abbreviation;
	}

	public List<HistoricalTeam> getAllTeams(final int season)
	{
		return this.getConferences(season).stream().flatMap(c -> c.getAllDivisions().stream())
				.flatMap(d -> d.getAllTeams().stream()).toList();
	}

	public HistoricalConference getConference(final int season, final String abbreviation)
	{
		return this.conferences.get(season).get(abbreviation.toLowerCase());
	}

	public Collection<HistoricalConference> getConferences(final int year)
	{
		return this.conferences.get(year).values();
	}

	public int getEndYear()
	{
		return this.conferences.lastKey();
	}

	public String getName()
	{
		return this.name;
	}

	public int getStartYear()
	{
		return this.conferences.firstKey();
	}

	@Override
	public String toString()
	{
		return this.getAbbreviation();
	}
}
