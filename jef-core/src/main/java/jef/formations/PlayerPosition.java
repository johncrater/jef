package jef.formations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// @formatter:off
public enum PlayerPosition
{

	// level 0
	ANY("Player", null, 0, null, null, null, null, null),

	// level 2 - statistical categories
	OL("Offensive Line", UnitType.OFFENSE, 2, ANY, 5.3f, 76.75f, 314.46f, 54.0f),
	OE("Offensive End", UnitType.OFFENSE, 2, ANY, 4.7f, 74.0f, 225.0f, 78.0f),
	QB("Quarterback", UnitType.OFFENSE, 2, ANY, 4.93f, 75.43f, 227.97f, 66.5f),
	RB("Running Back", UnitType.OFFENSE, 2, ANY, 4.49f, 214.0f, 214.48f, 75.1f),
	DB("Defensive Back", UnitType.DEFENSE, 2, ANY, 4.55f, 71.69f, 200.10f, 79.9f),
	LB("Linebacker", UnitType.DEFENSE, 2, ANY, 4.76f, 74.04f, 244.64f, 73.1f),
	DL("Defensive Line", UnitType.DEFENSE, 2, ANY, 4.9f, 75.5f, 293.0f, 63.5f),
	P("Punter", UnitType.KICKOFF, 2, ANY, 5.0f, 73.77f, 214.32f, 58.7f),
	K("Kicker", UnitType.KICKOFF, 2, ANY, 5.4f, 72.19f, 202.58f, 58.3f),
	KR("Kick Return", UnitType.KICKOFF_RETURN, 2, ANY, 4.5f, 72.4f, 200.3f, null),
	PR("Punt Return", UnitType.KICKOFF_RETURN, 2, ANY, 4.5f, 72.4f, 200.3f, null),

	// level 3 - general purpose positions -
	G("Offensive Guard", UnitType.OFFENSE, 3, OL, 5.37f, null, null, 53.0f),
	T("Offensive Tackle", UnitType.OFFENSE, 3, OL, 5.32f, null, null, 55.3f),
	C("Center", UnitType.OFFENSE, 3, OL, 5.30f, null, null, 53.0f),
	FB("Full Back", UnitType.OFFENSE, 3, RB, 4.80f, null, null, 78.6f),
	HB("Half Back", UnitType.OFFENSE, 3, RB),
	SE("Split End", UnitType.OFFENSE, 3, OE, 4.48f, 72.40f, 200.32f, 80.4f),
	TE("Tight End", UnitType.OFFENSE, 3, OE, 4.70f, 76.54f, 254.26f, 73.9f),
	DE("Defensive End", UnitType.DEFENSE, 3, DL, 4.80f, 75.82f, 278.99f, 67.7f),
	DT("Defensive Tackle", UnitType.DEFENSE, 3, DL, 5.06f, 75.22f, 308.97f, 59.0f),
	DG("Defensive Guard", UnitType.DEFENSE, 3, DL),
	OLB("Outside Linebacker", UnitType.DEFENSE, 3, LB, 4.60f, null, null, 72.7f),
	ILB("Inside Linebacker", UnitType.DEFENSE, 3, LB, 4.76f, null, null, 73.0f),
	S("Safety", UnitType.DEFENSE, 3, DB, 4.55f, null, null, 77.2f),
	CB("Corner Back", UnitType.DEFENSE, 3, DB, 4.48f, null, null, 83.1f),
	DH("Defensive Halfback", UnitType.DEFENSE, 3, DB),
	KOS("Kickoff Specialist", UnitType.KICKOFF, 3, K),
	FGS("Field Goal Specialist", UnitType.KICKOFF, 3, K),

	// level 4 - specific roles

	// offensive line
	LG("Left Guard", UnitType.OFFENSE, 4, G),
	RG("Right Guard", UnitType.OFFENSE, 4, G),
	RT("Right Tackle", UnitType.OFFENSE, 4, T),
	LT("Left Tackle", UnitType.OFFENSE, 4, T),
	LSC("Long Snap", UnitType.KICKOFF, 4, C),

