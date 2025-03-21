package jef.history;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import jef.core.PlayerInfo;

public class Database
{
	private static Database defaultDatabase;

	public static Database getDefaultDatabase()
	{
		try
		{
			if (Database.defaultDatabase == null)
			{
				Database.defaultDatabase = new Database();
				Database.defaultDatabase.connect();
			}
		}
		catch (final Exception e)
		{
			Database.report(e);
		}

		return Database.defaultDatabase;
	}

	public static void report(final Exception e)
	{
		e.printStackTrace(System.err);
	}

	protected Connection con;

	private int autoCommitCount = 0;

	public void addHistoricalGame(final HistoricalGame game) throws SQLException
	{
		final var stmt = this.con.prepareStatement(
				"""
						INSERT INTO jfg.historical_games
								(season, visitingTeamAbbreviation, homeTeamAbbreviation, date, visitingTeamScore, homeTeamScore, leagueAbbreviation, type, week)
							VALUES
								(?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE season = season""");

		var param = 1;
		stmt.setInt(param++, game.getSeason());
		stmt.setString(param++, game.getVisitingTeam());
		stmt.setString(param++, game.getHomeTeam());
		stmt.setDate(param++, new java.sql.Date(game.getDate().getTime()));
		stmt.setInt(param++, game.getVisitingTeamScore());
		stmt.setInt(param++, game.getHomeTeamScore());
		stmt.setString(param++, game.getLeague().getAbbreviation());
		stmt.setString(param++, game.getType());
		stmt.setInt(param++, game.getWeek());

		stmt.execute();

	}

