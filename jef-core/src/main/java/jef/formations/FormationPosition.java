package jef.formations;

import jef.geometry.Location;

/**
 * A Location relative to the location of the forward tip of the ball placement
 * before the snap. Negative X values indicate a relative position behind the line of
 * scrimmage and positive X values indicate a relative position beyond the line of scrimmage.
 * Negative Y values indicate a position to the right of the ball at the point it was spotted
 * and Positive Y values indicate a position to the left. This applies to both offense and defense.
 */
public class FormationPosition
{
//	/**
//	 * The depth on the neutral zone. The forward tip on the ball marks the line of
//	 * scrimmage.
//	 */
//	public static final double NEUTRAL_ZONE_DEPTH = Football.lengthOfTheMajorAxis;
//
//	// DL depths
//	public static final double DEPTH_DL = -Player.SIZE / 2;
//	public static final double DEPTH_DL_FLEX = -Player.SIZE;
//
//	// LB depths
//	public static final double DEPTH_LB_SHALLOW = -3.0;
//	public static final double DEPTH_LB = -4.0;
//	public static final double DEPTH_LB_DEEP = -5.0;
//
//	// DB depths
//	public static final double DEPTH_S_SHALLOW = -10.0;
//	public static final double DEPTH_S = -12.5;
//	public static final double DEPTH_S_DEEP = -15.0;
//	public static final double DEPTH_CB_SHALLOW = -4.0;
//	public static final double DEPTH_CB = -5.0;
//	public static final double DEPTH_CB_DEEP = -6.0;
//
//	// punt return team
//	public static final double DEPTH_PPR = -40.0;
//
//	// field goal depths
//	public static final double DEPTH_FG_H = 7.0;
//	public static final double DEPTH_FG_K = 8.5;
//
//	// kickoff
//	public static final double DEPTH_KO_OL = 5.0;
//	public static final double DEPTH_KO_K = 10.0;
//
//	// kick return team
//	public static final double DEPTH_KO_DL = -10;
//	public static final double DEPTH_KO_MF = -40;
//	public static final double DEPTH_KO_KR = -65;

	private String name;
	private Location location;
	private PlayerPosition playerPosition;

	public FormationPosition(String name, Location location, PlayerPosition playerPosition)
	{
		this.name = name;
		this.location = location;
		this.playerPosition = playerPosition;
	}

	public String getName()
	{
		return this.name;
	}

	public PlayerPosition getPlayerPosition()
	{
		return this.playerPosition;
	}

	public Location getLocation()
	{
		return this.location;
	}
}
