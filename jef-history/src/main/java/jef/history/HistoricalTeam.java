package jef.history;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jef.core.PlayerPosition;
import jef.core.PlayerStats;

public class HistoricalTeam
{
	private enum HelmetImageType
	{
		HOME_RIGHT, HOME_LEFT, AWAY_RIGHT, AWAY_LEFT
	}
	
	private final HistoricalDivision division;
	private final String abbreviation;
	private String location;
	private String nickname;

	private final Map<String, HistoricalPlayer> players = new HashMap<>();

	private int wins;
	private int losses;
	private int ties;
	private int pointsFor;
	private int pointsAgainst;
	private String stadium;
	private String coach;
	private String owner;

	private int yards;
	private int plays;
	private int turnovers;
	private int fumblesLost;
	private int firstDowns;
	private int completions;
	private int passingAttempts;
	private int passingYards;
	private int passingTDs;
	private int interceptions;
	private int firstDownsPassing;
	private int rushingAttempts;
	private int rushingYards;
	private int rushingTDs;
	private int penalties;
	private int penaltyYards;
	private int penaltyFirstDowns;

	private int yardsOpp;
	private int playsOpp;
	private int turnoversOpp;
	private int fumblesLostOpp;
	private int firstDownsOpp;
	private int completionsOpp;
	private int passingAttemptsOpp;
	private int passingYardsOpp;
	private int passingTDsOpp;
	private int interceptionsOpp;
	private int firstDownsPassingOpp;
	private int rushingAttemptsOpp;
	private int rushingYardsOpp;
	private int rushingTDsOpp;
	private int firstRushingDowns;
	private int rushingFirstDownsOpp;
	private int penaltiesOpp;
	private int penaltyYardsOpp;
	private int penaltyFirstDownsOpp;
	
	private String homeJerseyColor;
	private String homePantsColor;
	private String homeNumberColor;
	private String homeTrimColor;
	private String homeSocksColor;
	private String awayJerseyColor;
	private String awayPantsColor;
	private String awayNumberColor;
	private String awayTrimColor;
	private String awaySocksColor;

	public String getHomeSocksColor()
	{
		return this.homeSocksColor;
	}

	public void setHomeSocksColor(String homeSocksColor)
	{
		this.homeSocksColor = homeSocksColor;
	}

	public String getAwaySocksColor()
	{
		return this.awaySocksColor;
	}

	public void setAwaySocksColor(String awaySocksColor)
	{
		this.awaySocksColor = awaySocksColor;
	}

	public HistoricalTeam(final HistoricalDivision division, final String abbreviation)
	{
		this.division = division;
		this.abbreviation = abbreviation;
	}

	public void addPlayer(final HistoricalPlayer player)
	{
		this.players.put(player.getPlayerID(), player);
	}

	public String getAbbreviation()
	{
		return this.abbreviation;
	}

	public String getAwayJerseyColor()
	{
		return this.awayJerseyColor;
	}

	public String getAwayNumberColor()
	{
		return this.awayNumberColor;
	}

	public String getAwayPantsColor()
	{
		return this.awayPantsColor;
	}

	public String getAwayTrimColor()
	{
		return this.awayTrimColor;
	}

	public String getCoach()
	{
		return this.coach;
	}

	public int getCompletions()
	{
		return this.completions;
	}

	public int getCompletionsOpp()
	{
		return this.completionsOpp;
	}

	public HistoricalDivision getDivision()
	{
		return this.division;
	}

	public int getFirstDowns()
	{
		return this.firstDowns;
	}

	public int getFirstDownsOpp()
	{
		return this.firstDownsOpp;
	}

	public int getFirstDownsPassing()
	{
		return this.firstDownsPassing;
	}

	public int getFirstDownsPassingOpp()
	{
		return this.firstDownsPassingOpp;
	}

	public int getFirstDownsRushing()
	{
		return this.firstRushingDowns;
	}

	public int getFirstDownsRushingOpp()
	{
		return this.rushingFirstDownsOpp;
	}