	public void addHistoricalPlayer(final HistoricalPlayer player) throws SQLException
	{
		final var stmt = this.con.prepareStatement(
				"""
						INSERT INTO jfg.historical_players
								(
									season, teamAbbreviation, playerID, firstName, lastName,
									number, age, position, games, starts,
									passingCompletions, passingAttempts, passingYards, passingTDs, passingInterceptions,
									passingLongest, passingTimesSacked, passingSackedYards, rushingAttempts, rushingYards,
									rushingTDs, rushingLongest, receivingReceptions, receivingYards, receivingTDs,
									receivingLongest, offensiveFumbles, puntsReturned, puntsReturnedYards, puntsReturnedTDs,
									puntsReturnedLongest, kicksReturned, kicksReturnedYards, kicksReturnedTDs, kicksReturnedLongest,
									fgAttempted19, fgMade19, fgAttempted29, fgMade29, fgAttempted39,
									fgMade39, fgAttempted49, fgMade49, fgAttempted50, fgMade50,
									fgLongest, xpAttempted, xpMade, kickoffs, kickoffsYards,
									kickoffsTouchbacks, punts, puntsYards, puntsLongest, puntsBlocked,
									interceptions, interceptionsYards, interceptionsTDs, interceptionsLongest, passesDefended,
									fumblesForced, allFumbles, fumblesRecovered, fumblesYards, fumblesRecoveredTDs,
									sacks, tackles_combined, tackles_solo, tackles_assists, tackles_loss,
									qb_hits, safety_md, ratingPassRush, ratingPassDefense,
									ratingTackles, ratingBlocking, ratingPassBlocking, csvPassRush, csvCoverage,
									csvRunDefense, csvTackling, csvPassBlocking, csvRunBlocking, csvAccuracy,
									csvControl, csvDiscipline, csvDistance, csvFgAccuracy, csvFgPower,
									csvHands, csvKickReturns, csvMobility, csvPower, csvPuntAccuracy,
									csvPuntPower, csvPuntReturns, csvQuickness, csvReceiving, csvRedZone,
									csvSpeed, csvTurnovers, csvVision, height, weight

							)
							VALUES
								( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
								, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
								, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
								, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
								, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
								, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
								, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
								, ?, ?, ?, ?, ?, ?, ?, ?, ?
								, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
								, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
								, ?, ?, ?, ?, ?)
								""");
		var param = 1;

		stmt.setInt(param++, player.getTeam().getDivision().getConference().getSeason());
		stmt.setString(param++, player.getTeam().getAbbreviation());
		stmt.setString(param++, player.getPlayerID());
		stmt.setString(param++, player.getFirstName());
		stmt.setString(param++, player.getLastName());
		stmt.setInt(param++, player.getNumber());
		stmt.setInt(param++, player.getAge());
		stmt.setString(param++, player.getPosition());
		stmt.setInt(param++, player.getGames());
		stmt.setInt(param++, player.getStarts());

		stmt.setInt(param++, player.getPassingCompletions());
		stmt.setInt(param++, player.getPassingAttempts());
		stmt.setInt(param++, player.getPassingYards());
		stmt.setInt(param++, player.getPassingTDs());
		stmt.setInt(param++, player.getPassingInterceptions());
		stmt.setInt(param++, player.getPassingLongest());
		stmt.setInt(param++, player.getPassingTimesSacked());
		stmt.setInt(param++, player.getPassingSackedYards());
		stmt.setInt(param++, player.getRushingAttempts());
		stmt.setInt(param++, player.getRushingYards());

		stmt.setInt(param++, player.getRushingTDs());
		stmt.setInt(param++, player.getRushingLongest());
		stmt.setInt(param++, player.getReceivingReceptions());
		stmt.setInt(param++, player.getReceivingYards());
		stmt.setInt(param++, player.getReceivingTDs());
		stmt.setInt(param++, player.getReceivingLongest());
		stmt.setInt(param++, player.getOffensiveFumbles());
		stmt.setInt(param++, player.getPuntsReturned());
		stmt.setInt(param++, player.getPuntsReturnedYards());
		stmt.setInt(param++, player.getPuntsReturnedTDs());

		stmt.setInt(param++, player.getPuntsReturnedLongest());
		stmt.setInt(param++, player.getKicksReturned());
		stmt.setInt(param++, player.getKicksReturnedYards());
		stmt.setInt(param++, player.getKicksReturnedTDs());
		stmt.setInt(param++, player.getKicksReturnedLongest());
		stmt.setInt(param++, player.getFgAttempted19());
		stmt.setInt(param++, player.getFgMade19());
		stmt.setInt(param++, player.getFgAttempted29());
		stmt.setInt(param++, player.getFgMade29());
		stmt.setInt(param++, player.getFgAttempted39());

		stmt.setInt(param++, player.getFgMade39());
		stmt.setInt(param++, player.getFgAttempted49());
		stmt.setInt(param++, player.getFgMade49());
		stmt.setInt(param++, player.getFgAttempted50());
		stmt.setInt(param++, player.getFgMade50());
		stmt.setInt(param++, player.getFgLongest());
		stmt.setInt(param++, player.getXpAttempted());
		stmt.setInt(param++, player.getXpMade());
		stmt.setInt(param++, player.getKickoffs());
		stmt.setInt(param++, player.getKickoffsYards());

		stmt.setInt(param++, player.getKickoffsTouchbacks());
		stmt.setInt(param++, player.getPunts());
		stmt.setInt(param++, player.getPuntsYards());
		stmt.setInt(param++, player.getPuntsLongest());
		stmt.setInt(param++, player.getPuntsBlocked());
		stmt.setInt(param++, player.getInterceptions());
		stmt.setInt(param++, player.getInterceptionsYards());
		stmt.setInt(param++, player.getInterceptionsTDs());
		stmt.setInt(param++, player.getInterceptionsLongest());
		stmt.setInt(param++, player.getPassesDefended());

		stmt.setInt(param++, player.getFumblesForced());
		stmt.setInt(param++, player.getAllFumbles());
		stmt.setInt(param++, player.getFumblesRecovered());
		stmt.setInt(param++, player.getFumblesYards());
		stmt.setInt(param++, player.getFumblesRecoveredTDs());
		stmt.setDouble(param++, player.getSacks());
		stmt.setInt(param++, player.getTackles_combined());
		stmt.setInt(param++, player.getTackles_solo());
		stmt.setInt(param++, player.getTackles_assists());
		stmt.setInt(param++, player.getTackles_loss());

		stmt.setInt(param++, player.getQb_hits());
		stmt.setInt(param++, player.getSafety_md());
		stmt.setInt(param++, player.getRatingPassRush());
		stmt.setInt(param++, player.getRatingPassDefense());
		stmt.setInt(param++, player.getRatingTackles());
		stmt.setInt(param++, player.getRatingBlocking());
		stmt.setInt(param++, player.getRatingPassBlocking());
		stmt.setInt(param++, player.getCsvPassRush());
		stmt.setInt(param++, player.getCsvCoverage());

		stmt.setInt(param++, player.getCsvRunDefense());
		stmt.setInt(param++, player.getCsvTackling());
		stmt.setInt(param++, player.getCsvPassBlocking());
		stmt.setInt(param++, player.getCsvRunBlocking());
		stmt.setInt(param++, player.getCsvAccuracy());
		stmt.setInt(param++, player.getCsvControl());
		stmt.setInt(param++, player.getCsvDiscipline());
		stmt.setInt(param++, player.getCsvDistance());
		stmt.setInt(param++, player.getCsvFgAccuracy());
		stmt.setInt(param++, player.getCsvFgPower());

		stmt.setInt(param++, player.getCsvHands());
		stmt.setInt(param++, player.getCsvKickReturns());
		stmt.setInt(param++, player.getCsvMobility());
		stmt.setInt(param++, player.getCsvPower());
		stmt.setInt(param++, player.getCsvPuntAccuracy());
		stmt.setInt(param++, player.getCsvPuntPower());
		stmt.setInt(param++, player.getCsvPuntReturns());
		stmt.setInt(param++, player.getCsvQuickness());
		stmt.setInt(param++, player.getCsvReceiving());
		stmt.setInt(param++, player.getCsvRedZone());

		stmt.setInt(param++, player.getCsvSpeed());
		stmt.setInt(param++, player.getCsvTurnovers());
		stmt.setInt(param++, player.getCsvVision());
		stmt.setInt(param++, player.getHeight());
		stmt.setInt(param++, player.getWeight());

		stmt.execute();
		stmt.close();
	}

