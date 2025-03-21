package jef.history;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import jef.core.PlayerInfo;
import jef.core.PlayerPosition;
import jef.core.PlayerRatings;
import jef.core.PlayerStats;

public class HistoricalPlayer implements PlayerRatings, PlayerStats, PlayerInfo
{
	private final HistoricalTeam team;
	private final String playerID;

	private String firstName;
	private String lastName;
	private int number;
	private int age;
	private String position;
	private String startingPosition;
	private int games;
	private int starts;

	private int passingCompletions;
	private int passingAttempts;
	private int passingYards;
	private int passingTDs;
	private int passingInterceptions;
	private int passingLongest;
	private int passingTimesSacked;
	private int passingSackedYards;

	private int rushingAttempts;
	private int rushingYards;
	private int rushingTDs;
	private int rushingLongest;

	private int receivingReceptions;
	private int receivingYards;
	private int receivingTDs;
	private int receivingLongest;

	private int offensiveFumbles;

	private int puntsReturned;
	private int puntsReturnedYards;
	private int puntsReturnedTDs;
	private int puntsReturnedLongest;

	private int kicksReturned;
	private int kicksReturnedYards;
	private int kicksReturnedTDs;
	private int kicksReturnedLongest;

	private int fgAttempted19;
	private int fgMade19;
	private int fgAttempted29;
	private int fgMade29;
	private int fgAttempted39;
	private int fgMade39;
	private int fgAttempted49;
	private int fgMade49;
	private int fgAttempted50;
	private int fgMade50;
	private int fgLongest;

	private int xpAttempted;
	private int xpMade;

	private int kickoffs;
	private int kickoffsYards;
	private int kickoffsTouchbacks;

	private int punts;
	private int puntsYards;
	private int puntsLongest;
	private int puntsBlocked;

	private int interceptions;
	private int interceptionsYards;
	private int interceptionsTDs;
	private int interceptionsLongest;

	private int passesDefended;
	private int fumblesForced;
	private int allFumbles;
	private int fumblesRecovered;
	private int fumblesYards;
	private int fumblesRecoveredTDs;
	private float sacks;
	private int tackles_combined;
	private int tackles_solo;
	private int tackles_assists;
	private int tackles_loss;
	private int qb_hits;
	private int safety_md;

	private int ratingPassRush;
	private int ratingPassDefense;
	private int ratingTackles;
	private int ratingBlocking;
	private int ratingPassBlocking;
	private int csvPassRush;
	private int csvCoverage;
	private int csvRunDefense;
	private int csvTackling;
	private int csvPassBlocking;
	private int csvRunBlocking;

	private int csvAccuracy;
	private int csvDistance;
	private int csvControl;
	private int csvRedZone;
	private int csvMobility;
	private int csvSpeed;
	private int csvPower;
	private int csvQuickness;
	private int csvVision;
	private int csvReceiving;
	private int csvHands;
	private int csvKickReturns;
	private int csvPuntReturns;
	private int csvDiscipline;
	private int csvTurnovers;
	private int csvFgAccuracy;
	private int csvFgPower;
	private int csvPuntAccuracy;
	private int csvPuntPower;
	private int csvDurability;

	private int height; // in inches
	private int weight; // in lbs;

	public HistoricalPlayer(final HistoricalTeam team, final String playerID)
	{
		this.team = team;
		this.playerID = playerID;
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
		HistoricalPlayer other = (HistoricalPlayer) obj;
		return Objects.equals(this.playerID, other.playerID);
	}

	@Override
	public int getAge()
	{
		return this.age;
	}

	@Override
	public int getAllFumbles()
	{
		return this.allFumbles;
	}

	@Override
	public int getCsvAccuracy()
	{
		return this.csvAccuracy;
	}

	@Override
	public int getCsvControl()
	{
		return this.csvControl;
	}

	@Override
	public int getCsvCoverage()
	{
		return this.csvCoverage;
	}

	@Override
	public int getCsvDiscipline()
	{
		return this.csvDiscipline;
	}

	@Override
	public int getCsvDistance()
	{
		return this.csvDistance;
	}

	@Override
	public int getCsvDurability()
	{
		return this.csvDurability;
	}

	@Override
	public int getCsvFgAccuracy()
	{
		return this.csvFgAccuracy;
	}

	@Override
	public int getCsvFgPower()
	{
		return this.csvFgPower;
	}

	@Override
	public int getCsvHands()
	{
		return this.csvHands;
	}

	@Override
	public int getCsvKickReturns()
	{
		return this.csvKickReturns;
	}

	@Override
	public int getCsvMobility()
	{
		return this.csvMobility;
	}

	@Override
	public int getCsvPassBlocking()
	{
		return this.csvPassBlocking;
	}

	@Override
	public int getCsvPassRush()
	{
		return this.csvPassRush;
	}

