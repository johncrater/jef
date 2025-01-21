package jef.history;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.csv.CSVRecord;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import jef.core.PlayerPosition;
import jef.core.PlayerRatings;

public class CardGenerator
{
	public static final int START_YEAR = 1951;
	public static final int END_YEAR = 2023;

	public static void main(final String[] args) throws SQLException, IOException, InterruptedException,
			ParserConfigurationException, SAXException, JDOMException, ParseException
	{
		new CardGenerator().loadLeagues();
	}

	public void calculateOffensiveBlockingOE(final HistoricalTeam team)
	{
		final var yardsPerAttempt = team.getRushingYards() / (float) team.getGamesPlayed();
		final var players = team.getPlayers(PlayerPosition.OE).stream()
				.sorted(Comparator.comparing(HistoricalPlayer::getCsvRunBlocking).reversed()).toList();

		final int[][] blockingChart =
		{
				{ 150, 4, 4, 3, 3, 3, 2, 1, 1 },
				{ 140, 4, 4, 3, 3, 3, 2, 1, 1 },
				{ 130, 4, 3, 3, 3, 3, 1, 1, 1 },
				{ 120, 4, 3, 3, 2, 2, 1, 1, 0 },
				{ 110, 4, 3, 3, 2, 2, 1, 1, 0 },
				{ 105, 3, 3, 3, 2, 2, 1, 1, 0 },
				{ 100, 3, 3, 2, 2, 2, 1, 1, 0 },
				{ 95, 3, 2, 2, 2, 1, 1, 0, -1 },
				{ 90, 3, 2, 2, 1, 1, 1, 0, -1 },
				{ 80, 2, 2, 2, 1, 1, 1, 0, -1 },
				{ 0, 2, 2, 1, 1, 0, 0, -1, -1 },

		};

		for (final int[] chart : blockingChart)
			if (yardsPerAttempt >= chart[0])
			{
				for (var i = 0; i < players.size(); i++)
					if (i >= chart.length - 1)
						players.get(i).setRatingBlocking(chart[chart.length - 1]);
					else
						players.get(i).setRatingBlocking(chart[i + 1]);

				break;
			}
	}

	public void calculateOffensiveBlockingOL(final HistoricalTeam team)
	{
		final var yardsPerAttempt = team.getRushingYards() / (float) team.getGamesPlayed();
		final var players = team.getPlayers(PlayerPosition.OL).stream()
				.sorted(Comparator.comparing(HistoricalPlayer::getCsvRunBlocking).reversed()).toList();

		final int[][] blockingChart =
		{
				{ 150, 4, 4, 3, 3, 3, 2, 1, 1 },
				{ 140, 4, 4, 3, 3, 3, 2, 1, 1 },
				{ 130, 4, 3, 3, 3, 3, 1, 1, 1 },
				{ 120, 4, 3, 3, 2, 2, 1, 1, 0 },
				{ 110, 4, 3, 3, 2, 2, 1, 1, 0 },
				{ 105, 3, 3, 3, 2, 2, 1, 1, 0 },
				{ 100, 3, 3, 2, 2, 2, 1, 1, 0 },
				{ 95, 3, 2, 2, 2, 1, 1, 0, -1 },
				{ 90, 3, 2, 2, 1, 1, 1, 0, -1 },
				{ 80, 2, 2, 2, 1, 1, 1, 0, -1 },
				{ 0, 2, 2, 1, 1, 0, 0, -1, -1 },

		};

		for (final int[] chart : blockingChart)
			if (yardsPerAttempt >= chart[0])
			{
				for (var i = 0; i < players.size(); i++)
					if (i >= chart.length - 1)
						players.get(i).setRatingBlocking(chart[chart.length - 1]);
					else
						players.get(i).setRatingBlocking(chart[i + 1]);

				break;
			}
	}