	public void beginTransaction() throws SQLException
	{
		this.autoCommitCount += 1;
	}

	public void commit() throws SQLException
	{
		assert this.autoCommitCount > 0;

		this.autoCommitCount -= 1;
		if (this.autoCommitCount == 0)
		{
			this.con.commit();
			this.con.setAutoCommit(true);
		}
	}

	public void connect() throws Exception
	{
		final var props = new Properties();
		props.load(new FileInputStream("jfg.props"));
		this.con = DriverManager.getConnection(props.getProperty("jfg.db.connection"), props.getProperty("jfg.db.user"),
				props.getProperty("jfg.db.password"));
		this.con.setAutoCommit(true);
	}

	public HistoricalConference createHistoricalConference(final HistoricalConference conference) throws SQLException
	{
		final var stmt = this.con.prepareStatement("""
				INSERT INTO jfg.historical_conferences
						(season, leagueAbbreviation, abbreviation, name)
					VALUES
						(?, ?, ?, ?)""");

		stmt.setInt(1, conference.getSeason());
		stmt.setString(2, conference.getLeague().getAbbreviation());
		stmt.setString(3, conference.getAbbreviation());
		stmt.setString(4, conference.getName());

		stmt.execute();
		stmt.close();

		return conference;
	}

	public HistoricalDivision createHistoricalDivision(final HistoricalDivision division) throws SQLException
	{
		final var stmt = this.con.prepareStatement("""
				INSERT INTO jfg.historical_divisions
						(season, leagueAbbreviation, conferenceAbbreviation, name)
					VALUES
						(?, ?, ?, ?)""");

		stmt.setInt(1, division.getConference().getSeason());
		stmt.setString(2, division.getConference().getLeague().getAbbreviation());
		stmt.setString(3, division.getConference().getAbbreviation());
		stmt.setString(4, division.getName());

		stmt.execute();
		stmt.close();

		return division;
	}

	public HistoricalLeague createHistoricalLeague(final HistoricalLeague league) throws SQLException
	{
		final var stmt = this.con.prepareStatement("""
				INSERT INTO jfg.historical_leagues
					(abbreviation, name)
				VALUES
					(?, ?)
				ON DUPLICATE KEY UPDATE name = name
				""");

		var param = 1;
		stmt.setString(param++, league.getAbbreviation());
		stmt.setString(param++, league.getName());

		stmt.execute();
		stmt.close();

		return league;
	}

	public HistoricalTeam createHistoricalTeam(final HistoricalTeam historicalTeam) throws SQLException
	{
		final var stmt = this.con.prepareStatement("""
				INSERT INTO jfg.historical_teams
					(season, abbreviation, leagueAbbreviation, conferenceAbbreviation, division)
				VALUES
					(?, ?, ?, ?, ?)
				ON DUPLICATE KEY UPDATE season = season
				""");

		var param = 1;
		stmt.setInt(param++, historicalTeam.getDivision().getConference().getSeason());
		stmt.setString(param++, historicalTeam.getAbbreviation());
		stmt.setString(param++, historicalTeam.getDivision().getConference().getLeague().getAbbreviation());
		stmt.setString(param++, historicalTeam.getDivision().getConference().getAbbreviation());
		stmt.setString(param++, historicalTeam.getDivision().getName());

		stmt.execute();
		stmt.close();

		return historicalTeam;
	}

	public void deleteAllPlayers() throws SQLException
	{
		this.con.createStatement().execute("DELETE FROM jfg.cards");
	}

	public void disconnect() throws SQLException
	{
		this.con.close();
	}

	public List<HistoricalConference> loadHistoricalConferences(final HistoricalLeague league) throws SQLException
	{
		final List<HistoricalConference> ret = new ArrayList<>();

		final var stmt = this.con
				.prepareStatement("SELECT * FROM jfg.historical_conferences WHERE leagueAbbreviation = ?");

		stmt.setString(1, league.getAbbreviation());

		final var results = stmt.executeQuery();
		while (results.next())
			ret.add(new HistoricalConference(league, results.getInt("season"), results.getString("abbreviation"),
					results.getString("name")));

		stmt.close();
		return ret;
	}