	// backfield
	BB("Blocking Back", UnitType.OFFENSE, 4, FB),
	WB("Wing Back", UnitType.OFFENSE, 4, HB),
	TB("Tail Back", UnitType.OFFENSE, 4, HB),
	LH("Left Halfback", UnitType.OFFENSE, 4, HB),
	LHB("Left Halfback", UnitType.OFFENSE, 4, HB),
	RH("Right Halfback", UnitType.OFFENSE, 4, HB),
	RHB("Right Halfback", UnitType.OFFENSE, 4, HB),

	// receivers
	FL("Flanker", UnitType.OFFENSE, 4, SE),
	WR("Wide Receiver", UnitType.OFFENSE, 4, SE),
	LE("Left End", UnitType.OFFENSE, 4, TE),
	RE("Right End", UnitType.OFFENSE, 4, TE),

	// defensive backs
	FS("Free Safety", UnitType.DEFENSE, 4, S, 4.53f, null, null, 77.4f),
	SS("Strong Safety", UnitType.DEFENSE, 4, S, 4.55f, null, null, 77.2f),
	RS("Right Safety", UnitType.DEFENSE, 4, S),
	LS("Left Safety", UnitType.DEFENSE, 4, S),
	RCB("Right Corner Back", UnitType.DEFENSE, 4, CB),
	LCB("Left Corner Back", UnitType.DEFENSE, 4, CB),
	LDH("Left Defensive Halfback", UnitType.DEFENSE, 4, DH),
	RDH("Right Defensive Halfback", UnitType.DEFENSE, 4, DH),

	// linebackers
	LLB("Left Linebacker", UnitType.DEFENSE, 4, OLB),
	RLB("Right Linebacker", UnitType.DEFENSE, 4, OLB),
	MLB("Middle Linebacker", UnitType.DEFENSE, 4, ILB),

	// defensive line
	LDE("Left Defensive End", UnitType.DEFENSE, 4, DE),
	RDE("Right Defensive End", UnitType.DEFENSE, 4, DE),
	RDT("Right Defensive Tackle", UnitType.DEFENSE, 4, DT),
	LDT("Left Defensive Tackle", UnitType.DEFENSE, 4, DT),
	NT("Nose Tackle", UnitType.DEFENSE, 4, DG),
	MG("Middle Guard", UnitType.DEFENSE, 4, DG),
	LDG("Left Defensive Guard", UnitType.DEFENSE, 4, DG),
	RDG("Right Defensive Guard", UnitType.DEFENSE, 4, DG),

	LOLB("Left Outside Lineback", UnitType.DEFENSE, 5, LLB),
	ROLB("Right Outside Linebacker", UnitType.DEFENSE, 5, RLB),
	RILB("Right Inside Linebacker", UnitType.DEFENSE, 5, MLB),
	LILB("Left Inside Linebacker", UnitType.DEFENSE, 5, MLB),

	// special teams
	LKOL5("Left Kickoff Lineman 5", UnitType.KICKOFF, 5, LCB),
	LKOL4("Left Kickoff Lineman 4", UnitType.KICKOFF, 5, SS),
	LKOL3("Left Kickoff Lineman 3", UnitType.KICKOFF, 5, LLB),
	LKOL2("Left Kickoff Lineman 2", UnitType.KICKOFF, 5, LB),
	LKOL1("Left Kickoff Lineman 1", UnitType.KICKOFF, 5, LDE),
	RKOL5("Right Kickoff Lineman 5", UnitType.KICKOFF, 5, RDE),
	RKOL4("Right Kickoff Lineman 4", UnitType.KICKOFF, 5, LB),
	RKOL3("Right Kickoff Lineman 3", UnitType.KICKOFF, 5, RLB),
	RKOL2("Right Kickoff Lineman 2", UnitType.KICKOFF, 5, FS),
	RKOL1("Right Kickoff Lineman 1", UnitType.KICKOFF, 5, RCB),

	H("Holder", UnitType.KICKOFF, 5, ANY),

	UB("Up Back", UnitType.KICKOFF_RETURN, 5, RB),
	LUB("Left Up Back", UnitType.KICKOFF_RETURN, 5, UB),
	RUB("Right Up Back", UnitType.KICKOFF_RETURN, 5, UB),