	public String getFullTeamName()
	{
		return this.getLocation() + " " + this.getNickname();
	}

	public int getFumblesLost()
	{
		return this.fumblesLost;
	}

	public int getFumblesLostOpp()
	{
		return this.fumblesLostOpp;
	}

	public int getGamesPlayed()
	{
		return this.getWins() + this.getLosses() + this.getTies();
	}

	public String getHomeJerseyColor()
	{
		return this.homeJerseyColor;
	}

	public String getHomeNumberColor()
	{
		return this.homeNumberColor;
	}

	public String getHomePantsColor()
	{
		return this.homePantsColor;
	}

	public String getHomeTrimColor()
	{
		return this.homeTrimColor;
	}

	public int getInterceptions()
	{
		return this.interceptions;
	}

	public int getInterceptionsOpp()
	{
		return this.interceptionsOpp;
	}

	public float getLeagueAverageFieldGoal(float distance)
	{
		float attempts = 0;
		float made = 0;
		float longest = 0;
		for (PlayerStats player : this.division.getConference().getLeague().getConferences(this.getDivision().getConference().getSeason())
				.stream().flatMap(c -> c.getAllDivisions().stream()).flatMap(d -> d.getAllTeams().stream()).flatMap(t -> t.getPlayers().stream()).toList())
		{
			if (distance <= 19)
			{
				attempts += player.getFgAttempted19();
				made += player.getFgMade19();
			}
			else if (distance <= 29)
			{
				attempts += player.getFgAttempted29();
				made += player.getFgMade29();
			}
			else if (distance <= 39)
			{
				attempts += player.getFgAttempted39();
				made += player.getFgMade39();
			}
			else if (distance <= 49)
			{
				attempts += player.getFgAttempted49();
				made += player.getFgMade49();
			}
			else
			{
				attempts += player.getFgAttempted50();
				made += player.getFgMade50();
			}
			
			longest = Math.max(longest, player.getFgLongest());
		}
		
		if (distance >= 50)
		{
			return made / attempts * (1 - (distance - 50) / (longest - 50 + 1));
		}
		else
		{
			return made / attempts;
		}
	}

	public String getLocation()
	{
		return this.location;
	}

	public int getLosses()
	{
		return this.losses;
	}

	public String getNickname()
	{
		return this.nickname;
	}

	public String getOwner()
	{
		return this.owner;
	}

	public int getPassingAttempts()
	{
		return this.passingAttempts;
	}

	public int getPassingAttemptsOpp()
	{
		return this.passingAttemptsOpp;
	}

	public int getPassingTDs()
	{
		return this.passingTDs;
	}

	public int getPassingTDsOpp()
	{
		return this.passingTDsOpp;
	}

	public int getPassingYards()
	{
		return this.passingYards;
	}

	public int getPassingYardsOpp()
	{
		return this.passingYardsOpp;
	}

	public int getPenalties()
	{
		return this.penalties;
	}

	public int getPenaltiesOpp()
	{
		return this.penaltiesOpp;
	}

	public int getPenaltyFirstDowns()
	{
		return this.penaltyFirstDowns;
	}

	public int getPenaltyFirstDownsOpp()
	{
		return this.penaltyFirstDownsOpp;
	}

	public int getPenaltyYards()
	{
		return this.penaltyYards;
	}

	public int getPenaltyYardsOpp()
	{
		return this.penaltyYardsOpp;
	}

	public HistoricalPlayer getPlayer(final String playerID)
	{
		return this.players.get(playerID);
	}

	public List<HistoricalPlayer> getPlayers()
	{
		return new ArrayList<HistoricalPlayer>(this.players.values());
	}

	public List<HistoricalPlayer> getPlayers(final PlayerPosition... positions)
	{
		final Set<HistoricalPlayer> ret = new HashSet<>();

		for (final HistoricalPlayer player : this.players.values())
			for (final PlayerPosition pos : positions)
				if (player.playsPosition(pos))
					ret.add(player);

		return new ArrayList<>(ret);
	}