	@Override
	public int getCsvPower()
	{
		return this.csvPower;
	}

	@Override
	public int getCsvPuntAccuracy()
	{
		return this.csvPuntAccuracy;
	}

	@Override
	public int getCsvPuntPower()
	{
		return this.csvPuntPower;
	}

	@Override
	public int getCsvPuntReturns()
	{
		return this.csvPuntReturns;
	}

	@Override
	public int getCsvQuickness()
	{
		return this.csvQuickness;
	}

	@Override
	public int getCsvReceiving()
	{
		return this.csvReceiving;
	}

	@Override
	public int getCsvRedZone()
	{
		return this.csvRedZone;
	}

	@Override
	public int getCsvRunBlocking()
	{
		return this.csvRunBlocking;
	}

	@Override
	public int getCsvRunDefense()
	{
		return this.csvRunDefense;
	}

	@Override
	public int getCsvSpeed()
	{
		return this.csvSpeed;
	}

	@Override
	public int getCsvTackling()
	{
		return this.csvTackling;
	}

	@Override
	public int getCsvTurnovers()
	{
		return this.csvTurnovers;
	}

	@Override
	public int getCsvVision()
	{
		return this.csvVision;
	}

	@Override
	public int getFgAttempted()
	{
		return this.getFgAttempted19() + this.getFgAttempted29() + this.getFgAttempted39() + this.getFgAttempted49()
				+ this.getFgAttempted50();
	}

	@Override
	public int getFgAttempted19()
	{
		return this.fgAttempted19;
	}

	@Override
	public int getFgAttempted29()
	{
		return this.fgAttempted29;
	}

	@Override
	public int getFgAttempted39()
	{
		return this.fgAttempted39;
	}

	@Override
	public int getFgAttempted49()
	{
		return this.fgAttempted49;
	}

	@Override
	public int getFgAttempted50()
	{
		return this.fgAttempted50;
	}

	@Override
	public int getFgLongest()
	{
		return this.fgLongest;
	}

	@Override
	public int getFgMade()
	{
		return this.getFgMade19() + this.getFgMade29() + this.getFgMade39() + this.getFgMade49() + this.getFgMade50();
	}

	@Override
	public int getFgMade19()
	{
		return this.fgMade19;
	}

	@Override
	public int getFgMade29()
	{
		return this.fgMade29;
	}

	@Override
	public int getFgMade39()
	{
		return this.fgMade39;
	}

	@Override
	public int getFgMade49()
	{
		return this.fgMade49;
	}

	@Override
	public int getFgMade50()
	{
		return this.fgMade50;
	}