	public List<HistoricalDivision> loadHistoricalDivisions(final HistoricalConference conference) throws SQLException
	{
		final List<HistoricalDivision> ret = new ArrayList<>();

		final var stmt = this.con.prepareStatement(
				"SELECT * FROM jfg.historical_divisions WHERE leagueAbbreviation = ? AND conferenceAbbreviation = ? AND season = ?");

		stmt.setString(1, conference.getLeague().getAbbreviation());
		stmt.setString(2, conference.getAbbreviation());
		stmt.setInt(3, conference.getSeason());

		final var results = stmt.executeQuery();
		while (results.next())
			ret.add(new HistoricalDivision(conference, results.getString("name")));

		stmt.close();
		return ret;
	}

	public List<HistoricalLeague> loadHistoricalLeagues() throws SQLException
	{
		final List<HistoricalLeague> ret = new ArrayList<>();

		final var stmt = this.con.createStatement();

		final var results = stmt.executeQuery("SELECT * FROM jfg.historical_leagues order by abbreviation");
		while (results.next())
			ret.add(new HistoricalLeague(results.getString("abbreviation"), results.getString("name")));

		stmt.close();
		return ret;
	}

	public List<HistoricalPlayer> loadHistoricalPlayers(final HistoricalTeam team) throws SQLException
	{
		final List<HistoricalPlayer> ret = new ArrayList<>();

		final var stmt = this.con.prepareStatement(
				"""
						SELECT
							season, teamAbbreviation, playerID, firstName, lastName, number, age, position,
							games, starts, passingCompletions, passingAttempts, passingYards, passingTDs,
							passingInterceptions, passingLongest, passingTimesSacked, passingSackedYards,
							rushingAttempts, rushingYards, rushingTDs, rushingLongest,
							receivingReceptions, receivingYards, receivingTDs, receivingLongest,
							offensiveFumbles, puntsReturned, puntsReturnedYards, puntsReturnedTDs, puntsReturnedLongest,
							kicksReturned, kicksReturnedYards, kicksReturnedTDs, kicksReturnedLongest,
							fgAttempted19, fgMade19, fgAttempted29, fgMade29, fgAttempted39, fgMade39, fgAttempted49, fgMade49, fgAttempted50, fgMade50, fgLongest,
							xpAttempted, xpMade, kickoffs, kickoffsYards, kickoffsTouchbacks,
							punts, puntsYards, puntsLongest, puntsBlocked, interceptions, interceptionsYards, interceptionsTDs, interceptionsLongest,
							passesDefended, fumblesForced, allFumbles, fumblesRecovered, fumblesYards, fumblesRecoveredTDs, sacks, tackles_combined,
							tackles_solo, tackles_assists, tackles_loss, qb_hits, safety_md,
							ratingPassRush, ratingPassDefense,
							ratingTackles, ratingBlocking, ratingPassBlocking,
							csvPassRush, csvCoverage, csvRunDefense, csvTackling, csvPassBlocking, csvRunBlocking, csvAccuracy, csvControl,
							csvDiscipline, csvDistance, csvFgAccuracy, csvFgPower, csvHands,
							csvKickReturns, csvMobility, csvPower, csvPuntAccuracy, csvPuntPower, csvPuntReturns, csvQuickness,
							csvReceiving, csvRedZone, csvSpeed, csvTurnovers, csvVision, height, weight
						FROM
							jfg.historical_players WHERE
							"""
						+ (team != null ? "season = ? AND teamAbbreviation = ? " : "height is null or weight is null"));

		var param = 1;
		if (team != null)
		{
			stmt.setInt(param++, team.getDivision().getConference().getSeason());
			stmt.setString(param++, team.getAbbreviation());
		}

		final var results = stmt.executeQuery();
		while (results.next())
		{
			param = 3;
			final var player = new HistoricalPlayer(team, results.getString(param++));

			player.setFirstName(results.getString(param++));
			player.setLastName(results.getString(param++));
			player.setNumber(results.getInt(param++));
			player.setAge(results.getInt(param++));
			player.setPosition(results.getString(param++));

			player.setGames(results.getInt(param++));
			player.setStarts(results.getInt(param++));

			player.setPassingCompletions(results.getInt(param++));
			player.setPassingAttempts(results.getInt(param++));
			player.setPassingYards(results.getInt(param++));
			player.setPassingTDs(results.getInt(param++));
			player.setPassingInterceptions(results.getInt(param++));
			player.setPassingLongest(results.getInt(param++));
			player.setPassingTimesSacked(results.getInt(param++));
			player.setPassingSackedYards(results.getInt(param++));

			player.setRushingAttempts(results.getInt(param++));
			player.setRushingYards(results.getInt(param++));
			player.setRushingTDs(results.getInt(param++));
			player.setRushingLongest(results.getInt(param++));

			player.setReceivingReceptions(results.getInt(param++));
			player.setReceivingYards(results.getInt(param++));
			player.setReceivingTDs(results.getInt(param++));
			player.setReceivingLongest(results.getInt(param++));

			player.setOffensiveFumbles(results.getInt(param++));

			player.setPuntsReturned(results.getInt(param++));
			player.setPuntsReturnedYards(results.getInt(param++));
			player.setPuntsReturnedTDs(results.getInt(param++));
			player.setPuntsReturnedLongest(results.getInt(param++));

			player.setKicksReturned(results.getInt(param++));
			player.setKicksReturnedYards(results.getInt(param++));
			player.setKicksReturnedTDs(results.getInt(param++));
			player.setKicksReturnedLongest(results.getInt(param++));

			player.setFgAttempted19(results.getInt(param++));
			player.setFgMade19(results.getInt(param++));
			player.setFgAttempted29(results.getInt(param++));
			player.setFgMade29(results.getInt(param++));
			player.setFgAttempted39(results.getInt(param++));
			player.setFgMade39(results.getInt(param++));
			player.setFgAttempted49(results.getInt(param++));
			player.setFgMade49(results.getInt(param++));
			player.setFgAttempted50(results.getInt(param++));
			player.setFgMade50(results.getInt(param++));
			player.setFgLongest(results.getInt(param++));

			player.setXpAttempted(results.getInt(param++));
			player.setXpMade(results.getInt(param++));

			player.setKickoffs(results.getInt(param++));
			player.setKickoffsYards(results.getInt(param++));
			player.setKickoffsTouchbacks(results.getInt(param++));

			player.setPunts(results.getInt(param++));
			player.setPuntsYards(results.getInt(param++));
			player.setPuntsLongest(results.getInt(param++));
			player.setPuntsBlocked(results.getInt(param++));

			player.setInterceptions(results.getInt(param++));
			player.setInterceptionsYards(results.getInt(param++));
			player.setInterceptionsTDs(results.getInt(param++));
			player.setInterceptionsLongest(results.getInt(param++));

			player.setPassesDefended(results.getInt(param++));
			player.setFumblesForced(results.getInt(param++));
			player.setAllFumbles(results.getInt(param++));
			player.setFumblesRecovered(results.getInt(param++));
			player.setFumblesYards(results.getInt(param++));
			player.setFumblesRecoveredTDs(results.getInt(param++));
			player.setSacks(results.getFloat(param++));
			player.setTackles_combined(results.getInt(param++));
			player.setTackles_solo(results.getInt(param++));
			player.setTackles_assists(results.getInt(param++));
			player.setTackles_loss(results.getInt(param++));
			player.setQb_hits(results.getInt(param++));
			player.setSafety_md(results.getInt(param++));

			player.setRatingPassRush(results.getInt(param++));
			player.setRatingPassDefense(results.getInt(param++));
			player.setRatingTackles(results.getInt(param++));
			player.setRatingBlocking(results.getInt(param++));
			player.setRatingPassBlocking(results.getInt(param++));

			player.setCsvPassRush(results.getInt(param++));
			player.setCsvCoverage(results.getInt(param++));
			player.setCsvRunDefense(results.getInt(param++));
			player.setCsvTackling(results.getInt(param++));
			player.setCsvPassBlocking(results.getInt(param++));
			player.setCsvRunBlocking(results.getInt(param++));

			player.setCsvAccuracy(results.getInt(param++));
			player.setCsvControl(results.getInt(param++));
			player.setCsvDiscipline(results.getInt(param++));
			player.setCsvDistance(results.getInt(param++));
			player.setCsvFgAccuracy(results.getInt(param++));
			player.setCsvFgPower(results.getInt(param++));
			player.setCsvHands(results.getInt(param++));
			player.setCsvKickReturns(results.getInt(param++));
			player.setCsvMobility(results.getInt(param++));
			player.setCsvPower(results.getInt(param++));
			player.setCsvPuntAccuracy(results.getInt(param++));
			player.setCsvPuntPower(results.getInt(param++));
			player.setCsvPuntReturns(results.getInt(param++));
			player.setCsvQuickness(results.getInt(param++));
			player.setCsvReceiving(results.getInt(param++));
			player.setCsvRedZone(results.getInt(param++));
			player.setCsvSpeed(results.getInt(param++));
			player.setCsvTurnovers(results.getInt(param++));
			player.setCsvVision(results.getInt(param++));

			player.setHeight(results.getInt(param++));
			player.setWeight(results.getInt(param++));

			ret.add(player);
		}

		stmt.close();
		return ret;
	}

