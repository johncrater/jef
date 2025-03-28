package jef.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jef.geometry.Location;

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
	public static final double DEPTH_CENTER = Player.SIZE / 2.0;

	/**
	 * Offensive line depth. At least 7 players must be at this depth at the snap
	 */
	public static final double DEPTH_OFFSENSIVE_LINE = DEPTH_CENTER + Player.SIZE / 2;

	// QB depths
	public static final double DEPTH_QB_BEHIND_CENTER = DEPTH_CENTER + Player.SIZE;
	public static final double DEPTH_QB_SHOTGUN_SHALLOW = DEPTH_CENTER + 5.0;
	public static final double DEPTH_QB_SHOTGUN = DEPTH_CENTER + 6.0;
	public static final double DEPTH_QB_SHOTGUN_DEEP = DEPTH_CENTER + 7.0;

	// FB depths
	public static final double DEPTH_FB_SHALLOW = DEPTH_CENTER + 3.0;
	public static final double DEPTH_FB = DEPTH_CENTER + 4.0;
	public static final double DEPTH_FB_DEEP = DEPTH_CENTER + 5.0;

	// TB depths
	public static final double DEPTH_TB_SHALLOW = DEPTH_CENTER + 5.0;
	public static final double DEPTH_TB = DEPTH_CENTER + 6.0;
	public static final double DEPTH_TB_DEEP = DEPTH_CENTER + 7.0;

	// slot depth
	public static final double DEPTH_SLOT = DEPTH_CENTER + Player.SIZE;

	// DL depths
	public static final double DEPTH_DL = -Player.SIZE / 2;
	public static final double DEPTH_DL_FLEX = -Player.SIZE;

	// LB depths
	public static final double DEPTH_LB_SHALLOW = -3.0;
	public static final double DEPTH_LB = -4.0;
	public static final double DEPTH_LB_DEEP = -5.0;

	// DB depths
	public static final double DEPTH_S_SHALLOW = -10.0;
	public static final double DEPTH_S = -12.5;
	public static final double DEPTH_S_DEEP = -15.0;
	public static final double DEPTH_CB_SHALLOW = -4.0;
	public static final double DEPTH_CB = -5.0;
	public static final double DEPTH_CB_DEEP = -6.0;

	// Punter depth
	public static final double DEPTH_P = 15.0;
	public static final double DEPTH_UB = 7.0;

	// punt return team
	public static final double DEPTH_PPR = -40.0;

	// field goal depths
	public static final double DEPTH_FG_H = 7.0;
	public static final double DEPTH_FG_K = 8.5;

	// kickoff
	public static final double DEPTH_KO_OL = 5.0;
	public static final double DEPTH_KO_K = 10.0;

	// kick return team
	public static final double DEPTH_KO_DL = -10;
	public static final double DEPTH_KO_MF = -40;
	public static final double DEPTH_KO_KR = -65;

	// offensive splits
	public static final double OFFENSIVE_LINE_WIDTH_TIGHT_SPLIT = 1.0 / 3.0;
	public static final double OFFENSIVE_LINE_WIDTH_STANDARD_SPLIT = 2.0 / 3.0;;
	public static final double OFFENSIVE_LINE_WIDTH_WIDE_SPLIT = 1.0;

	// X receiver left width from center
	public static final double OFFENSIVE_LINE_WIDTH_X_RECEIVER = 15.0;
	public static final double OFFENSIVE_LINE_WIDTH_Z_RECEIVER = -15.0;
	
	private String name;
	private Location relativeLocation;
	private Location anchorLocation;
	private String anchorPosition;
	private List<PlayerPosition> playerPositions;

	public FormationPosition(String name, Location anchorLocation, Location relativeLocation, PlayerPosition...playerPositions)
	{
		this.name = name;
		this.anchorLocation = anchorLocation;
		this.relativeLocation = relativeLocation;
		this.playerPositions = Arrays.asList(playerPositions);
	}

	public FormationPosition(String name, String anchorPosition, Location relativeLocation, PlayerPosition...playerPositions)
	{
		this.name = name;
		this.anchorPosition = anchorPosition;
		this.relativeLocation = relativeLocation;
		this.playerPositions = Arrays.asList(playerPositions);
	}

	public FormationPosition(String name, Location relativeLocation, PlayerPosition...playerPositions)
	{
		this.name = name;
		this.relativeLocation = relativeLocation;
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

	public Location getRelativeLocation()
	{
		return this.relativeLocation;
	}

	public Location getAnchorLocation()
	{
		return this.anchorLocation;
	}

	public String getAnchorPosition()
	{
		return this.anchorPosition;
	}

}