	@Override
	public String getFieldValue(final PlayerPosition posType, final String field)
	{
		if (PlayerStats.G.equalsIgnoreCase(field))
			return "" + this.getGames();
		if (PlayerStats.GS.equalsIgnoreCase(field))
			return "" + this.getStarts();
		if (PlayerStats.G_S.equalsIgnoreCase(field))
			return "" + this.getGames() + "/" + this.getStarts();
		else if (PlayerStats.Pos.equalsIgnoreCase(field))
			return this.getPrimaryPosition().toString();
		else if (PlayerStats.PBR.equalsIgnoreCase(field))
			return "" + this.getRatingPassBlocking();
		else if (PlayerStats.RBR.equalsIgnoreCase(field))
			return "" + this.getRatingBlocking();
		else if (PlayerStats.PB_RB.equalsIgnoreCase(field))
			return "" + this.getRatingPassBlocking() + "/" + this.getRatingBlocking();
		else if (PlayerStats.CMP_PCT.equalsIgnoreCase(field))
			return "" + Conversions.avgToPctString(this.getPassingAvg());
		else if (PlayerStats.Att.equalsIgnoreCase(field))
		{
			if (posType.isA(PlayerPosition.QB))
				return "" + this.getPassingAttempts();
			else if (posType.isA(PlayerPosition.RB))
				return "" + this.getRushingAttempts();
			else if (posType.isA(PlayerPosition.OE))
				return "" + this.getReceivingReceptions();
			else if (posType.isA(PlayerPosition.KR))
				return "" + this.getKicksReturned();
			else if (posType.isA(PlayerPosition.PR))
				return "" + this.getPuntsReturned();
			else if (posType.isA(PlayerPosition.KOS))
				return "" + this.getKickoffs();
			else if (posType.isA(PlayerPosition.FGS))
				return "" + this.getFgAttempted();
			else if (posType.isA(PlayerPosition.P))
				return "" + this.getPunts();
		}
		else if (PlayerStats.CMP.equalsIgnoreCase(field))
			return "" + this.getPassingCompletions();
		else if (PlayerStats.YDS.equalsIgnoreCase(field))
		{
			if (posType.isA(PlayerPosition.QB))
				return "" + this.getPassingYards();
			else if (posType.isA(PlayerPosition.RB))
				return "" + this.getRushingYards();
			else if (posType.isA(PlayerPosition.OE))
				return "" + this.getReceivingYards();
			else if (posType.isA(PlayerPosition.KR))
				return "" + this.getKicksReturnedYards();
			else if (posType.isA(PlayerPosition.PR))
				return "" + this.getPuntsReturnedYards();
		}
		else if (PlayerStats.TDS.equalsIgnoreCase(field))
		{
			if (posType.isA(PlayerPosition.QB))
				return "" + this.getPassingTDs();
			else if (posType.isA(PlayerPosition.RB))
				return "" + this.getRushingTDs();
			else if (posType.isA(PlayerPosition.OE))
				return "" + this.getReceivingTDs();
			else if (posType.isA(PlayerPosition.KR))
				return "" + this.getKicksReturnedTDs();
			else if (posType.isA(PlayerPosition.PR))
				return "" + this.getPuntsReturnedTDs();
		}
		else if (PlayerStats.INT.equalsIgnoreCase(field))
		{
			if (posType.isA(PlayerPosition.QB))
				return "" + this.getPassingInterceptions();
			else
				return "" + this.getInterceptions();
		}
		else if (PlayerStats.Y_A.equalsIgnoreCase(field))
		{
			if (posType.isA(PlayerPosition.QB))
				return "" + Conversions.avgToString(this.getPassingYardsAverage());
			else if (posType.isA(PlayerPosition.RB))
				return "" + Conversions.avgToString(this.getRushingAvg());
			else if (posType.isA(PlayerPosition.OE))
				return "" + Conversions.avgToString(this.getReceivingAvg());
			else if (posType.isA(PlayerPosition.KR))
				return "" + Conversions.avgToString(this.getKicksReturnedAvg());
			else if (posType.isA(PlayerPosition.PR))
				return "" + Conversions.avgToString(this.getPuntsReturnedAvg());
			else if (posType.isA(PlayerPosition.KOS))
				return "" + Conversions.avgToString(this.getKickoffAvg());
			else if (posType.isA(PlayerPosition.FGS))
				return "" + Conversions.avgToString(this.getFgMade() / (float) this.getFgAttempted());
			else if (posType.isA(PlayerPosition.P))
				return "" + Conversions.avgToString(this.getPuntsAvg());
		}
		else if (PlayerStats.LNG.equalsIgnoreCase(field))
		{
			if (posType.isA(PlayerPosition.QB))
				return "" + this.getPassingLongest();
			else if (posType.isA(PlayerPosition.RB))
				return "" + this.getRushingLongest();
			else if (posType.isA(PlayerPosition.OE))
				return "" + this.getReceivingLongest();
			else if (posType.isA(PlayerPosition.KR))
				return "" + this.getKicksReturnedLongest();
			else if (posType.isA(PlayerPosition.PR))
				return "" + this.getPuntsReturnedLongest();
			else if (posType.isA(PlayerPosition.FGS))
				return "" + this.getFgLongest();
			else if (posType.isA(PlayerPosition.P))
				return "" + this.getPuntsLongest();
		}
		else if (PlayerStats._20.equalsIgnoreCase(field))
			return "" + this.getFgMade19() + "/" + this.getFgAttempted19();
		else if (PlayerStats._30.equalsIgnoreCase(field))
			return "" + this.getFgMade29() + "/" + this.getFgAttempted29();
		else if (PlayerStats._40.equalsIgnoreCase(field))
			return "" + this.getFgMade39() + "/" + this.getFgAttempted39();
		else if (PlayerStats._50.equalsIgnoreCase(field))
			return "" + this.getFgMade49() + "/" + this.getFgAttempted49();
		else if (PlayerStats._50PLUS.equalsIgnoreCase(field))
			return "" + this.getFgMade50() + "/" + this.getFgAttempted50();
		else if (PlayerStats.XPPCT.equalsIgnoreCase(field))
			return "" + Conversions.avgToPctString(this.getXpAttempted() == 0 ? 0 : this.getXpMade() / (float) this.getXpAttempted());
		else if (PlayerStats.TBPCT.equalsIgnoreCase(field))
			return "" + Conversions.avgToPctString(this.getKickoffs() == 0 ? 0 : this.getKickoffsTouchbacks() / (float) this.getKickoffs());
		else if (PlayerStats.PRR.equalsIgnoreCase(field))
			return "" + this.getRatingPassRush();
		else if (PlayerStats.PDR.equalsIgnoreCase(field))
			return "" + this.getRatingPassDefense();
		else if (PlayerStats.Sks.equalsIgnoreCase(field))
			return "" + Conversions.avgToString(this.getSacks());
		else if (PlayerStats.TkR.equalsIgnoreCase(field))
			return "" + this.getRatingTackles();
		else if (PlayerStats.PD_PR_Tk.equalsIgnoreCase(field))
			return "" + this.getRatingPassDefense() + "/" + this.getRatingPassRush() + "/" + this.getRatingTackles();

		return "";
	}

