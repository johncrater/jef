package jef.history;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoricalConference
{
	private final HistoricalLeague league;
	private final Map<String, HistoricalDivision> divisions = new HashMap<>();
	private final String name;
	private final int season;
	private final String abbreviation;

	public HistoricalConference(final HistoricalLeague league, final int season, final String abbreviation,
			final String name)
	{
		this.league = league;
		this.season = season;
		this.abbreviation = abbreviation;
		this.name = name;
	}

	public void addDivision(final HistoricalDivision division)
	{
		this.divisions.put(division.getName().toLowerCase(), division);
	}

	public String getAbbreviation()
	{
		return this.abbreviation;
	}

	public List<HistoricalDivision> getAllDivisions()
	{
		return this.divisions.values().stream().toList();
	}

	public HistoricalDivision getDivision(final String name)
	{
		return this.divisions.get(name.toLowerCase());
	}

	public HistoricalLeague getLeague()
	{
		return this.league;
	}

	public String getName()
	{
		return this.name;
	}

	public int getSeason()
	{
		return this.season;
	}

	@Override
	public String toString()
	{
		return "" + this.getSeason() + " " + this.getAbbreviation();
	}
}