	LKR("Left Deep Back", UnitType.KICKOFF_RETURN, 5, KR),
	RKR("Right Deep Back", UnitType.KICKOFF_RETURN, 5, KR),

	LKRMF("Left Kick Return Midfielder", UnitType.KICKOFF_RETURN, 5, TE),
	RKRMF("Right Kick Return Midfielder", UnitType.KICKOFF_RETURN, 5, TE),
	LTKRL("Left Tackle Kick Return Lineman", UnitType.KICKOFF_RETURN, 5, LT),
	LGKRL("Left Guard Kick Return Lineman", UnitType.KICKOFF_RETURN, 5, LG),
	CKRL("Center Kick Return Lineman", UnitType.KICKOFF_RETURN, 5, C),
	RTKRL("Right Tackle Kick Return Lineman", UnitType.KICKOFF_RETURN, 5, RT),
	RGKRL("Right Guard Kick Return Lineman", UnitType.KICKOFF_RETURN, 5, RG),
	;
	public static List<PlayerPosition> getLevel2Positions(final PlayerPosition posType)
	{
		return Arrays.asList(PlayerPosition.values()).stream().filter(p -> (p.getLevel() == 2) && p.isA(posType))
				.toList();
	}

	private final PlayerPosition parent;
	private final String name;
	private final int level;
	private final Float average40YardDashTime;
	private final Float avgHeightInInches;
	private final Float avgWeightInPounds;
	private final Float avgCsvSpeed;

	private final UnitType unitType;

	PlayerPosition(final String name, final UnitType unitType, final int level, final PlayerPosition parent)
	{
		this(name, unitType, level, parent, null, null, null, null);
	}

	PlayerPosition(final String name, final UnitType unitType, final int level, final PlayerPosition parent, final Float average40YardDashTime,
			final Float avgHeightInInches, final Float avgWeightInPounds, final Float avgCsvSpeed)
	{
		this.parent = parent;
		this.name = name;
		this.unitType = unitType;
		this.level = level;
		this.average40YardDashTime = average40YardDashTime;
		this.avgHeightInInches = avgHeightInInches;
		this.avgWeightInPounds = avgWeightInPounds;
		this.avgCsvSpeed = avgCsvSpeed;
	}

	public float getAdjusted40ydDashTime(final int csvSpeed)
	{

		return (this.getAverage40YardDashTime() * csvSpeed) / this.getAvgCsvSpeed();
	}

	public List<PlayerPosition> getAncestors()
	{
		final var ret = new ArrayList<PlayerPosition>();
		if (this.parent == null)
			return ret;

		ret.add(this.parent);
		ret.addAll(this.parent.getAncestors());
		return ret;
	}

	public float getAverage40YardDashTime()
	{
		if (this.average40YardDashTime == null)
			return this.getParent().getAverage40YardDashTime();

		return this.average40YardDashTime;
	}

	public float getAvgCsvSpeed()
	{
		return this.avgCsvSpeed == null ? this.getParent().getAvgCsvSpeed() : this.avgCsvSpeed;
	}

	public float getAvgHeightInInches()
	{
		return this.avgHeightInInches == null ? this.getAvgHeightInInches() : this.avgHeightInInches;
	}

	public float getAvgWeightInPounds()
	{
		return this.avgWeightInPounds == null ? this.getParent().getAvgWeightInPounds() : this.avgWeightInPounds;
	}

	public List<PlayerPosition> getDescendants()
	{
		final var ret = new ArrayList<PlayerPosition>();

		for (final PlayerPosition pos : PlayerPosition.values())
			if (pos.getParent() == this)
			{
				ret.add(pos);
				ret.addAll(pos.getDescendants());
			}

		return ret;
	}

	public int getLevel()
	{
		return this.level;
	}

	public String getName()
	{
		return this.name;
	}

	public PlayerPosition getParent()
	{
		return this.parent;
	}

	public UnitType getUnitType()
	{
		return this.unitType;
	}

	public boolean isA(final PlayerPosition pos)
	{
		if (pos == this)
			return true;

		if (this.parent != null)
			return this.parent.isA(pos);

		return false;
	}

	public PlayerPosition mapToLevel2()
	{
		if (this.getLevel() > 2)
			return this.getParent().mapToLevel2();

		return this;
	}
}