	@Override
	public String getFirstName()
	{
		return this.firstName;
	}

	@Override
	public int getFumblesForced()
	{
		return this.fumblesForced;
	}

	@Override
	public int getFumblesRecovered()
	{
		return this.fumblesRecovered;
	}

	@Override
	public int getFumblesRecoveredTDs()
	{
		return this.fumblesRecoveredTDs;
	}

	@Override
	public int getFumblesYards()
	{
		return this.fumblesYards;
	}

	@Override
	public int getGames()
	{
		return this.games;
	}

	@Override
	public int getHeight()
	{
		return this.height;
	}

	@Override
	public int getInterceptions()
	{
		return this.interceptions;
	}

	@Override
	public int getInterceptionsLongest()
	{
		return this.interceptionsLongest;
	}

	@Override
	public int getInterceptionsTDs()
	{
		return this.interceptionsTDs;
	}

	@Override
	public int getInterceptionsYards()
	{
		return this.interceptionsYards;
	}

	@Override
	public int getKickoffs()
	{
		return this.kickoffs;
	}

	@Override
	public int getKickoffsTouchbacks()
	{
		return this.kickoffsTouchbacks;
	}

	@Override
	public int getKickoffsYards()
	{
		return this.kickoffsYards;
	}

	@Override
	public int getKicksReturned()
	{
		return this.kicksReturned;
	}

	@Override
	public float getKicksReturnedAvg()
	{
		return this.getKicksReturned() == 0 ? 0 : this.getKicksReturnedYards() / this.getKicksReturned();
	}

	@Override
	public int getKicksReturnedLongest()
	{
		return this.kicksReturnedLongest;
	}

	@Override
	public int getKicksReturnedTDs()
	{
		return this.kicksReturnedTDs;
	}

	@Override
	public int getKicksReturnedYards()
	{
		return this.kicksReturnedYards;
	}

	@Override
	public String getLastName()
	{
		return this.lastName;
	}

	@Override
	public int getNumber()
	{
		return this.number;
	}

	@Override
	public int getOffensiveFumbles()
	{
		return this.offensiveFumbles;
	}

	@Override
	public int getPassesDefended()
	{
		return this.passesDefended;
	}

	@Override
	public int getPassingAttempts()
	{
		return this.passingAttempts;
	}

	@Override
	public int getPassingCompletions()
	{
		return this.passingCompletions;
	}

	@Override
	public int getPassingInterceptions()
	{
		return this.passingInterceptions;
	}

	@Override
	public int getPassingLongest()
	{
		return this.passingLongest;
	}

	@Override
	public int getPassingSackedYards()
	{
		return this.passingSackedYards;
	}

	@Override
	public int getPassingTDs()
	{
		return this.passingTDs;
	}

	@Override
	public int getPassingTimesSacked()
	{
		return this.passingTimesSacked;
	}

	@Override
	public int getPassingYards()
	{
		return this.passingYards;
	}

	@Override
	public String getPlayerID()
	{
		return this.playerID;
	}

	public String getPosition()
	{
		return this.position;
	}

	@Override
	public List<PlayerPosition> getPositions()
	{
		return Arrays.asList(this.getPrimaryPosition(), this.getSecondaryPosition(), this.getTertiaryPosition());
	}

	@Override
	public PlayerPosition getPrimaryPosition()
	{
		var positions = this.getPosition().split("/");
		if ("E".equals(positions[0]))
			positions[0] = "OE";
		else if ("B".equals(positions[0]))
			positions[0] = "RB";
		else if ("LB-DE".equals(positions[0]))
			positions = new String[]
			{ "LB", "DE" };
		else if ("DET".equals(positions[0]) || "D".equals(positions[0]))
			positions = new String[]
			{ "DE", "DT" };
		else if ("DTN".equals(positions[0]))
			positions = new String[]
			{ "DT", "NT" };
		else if ("G-C".equals(positions[0]))
			positions = new String[]
			{ "G", "C" };
		else if ("RBOTE".equals(positions[0]))
			positions = new String[]
			{ "RB", "TE" };
		else if ("TOG".equals(positions[0]))
			positions = new String[]
			{ "T", "G" };
		else if ("LBODE".equals(positions[0]))
			positions = new String[]
			{ "LB", "DE" };
		else if ("GOC".equals(positions[0]))
			positions = new String[]
			{ "G", "C" };
		else if ("LD".equals(positions[0]))
			positions = new String[]
			{ "DL" };
		else if ("BT".equals(positions[0]))
			positions = new String[]
			{ "TB" };
		else if ("T-G".equals(positions[0]))
			positions = new String[]
			{ "T", "G" };
		else if ("LA".equals(positions[0]))
			positions = new String[]
			{ "LB" };
		else if ("KB".equals(positions[0]))
			positions = new String[]
			{ "K" };

		return PlayerPosition.valueOf(positions[0]);
	}