	public int getPlays()
	{
		return this.plays;
	}

	public int getPlaysOpp()
	{
		return this.playsOpp;
	}

	public int getPointsAgainst()
	{
		return this.pointsAgainst;
	}

	public int getPointsFor()
	{
		return this.pointsFor;
	}

	public int getRushingAttempts()
	{
		return this.rushingAttempts;
	}

	public int getRushingAttemptsOpp()
	{
		return this.rushingAttemptsOpp;
	}

	public int getRushingTDs()
	{
		return this.rushingTDs;
	}

	public int getRushingTDsOpp()
	{
		return this.rushingTDsOpp;
	}

	public int getRushingYards()
	{
		return this.rushingYards;
	}

	public int getRushingYardsOpp()
	{
		return this.rushingYardsOpp;
	}

	public int getSeason()
	{
		return this.getDivision().getConference().getSeason();
	}

	public String getStadium()
	{
		return this.stadium;
	}

	public int getTies()
	{
		return this.ties;
	}

	public int getTurnovers()
	{
		return this.turnovers;
	}

	public int getTurnoversOpp()
	{
		return this.turnoversOpp;
	}

	public int getWins()
	{
		return this.wins;
	}

	public int getYards()
	{
		return this.yards;
	}

	public int getYardsOpp()
	{
		return this.yardsOpp;
	}

	public void setAwayJerseyColor(String awayJerseyColor)
	{
		this.awayJerseyColor = awayJerseyColor;
	}

	public void setAwayNumberColor(String awayNumberColor)
	{
		this.awayNumberColor = awayNumberColor;
	}

	public void setAwayPantsColor(String awayPantsColor)
	{
		this.awayPantsColor = awayPantsColor;
	}

	public void setAwayTrimColor(String awayTrimColor)
	{
		this.awayTrimColor = awayTrimColor;
	}

	public void setCoach(final String coach)
	{
		this.coach = coach;
	}

	public void setCompletions(final int completions)
	{
		this.completions = completions;
	}

	public void setCompletionsOpp(final int completionsOpp)
	{
		this.completionsOpp = completionsOpp;
	}

	public void setFirstDowns(final int firstDowns)
	{
		this.firstDowns = firstDowns;
	}

	public void setFirstDownsOpp(final int firstDownsOpp)
	{
		this.firstDownsOpp = firstDownsOpp;
	}

	public void setFirstDownsPassing(final int firstDownsPassing)
	{
		this.firstDownsPassing = firstDownsPassing;
	}

	public void setFirstDownsPassingOpp(final int firstDownsPassingOpp)
	{
		this.firstDownsPassingOpp = firstDownsPassingOpp;
	}

	public void setFirstDownsRushing(final int rushingFirstDowns)
	{
		this.firstRushingDowns = rushingFirstDowns;
	}

	public void setFirstDownsRushingOpp(final int rushingFirstDownsOpp)
	{
		this.rushingFirstDownsOpp = rushingFirstDownsOpp;
	}

	public void setFumblesLost(final int fumblesLost)
	{
		this.fumblesLost = fumblesLost;
	}

	public void setFumblesLostOpp(final int fumblesLostOpp)
	{
		this.fumblesLostOpp = fumblesLostOpp;
	}

	public void setHomeJerseyColor(String homeJerseyColor)
	{
		this.homeJerseyColor = homeJerseyColor;
	}

	public void setHomeNumberColor(String homeNumberColor)
	{
		this.homeNumberColor = homeNumberColor;
	}

	public void setHomePantsColor(String homePantsColor)
	{
		this.homePantsColor = homePantsColor;
	}

	public void setHomeTrimColor(String homeTrimColor)
	{
		this.homeTrimColor = homeTrimColor;
	}

	public void setInterceptions(final int interceptions)
	{
		this.interceptions = interceptions;
	}

	public void setInterceptionsOpp(final int interceptionsOpp)
	{
		this.interceptionsOpp = interceptionsOpp;
	}

