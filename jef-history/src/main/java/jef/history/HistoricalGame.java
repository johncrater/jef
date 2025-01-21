package jef.history;

import java.util.Date;

public class HistoricalGame
{
	private int season;
	private int week;
	private String visitingTeam;
	private String homeTeam;
	private Date date;
	private int visitingTeamScore;
	private int homeTeamScore;
	private final HistoricalLeague league;
	private String type;

	public HistoricalGame(final HistoricalLeague league)
	{
		this.league = league;
	}

	public Date getDate()
	{
		return this.date;
	}

	public String getHomeTeam()
	{
		return this.homeTeam;
	}

	public int getHomeTeamScore()
	{
		return this.homeTeamScore;
	}

	public HistoricalLeague getLeague()
	{
		return this.league;
	}

	public int getSeason()
	{
		return this.season;
	}

	public String getType()
	{
		return this.type;
	}

	public String getVisitingTeam()
	{
		return this.visitingTeam;
	}

	public int getVisitingTeamScore()
	{
		return this.visitingTeamScore;
	}

	public int getWeek()
	{
		return this.week;
	}

	public void setDate(final Date date)
	{
		this.date = date;
	}

	public void setHomeTeam(final String homeTeam)
	{
		this.homeTeam = homeTeam;
	}

	public void setHomeTeamScore(final int homeTeamScore)
	{
		this.homeTeamScore = homeTeamScore;
	}

	public void setSeason(final int season)
	{
		this.season = season;
	}

	public void setType(final String type)
	{
		this.type = type;
	}

	public void setVisitingTeam(final String visitingTeam)
	{
		this.visitingTeam = visitingTeam;
	}

	public void setVisitingTeamScore(final int visitingTeamScore)
	{
		this.visitingTeamScore = visitingTeamScore;
	}

	public void setWeek(final int week)
	{
		this.week = week;
	}

}