	@Override
	public int getPunts()
	{
		return this.punts;
	}

	@Override
	public int getPuntsBlocked()
	{
		return this.puntsBlocked;
	}

	@Override
	public int getPuntsLongest()
	{
		return this.puntsLongest;
	}

	@Override
	public int getPuntsReturned()
	{
		return this.puntsReturned;
	}

	@Override
	public int getPuntsReturnedLongest()
	{
		return this.puntsReturnedLongest;
	}

	@Override
	public int getPuntsReturnedTDs()
	{
		return this.puntsReturnedTDs;
	}

	@Override
	public int getPuntsReturnedYards()
	{
		return this.puntsReturnedYards;
	}

	@Override
	public int getPuntsYards()
	{
		return this.puntsYards;
	}

	@Override
	public int getQb_hits()
	{
		return this.qb_hits;
	}

	@Override
	public int getRatingBlocking()
	{
		return this.ratingBlocking;
	}

	@Override
	public int getRatingPassBlocking()
	{
		return this.ratingPassBlocking;
	}

	@Override
	public int getRatingPassDefense()
	{
		return this.ratingPassDefense;
	}

	@Override
	public int getRatingPassRush()
	{
		return this.ratingPassRush;
	}

	@Override
	public int getRatingTackles()
	{
		return this.ratingTackles;
	}

	@Override
	public float getReceivingAvg()
	{
		if (this.getReceivingReceptions() == 0)
			return 0;

		return (float) this.getReceivingYards() / (float) this.getReceivingReceptions();
	}

	@Override
	public int getReceivingLongest()
	{
		return this.receivingLongest;
	}

	@Override
	public int getReceivingReceptions()
	{
		return this.receivingReceptions;
	}

	@Override
	public int getReceivingTDs()
	{
		return this.receivingTDs;
	}

	@Override
	public int getReceivingYards()
	{
		return this.receivingYards;
	}

	@Override
	public int getRushingAttempts()
	{
		return this.rushingAttempts;
	}

	@Override
	public float getRushingAvg()
	{
		if (this.getRushingAttempts() == 0)
			return 0;

		return (float) this.getRushingYards() / (float) this.getRushingAttempts();
	}

	@Override
	public int getRushingLongest()
	{
		return this.rushingLongest;
	}

	@Override
	public int getRushingTDs()
	{
		return this.rushingTDs;
	}

	@Override
	public int getRushingYards()
	{
		return this.rushingYards;
	}

	@Override
	public float getSacks()
	{
		return this.sacks;
	}

	@Override
	public int getSafety_md()
	{
		return this.safety_md;
	}

	@Override
	public PlayerPosition getSecondaryPosition()
	{
		final var positions = this.getPosition().split("/");
		if (positions.length > 1)
			return PlayerPosition.valueOf(positions[1]);

		if (this.getKicksReturned() > 0)
			return PlayerPosition.KR;

		if (this.getPuntsReturned() > 0)
			return PlayerPosition.PR;

		return null;
	}

	public String getStartingPosition()
	{
		return this.startingPosition;
	}

	@Override
	public int getStartingValue(final PlayerPosition posType)
	{
		return Integer.parseInt(switch (posType)
		{
			case BB, FGS, FL, HB, K, KOS, KR, LE, LH, LHB, LKR, LUB, OE, P, PR, QB, RB, RE, RH, RHB, RKR, RUB, SE, TB,
					TE, WB, WR ->
				this.getFieldValue(posType, PlayerStats.Att);

			default -> this.getFieldValue(posType, PlayerStats.GS);
		});
	}

	@Override
	public int getStarts()
	{
		return this.starts;
	}

	@Override
	public int getTackles_assists()
	{
		return this.tackles_assists;
	}

	@Override
	public int getTackles_combined()
	{
		return this.tackles_combined;
	}

	@Override
	public int getTackles_loss()
	{
		return this.tackles_loss;
	}

	@Override
	public int getTackles_solo()
	{
		return this.tackles_solo;
	}

	public HistoricalTeam getTeam()
	{
		return this.team;
	}

	@Override
	public PlayerPosition getTertiaryPosition()
	{
		final var positions = this.getPosition().split("/");
		if (positions.length > 2)
			return PlayerPosition.valueOf(positions[2]);

		if (this.getPuntsReturned() > 0 && this.getKicksReturned() != 0)
			return PlayerPosition.PR;

		return null;
	}

	@Override
	public int getWeight()
	{
		return this.weight;
	}

	@Override
	public int getXpAttempted()
	{
		return this.xpAttempted;
	}

	@Override
	public int getXpMade()
	{
		return this.xpMade;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(playerID);
	}

	public boolean playsPosition(final PlayerPosition... positions)
	{
		return Arrays.asList(positions).stream().anyMatch(this::playsPosition);
	}