	public void setLocation(final String location)
	{
		this.location = location;
	}

	public void setLosses(final int losses)
	{
		this.losses = losses;
	}

	public void setNickname(final String nickname)
	{
		this.nickname = nickname;
	}

	public void setOwner(final String owner)
	{
		this.owner = owner;
	}

	public void setPassingAttempts(final int passingAttempts)
	{
		this.passingAttempts = passingAttempts;
	}

	public void setPassingAttemptsOpp(final int passingAttemptsOpp)
	{
		this.passingAttemptsOpp = passingAttemptsOpp;
	}

	public void setPassingTDs(final int passingTDs)
	{
		this.passingTDs = passingTDs;
	}

	public void setPassingTDsOpp(final int passingTDsOpp)
	{
		this.passingTDsOpp = passingTDsOpp;
	}

	public void setPassingYards(final int passingYards)
	{
		this.passingYards = passingYards;
	}

	public void setPassingYardsOpp(final int passingYardsOpp)
	{
		this.passingYardsOpp = passingYardsOpp;
	}

	public void setPenalties(final int penalties)
	{
		this.penalties = penalties;
	}

	public void setPenaltiesOpp(final int penaltiesOpp)
	{
		this.penaltiesOpp = penaltiesOpp;
	}

	public void setPenaltyFirstDowns(final int penaltyFirstDowns)
	{
		this.penaltyFirstDowns = penaltyFirstDowns;
	}

	public void setPenaltyFirstDownsOpp(final int penaltyFirstDownsOpp)
	{
		this.penaltyFirstDownsOpp = penaltyFirstDownsOpp;
	}

	public void setPenaltyYards(final int penaltyYards)
	{
		this.penaltyYards = penaltyYards;
	}

	public void setPenaltyYardsOpp(final int penaltyYardsOpp)
	{
		this.penaltyYardsOpp = penaltyYardsOpp;
	}

	public void setPlays(final int plays)
	{
		this.plays = plays;
	}

	public void setPlaysOpp(final int playsOpp)
	{
		this.playsOpp = playsOpp;
	}

	public void setPointsAgainst(final int pointsAgainst)
	{
		this.pointsAgainst = pointsAgainst;
	}

	public void setPointsFor(final int pointsFor)
	{
		this.pointsFor = pointsFor;
	}

	public void setRushingAttempts(final int rushingAttempts)
	{
		this.rushingAttempts = rushingAttempts;
	}

	public void setRushingAttemptsOpp(final int rushingAttemptsOpp)
	{
		this.rushingAttemptsOpp = rushingAttemptsOpp;
	}

	public void setRushingTDs(final int rushingTDs)
	{
		this.rushingTDs = rushingTDs;
	}

	public void setRushingTDsOpp(final int rushingTDsOpp)
	{
		this.rushingTDsOpp = rushingTDsOpp;
	}

	public void setRushingYards(final int rushingYards)
	{
		this.rushingYards = rushingYards;
	}

	public void setRushingYardsOpp(final int rushingYardsOpp)
	{
		this.rushingYardsOpp = rushingYardsOpp;
	}

	public void setStadium(final String stadium)
	{
		this.stadium = stadium;
	}

	public void setTies(final int ties)
	{
		this.ties = ties;
	}

	public void setTurnovers(final int turnovers)
	{
		this.turnovers = turnovers;
	}

	public void setTurnoversOpp(final int turnoversOpp)
	{
		this.turnoversOpp = turnoversOpp;
	}

	public void setWins(final int wins)
	{
		this.wins = wins;
	}

	public void setYards(final int yards)
	{
		this.yards = yards;
	}

	public void setYardsOpp(final int yardsOpp)
	{
		this.yardsOpp = yardsOpp;
	}
	
	@Override
	public String toString()
	{
		return "" + this.getDivision().getConference().getSeason() + " " + this.getLocation() + " "
				+ this.getNickname();
	}
}