	public void calculateOffensiveBlockingRB(final HistoricalTeam team)
	{
		final var yardsPerAttempt = team.getRushingYards() / (float) team.getGamesPlayed();
		final var players = team.getPlayers(PlayerPosition.RB).stream()
				.sorted(Comparator.comparing(HistoricalPlayer::getCsvRunBlocking).reversed()).toList();

		final int[][] blockingChart =
		{
				{ 150, 3, 3, 2, 2, 2, 1, 0, 0 },
				{ 140, 3, 3, 2, 2, 2, 1, 0, 0 },
				{ 130, 3, 2, 2, 2, 2, 0, 0, 0 },
				{ 120, 3, 2, 2, 1, 1, 0, 0, -1 },
				{ 110, 3, 2, 2, 1, 1, 0, 0, -1 },
				{ 105, 2, 2, 2, 1, 1, 0, 0, -1 },
				{ 100, 2, 2, 1, 1, 1, 0, 0, -1 },
				{ 95, 2, 1, 1, 1, 0, 0, -1, -2 },
				{ 90, 2, 1, 1, 0, 0, 0, -1, -2 },
				{ 80, 1, 1, 1, 0, 0, 0, -1, -2 },
				{ 0, 1, 1, 0, 0, -1, -1, -2, -2 },

		};

		for (final int[] chart : blockingChart)
			if (yardsPerAttempt >= chart[0])
			{
				for (var i = 0; i < players.size(); i++)
					if (i >= chart.length - 1)
						players.get(i).setRatingBlocking(chart[chart.length - 1]);
					else
						players.get(i).setRatingBlocking(chart[i + 1]);

				break;
			}
	}

	public void calculatePassBlockingOL(final HistoricalTeam team)
	{
		final var sacks = team.getPlayers().stream().mapToDouble(HistoricalPlayer::getSacks).sum() * 16.0
				/ team.getGamesPlayed();

		final var players = team.getPlayers(PlayerPosition.OL).stream()
				.sorted(Comparator.comparing(HistoricalPlayer::getCsvPassBlocking).reversed()).toList();

		final int[][] blockingChart =
		{
				{ 10, 4, 4, 3, 3, 3, 2, 1, 1 },
				{ 16, 4, 4, 3, 3, 3, 2, 1, 1 },
				{ 23, 4, 3, 3, 3, 3, 1, 1, 1 },
				{ 30, 4, 3, 3, 2, 2, 1, 1, 0 },
				{ 36, 4, 3, 3, 2, 2, 1, 1, 0 },
				{ 43, 3, 3, 3, 2, 2, 1, 1, 0 },
				{ 50, 3, 3, 2, 2, 2, 1, 1, 0 },
				{ 56, 3, 2, 2, 2, 1, 1, 0, -1 },
				{ 63, 3, 2, 2, 1, 1, 1, 0, -1 },
				{ 70, 2, 2, 2, 1, 1, 1, 0, -1 },
				{ 76, 2, 2, 1, 1, 0, 0, -1, -1 },

		};

		for (final int[] chart : blockingChart)
			if (sacks >= chart[0])
			{
				for (var i = 0; i < players.size(); i++)
					if (i >= chart.length - 1)
						players.get(i).setRatingPassBlocking(chart[chart.length - 1]);
					else
						players.get(i).setRatingPassBlocking(chart[i + 1]);

				break;
			}
	}

	public void calculatePassDefenseDB(final HistoricalTeam team)
	{
		var yardsPerAttempt = team.getPassingYardsOpp() / (float) team.getPassingAttemptsOpp();
		final var players = team.getPlayers(PlayerPosition.DB).stream()
				.sorted(Comparator.comparing(HistoricalPlayer::getCsvCoverage).reversed()).toList();

		final int[][] passDefenseChart =
		{
				{ 51, -4, -3, -2, -1, 1, 1, 1 },
				{ 53, -4, -3, -1, -1, 1, 1, 1 },
				{ 55, -4, -2, -1, -1, 1, 1, 1 },
				{ 57, -4, -2, -1, 0, 1, 1, 2 },
				{ 59, -3, -2, -1, 0, 1, 1, 2 },
				{ 61, -3, -2, -1, 1, 2, 2, 2 },
				{ 63, -3, -2, 0, 1, 2, 2, 2 },
				{ 65, -3, -1, 0, 1, 2, 2, 2 },
				{ 67, -2, -1, 0, 1, 2, 2, 2 },
				{ 69, -2, -1, 0, 2, 3, 3, 3 },
				{ 71, -2, -1, 1, 2, 3, 3, 3 },
				{ 73, -2, -1, 2, 2, 3, 3, 3 },
				{ 75, -1, -1, 2, 2, 3, 3, 3 },
				{ 77, -1, 0, 2, 2, 3, 3, 4 },
				{ 79, -1, 0, 2, 2, 3, 4, 4 },
				{ 80, -1, 0, 2, 3, 4, 4, 4 }, };

		for (final int[] chart : passDefenseChart)
		{
			yardsPerAttempt = Math.max(5.0f, Math.min(yardsPerAttempt, 8.0f));

			if (yardsPerAttempt <= chart[0] / 10.0)
			{
				for (var i = 0; i < players.size(); i++)
					if (i >= chart.length - 1)
						players.get(i).setRatingPassDefense(chart[chart.length - 1]);
					else
						players.get(i).setRatingPassDefense(chart[i + 1]);

				break;
			}
		}
	}