	public boolean playsPosition(final PlayerPosition playerPosition)
	{
		return this.getPrimaryPosition().isA(playerPosition)
				|| this.getSecondaryPosition() != null && this.getSecondaryPosition().isA(playerPosition)
				|| this.getTertiaryPosition() != null && this.getTertiaryPosition().isA(playerPosition);
	}

	@Override
	public void setAge(final int age)
	{
		this.age = age;
	}

	@Override
	public void setAllFumbles(final int allFumbles)
	{
		this.allFumbles = allFumbles;
	}

	@Override
	public void setCsvAccuracy(final int csvAccuracy)
	{
		this.csvAccuracy = csvAccuracy;
	}

	@Override
	public void setCsvControl(final int csvControl)
	{
		this.csvControl = csvControl;
	}

	@Override
	public void setCsvCoverage(final int csvCoverage)
	{
		this.csvCoverage = csvCoverage;
	}

	@Override
	public void setCsvDiscipline(final int csvDiscipline)
	{
		this.csvDiscipline = csvDiscipline;
	}

	@Override
	public void setCsvDistance(final int csvDistance)
	{
		this.csvDistance = csvDistance;
	}

	@Override
	public void setCsvDurability(final int csvDurability)
	{
		this.csvDurability = csvDurability;
	}

	@Override
	public void setCsvFgAccuracy(final int csvFgAccuracy)
	{
		this.csvFgAccuracy = csvFgAccuracy;
	}

	@Override
	public void setCsvFgPower(final int csvFgPower)
	{
		this.csvFgPower = csvFgPower;
	}

	@Override
	public void setCsvHands(final int csvHands)
	{
		this.csvHands = csvHands;
	}

	@Override
	public void setCsvKickReturns(final int csvKickReturns)
	{
		this.csvKickReturns = csvKickReturns;
	}

	@Override
	public void setCsvMobility(final int csvMobility)
	{
		this.csvMobility = csvMobility;
	}

	@Override
	public void setCsvPassBlocking(final int csvPassBlocking)
	{
		this.csvPassBlocking = csvPassBlocking;
	}

	@Override
	public void setCsvPassRush(final int csvPassRush)
	{
		this.csvPassRush = csvPassRush;
	}

	@Override
	public void setCsvPower(final int csvPower)
	{
		this.csvPower = csvPower;
	}

	@Override
	public void setCsvPuntAccuracy(final int csvPuntAccuracy)
	{
		this.csvPuntAccuracy = csvPuntAccuracy;
	}

	@Override
	public void setCsvPuntPower(final int csvPuntPower)
	{
		this.csvPuntPower = csvPuntPower;
	}

	@Override
	public void setCsvPuntReturns(final int csvPuntReturns)
	{
		this.csvPuntReturns = csvPuntReturns;
	}

	@Override
	public void setCsvQuickness(final int csvQuickness)
	{
		this.csvQuickness = csvQuickness;
	}

	@Override
	public void setCsvReceiving(final int csvReceiving)
	{
		this.csvReceiving = csvReceiving;
	}

	@Override
	public void setCsvRedZone(final int csvRedZone)
	{
		this.csvRedZone = csvRedZone;
	}

	@Override
	public void setCsvRunBlocking(final int csvRunBlocking)
	{
		this.csvRunBlocking = csvRunBlocking;
	}

	@Override
	public void setCsvRunDefense(final int csvRunDefense)
	{
		this.csvRunDefense = csvRunDefense;
	}

	@Override
	public void setCsvSpeed(final int csvSpeed)
	{
		this.csvSpeed = csvSpeed;
	}

	@Override
	public void setCsvTackling(final int csvTackling)
	{
		this.csvTackling = csvTackling;
	}

	@Override
	public void setCsvTurnovers(final int csvTurnovers)
	{
		this.csvTurnovers = csvTurnovers;
	}

	@Override
	public void setCsvVision(final int csvVision)
	{
		this.csvVision = csvVision;
	}

	@Override
	public void setFgAttempted19(final int fgAttempted19)
	{
		this.fgAttempted19 = fgAttempted19;
	}

	@Override
	public void setFgAttempted29(final int fgAttempted29)
	{
		this.fgAttempted29 = fgAttempted29;
	}

	@Override
	public void setFgAttempted39(final int fgAttempted39)
	{
		this.fgAttempted39 = fgAttempted39;
	}

	@Override
	public void setFgAttempted49(final int fgAttempted49)
	{
		this.fgAttempted49 = fgAttempted49;
	}

	@Override
	public void setFgAttempted50(final int fgAttempted50)
	{
		this.fgAttempted50 = fgAttempted50;
	}

	@Override
	public void setFgLongest(final int fgLongest)
	{
		this.fgLongest = fgLongest;
	}

	@Override
	public void setFgMade19(final int fgMade19)
	{
		this.fgMade19 = fgMade19;
	}

