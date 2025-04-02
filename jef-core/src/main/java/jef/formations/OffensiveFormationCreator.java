package jef.formations;

import jef.geometry.Location;

public abstract class OffensiveFormationCreator extends FormationCreator
{
//	/**
//	 * The location of the center is Player.SIZE / 2.0 yards back from the line of
//	 * scrimmage;
//	 */
//	public static final double DEPTH_CENTER = -Player.SIZE / 2.0;
//
//	/**
//	 * Offensive line depth. At least 7 players must be at this depth at the snap
//	 */
//	public static final double DEPTH_OFFSENSIVE_LINE = -Player.SIZE / 2;
//
//	// QB depths
//	public static final double DEPTH_QB_BEHIND_CENTER = Player.SIZE;
//	public static final double DEPTH_QB_SHOTGUN_SHALLOW = 5.0;
//	public static final double DEPTH_QB_SHOTGUN = 6.0;
//	public static final double DEPTH_QB_SHOTGUN_DEEP = 7.0;
//
//	// FB depths
//	public static final double DEPTH_FB_SHALLOW = DEPTH_CENTER + 3.0;
//	public static final double DEPTH_FB = DEPTH_CENTER + 4.0;
//	public static final double DEPTH_FB_DEEP = DEPTH_CENTER + 5.0;
//
//	// TB depths
//	public static final double DEPTH_TB_SHALLOW = DEPTH_CENTER + 5.0;
//	public static final double DEPTH_TB = DEPTH_CENTER + 6.0;
//	public static final double DEPTH_TB_DEEP = DEPTH_CENTER + 7.0;
//
//	// HB depths
//	public static final double DEPTH_HB_SHALLOW = DEPTH_CENTER + 4.0;
//	public static final double DEPTH_HB = DEPTH_CENTER + 5.0;
//	public static final double DEPTH_HB_DEEP = DEPTH_CENTER + 6.0;
//
//	// slot depth
//	public static final double DEPTH_SLOT = DEPTH_CENTER + Player.SIZE;
//
//	// Punter depth
//	public static final double DEPTH_P = 15.0;
//	public static final double DEPTH_UB = 7.0;
//
//	// offensive splits
	public static final double OFFENSIVE_LINE_WIDTH_TIGHT_SPLIT = 1.0 / 3.0;
	public static final double OFFENSIVE_LINE_WIDTH_STANDARD_SPLIT = 2.0 / 3.0;;
	public static final double OFFENSIVE_LINE_WIDTH_WIDE_SPLIT = 1.0;
//
//	// X receiver left width from center
//	public static final double OFFENSIVE_LINE_WIDTH_X_RECEIVER = 15.0;
//	public static final double OFFENSIVE_LINE_WIDTH_Z_RECEIVER = -15.0;
//	

	private double lineSplits;

	public OffensiveFormationCreator(String name, double lineSplits)
	{
		super(name, UnitType.OFFENSE);
		this.lineSplits = lineSplits;
	}

	public double getLineSplits()
	{
		return this.lineSplits;
	}

	protected void addFiveManLine(Formation formation)
	{
		FormationPosition center = addPlayer(formation, "C", getScrimmage(), createLocation(null, IFormationPlayer.SIZE / 2, 0),
				PlayerPosition.C);

		FormationPosition leftGuard = addPlayer(formation, "LG", center.getLocation(),
				createLocation(Side.LEFT, IFormationPlayer.SIZE / 2, IFormationPlayer.SIZE + lineSplits),
				PlayerPosition.LG);

		FormationPosition rightGuard = addPlayer(formation, "RG", center.getLocation(),
				createLocation(Side.RIGHT, IFormationPlayer.SIZE / 2, IFormationPlayer.SIZE + lineSplits),
				PlayerPosition.RG);

		addPlayer(formation, "LT", leftGuard.getLocation(),
				createLocation(Side.LEFT, 0, IFormationPlayer.SIZE + lineSplits), PlayerPosition.LT);

		addPlayer(formation, "RT", rightGuard.getLocation(),
				createLocation(Side.RIGHT, 0, IFormationPlayer.SIZE + lineSplits), PlayerPosition.RT);

	}

	protected void addDoubleTightEnds(Formation formation)
	{
		this.addTightEnd(formation, Side.LEFT);
		this.addTightEnd(formation, Side.RIGHT);
	}

	protected void addTightEnd(Formation formation, Side side)
	{
		addPlayer(formation, side.getPrefix() + "TE", formation.getPosition(side.getPrefix() + "T").getLocation(),
				createLocation(side, 0, IFormationPlayer.SIZE + this.getLineSplits()), PlayerPosition.TE);
	}

	protected void addQuarterBackBehindCenter(Formation formation)
	{
		addPlayer(formation, "QB", formation.getPosition("C").getLocation(), createLocation(null, IFormationPlayer.SIZE, 0), PlayerPosition.QB);
	}

	/**
	 * @param linemanToStandBehind "T" for tackles, "G" for guards
	 */
	protected void addSplitBacks(Formation formation, String linemanToStandBehind, Side fullbackSide)
	{
		addPlayer(formation, "FB", formation.getPosition(fullbackSide.getPrefix() + linemanToStandBehind).getLocation(),
				createLocation(fullbackSide, 5.0, 0), PlayerPosition.FB);
		addPlayer(formation, "HB", formation.getPosition(fullbackSide.getOpposite().getPrefix() + linemanToStandBehind).getLocation(),
				createLocation(fullbackSide.getOpposite(), 5, 0), PlayerPosition.HB);
	}

	protected FormationPosition addPlayer(Formation formation, String positionName, Location anchorPosition, Location relativeOffset,
			PlayerPosition playerPosition)
	{
		FormationPosition pos = new FormationPosition(positionName, anchorPosition.add(relativeOffset), playerPosition);
		formation.addPosition(pos);
		return pos;
	}
}