	public List<HistoricalPlayer> loadHistoricalPlayersForUpdateOfHeightAndWeight() throws SQLException
	{
		final List<HistoricalPlayer> ret = new ArrayList<>();

		final var stmt = this.con.prepareStatement("""
				SELECT DISTINCT
					playerID, firstName, lastName
				FROM
					jfg.historical_players WHERE height is null or weight is null
					""");

		final var results = stmt.executeQuery();
		while (results.next())
		{
			var param = 1;
			final var player = new HistoricalPlayer(null, results.getString(param++));

			player.setFirstName(results.getString(param++));
			player.setLastName(results.getString(param++));

			ret.add(player);
		}

		stmt.close();
		return ret;
	}

	public List<HistoricalTeam> loadHistoricalTeams(final HistoricalDivision division) throws SQLException
	{
		final List<HistoricalTeam> ret = new ArrayList<>();

		final var stmt = this.con.prepareStatement(
				"SELECT * FROM jfg.historical_teams WHERE leagueAbbreviation = ? AND conferenceAbbreviation = ? AND division = ? AND season = ?");

		stmt.setString(1, division.getConference().getLeague().getAbbreviation());
		stmt.setString(2, division.getConference().getAbbreviation());
		stmt.setString(3, division.getName());
		stmt.setInt(4, division.getConference().getSeason());

		final var results = stmt.executeQuery();
		while (results.next())
		{
			final var team = new HistoricalTeam(division, results.getString("abbreviation"));
			team.setLocation(results.getString("location"));
			team.setNickname(results.getString("nickname"));
			team.setCoach(results.getString("coach"));
			team.setOwner(results.getString("owner"));
			team.setStadium(results.getString("stadium"));
			team.setCompletions(results.getInt("completions"));
			team.setCompletionsOpp(results.getInt("completionsOpp"));
			team.setFirstDowns(results.getInt("firstDowns"));
			team.setFirstDownsOpp(results.getInt("firstDownsOpp"));
			team.setFirstDownsPassing(results.getInt("firstDownsPassing"));
			team.setFirstDownsPassingOpp(results.getInt("firstDownsPassingOpp"));
			team.setFumblesLost(results.getInt("fumblesLost"));
			team.setFumblesLostOpp(results.getInt("fumblesLostOpp"));
			team.setInterceptions(results.getInt("interceptions"));
			team.setInterceptionsOpp(results.getInt("interceptionsOpp"));
			team.setLosses(results.getInt("losses"));
			team.setPassingAttempts(results.getInt("passingAttempts"));
			team.setPassingAttemptsOpp(results.getInt("passingAttemptsOpp"));
			team.setPassingTDs(results.getInt("passingTDs"));
			team.setPassingTDsOpp(results.getInt("passingTDsOpp"));
			team.setPassingYards(results.getInt("passingYards"));
			team.setPassingYardsOpp(results.getInt("passingYardsOpp"));
			team.setPenalties(results.getInt("penalties"));
			team.setPenaltiesOpp(results.getInt("penaltiesOpp"));
			team.setPenaltyFirstDowns(results.getInt("penaltyFirstDowns"));
			team.setPenaltyFirstDownsOpp(results.getInt("penaltyFirstDownsOpp"));
			team.setPenaltyYards(results.getInt("penaltyYards"));
			team.setPenaltyYardsOpp(results.getInt("penaltyYardsOpp"));
			team.setPlays(results.getInt("plays"));
			team.setPlaysOpp(results.getInt("playsOpp"));
			team.setPointsAgainst(results.getInt("pointsFor"));
			team.setPointsFor(results.getInt("pointsAgainst"));
			team.setRushingAttempts(results.getInt("rushingAttempts"));
			team.setRushingAttemptsOpp(results.getInt("rushingAttemptsOpp"));
			team.setRushingTDs(results.getInt("rushingTDs"));
			team.setRushingTDsOpp(results.getInt("rushingTDsOpp"));
			team.setRushingYards(results.getInt("rushingYards"));
			team.setRushingYardsOpp(results.getInt("rushingYardsOpp"));
			team.setTies(results.getInt("ties"));
			team.setTurnovers(results.getInt("turnovers"));
			team.setTurnoversOpp(results.getInt("turnoversOpp"));
			team.setWins(results.getInt("wins"));
			team.setYards(results.getInt("yards"));
			team.setYardsOpp(results.getInt("yardsOpp"));

			team.setHomeJerseyColor(results.getString("color_home_shirt"));
			team.setHomePantsColor(results.getString("color_home_pants"));
			team.setHomeNumberColor(results.getString("color_home_number"));
			team.setHomeTrimColor(results.getString("color_home_trim"));
			team.setHomeSocksColor(results.getString("color_home_socks"));
			team.setAwayJerseyColor(results.getString("color_away_shirt"));
			team.setAwayPantsColor(results.getString("color_away_pants"));
			team.setAwayNumberColor(results.getString("color_away_number"));
			team.setAwayTrimColor(results.getString("color_away_trim"));
			team.setAwaySocksColor(results.getString("color_away_socks"));

			ret.add(team);
		}

		stmt.close();
		return ret;
	}