	@Override
	public void setFgMade29(final int fgMade29)
	{
		this.fgMade29 = fgMade29;
	}

	@Override
	public void setFgMade39(final int fgMade39)
	{
		this.fgMade39 = fgMade39;
	}

	@Override
	public void setFgMade49(final int fgMade49)
	{
		this.fgMade49 = fgMade49;
	}

	@Override
	public void setFgMade50(final int fgMade50)
	{
		this.fgMade50 = fgMade50;
	}

	@Override
	public void setFirstName(final String firstName)
	{
		this.firstName = firstName;
	}

	@Override
	public void setFumblesForced(final int fumblesForced)
	{
		this.fumblesForced = fumblesForced;
	}

	@Override
	public void setFumblesRecovered(final int fumblesRecovered)
	{
		this.fumblesRecovered = fumblesRecovered;
	}

	@Override
	public void setFumblesRecoveredTDs(final int fumblesRecoveredTDs)
	{
		this.fumblesRecoveredTDs = fumblesRecoveredTDs;
	}

	@Override
	public void setFumblesYards(final int fumblesYards)
	{
		this.fumblesYards = fumblesYards;
	}

	@Override
	public void setGames(final int games)
	{
		this.games = games;
	}

	@Override
	public void setHeight(final int height)
	{
		this.height = height;
	}

	@Override
	public void setInterceptions(final int interceptions)
	{
		this.interceptions = interceptions;
	}

	@Override
	public void setInterceptionsLongest(final int interceptionsLongest)
	{
		this.interceptionsLongest = interceptionsLongest;
	}

	@Override
	public void setInterceptionsTDs(final int interceptionsTDs)
	{
		this.interceptionsTDs = interceptionsTDs;
	}

	@Override
	public void setInterceptionsYards(final int interceptionsYards)
	{
		this.interceptionsYards = interceptionsYards;
	}

	@Override
	public void setKickoffs(final int kickoffs)
	{
		this.kickoffs = kickoffs;
	}

	@Override
	public void setKickoffsTouchbacks(final int kickoffsTouchbacks)
	{
		this.kickoffsTouchbacks = kickoffsTouchbacks;
	}

	@Override
	public void setKickoffsYards(final int kickoffsYards)
	{
		this.kickoffsYards = kickoffsYards;
	}

	@Override
	public void setKicksReturned(final int kicksReturned)
	{
		this.kicksReturned = kicksReturned;
	}

	@Override
	public void setKicksReturnedLongest(final int kicksReturnedLongest)
	{
		this.kicksReturnedLongest = kicksReturnedLongest;
	}

	@Override
	public void setKicksReturnedTDs(final int kicksReturnedTDs)
	{
		this.kicksReturnedTDs = kicksReturnedTDs;
	}

	@Override
	public void setKicksReturnedYards(final int kicksReturnedYards)
	{
		this.kicksReturnedYards = kicksReturnedYards;
	}

	@Override
	public void setLastName(final String lastName)
	{
		this.lastName = lastName;
	}

	@Override
	public void setNumber(final int number)
	{
		this.number = number;
	}

	@Override
	public void setOffensiveFumbles(final int offensiveFumbles)
	{
		this.offensiveFumbles = offensiveFumbles;
	}

	@Override
	public void setPassesDefended(final int passesDefended)
	{
		this.passesDefended = passesDefended;
	}

	@Override
	public void setPassingAttempts(final int passingAttempts)
	{
		this.passingAttempts = passingAttempts;
	}

	@Override
	public void setPassingCompletions(final int passingCompletions)
	{
		this.passingCompletions = passingCompletions;
	}

	@Override
	public void setPassingInterceptions(final int passingInterceptions)
	{
		this.passingInterceptions = passingInterceptions;
	}

	@Override
	public void setPassingLongest(final int passingLongest)
	{
		this.passingLongest = passingLongest;
	}

	@Override
	public void setPassingSackedYards(final int passingSackedYards)
	{
		this.passingSackedYards = passingSackedYards;
	}

	@Override
	public void setPassingTDs(final int passingTDs)
	{
		this.passingTDs = passingTDs;
	}

	@Override
	public void setPassingTimesSacked(final int passingTimesSacked)
	{
		this.passingTimesSacked = passingTimesSacked;
	}

	@Override
	public void setPassingYards(final int passingYards)
	{
		this.passingYards = passingYards;
	}

	public void setPosition(final String position)
	{
		this.position = position;
	}

	@Override
	public void setPunts(final int punts)
	{
		this.punts = punts;
	}

	@Override
	public void setPuntsBlocked(final int puntsBlocked)
	{
		this.puntsBlocked = puntsBlocked;
	}

	@Override
	public void setPuntsLongest(final int puntsLongest)
	{
		this.puntsLongest = puntsLongest;
	}