	public void calculatePassDefenseLB(final HistoricalTeam team)
	{
		var yardsPerAttempt = team.getPassingYardsOpp() / (float) team.getPassingAttemptsOpp();
		final var players = team.getPlayers(PlayerPosition.LB).stream()
				.sorted(Comparator.comparing(HistoricalPlayer::getCsvCoverage).reversed()).toList();

		final int[][] passDefenseChart =
		{
				{ 51, -3, -2, -2, -1, 1, 0, 1, 2 },
				{ 53, -3, -2, -1, -1, 1, 1, 1, 2 },
				{ 55, -3, -1, -1, -1, 1, 1, 1, 2 },
				{ 57, -3, -1, -1, 0, 1, 1, 2, 3 },
				{ 59, -2, -1, -1, 0, 1, 1, 2, 3 },
				{ 61, -2, -1, -1, 1, 2, 1, 2, 3 },
				{ 63, -2, -1, 0, 1, 2, 1, 2, 3 },
				{ 65, -2, 0, 0, 1, 2, 1, 2, 3 },
				{ 67, -1, 0, 0, 1, 2, 1, 2, 3 },
				{ 69, -1, 0, 0, 2, 3, 1, 2, 3 },
				{ 71, -1, 0, 1, 2, 3, 1, 2, 3 },
				{ 73, -1, 0, 2, 2, 3, 2, 3, 4 },
				{ 75, 0, 0, 2, 2, 3, 3, 3, 4 },
				{ 77, 0, 0, 2, 2, 3, 3, 4, 4 },
				{ 79, 0, 0, 2, 2, 3, 3, 4, 4 },
				{ 80, 0, 0, 2, 3, 4, 4, 4, 4 }, };

		for (final int[] chart : passDefenseChart)
		{
			yardsPerAttempt = Math.max(5.0f, Math.min(yardsPerAttempt, 8.0f));

			if (yardsPerAttempt <= chart[0] / 10.0)
			{
				for (var i = 0; i < players.size(); i++)
					if (i >= chart.length - 1)
						players.get(i).setRatingPassDefense(chart[chart.length - 1]);
					else
						players.get(i).setRatingPassDefense(chart[i + 1]);

				break;
			}
		}
	}

	public void calculatePassRush(final HistoricalTeam team)
	{
		team.getPlayers().stream().forEach(player ->
		{
			if (player.getSacks() >= player.getGames() || player.getSacks() == 0 && player.getCsvPassRush() > 90)
				player.setRatingPassRush(4);
			else if (player.getSacks() >= player.getGames() * 6.0 / 16.0
					|| player.getSacks() == 0 && player.getCsvPassRush() > 83)
				player.setRatingPassRush(3);
			else if (player.getSacks() >= player.getGames() * 4.0 / 16.0
					|| player.getSacks() == 0 && player.getCsvPassRush() > 76)
				player.setRatingPassRush(2);
			else if (player.getSacks() >= player.getGames() * 2.0 / 16.0
					|| player.getSacks() == 0 && player.getCsvPassRush() > 70)
				player.setRatingPassRush(1);
			else
				player.setRatingPassRush(0);
		});
	}

	public void calculateTacklesDL(final HistoricalTeam team)
	{
		final var yardsPerGame = team.getRushingYardsOpp() / team.getGamesPlayed();

		final var players = team.getPlayers(PlayerPosition.DL).stream()
				.sorted(Comparator.comparing(HistoricalPlayer::getCsvTackling).reversed()).toList();

		final int[][] tackleChart =
		{
				{ 0, -4, -3, -3, -2, -2, -2 },
				{ 86, -4, -3, -3, -2, -2, -1 },
				{ 93, -4, -3, -3, -2, -1, -1 },
				{ 100, -4, -2, -2, -2, -1, -1 },
				{ 106, -3, -2, -2, -2, -1, -1 },
				{ 113, -3, -2, -2, -1, -1, -1 },
				{ 120, -3, -2, -1, -1, 0, 0 },
				{ 126, -3, -2, -1, 0, 0, 1 },
				{ 133, -2, -2, -1, 0, 1, 1 },
				{ 140, -2, -1, -1, 0, 1, 2 },
				{ 146, -2, -1, 0, 0, 1, 2 },
				{ 153, -1, -1, 0, 1, 1, 2 },
				{ 160, -1, -1, 0, 1, 2, 3 },
				{ 166, -1, 0, 0, 1, 2, 3 },
				{ 173, 0, 0, 1, 2, 2, 3 },
				{ 180, 1, 1, 2, 2, 3, 3 }, };

		for (final int[] chart : tackleChart)
			if (yardsPerGame >= chart[0])
			{
				for (var i = 0; i < players.size(); i++)
					if (i >= chart.length - 1)
						players.get(i).setRatingTackles(chart[chart.length - 1]);
					else
						players.get(i).setRatingTackles(chart[i + 1]);

				break;
			}
	}

