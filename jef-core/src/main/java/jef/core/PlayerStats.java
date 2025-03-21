package jef.core;

public interface PlayerStats
{

	String Sks = "SkS";
	String PDR = "PDR";
	String PRR = "PRR";
	String TkR = "TkR";
	String PD_PR_Tk = "PD/PR/Tk";
	String TBPCT = "Tb%";
	String XPPCT = "Xp%";
	String _50PLUS = "50+";
	String _50 = "<50";
	String _40 = "<40";
	String _30 = "<30";
	String _20 = "<20";
	String LNG = "Lng";
	String Y_A = "Y/A";
	String INT = "Int";
	String TDS = "Tds";
	String YDS = "Yds";
	String CMP = "Cmp";
	String Att = "Att";
	String CMP_PCT = "Cmp%";
	String RBR = "RBR";
	String PBR = "PBR";
	String PB_RB = "PB/RB";
	String Pos = "Pos";
	String GS = "GS";
	String G = "G";
	String G_S = "G/S";

	int getAllFumbles();

	int getFgAttempted();

	int getFgAttempted19();

	int getFgAttempted29();

	int getFgAttempted39();

	int getFgAttempted49();

	int getFgAttempted50();

	int getFgLongest();

	int getFgMade();

	int getFgMade19();

	int getFgMade29();

	int getFgMade39();

	int getFgMade49();

	int getFgMade50();

	String getFieldValue(PlayerPosition posType, String field);

	String getFirstName();

	int getFumblesForced();

	int getFumblesRecovered();

	int getFumblesRecoveredTDs();

	int getFumblesYards();

	int getGames();

	int getInterceptions();

	int getInterceptionsLongest();

	int getInterceptionsTDs();

	int getInterceptionsYards();

	int getKickoffs();

	int getKickoffsTouchbacks();

	int getKickoffsYards();

	int getKicksReturned();

	float getKicksReturnedAvg();

	int getKicksReturnedLongest();

	int getKicksReturnedTDs();

	int getKicksReturnedYards();

	int getOffensiveFumbles();

	int getPassesDefended();

	int getPassingAttempts();

	int getPassingCompletions();

	int getPassingInterceptions();

	int getPassingLongest();

	int getPassingSackedYards();

	int getPassingTDs();

	int getPassingTimesSacked();

	int getPassingYards();

	int getPunts();

	int getPuntsBlocked();

	int getPuntsLongest();

	int getPuntsReturned();

	int getPuntsReturnedLongest();

	int getPuntsReturnedTDs();

	int getPuntsReturnedYards();

	int getPuntsYards();

	int getQb_hits();

	float getReceivingAvg();

	int getReceivingLongest();

	int getReceivingReceptions();

	int getReceivingTDs();

	int getReceivingYards();

	int getRushingAttempts();

	float getRushingAvg();

	int getRushingLongest();

	int getRushingTDs();

	int getRushingYards();

	float getSacks();

	int getSafety_md();

	int getStartingValue(PlayerPosition posType);

	int getStarts();

	int getTackles_assists();

	int getTackles_combined();

	int getTackles_loss();

	int getTackles_solo();

	int getXpAttempted();

	int getXpMade();

	void setAllFumbles(int allFumbles);

	void setFgAttempted19(int fgAttempted19);

	void setFgAttempted29(int fgAttempted29);

	void setFgAttempted39(int fgAttempted39);

	void setFgAttempted49(int fgAttempted49);

	void setFgAttempted50(int fgAttempted50);

	void setFgLongest(int fgLongest);

	void setFgMade19(int fgMade19);

	void setFgMade29(int fgMade29);

	void setFgMade39(int fgMade39);

	void setFgMade49(int fgMade49);

	void setFgMade50(int fgMade50);

	void setFirstName(String firstName);

	void setFumblesForced(int fumblesForced);

	void setFumblesRecovered(int fumblesRecovered);

	void setFumblesRecoveredTDs(int fumblesRecoveredTDs);

	void setFumblesYards(int fumblesYards);

	void setGames(int games);

	void setInterceptions(int interceptions);

	void setInterceptionsLongest(int interceptionsLongest);

	void setInterceptionsTDs(int interceptionsTDs);

	void setInterceptionsYards(int interceptionsYards);

	void setKickoffs(int kickoffs);

	void setKickoffsTouchbacks(int kickoffsTouchbacks);

	void setKickoffsYards(int kickoffsYards);

	void setKicksReturned(int kicksReturned);

	void setKicksReturnedLongest(int kicksReturnedLongest);

	void setKicksReturnedTDs(int kicksReturnedTDs);

	void setKicksReturnedYards(int kicksReturnedYards);

	void setOffensiveFumbles(int offensiveFumbles);

	void setPassesDefended(int passesDefended);

	void setPassingAttempts(int passingAttempts);

	void setPassingCompletions(int passingCompletions);

	void setPassingInterceptions(int passingInterceptions);

	void setPassingLongest(int passingLongest);

	void setPassingSackedYards(int passingSackedYards);

	void setPassingTDs(int passingTDs);

	void setPassingTimesSacked(int passingTimesSacked);

	void setPassingYards(int passingYards);

	void setPunts(int punts);

	void setPuntsBlocked(int puntsBlocked);

	void setPuntsLongest(int puntsLongest);

	void setPuntsReturned(int puntsReturned);

	void setPuntsReturnedLongest(int puntsReturnedLongest);

	void setPuntsReturnedTDs(int puntsReturnedTDs);

	void setPuntsReturnedYards(int puntsReturnedYards);

	void setPuntsYards(int puntsYards);

	void setQb_hits(int qb_hits);

	void setReceivingLongest(int receivingLongest);

	void setReceivingReceptions(int receivingReceptions);

	void setReceivingTDs(int receivingTDs);

	void setReceivingYards(int receivingYards);

	void setRushingAttempts(int rushingAttempts);

	void setRushingLongest(int rushingLongest);

	void setRushingTDs(int rushingTDs);

	void setRushingYards(int rushingYards);

	void setSacks(float sacks);

	void setSafety_md(int safety_md);

	void setStarts(int starts);

	void setTackles_assists(int tackles_assists);

	void setTackles_combined(int tackles_combined);

	void setTackles_loss(int tackles_loss);

	void setTackles_solo(int tackles_solo);

	void setXpAttempted(int xpAttempted);

	void setXpMade(int xpMade);

}