	public List<HistoricalLeague> loadUpEverything() throws SQLException
	{
		final var leagues = Database.getDefaultDatabase().loadHistoricalLeagues();
		for (final HistoricalLeague league : leagues)
		{
			final var conferences = Database.getDefaultDatabase().loadHistoricalConferences(league);
			for (final HistoricalConference conference : conferences)
			{
				league.addHistoricalConference(conference);
				final var divisions = Database.getDefaultDatabase().loadHistoricalDivisions(conference);
				for (final HistoricalDivision division : divisions)
				{
					conference.addDivision(division);
					final var teams = Database.getDefaultDatabase().loadHistoricalTeams(division);
					for (final HistoricalTeam team : teams)
					{
						division.addTeam(team);
						final var players = Database.getDefaultDatabase().loadHistoricalPlayers(team);
						for (final HistoricalPlayer player : players)
							team.addPlayer(player);
					}
				}
			}
		}

		return leagues;
	}

	public void rollback()
	{
		try
		{
			assert this.autoCommitCount > 0;

			this.autoCommitCount -= 1;
			if (this.autoCommitCount == 0)
			{
				this.con.rollback();
				this.con.setAutoCommit(true);
			}
		}
		catch (final SQLException e)
		{
			Database.report(e);
		}
	}

	public void updateHeightAndWeight(final PlayerInfo player) throws SQLException
	{
		final var stmt = this.con.prepareStatement("""
				UPDATE jfg.historical_players
				SET height = ?, weight = ?
				WHERE
					playerID = ?

				""");

		var param = 1;
		stmt.setInt(param++, player.getHeight());
		stmt.setInt(param++, player.getWeight());
		stmt.setString(param++, player.getPlayerID());

		stmt.execute();
		stmt.close();
	}