	public void calculateTacklesLB(final HistoricalTeam team)
	{
		final var yardsPerGame = team.getRushingYardsOpp() / team.getGamesPlayed();

		final var players = team.getPlayers(PlayerPosition.LB).stream()
				.sorted(Comparator.comparing(HistoricalPlayer::getCsvTackling).reversed()).toList();

		final int[][] tackleChart =
		{
				{ 0, -5, -4, -3, -2, -1, 0, 1, 2 },
				{ 86, -5, -4, -3, -2, -1, 1, 1, 2 },
				{ 93, -5, -4, -3, -2, 0, 1, 1, 2 },
				{ 100, -5, -4, -3, -2, 0, 1, 2, 2 },
				{ 106, -4, -4, -3, -2, 0, 1, 2, 2 },
				{ 113, -4, -4, -2, -2, 0, 1, 2, 3 },
				{ 120, -4, -3, -2, -1, 0, 1, 2, 3 },
				{ 126, -4, -3, -2, 0, 1, 1, 2, 3 },
				{ 133, -3, -3, -1, 0, 1, 1, 2, 3 },
				{ 140, -3, -2, -1, 0, 1, 1, 2, 3 },
				{ 146, -3, -2, -1, 0, 2, 1, 2, 3 },
				{ 153, -3, -2, -1, 1, 2, 2, 3, 4 },
				{ 160, -2, -2, 0, 1, 2, 3, 3, 4 },
				{ 166, -2, -1, 1, 2, 2, 3, 4, 4 },
				{ 173, -2, -1, 1, 2, 3, 3, 4, 4 },
				{ 180, -1, 0, 1, 2, 3, 4, 4, 4 } };

		for (final int[] chart : tackleChart)
			if (yardsPerGame >= chart[0])
			{
				for (var i = 0; i < players.size(); i++)
					if (i >= chart.length - 1)
						players.get(i).setRatingTackles(chart[chart.length - 1]);
					else
						players.get(i).setRatingTackles(chart[i + 1]);

				break;
			}
	}

	public void updatePlayerFromCSV(final CSVRecord csvRecord, final PlayerRatings player)
	{
		player.setCsvCoverage(this.intOr0(csvRecord.get("Coverage")));
		player.setCsvPassBlocking(this.intOr0(csvRecord.get("Pass Blocking")));
		player.setCsvPassRush(this.intOr0(csvRecord.get("Pass Rush")));
		player.setCsvRunBlocking(this.intOr0(csvRecord.get("Run Blocking")));
		player.setCsvRunDefense(this.intOr0(csvRecord.get("Run Defense")));
		player.setCsvTackling(this.intOr0(csvRecord.get("Tackling")));

		player.setCsvAccuracy(this.intOr0(csvRecord.get("Accuracy")));
		player.setCsvControl(this.intOr0(csvRecord.get("Control")));
		player.setCsvDiscipline(this.intOr0(csvRecord.get("Discipline")));
		player.setCsvDistance(this.intOr0(csvRecord.get("Distance")));
		player.setCsvDurability(this.intOr0(csvRecord.get("Durability")));
		player.setCsvFgAccuracy(this.intOr0(csvRecord.get("FG Accuracy")));
		player.setCsvFgPower(this.intOr0(csvRecord.get("FG Power")));
		player.setCsvHands(this.intOr0(csvRecord.get("Hands")));
		player.setCsvKickReturns(this.intOr0(csvRecord.get("Kick Returns")));
		player.setCsvMobility(this.intOr0(csvRecord.get("Mobility")));
		player.setCsvPower(this.intOr0(csvRecord.get("Power")));
		player.setCsvPuntAccuracy(this.intOr0(csvRecord.get("Punt Accuracy")));
		player.setCsvPuntPower(this.intOr0(csvRecord.get("Punt Power")));
		player.setCsvPuntReturns(this.intOr0(csvRecord.get("Punt Returns")));
		player.setCsvQuickness(this.intOr0(csvRecord.get("Quickness")));
		player.setCsvReceiving(this.intOr0(csvRecord.get("Receiving")));
		player.setCsvRedZone(this.intOr0(csvRecord.get("Red Zone")));
		player.setCsvSpeed(this.intOr0(csvRecord.get("Speed")));
		player.setCsvTurnovers(this.intOr0(csvRecord.get("Turnovers")));
		player.setCsvVision(this.intOr0(csvRecord.get("Vision")));
	}

