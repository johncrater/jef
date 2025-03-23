package jef.actions.formations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jef.core.Football;
import jef.core.Location;
import jef.core.Player;
import jef.core.PlayerPosition;

/**
 * A Location relative to the location of the forward tip of the ball placement
 * before the snap. By default, offense is heading west.
 */
public class FormationPosition
{
	/**
	 * The depth on the neutral zone. The forward tip on the ball marks the line of
	 * scrimmage.
	 */
	public static final double NEUTRAL_ZONE_DEPTH = Football.lengthOfTheMajorAxis;

	/**
	 * The location of the center is Player.SIZE / 2.0 yards back from the line of
	 * scrimmage;
	 */
	public static final double DIM_DEPTH_CENTER = -Player.SIZE / 2.0;

	/**
	 * Offensive line depth. At least 7 players must be at this depth at the snap
	 */
	public static final double DIM_DEPTH_OFFSENSIVE_LINE = DIM_DEPTH_CENTER - Player.SIZE / 2;

	// QB depths
	public static final double DIM_DEPTH_QB_BEHIND_CENTER = DIM_DEPTH_CENTER - Player.SIZE;
	public static final double DIM_DEPTH_QB_SHOTGUN_SHALLOW = DIM_DEPTH_CENTER - 5.0;
	public static final double DIM_DEPTH_QB_SHOTGUN = DIM_DEPTH_CENTER - 6.0;
	public static final double DIM_DEPTH_QB_SHOTGUN_DEEP = DIM_DEPTH_CENTER - 7.0;

	// FB depths
	public static final double DIM_DEPTH_FB_SHALLOW = DIM_DEPTH_CENTER - 3.0;
	public static final double DIM_DEPTH_FB = DIM_DEPTH_CENTER - 4.0;
	public static final double DIM_DEPTH_FB_DEEP = DIM_DEPTH_CENTER - 5.0;

	// TB depths
	public static final double DIM_DEPTH_TB_SHALLOW = DIM_DEPTH_CENTER - 5.0;
	public static final double DIM_DEPTH_TB = DIM_DEPTH_CENTER - 6.0;
	public static final double DIM_DEPTH_TB_DEEP = DIM_DEPTH_CENTER - 7.0;

	// slot depth
	public static final double DIM_DEPTH_SLOT = DIM_DEPTH_CENTER - Player.SIZE;

	// DL depths
	public static final double DIM_DEPTH_DL = Player.SIZE / 2;
	public static final double DIM_DEPTH_DL_FLEX = Player.SIZE;

	// LB depths
	public static final double DIM_DEPTH_LB_SHALLOW = 3.0;
	public static final double DIM_DEPTH_LB = 4.0;
	public static final double DIM_DEPTH_LB_DEEP = 5.0;

	// DB depths
	public static final double DIM_DEPTH_S_SHALLOW = 10.0;
	public static final double DIM_DEPTH_S = 12.5;
	public static final double DIM_DEPTH_S_DEEP = 15.0;
	public static final double DIM_DEPTH_CB_SHALLOW = 4.0;
	public static final double DIM_DEPTH_CB = 5.0;
	public static final double DIM_DEPTH_CB_DEEP = 6.0;

	// Punter depth
	public static final double DIM_DEPTH_P = -15.0;
	public static final double DIM_DEPTH_UB = -7.0;

	// punt return team
	public static final double DIM_DEPTH_PPR = 40.0;

	// field goal depths
	public static final double DIM_DEPTH_FG_H = -7.0;
	public static final double DIM_DEPTH_FG_K = -8.5;

	// kickoff
	public static final double DIM_DEPTH_KO_OL = -5.0;
	public static final double DIM_DEPTH_KO_K = -10.0;

	// kick return team
	public static final double DIM_DEPTH_KO_DL = 10;
	public static final double DIM_DEPTH_KO_MF = 40;
	public static final double DIM_DEPTH_KO_KR = 65;

	// offensive splits
	public static final double OFFENSIVE_LINE_WIDTH_TIGHT_SPLIT = 1.0 / 3.0;
	public static final double OFFENSIVE_LINE_WIDTH_STANDARD_SPLIT = 2.0 / 3.0;;
	public static final double OFFENSIVE_LINE_WIDTH_WIDE_SPLIT = 1.0;

	// X receiver left width from center
	public static final double OFFENSIVE_LINE_WIDTH_X_RECEIVER = 10.0;
	public static final double OFFENSIVE_LINE_WIDTH_Z_RECEIVER = -10.0;
	
	private String name;
	private Location loc;
	private List<PlayerPosition> playerPositions;

	public FormationPosition(String name, Location loc, PlayerPosition...playerPositions)
	{
		this.name = name;
		this.loc = loc;
		this.playerPositions = Arrays.asList(playerPositions);
	}

	public String getName()
	{
		return this.name;
	}

	public List<PlayerPosition> getPlayerPositions()
	{
		return Collections.unmodifiableList(this.playerPositions);
	}

	public Location getLoc()
	{
		return this.loc;
	}

	public Location place(Location ballSpot)
	{
		return ballSpot.add(getLoc());
	}
}