	public void updateHistoricalTeam(final HistoricalTeam historicalTeam) throws SQLException
	{
		final var stmt = this.con.prepareStatement(
				"""
						UPDATE jfg.historical_teams
							set location = ?, nickname = ?, wins = ?, losses = ?, ties = ?, pointsFor = ?, pointsAgainst = ?, stadium = ?, coach = ?, owner = ?,
						completions = ?, completionsOpp = ?, firstDowns = ?, firstDownsOpp = ?, firstDownsPassing = ?, firstDownsPassingOpp = ?,
						firstDownsRushing = ?, firstDownsRushingOpp = ?,
						fumblesLost = ?, fumblesLostOpp = ?, interceptions = ?, interceptionsOpp = ?, passingAttempts = ?, passingAttemptsOpp = ?,
						passingTDs = ?, passingTDsOpp = ?, passingYards = ?, passingYardsOpp = ?, penalties = ?, penaltiesOpp = ?,
						penaltyFirstDowns = ?, penaltyFirstDownsOpp = ?, penaltyYards = ?, penaltyYardsOpp = ?, plays = ?, playsOpp = ?,
						rushingAttempts = ?, rushingAttemptsOpp = ?,
						rushingTDs = ?, rushingTDsOpp = ?, rushingYards = ?, rushingYardsOpp = ?, turnovers = ?, turnoversOpp = ?, yards = ?, yardsOpp = ?
						where season = ? and abbreviation = ?""");

		stmt.setString(1, historicalTeam.getLocation());
		stmt.setString(2, historicalTeam.getNickname());
		stmt.setInt(3, historicalTeam.getWins());
		stmt.setInt(4, historicalTeam.getLosses());
		stmt.setInt(5, historicalTeam.getTies());
		stmt.setInt(6, historicalTeam.getPointsFor());
		stmt.setInt(7, historicalTeam.getPointsAgainst());
		stmt.setString(8, historicalTeam.getStadium());
		stmt.setString(9, historicalTeam.getCoach());
		stmt.setString(10, historicalTeam.getOwner());

		var param = 11;
		stmt.setInt(param++, historicalTeam.getCompletions());
		stmt.setInt(param++, historicalTeam.getCompletionsOpp());
		stmt.setInt(param++, historicalTeam.getFirstDowns());
		stmt.setInt(param++, historicalTeam.getFirstDownsOpp());
		stmt.setInt(param++, historicalTeam.getFirstDownsPassing());
		stmt.setInt(param++, historicalTeam.getFirstDownsPassingOpp());

		stmt.setInt(param++, historicalTeam.getFirstDownsRushing());
		stmt.setInt(param++, historicalTeam.getFirstDownsRushingOpp());

		stmt.setInt(param++, historicalTeam.getFumblesLost());
		stmt.setInt(param++, historicalTeam.getFumblesLostOpp());
		stmt.setInt(param++, historicalTeam.getInterceptions());
		stmt.setInt(param++, historicalTeam.getInterceptionsOpp());
		stmt.setInt(param++, historicalTeam.getPassingAttempts());
		stmt.setInt(param++, historicalTeam.getPassingAttemptsOpp());

		stmt.setInt(param++, historicalTeam.getPassingTDs());
		stmt.setInt(param++, historicalTeam.getPassingTDsOpp());
		stmt.setInt(param++, historicalTeam.getPassingYards());
		stmt.setInt(param++, historicalTeam.getPassingYardsOpp());
		stmt.setInt(param++, historicalTeam.getPenalties());
		stmt.setInt(param++, historicalTeam.getPenaltiesOpp());

		stmt.setInt(param++, historicalTeam.getPenaltyFirstDowns());
		stmt.setInt(param++, historicalTeam.getPenaltyFirstDownsOpp());
		stmt.setInt(param++, historicalTeam.getPenaltyYards());
		stmt.setInt(param++, historicalTeam.getPenaltyYardsOpp());
		stmt.setInt(param++, historicalTeam.getPlays());
		stmt.setInt(param++, historicalTeam.getPlaysOpp());

		stmt.setInt(param++, historicalTeam.getRushingAttempts());
		stmt.setInt(param++, historicalTeam.getRushingAttemptsOpp());
		stmt.setInt(param++, historicalTeam.getRushingTDs());
		stmt.setInt(param++, historicalTeam.getRushingTDsOpp());
		stmt.setInt(param++, historicalTeam.getRushingYards());
		stmt.setInt(param++, historicalTeam.getRushingYardsOpp());

		stmt.setInt(param++, historicalTeam.getTurnovers());
		stmt.setInt(param++, historicalTeam.getTurnoversOpp());
		stmt.setInt(param++, historicalTeam.getYards());
		stmt.setInt(param++, historicalTeam.getYardsOpp());

		stmt.setInt(param++, historicalTeam.getDivision().getConference().getSeason());
		stmt.setString(param++, historicalTeam.getAbbreviation());

		stmt.execute();
		stmt.close();
	}