	@Override
	public void setPuntsReturned(final int puntsReturned)
	{
		this.puntsReturned = puntsReturned;
	}

	@Override
	public void setPuntsReturnedLongest(final int puntsReturnedLongest)
	{
		this.puntsReturnedLongest = puntsReturnedLongest;
	}

	@Override
	public void setPuntsReturnedTDs(final int puntsReturnedTDs)
	{
		this.puntsReturnedTDs = puntsReturnedTDs;
	}

	@Override
	public void setPuntsReturnedYards(final int puntsReturnedYards)
	{
		this.puntsReturnedYards = puntsReturnedYards;
	}

	@Override
	public void setPuntsYards(final int puntsYards)
	{
		this.puntsYards = puntsYards;
	}

	@Override
	public void setQb_hits(final int qb_hits)
	{
		this.qb_hits = qb_hits;
	}

	@Override
	public void setRatingBlocking(final int ratingBlocking)
	{
		this.ratingBlocking = ratingBlocking;
	}

	@Override
	public void setRatingPassBlocking(final int ratingPassBlocking)
	{
		this.ratingPassBlocking = ratingPassBlocking;
	}

	@Override
	public void setRatingPassDefense(final int ratingPassDefense)
	{
		this.ratingPassDefense = ratingPassDefense;
	}

	@Override
	public void setRatingPassRush(final int ratingPassRush)
	{
		this.ratingPassRush = ratingPassRush;
	}

	@Override
	public void setRatingTackles(final int ratingTackles)
	{
		this.ratingTackles = ratingTackles;
	}

	@Override
	public void setReceivingLongest(final int receivingLongest)
	{
		this.receivingLongest = receivingLongest;
	}

	@Override
	public void setReceivingReceptions(final int receivingReceptions)
	{
		this.receivingReceptions = receivingReceptions;
	}

	@Override
	public void setReceivingTDs(final int receivingTDs)
	{
		this.receivingTDs = receivingTDs;
	}

	@Override
	public void setReceivingYards(final int receivingYards)
	{
		this.receivingYards = receivingYards;
	}

	@Override
	public void setRushingAttempts(final int rushingAttempts)
	{
		this.rushingAttempts = rushingAttempts;
	}

	@Override
	public void setRushingLongest(final int rushingLongest)
	{
		this.rushingLongest = rushingLongest;
	}

	@Override
	public void setRushingTDs(final int rushingTDs)
	{
		this.rushingTDs = rushingTDs;
	}

	@Override
	public void setRushingYards(final int rushingYards)
	{
		this.rushingYards = rushingYards;
	}

	@Override
	public void setSacks(final float sacks)
	{
		this.sacks = sacks;
	}

	@Override
	public void setSafety_md(final int safety_md)
	{
		this.safety_md = safety_md;
	}

	public void setStartingPosition(final String startingPosition)
	{
		this.startingPosition = startingPosition;
	}

	@Override
	public void setStarts(final int starts)
	{
		this.starts = starts;
	}

	@Override
	public void setTackles_assists(final int tackles_assists)
	{
		this.tackles_assists = tackles_assists;
	}

	@Override
	public void setTackles_combined(final int tackles_combined)
	{
		this.tackles_combined = tackles_combined;
	}

	@Override
	public void setTackles_loss(final int tackles_loss)
	{
		this.tackles_loss = tackles_loss;
	}

	@Override
	public void setTackles_solo(final int tackles_solo)
	{
		this.tackles_solo = tackles_solo;
	}

	@Override
	public void setWeight(final int weight)
	{
		this.weight = weight;
	}

	@Override
	public void setXpAttempted(final int xpAttempted)
	{
		this.xpAttempted = xpAttempted;
	}

	@Override
	public void setXpMade(final int xpMade)
	{
		this.xpMade = xpMade;
	}

	@Override
	public String toString()
	{
		return "" + this.team.getDivision().getConference().getSeason() + " " + this.getFirstName() + " "
				+ this.getLastName();
	}

	private float getKickoffAvg()
	{
		return this.getKickoffs() == 0 ? 0 : this.getKickoffsYards() / (float) this.getKickoffs();
	}

	private float getPassingAvg()
	{
		// TODO: Tarkenton has zero attempts in 1976
		return this.getPassingAttempts() == 0 ? 0 : this.getPassingCompletions() / (float) this.getPassingAttempts();
	}

	private float getPassingYardsAverage()
	{
		return this.getPassingAttempts() == 0 ? 0 : this.getPassingYards() / (float) this.getPassingAttempts();
	}

	private float getPuntsAvg()
	{
		return this.getPunts() == 0 ? 0 : this.getPuntsYards() / (float) this.getPunts();
	}

	private float getPuntsReturnedAvg()
	{
		return this.getPuntsReturned() == 0 ? 0 : this.getPuntsReturnedYards() / (float) this.getPuntsReturned();
	}
}
