package jef.core;

import jef.core.geometry.LineSegment;
import jef.core.movement.DefaultLocation;
import jef.core.movement.Location;
import jef.core.pathfinding.Direction;

public class Field
{
	// field constants in yards
	public static final double DIM_SIDELINE_WIDTH = 2.5;
	public static final double DIM_END_ZONE_WIDTH = 10;
	public static final double DIM_FIELD_OF_PLAY_LENGTH = 100;
	public static final double DIM_FIELD_OF_PLAY_WIDTH = 162.0f / 3.0f; // so it divides evenly
	public static final double DIM_LINE_WIDTH = 3f / 36.0f;
	public static final double DIM_HASH_MARK_LENGTH = 1f;
	public static final double DIM_YARDLINE_DISTANCE_FROM_SIDELINE = 9f;
	public static final double DIM_HASH_MARK_INDENT = 20;  // pre 1972 hash marks
//	public static final double FIELD_HASH_MARK_INDENT = (double) Distance.ofFeet(70).plus(Distance.ofInches(9))
//			.getInUnit(DUnits.YARD); // post 1972 hash marks
	
	public static final double DIM_TOTAL_LENGTH = DIM_FIELD_OF_PLAY_LENGTH + 2 * DIM_END_ZONE_WIDTH + (2f * DIM_SIDELINE_WIDTH);
	public static final double DIM_TOTAL_WIDTH = DIM_FIELD_OF_PLAY_WIDTH + (2f * DIM_SIDELINE_WIDTH);

	public static final double DIM_STADIUM_TOTAL_WIDTH = Math.round(DIM_TOTAL_WIDTH * 1.5f);
	public static final double DIM_STADIUM_TOTAL_LENGTH = Math.round(DIM_TOTAL_LENGTH * 1.5f);
	public static final double DIM_STADIUM_ORIGIN_X = -Math.round(DIM_TOTAL_WIDTH * .25f);
	public static final double DIM_STADIUM_ORIGIN_Y = -Math.round(DIM_TOTAL_LENGTH * .25f);

	public static final double DIM_FIELD_GOAL_POST_DIAMETER = .5f; // 3
	public static final double DIM_FIELD_GOAL_POST_HEIGHT = 10f / 3f;
	public static final double DIM_FIELD_GOAL_POST_WIDTH = 18.5f / 3f;
	public static final double DIM_FIELD_GOAL_POST_INSET = 6.5f / 3f;
	public static final double DIM_FIELD_GOAL_POST_UPRIGHTS = 35f / 3f;
	public static final double DIM_FIELD_GOAL_POST_DEPTH = 0; 	// pre 1974
//	public static final double DIM_FIELD_GOAL_POST_DEPTH = 10; // pos 1974

	public static final Location FIELD_NW_CORNER = new DefaultLocation(0, 0);
	public static final Location FIELD_NE_CORNER = new DefaultLocation(DIM_TOTAL_LENGTH, 0);
	public static final Location FIELD_SW_CORNER = new DefaultLocation(0, DIM_TOTAL_WIDTH);
	public static final Location FIELD_SE_CORNER = new DefaultLocation(DIM_TOTAL_LENGTH, DIM_TOTAL_WIDTH);
	
	public static final Location PLAYABLE_AREA_NW_CORNER = FIELD_NW_CORNER.add(DIM_SIDELINE_WIDTH, DIM_SIDELINE_WIDTH, 0);
	public static final Location PLAYABLE_AREA_NE_CORNER = FIELD_NE_CORNER.add(-DIM_SIDELINE_WIDTH, DIM_SIDELINE_WIDTH, 0);
	public static final Location PLAYABLE_AREA_SW_CORNER = FIELD_SW_CORNER.add(DIM_SIDELINE_WIDTH, -DIM_SIDELINE_WIDTH, 0);
	public static final Location PLAYABLE_AREA_SE_CORNER = FIELD_SE_CORNER.add(-DIM_SIDELINE_WIDTH, -DIM_SIDELINE_WIDTH, 0);
	