	private int intOr0(final String text)
	{
		if (text == null || text.isBlank())
			return 0;

		return Integer.parseInt(text);
	}

	private void loadLeagues() throws SQLException, IOException, InterruptedException, ParserConfigurationException,
			SAXException, JDOMException, ParseException
	{
		final Map<String, String> csvToHistoricalTeamAbbr = new HashMap<>();
		csvToHistoricalTeamAbbr.put("stl", "crd");
		csvToHistoricalTeamAbbr.put("gb", "gnb");
		csvToHistoricalTeamAbbr.put("la", "ram");
		csvToHistoricalTeamAbbr.put("sf", "sfo");
		csvToHistoricalTeamAbbr.put("bal", "clt");
		csvToHistoricalTeamAbbr.put("ne", "nwe");
		csvToHistoricalTeamAbbr.put("kc", "kan");
		csvToHistoricalTeamAbbr.put("hou", "oti");
		csvToHistoricalTeamAbbr.put("sd", "sdg");
		csvToHistoricalTeamAbbr.put("oak", "rai");
		csvToHistoricalTeamAbbr.put("no", "nor");
		csvToHistoricalTeamAbbr.put("tb", "tam");
		csvToHistoricalTeamAbbr.put("ind", "clt");
		csvToHistoricalTeamAbbr.put("phx", "crd");
		csvToHistoricalTeamAbbr.put("ari", "crd");
		csvToHistoricalTeamAbbr.put("jac", "jax");
		csvToHistoricalTeamAbbr.put("ten", "oti");
		csvToHistoricalTeamAbbr.put("lac", "sdg");
		csvToHistoricalTeamAbbr.put("lar", "rai");
		csvToHistoricalTeamAbbr.put("lvr", "rai");

		final Map<Integer, Map<String, HistoricalTeam>> historicalTeams = new HashMap<>();

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
						System.out.println(team);

						division.addTeam(team);
						var teamMap = historicalTeams.get(team.getDivision().getConference().getSeason());
						if (teamMap == null)
						{
							teamMap = new HashMap<>();
							historicalTeams.put(team.getDivision().getConference().getSeason(), teamMap);
						}

						teamMap.put(team.getAbbreviation(), team);

						final var players = Database.getDefaultDatabase().loadHistoricalPlayers(team);
						for (final HistoricalPlayer player : players)
						{
							if (player.getRatingBlocking() < 100)
								continue;

							team.addPlayer(player);

							player.setRatingBlocking(0);
							player.setRatingPassBlocking(-3);
							player.setRatingPassDefense(5);
							player.setRatingPassRush(0);
							player.setRatingTackles(4);
						}

						this.calculatePassRush(team);
						this.calculatePassDefenseDB(team);
						this.calculatePassDefenseLB(team);
						this.calculateTacklesLB(team);
						this.calculateTacklesDL(team);
						this.calculateOffensiveBlockingOL(team);
						this.calculateOffensiveBlockingRB(team);
						this.calculateOffensiveBlockingOE(team);
						this.calculatePassBlockingOL(team);

						for (final HistoricalPlayer player : team.getPlayers())
							Database.getDefaultDatabase().updateStatisproRatings(player);
					}
				}
			}
		}

//		for (var i = CardGenerator.START_YEAR; i <= CardGenerator.END_YEAR; i++)
//		{
//			System.out.println(i);
//
//			final var parser = CSVParser.parse(new FileReader(new File("csv", "" + i + "_player_ratings.csv")),
//					CSVFormat.Builder.create(CSVFormat.EXCEL).setHeader().build());
//			for (final CSVRecord csvRecord : parser)
//			{
//				var csvTeam = csvRecord.get("" + i + "_Team").toLowerCase();
//				if ("-".equals(csvTeam))
//					continue;
//
//				final var csvPlayerID = csvRecord.get("PlayerID");
//
//				if (csvToHistoricalTeamAbbr.get(csvTeam) != null)
//					csvTeam = csvToHistoricalTeamAbbr.get(csvTeam);
//
//				final var historicalTeam = historicalTeams.get(i).get(csvTeam.toLowerCase());
//				if (historicalTeam == null)
//					continue;
//
//				final var player = historicalTeam.getPlayer(csvPlayerID);
//				if (player == null)
//					continue;
//
//				updatePlayerFromCSV(csvRecord, player);
//
////				Database.getDefaultDatabase().updatePlayerRatings(player);
//			}
//		}
//
	}

}