	public void updateHistoricalTeamColors(final HistoricalTeam historicalTeam) throws SQLException
	{
		final var stmt = this.con.prepareStatement(
				"""
						UPDATE jfg.historical_teams
							set color_home_pants = ?, color_home_shirt = ?, color_home_number = ?, color_home_trim = ? ,
							color_home_socks = ?,
							color_away_pants = ?, color_away_shirt = ?, color_away_number = ?, color_away_trim = ?, color_away_socks = ? 
							where season = ? and abbreviation = ?""");

		int param = 1;
		stmt.setString(param++, historicalTeam.getHomePantsColor());
		stmt.setString(param++, historicalTeam.getHomeJerseyColor());
		stmt.setString(param++, historicalTeam.getHomeNumberColor());
		stmt.setString(param++, historicalTeam.getHomeTrimColor());
		stmt.setString(param++, historicalTeam.getHomeSocksColor());
		stmt.setString(param++, historicalTeam.getAwayPantsColor());
		stmt.setString(param++, historicalTeam.getAwayJerseyColor());
		stmt.setString(param++, historicalTeam.getAwayNumberColor());
		stmt.setString(param++, historicalTeam.getAwayTrimColor());
		stmt.setString(param++, historicalTeam.getAwaySocksColor());

		stmt.setInt(param++, historicalTeam.getDivision().getConference().getSeason());
		stmt.setString(param++, historicalTeam.getAbbreviation());

		stmt.execute();
		stmt.close();
	}

	public void updateStatisproRatings(final HistoricalPlayer player) throws SQLException
	{
		final var stmt = this.con.prepareStatement(
				"""
						UPDATE jfg.historical_players
						SET ratingBlocking = ?, ratingPassBlocking = ?, ratingPassDefense = ?, ratingPassRush = ?, ratingTackles = ?
						WHERE
							playerID = ?

						""");

		var param = 1;
		stmt.setInt(param++, player.getRatingBlocking());
		stmt.setInt(param++, player.getRatingPassBlocking());
		stmt.setInt(param++, player.getRatingPassDefense());
		stmt.setInt(param++, player.getRatingPassRush());
		stmt.setInt(param++, player.getRatingTackles());
		stmt.setString(param++, player.getPlayerID());

		stmt.execute();
		stmt.close();
	}
}