	public static final Location END_ZONE_WEST_NW_CORNER = PLAYABLE_AREA_NW_CORNER;
	public static final Location END_ZONE_WEST_NE_CORNER = PLAYABLE_AREA_NW_CORNER.add(DIM_END_ZONE_WIDTH, 0, 0);
	public static final Location END_ZONE_WEST_SW_CORNER = PLAYABLE_AREA_SW_CORNER;
	public static final Location END_ZONE_WEST_SE_CORNER = PLAYABLE_AREA_SW_CORNER.add(DIM_END_ZONE_WIDTH, 0, 0);
	
	public static final Location END_ZONE_EAST_NW_CORNER = PLAYABLE_AREA_NE_CORNER.add(-DIM_END_ZONE_WIDTH, 0, 0);
	public static final Location END_ZONE_EAST_NE_CORNER = PLAYABLE_AREA_NE_CORNER;
	public static final Location END_ZONE_EAST_SW_CORNER = PLAYABLE_AREA_SE_CORNER.add(-DIM_END_ZONE_WIDTH, 0, 0);
	public static final Location END_ZONE_EAST_SE_CORNER = PLAYABLE_AREA_SE_CORNER;
	
	public static final Location PLAYING_FIELD_NW_CORNER = END_ZONE_WEST_NE_CORNER;
	public static final Location PLAYING_FIELD_NE_CORNER = END_ZONE_EAST_NW_CORNER;
	public static final Location PLAYING_FIELD_SW_CORNER = END_ZONE_WEST_SE_CORNER;
	public static final Location PLAYING_FIELD_SE_CORNER = END_ZONE_EAST_SW_CORNER;

	public static final double MIDFIELD_X = DIM_TOTAL_LENGTH / 2;
	public static final double MIDFIELD_Y = DIM_TOTAL_WIDTH / 2;
	public static final Location MIDFIELD = new DefaultLocation(MIDFIELD_X, MIDFIELD_Y, 0.0);

	public static final LineSegment GOAL_LINE_WEST = new LineSegment(PLAYING_FIELD_NW_CORNER, PLAYING_FIELD_SW_CORNER);
	public static final LineSegment GOAL_LINE_EAST = new LineSegment(PLAYING_FIELD_NE_CORNER, PLAYING_FIELD_SE_CORNER);
	
	public static final LineSegment SIDELINE_SOUTH = new LineSegment(PLAYING_FIELD_SW_CORNER, PLAYING_FIELD_SE_CORNER);
	public static final LineSegment SIDELINE_NORTH = new LineSegment(PLAYING_FIELD_NW_CORNER, PLAYING_FIELD_NE_CORNER);

	public static final LineSegment END_LINE_WEST = new LineSegment(PLAYABLE_AREA_NW_CORNER, PLAYABLE_AREA_SW_CORNER);
	public static final LineSegment END_LINE_EAST = new LineSegment(PLAYABLE_AREA_SW_CORNER, PLAYABLE_AREA_SE_CORNER);

	public static final double WEST_END_ZONE_BACK_X = DIM_SIDELINE_WIDTH;
	public static final double WEST_END_ZONE_X = WEST_END_ZONE_BACK_X + DIM_END_ZONE_WIDTH;
	public static final double EAST_END_ZONE_X = DIM_TOTAL_LENGTH - (DIM_SIDELINE_WIDTH + DIM_END_ZONE_WIDTH);
	public static final double EAST_END_ZONE_BACK_X = DIM_TOTAL_LENGTH - DIM_SIDELINE_WIDTH;
	public static final double NORTH_SIDELINE_Y = DIM_SIDELINE_WIDTH + DIM_FIELD_OF_PLAY_WIDTH;
	public static final double SOUTH_SIDELINE_Y = DIM_SIDELINE_WIDTH;
	
	
	public static final double FIELD_NEUTRAL_ZONE_LENGTH = 11f / 36.0f;

	public static double yardLine(double yardLine, Direction direction)
	{
		double ret = PLAYABLE_AREA_NW_CORNER.getX() + yardLine;
		if (direction == Direction.west)
			ret = PLAYABLE_AREA_NE_CORNER.getX() - ret;

		return ret;
	}


}
