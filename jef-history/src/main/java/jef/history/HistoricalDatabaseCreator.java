package jef.history;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.DOMBuilder;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

import jef.core.PlayerRatings;
import jef.core.PlayerStats;

public class HistoricalDatabaseCreator
{
	public static final int START_YEAR = 1951;
	public static final int END_YEAR = 2023;

	public static void main(final String[] args) throws Exception
	{
		final var creator = new HistoricalDatabaseCreator();
		creator.createDatabase();
		creator.addHeightWeightData();
	}

	private final HttpClient client = HttpClient.newHttpClient();

	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

	private HistoricalDatabaseCreator()
	{
	}

	private void addHeightWeightData() throws Exception
	{
		final var players = Database.getDefaultDatabase()
				.loadHistoricalPlayersForUpdateOfHeightAndWeight();
		var count = players.size();
		for (final HistoricalPlayer player : players)
			if (player.getHeight() == 0 && player.getWeight() == 0)
			{
				System.out.println(String.format("%6d - %s - %s %s", count--, player.getPlayerID(),
						player.getLastName(), player.getFirstName()));

				try
				{
					final var refData = this.getRefData("/players/" + player.getLastName().toUpperCase().charAt(0) + "/"
							+ player.getPlayerID() + ".htm");
					final var scanner = new SimpleScanner(refData);

					final var heightString = scanner
							.extractBetween("\"height\": { \"@type\": \"QuantitativeValue\", \"value\": \"", "\"");
					final var feet = Integer.parseInt(heightString.split("-")[0]);
					final var inches = Integer.parseInt(heightString.split("-")[1]);
					final var weight = Integer.parseInt(scanner
							.extractBetween("\"weight\": { \"@type\": \"QuantitativeValue\", \"value\": \"", " lbs"));
					player.setHeight(feet * 12 + inches);
					player.setWeight(weight);
					Database.getDefaultDatabase().updateHeightAndWeight(player);
				}
				catch (final Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}

	private void addTeamData(final HistoricalTeam team) throws Exception
	{
		final var file = new File("orgs.csv");
		final var parser = CSVParser.parse(new FileReader(file),
				CSVFormat.Builder.create(CSVFormat.EXCEL).setHeader().build());
		for (final CSVRecord csvRecord : parser)
		{
			final var abbreviation = csvRecord.get("abbreviation");
			final var startYear = this.intOr0(csvRecord.get("startYear").trim());
			final var endYear = this.intOr0(csvRecord.get("endYear").trim());
			final var season = team.getDivision().getConference().getSeason();
			if (team.getAbbreviation().equalsIgnoreCase(abbreviation) && season >= startYear && season <= endYear)
			{
				team.setLocation(csvRecord.get("location"));
				team.setNickname(csvRecord.get("nickname"));
			}
		}
	}

	private void createConferences(final HistoricalLeague league) throws Exception
	{
		System.out.println("\t-> " + league.getAbbreviation());

		if ("NFL".equals(league.getAbbreviation()))
		{
			for (var year = HistoricalDatabaseCreator.START_YEAR; year < 1970; year++)
			{
				final var conference = new HistoricalConference(league, year, league.getAbbreviation(),
						league.getAbbreviation());
				Database.getDefaultDatabase().createHistoricalConference(conference);
			}

			for (var year = 1970; year < HistoricalDatabaseCreator.END_YEAR; year++)
			{
				var conference = new HistoricalConference(league, year, "NFC", "National Football League");
				Database.getDefaultDatabase().createHistoricalConference(conference);

				conference = new HistoricalConference(league, year, "AFC", "American Football League");
				Database.getDefaultDatabase().createHistoricalConference(conference);
			}
		}
		else if ("AFL".equals(league.getAbbreviation()))
			for (var year = 1960; year < 1970; year++)
			{
				final var conference = new HistoricalConference(league, year, league.getAbbreviation(),
						league.getAbbreviation());
				Database.getDefaultDatabase().createHistoricalConference(conference);
			}
	}

	private void createDatabase() throws Exception
	{
		this.createLeagues();
		final var leagues = Database.getDefaultDatabase().loadHistoricalLeagues();
		final var nfl = leagues.stream().filter(l -> "NFL".equalsIgnoreCase(l.getAbbreviation())).findFirst()
				.orElse(null);
		final var afl = leagues.stream().filter(l -> "AFL".equalsIgnoreCase(l.getAbbreviation())).findFirst()
				.orElse(null);

//		this.createLeague(nfl);
		this.createLeague(afl);

		// to check
		this.loadLeagues();
	}

	private void createDivisions(final HistoricalConference conference) throws IOException, InterruptedException,
			SQLException, ParserConfigurationException, SAXException, JDOMException
	{
		System.out.println("\t\t-> " + conference.getSeason() + " : " + conference.getAbbreviation());

		final var refData = this.getRefData("/years/" + conference.getSeason()
				+ ("AFL".equals(conference.getLeague().getAbbreviation()) ? "_AFL" : "") + "/index.htm");

		final var scanner = new SimpleScanner(refData);

		var xml = scanner.extract("<tbody>", "</table>");
		xml = xml.replaceFirst("<tbody>", "<table>");

		final var doc = this.createDocument(xml);
		final List<Element> elements = XPath.selectNodes(doc, "table/tr[@class='thead onecell']");

		for (final Element e : elements)
		{
			// might be a problem with division name containing conference abbreviation
			var divisionName = e.getChildTextTrim("td");
			if (divisionName == null || divisionName.isBlank())
				divisionName = conference.getAbbreviation();

			final var division = Database.getDefaultDatabase()
					.createHistoricalDivision(new HistoricalDivision(conference, divisionName));

			System.out.println("\t\t\t-> " + division.getName());
		}
	}

	private Document createDocument(String xml) throws ParserConfigurationException, SAXException, IOException
	{
		xml = xml.replace("&", "&amp;");
		final var domfactory = DocumentBuilderFactory.newInstance();
		domfactory.setValidating(false);
		domfactory.setNamespaceAware(true);
		final var docbuilder = domfactory.newDocumentBuilder();
		return new DOMBuilder().build(docbuilder.parse(new ByteArrayInputStream(xml.getBytes())));
	}

	private void createLeague(final HistoricalLeague league) throws Exception, SQLException, IOException,
			InterruptedException, ParserConfigurationException, SAXException, JDOMException, ParseException
	{
//		createConferences(league);

		final var conferences = Database.getDefaultDatabase().loadHistoricalConferences(league);
		for (final HistoricalConference conference : conferences)
		{
			league.addHistoricalConference(conference);

//			createDivisions(conference);
			final var divisions = Database.getDefaultDatabase().loadHistoricalDivisions(conference);
			for (final HistoricalDivision division : divisions)
				conference.addDivision(division);
		}

		for (var season = league.getStartYear(); season <= league.getEndYear(); season++)
			this.createPlayers(league, season);
	}

	private List<HistoricalLeague> createLeagues() throws SQLException, IOException, InterruptedException,
			ParserConfigurationException, SAXException, JDOMException
	{
		final var leagues = new ArrayList<HistoricalLeague>();

		var league = new HistoricalLeague("NFL", "National Football League");
		leagues.add(Database.getDefaultDatabase().createHistoricalLeague(league));

		league = new HistoricalLeague("AFL", "American Football League");
		leagues.add(Database.getDefaultDatabase().createHistoricalLeague(league));

		return leagues;
	}

	private void createPlayers(final HistoricalLeague league, final int season) throws SQLException, Exception,
			IOException, InterruptedException, ParserConfigurationException, SAXException, JDOMException, ParseException
	{
		Database.getDefaultDatabase().beginTransaction();
		this.createTeams(league, season);
		Database.getDefaultDatabase().commit();

		for (final HistoricalTeam team : league.getAllTeams(season))
		{
			Database.getDefaultDatabase().beginTransaction();
			this.addTeamData(team);
			this.readTeamRosterPage(team);
			this.readTeamPage(team);

			final List<HistoricalPlayer> players = new ArrayList<>();
			for (final HistoricalPlayer player : team.getPlayers())
			{
				team.addPlayer(player);
				player.setRatingBlocking(0);
				player.setRatingPassBlocking(-3);
				player.setRatingPassDefense(5);
				player.setRatingPassRush(0);
				player.setRatingTackles(4);

				players.add(player);
			}

			final var cg = new CardGenerator();

			cg.calculatePassRush(team);
			cg.calculatePassDefenseDB(team);
			cg.calculatePassDefenseLB(team);
			cg.calculateTacklesLB(team);
			cg.calculateTacklesDL(team);
			cg.calculateOffensiveBlockingOL(team);
			cg.calculateOffensiveBlockingRB(team);
			cg.calculateOffensiveBlockingOE(team);
			cg.calculatePassBlockingOL(team);

			Database.getDefaultDatabase().updateHistoricalTeam(team);

			final var parser = CSVParser.parse(new FileReader(new File("csv", "" + season + "_player_ratings.csv")),
					CSVFormat.Builder.create(CSVFormat.EXCEL).setHeader().build());
			for (final CSVRecord csvRecord : parser)
			{
				final var csvPlayerID = csvRecord.get("PlayerID");

				final PlayerRatings player = team.getPlayer(csvPlayerID);
				if (player == null)
					continue;

				cg.updatePlayerFromCSV(csvRecord, player);
			}

			for (final HistoricalPlayer player : players)
				Database.getDefaultDatabase().addHistoricalPlayer(player);

			Database.getDefaultDatabase().commit();
		}

	}

	private void createTeams(final HistoricalLeague league, final int season) throws Exception
	{
		System.out.println("" + league.getAbbreviation());

		final var refData = this
				.getRefData("/years/" + season + ("AFL".equals(league.getAbbreviation()) ? "_AFL" : "") + "/index.htm");
		final var scanner = new SimpleScanner(refData);

		while (true)
		{
			var xml = scanner.extract("<table", ">");
			xml = xml + "</table>";
			var doc = this.createDocument(xml);

			final var element = (Element) XPath.selectSingleNode(doc, "/table");
			final var conference = league.getConference(season, element.getAttributeValue("id"));
			if (conference == null)
				break;

			System.out.println("\t" + conference.getSeason() + " : " + conference.getAbbreviation());

			xml = scanner.extract("<tbody>", "</table>");
			xml = xml.replaceFirst("<tbody>", "<table>");
			doc = this.createDocument(xml);

			final List<Element> elements = XPath.selectNodes(doc, "/table/tr");

			HistoricalDivision currentDivision = null;
			for (final Element e : elements)
			{
				final var clazz = e.getAttributeValue("class");
				if ("thead onecell".equals(clazz))
				{
					var divisionName = e.getChildTextTrim("td");
					if (divisionName.startsWith(conference.getAbbreviation()))
						divisionName = divisionName.substring(conference.getAbbreviation().length()).trim();
					else if (divisionName.isBlank())
						divisionName = conference.getAbbreviation();

					currentDivision = conference.getDivision(divisionName);
					System.out.println("\t\t" + currentDivision.getName());
				}
				else
				{
					final var href = e.getChild("th").getChild("a").getAttributeValue("href");
					final var teamAbbreviation = href.substring(7, 10);
					final var team = Database.getDefaultDatabase()
							.createHistoricalTeam(new HistoricalTeam(currentDivision, teamAbbreviation));
					System.out.println("\t\t\t" + team.getAbbreviation());
					currentDivision.addTeam(team);
				}
			}
		}
	}

	private float floatOr0(final String text)
	{
		if (text == null || text.isBlank())
			return 0.0f;

		return Float.parseFloat(text);
	}

	private String getDataStat(final Element td, final String dataStat) throws Exception
	{
		final var e = (Element) XPath.selectSingleNode(td, "td[@data-stat='" + dataStat + "']");

		if (e == null)
			return null;

		return e.getTextTrim();
	}

	private String getRefData(final String subUrl) throws IOException, InterruptedException
	{
		return this.getRefData(subUrl, 7000);
	}

	private String getRefData(final String subUrl, final int delay) throws IOException, InterruptedException
	{
		Thread.sleep(delay);

		final var url = "https://www.pro-football-reference.com" + subUrl;

//		url = "https://api.zenrows.com/v1/?apikey=c4d732a0c77c9e0e967e728ae9a7d0e65da35f66&url=" + URLEncoder.encode(url);
		final var request = HttpRequest.newBuilder().uri(URI.create(url)).build();
		final HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
		return response.body();
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

	}

	private byte[] loadLogo(final String url) throws IOException, InterruptedException
	{
		final var request = HttpRequest.newBuilder().uri(URI.create(url)).build();
		final HttpResponse<byte[]> response = this.client.send(request, BodyHandlers.ofByteArray());
		return response.body();
	}

	private HistoricalPlayer readHistoricalPlayer(final HistoricalTeam team, final Element e) throws JDOMException
	{
		final List<Element> tds = e.getChildren("td");
		final var td = tds.get(0);

		final var playerIDElement = td.getChild("a");
		var playerID = playerIDElement.getAttributeValue("href");
		playerID = playerID.substring(playerID.lastIndexOf("/") + 1, playerID.lastIndexOf("."));

		var player = team.getPlayer(playerID);
		if (player == null)
		{
			player = new HistoricalPlayer(team, playerID);

			// formed LastName FirstName-00
			var cskPlayerName = td.getAttributeValue("csk");
			if (cskPlayerName.contains(","))
			{
				final var split = cskPlayerName.split(",");
				player.setLastName(split[0].trim());
				player.setFirstName(split[1].trim());
			}
			else
			{
				cskPlayerName = cskPlayerName.substring(0, cskPlayerName.lastIndexOf("-"));

				// formed FirstName LastName
				final var playerName = playerIDElement.getText();

				final var firstTokenCsk = cskPlayerName.split(" ")[0];
				final var firstTokenPlayerName = playerName.split(" ")[0];

				final var lastName = playerName.split(firstTokenCsk)[0].trim();
				final var firstName = cskPlayerName.split(firstTokenPlayerName)[0].trim();
				player.setFirstName(firstName);
				player.setLastName(lastName);
			}

			player.setAge(this.intOr0(tds.get(1).getTextTrim()));
			final var th = e.getChild("th");
			final var number = this.intOr0(th.getText());
			player.setNumber(number);
			player.setPosition(tds.get(2).getTextTrim());
			player.setGames(this.intOr0(tds.get(3).getTextTrim()));
			player.setStarts(this.intOr0(tds.get(4).getTextTrim()));
		}

		return player;
	}

	private void readTeamPage(final HistoricalTeam team) throws Exception
	{
		System.out.println("-> " + team.getDivision().getConference().getSeason() + " : " + team.getLocation() + " "
				+ team.getNickname());

		final var refData = this.getRefData(
				"/teams/" + team.getAbbreviation() + "/" + team.getDivision().getConference().getSeason() + ".htm");

		final var scanner = new SimpleScanner(refData);

		this.writeLogo(scanner);
		this.updateRecord(team, scanner);
		this.updateTeamStats(team, scanner);

		Database.getDefaultDatabase().updateHistoricalTeam(team);

		this.updateGames(team, scanner);

		this.updatePassing(team, scanner);
		this.updateRushingAndReceiving(team, scanner);
		this.updateKickAndPuntReturns(team, scanner);
		this.updateKicking(team, scanner);
		this.updatePunting(team, scanner);
		this.updateDefenseAndFumbles(team, scanner);

	}

	private void readTeamRosterPage(final HistoricalTeam team) throws IOException, InterruptedException,
			ParserConfigurationException, SAXException, JDOMException, SQLException, ParseException
	{
		System.out.println("-> " + team.getDivision().getConference().getSeason() + " : " + team.getLocation() + " "
				+ team.getNickname());

		final var refData = this.getRefData("/teams/" + team.getAbbreviation() + "/"
				+ team.getDivision().getConference().getSeason() + "_roster.htm");

		final var scanner = new SimpleScanner(refData);

		scanner.scanTo("<h2>Roster</h2>");

		var xml = scanner.extract("<tbody>", "</tbody>");
		xml = xml.replace("�", "");
		xml = xml.replaceAll("\\&", " and ");

//		System.out.println(xml);
		final var doc = this.createDocument(xml);

		final List<Element> elements = XPath.selectNodes(doc, "/tbody/tr");
		for (final Element e : elements)
		{
			final var player = this.readHistoricalPlayer(team, e);
			if (team.getPlayer(player.getPlayerID()) == null)
			{
				team.addPlayer(player);
				System.out.println(player);
			}
		}

	}

	private void updateDefenseAndFumbles(final HistoricalTeam team, final SimpleScanner scanner) throws Exception
	{
		scanner.scanTo(" <caption>Defense &amp; Fumbles Table</caption>");
		final var xml = scanner.extract("<tbody>", "</tbody>");
		System.out.println(xml);
		final var doc = this.createDocument(xml);

		final List<Element> elements = XPath.selectNodes(doc, "/tbody/tr");
		for (final Element e : elements)
		{
			final PlayerStats player = this.readHistoricalPlayer(team, e);

			player.setInterceptions(this.intOr0(this.getDataStat(e, "def_int")));
			player.setInterceptionsYards(this.intOr0(this.getDataStat(e, "def_int_yds")));
			player.setInterceptionsTDs(this.intOr0(this.getDataStat(e, "def_int_td")));
			player.setInterceptionsLongest(this.intOr0(this.getDataStat(e, "def_int_long")));

			player.setPassesDefended(this.intOr0(this.getDataStat(e, "pass_defended")));
			player.setFumblesForced(this.intOr0(this.getDataStat(e, "fumbles_forced")));
			player.setAllFumbles(this.intOr0(this.getDataStat(e, "fumbles")));
			player.setFumblesRecovered(this.intOr0(this.getDataStat(e, "fumbles_rec")));
			player.setFumblesYards(this.intOr0(this.getDataStat(e, "fumbles_rec_yds")));
			player.setFumblesRecoveredTDs(this.intOr0(this.getDataStat(e, "fumbles_rec_td")));
			player.setSacks((float)this.floatOr0(this.getDataStat(e, "sacks")));
		}
	}

	private void updateGames(final HistoricalTeam team, final SimpleScanner scanner)
			throws ParserConfigurationException, SAXException, IOException, JDOMException, ParseException, SQLException
	{
		var xml = scanner.extract("<tbody>", "</table>");
		xml = xml.replace("<tbody>", "<table>");
		final var doc = this.createDocument(xml);

		final List<Element> elements = XPath.selectNodes(doc, "/table/tr");
		for (final Element e : elements)
		{
			final var game = new HistoricalGame(team.getDivision().getConference().getLeague());
			game.setSeason(team.getDivision().getConference().getSeason());

			final var week = this.intOr0(e.getChildTextTrim("th"));
			if (week == 0)
				break;

			game.setWeek(week);

			final var dateString = ((Element) XPath.selectSingleNode(e, "td[2]")).getAttributeValue("csk");
			if (dateString == null || dateString.isBlank())
				continue;

			game.setDate(this.dateFormatter.parse(dateString));

			final var isVisitor = "@".equals(((Element) XPath.selectSingleNode(e, "td[8]")).getTextTrim());
			final var opponent = ((Element) XPath.selectSingleNode(e, "td[9]/a")).getAttributeValue("href")
					.split("/")[2];
			final var pointsScored = this.intOr0(((Element) XPath.selectSingleNode(e, "td[10]")).getTextTrim());
			final var pointsAgainst = this.intOr0(((Element) XPath.selectSingleNode(e, "td[11]")).getTextTrim());

			if (isVisitor)
			{
				game.setHomeTeam(opponent);
				game.setHomeTeamScore(pointsAgainst);
				game.setVisitingTeam(team.getAbbreviation());
				game.setVisitingTeamScore(pointsScored);
			}
			else
			{
				game.setHomeTeam(team.getAbbreviation());
				game.setHomeTeamScore(pointsScored);
				game.setVisitingTeam(opponent);
				game.setVisitingTeamScore(pointsAgainst);
			}

			game.setType("Regular Season");

			Database.getDefaultDatabase().addHistoricalGame(game);
		}
	}

	private void updateKickAndPuntReturns(final HistoricalTeam team, final SimpleScanner scanner) throws Exception
	{
		final var xml = scanner.extract("<tbody>", "</tbody>");
		final var doc = this.createDocument(xml);

		final List<Element> elements = XPath.selectNodes(doc, "/tbody/tr");
		for (final Element e : elements)
		{
			final PlayerStats player = this.readHistoricalPlayer(team, e);

			player.setPuntsReturned(this.intOr0(this.getDataStat(e, "punt_ret")));
			player.setPuntsReturnedYards(this.intOr0(this.getDataStat(e, "punt_ret_yds")));
			player.setPuntsReturnedTDs(this.intOr0(this.getDataStat(e, "punt_ret_td")));
			player.setPuntsReturnedLongest(this.intOr0(this.getDataStat(e, "punt_ret_long")));

			player.setKicksReturned(this.intOr0(this.getDataStat(e, "kick_ret")));
			player.setKicksReturnedYards(this.intOr0(this.getDataStat(e, "kick_ret_yds")));
			player.setKicksReturnedTDs(this.intOr0(this.getDataStat(e, "kick_ret_td")));
			player.setKicksReturnedLongest(this.intOr0(this.getDataStat(e, "kick_ret_long")));
		}
	}

	private void updateKicking(final HistoricalTeam team, final SimpleScanner scanner) throws Exception
	{
		final var xml = scanner.extract("<tbody>", "</tbody>");
		System.out.println(xml);
		final var doc = this.createDocument(xml);

		final List<Element> elements = XPath.selectNodes(doc, "/tbody/tr");
		for (final Element e : elements)
		{
			final PlayerStats player = this.readHistoricalPlayer(team, e);

			player.setFgAttempted19(this.intOr0(this.getDataStat(e, "fga1")));
			player.setFgMade19(this.intOr0(this.getDataStat(e, "fgm1")));
			player.setFgAttempted29(this.intOr0(this.getDataStat(e, "fga2")));
			player.setFgMade29(this.intOr0(this.getDataStat(e, "fgm2")));
			player.setFgAttempted39(this.intOr0(this.getDataStat(e, "fga3")));
			player.setFgMade39(this.intOr0(this.getDataStat(e, "fgm3")));
			player.setFgAttempted49(this.intOr0(this.getDataStat(e, "fga4")));
			player.setFgMade49(this.intOr0(this.getDataStat(e, "fgm4")));
			player.setFgAttempted50(this.intOr0(this.getDataStat(e, "fga5")));
			player.setFgMade50(this.intOr0(this.getDataStat(e, "fgm5")));

			player.setFgLongest(this.intOr0(this.getDataStat(e, "fg_long")));

			player.setXpAttempted(this.intOr0(this.getDataStat(e, "xpa")));
			player.setXpMade(this.intOr0(this.getDataStat(e, "xpm")));

			player.setKickoffs(this.intOr0(this.getDataStat(e, "kickoff")));
			player.setKickoffsYards(this.intOr0(this.getDataStat(e, "kickoff_yds")));
			player.setKickoffsTouchbacks(this.intOr0(this.getDataStat(e, "kickoff_tb")));
		}
	}

	private void updatePassing(final HistoricalTeam team, final SimpleScanner scanner) throws Exception
	{
		scanner.scanTo("id=\"div_passing\"");

		final var xml = scanner.extract("<tbody>", "</tbody>");
		final var doc = this.createDocument(xml);

		final List<Element> elements = XPath.selectNodes(doc, "/tbody/tr");
		for (final Element e : elements)
		{
			final PlayerStats player = this.readHistoricalPlayer(team, e);

			player.setPassingCompletions(this.intOr0(this.getDataStat(e, "pass_cmp")));
			player.setPassingAttempts(this.intOr0(this.getDataStat(e, "pass_att")));
			player.setPassingYards(this.intOr0(this.getDataStat(e, "pass_yds")));
			player.setPassingTDs(this.intOr0(this.getDataStat(e, "pass_td")));
			player.setPassingInterceptions(this.intOr0(this.getDataStat(e, "pass_int")));
			player.setPassingLongest(this.intOr0(this.getDataStat(e, "pass_long")));
			player.setPassingTimesSacked(this.intOr0(this.getDataStat(e, "pass_sacked")));
			player.setPassingSackedYards(this.intOr0(this.getDataStat(e, "pass_sacked_yds")));
		}
	}

	private void updatePunting(final HistoricalTeam team, final SimpleScanner scanner) throws Exception
	{
		final var xml = scanner.extract("<tbody>", "</tbody>");
		final var doc = this.createDocument(xml);

		final List<Element> elements = XPath.selectNodes(doc, "/tbody/tr");
		for (final Element e : elements)
		{
			final PlayerStats player = this.readHistoricalPlayer(team, e);

			player.setPunts(this.intOr0(this.getDataStat(e, "punt")));
			player.setPuntsYards(this.intOr0(this.getDataStat(e, "punt_yds")));
			player.setPuntsLongest(this.intOr0(this.getDataStat(e, "punt_long")));
			player.setPuntsBlocked(this.intOr0(this.getDataStat(e, "punt_blocked")));
		}
	}

	private void updateRecord(final HistoricalTeam team, final SimpleScanner scanner)
			throws ParserConfigurationException, SAXException, IOException, JDOMException
	{
		var xml = scanner.extract("<strong>Record:</strong>", ",", false);
		xml = "<a>" + xml + "</a>";

		var doc = this.createDocument(xml);
		var element = (Element) XPath.selectSingleNode(doc, "a");
		final var record = element.getTextTrim();
		final var recordSplit = record.split("-");
		final var wins = this.intOr0(recordSplit[0]);
		final var losses = this.intOr0(recordSplit[1]);
		final var ties = this.intOr0(recordSplit[2]);

		team.setWins(wins);
		team.setLosses(losses);
		team.setTies(ties);

		scanner.scanTo("<p>");
		xml = scanner.extract("<p>", "</p>");
		doc = this.createDocument(xml);
		element = (Element) XPath.selectSingleNode(doc, "p/a");
		team.setCoach(element.getTextTrim());

		try
		{
			scanner.scanTo("<p>");
			xml = scanner.extract("<p><strong>Stadium:</strong>", "</p>");
			doc = this.createDocument(xml);
			element = (Element) XPath.selectSingleNode(doc, "p/a");
			team.setStadium(element.getTextTrim());
		}
		catch (final Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void updateRushingAndReceiving(final HistoricalTeam team, final SimpleScanner scanner) throws Exception
	{
		final var xml = scanner.extract("<tbody>", "</tbody>");
		final var doc = this.createDocument(xml);

		final List<Element> elements = XPath.selectNodes(doc, "/tbody/tr");
		for (final Element e : elements)
		{
			final PlayerStats player = this.readHistoricalPlayer(team, e);

			player.setRushingAttempts(this.intOr0(this.getDataStat(e, "rush_att")));
			player.setRushingYards(this.intOr0(this.getDataStat(e, "rush_yds")));
			player.setRushingTDs(this.intOr0(this.getDataStat(e, "rush_td")));
			player.setRushingLongest(this.intOr0(this.getDataStat(e, "rush_long")));

			player.setReceivingReceptions(this.intOr0(this.getDataStat(e, "rec")));
			player.setReceivingYards(this.intOr0(this.getDataStat(e, "rec_yds")));
			player.setReceivingTDs(this.intOr0(this.getDataStat(e, "rec_td")));
			player.setReceivingLongest(this.intOr0(this.getDataStat(e, "rec_long")));

			player.setOffensiveFumbles(this.intOr0(this.getDataStat(e, "fumbles")));
		}
	}

	private void updateTeamStats(final HistoricalTeam team, final SimpleScanner scanner)
			throws ParserConfigurationException, SAXException, IOException, JDOMException
	{
		var xml = scanner.extract("<tbody>", "</table>");
		xml = xml.replace("<tbody>", "<table>");
		final var doc = this.createDocument(xml);

		team.setPointsFor(this
				.intOr0(((Element) XPath.selectSingleNode(doc, "/table/tr[1]/td[@data-stat='points']")).getTextTrim()));
		team.setPointsAgainst(this
				.intOr0(((Element) XPath.selectSingleNode(doc, "/table/tr[2]/td[@data-stat='points']")).getTextTrim()));

		team.setYards(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[1]/td[@data-stat='total_yards']")).getTextTrim()));
		team.setYardsOpp(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[2]/td[@data-stat='total_yards']")).getTextTrim()));

		team.setPlays(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[1]/td[@data-stat='plays_offense']")).getTextTrim()));
		team.setPlaysOpp(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[2]/td[@data-stat='plays_offense']")).getTextTrim()));

		team.setTurnovers(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[1]/td[@data-stat='turnovers']")).getTextTrim()));
		team.setTurnoversOpp(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[2]/td[@data-stat='turnovers']")).getTextTrim()));

		team.setFumblesLost(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[1]/td[@data-stat='fumbles_lost']")).getTextTrim()));
		team.setFumblesLostOpp(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[2]/td[@data-stat='fumbles_lost']")).getTextTrim()));

		team.setFirstDowns(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[1]/td[@data-stat='first_down']")).getTextTrim()));
		team.setFirstDownsOpp(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[2]/td[@data-stat='first_down']")).getTextTrim()));

		team.setCompletions(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[1]/td[@data-stat='pass_cmp']")).getTextTrim()));
		team.setCompletionsOpp(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[2]/td[@data-stat='pass_cmp']")).getTextTrim()));

		team.setPassingAttempts(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[1]/td[@data-stat='pass_att']")).getTextTrim()));
		team.setPassingAttemptsOpp(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[2]/td[@data-stat='pass_att']")).getTextTrim()));

		team.setPassingYards(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[1]/td[@data-stat='pass_yds']")).getTextTrim()));
		team.setPassingYardsOpp(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[2]/td[@data-stat='pass_yds']")).getTextTrim()));

		team.setPassingTDs(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[1]/td[@data-stat='pass_td']")).getTextTrim()));
		team.setPassingTDsOpp(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[2]/td[@data-stat='pass_td']")).getTextTrim()));

		team.setInterceptions(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[1]/td[@data-stat='pass_int']")).getTextTrim()));
		team.setInterceptionsOpp(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[2]/td[@data-stat='pass_int']")).getTextTrim()));

		team.setFirstDownsPassing(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[1]/td[@data-stat='pass_fd']")).getTextTrim()));
		team.setFirstDownsPassingOpp(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[2]/td[@data-stat='pass_fd']")).getTextTrim()));

		team.setRushingAttempts(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[1]/td[@data-stat='rush_att']")).getTextTrim()));
		team.setRushingAttemptsOpp(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[2]/td[@data-stat='rush_att']")).getTextTrim()));

		team.setRushingYards(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[1]/td[@data-stat='rush_yds']")).getTextTrim()));
		team.setRushingYardsOpp(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[2]/td[@data-stat='rush_yds']")).getTextTrim()));

		team.setRushingTDs(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[1]/td[@data-stat='rush_td']")).getTextTrim()));
		team.setRushingTDsOpp(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[2]/td[@data-stat='rush_td']")).getTextTrim()));

		team.setFirstDownsRushing(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[1]/td[@data-stat='rush_fd']")).getTextTrim()));
		team.setFirstDownsRushingOpp(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[2]/td[@data-stat='rush_fd']")).getTextTrim()));

		team.setPenalties(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[1]/td[@data-stat='penalties']")).getTextTrim()));
		team.setPenaltiesOpp(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[2]/td[@data-stat='penalties']")).getTextTrim()));

		team.setPenaltyYards(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[1]/td[@data-stat='penalties_yds']")).getTextTrim()));
		team.setPenaltyYardsOpp(this.intOr0(
				((Element) XPath.selectSingleNode(doc, "/table/tr[2]/td[@data-stat='penalties_yds']")).getTextTrim()));

		team.setPenaltyFirstDowns(this
				.intOr0(((Element) XPath.selectSingleNode(doc, "/table/tr[1]/td[@data-stat='pen_fd']")).getTextTrim()));
		team.setPenaltyFirstDownsOpp(this
				.intOr0(((Element) XPath.selectSingleNode(doc, "/table/tr[2]/td[@data-stat='pen_fd']")).getTextTrim()));
	}

	private void writeLogo(final SimpleScanner scanner)
			throws ParserConfigurationException, SAXException, IOException, JDOMException, InterruptedException
	{
		var xml = scanner.extract("<img class=\"teamlogo\"", ">");
		xml = xml + "</img>";

		final var doc = this.createDocument(xml);
		final var element = (Element) XPath.selectSingleNode(doc, "img");
		final var url = element.getAttributeValue("src");
		final var logoBytes = this.loadLogo(url);
		final var file = new File("logos/" + url.substring(url.lastIndexOf("/") + 1));
		FileUtils.writeByteArrayToFile(file, logoBytes);
	}

}
