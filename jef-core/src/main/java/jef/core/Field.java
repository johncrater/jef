package jef.core;

import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.synerset.unitility.unitsystem.common.Distance;

import jef.core.movement.DUnits;
import jef.core.movement.DefaultLocation;
import jef.core.movement.LinearVelocity;
import jef.core.movement.Location;

public class Field
{
	// field constants in inches/pixels
	public static final float FIELD_BORDER_WIDTH = 2.5f;
	public static final float FIELD_END_ZONE_DEPTH = 10f;
	public static final float FIELD_PLAYABLE_LENGTH = 120f;
	public static final float FIELD_PLAYABLE_WIDTH = 162.0f / 3.0f; // so it divides evenly

	// Field constants
	public static final float FIELD_LINE_WIDTH = 3f / 36.0f;

	// pre 1972 hash marks
	public static final float FIELD_HASH_MARK_INDENT = (float) Distance.of(20, DUnits.YARD)
			.getInUnit(DUnits.YARD);

	// post 1972 hash marks
//	public static final float FIELD_HASH_MARK_INDENT = (float) Distance.ofFeet(70).plus(Distance.ofInches(9))
//			.getInUnit(DUnits.YARD);

	public static final float FIELD_HASH_MARK_LENGTH = 1f;
	public static final float FIELD_YARDLINE_DISTANCE_FROM_SIDELINE = 9f;
	public static final float FIELD_TOTAL_LENGTH = FIELD_PLAYABLE_LENGTH
			+ (2f * FIELD_BORDER_WIDTH);
	public static final float FIELD_TOTAL_WIDTH = FIELD_PLAYABLE_WIDTH + (2f * FIELD_BORDER_WIDTH);
	public static final float FIELD_NEUTRAL_ZONE_LENGTH = 11f / 36.0f;

	public static final float FIELD_GOAL_POST_DIAMETER = .5f; // 3
	public static final float FIELD_GOAL_POST_HEIGHT = 10f / 3f;
	public static final float FIELD_GOAL_POST_WIDTH = 18.5f / 3f;
	public static final float FIELD_GOAL_POST_INSET = 6.5f / 3f;
	public static final float FIELD_GOAL_POST_UPRIGHTS = 35f / 3f;

	// pre 1974
	public static final float FIELD_GOAL_POST_DEPTH = 0;

	// pos 1974
	// public static final float FIELD_GOAL_POST_DEPTH = 10;

	// Quantising, comparing and rounding
	public static final float FIELD_LOCATION_EPSILON_IN_INCHES = .25f;
	public static final float FIELD_LOCATION_EPSILON_IN_YARDS = .25f / 36.0f;

	public static final float STADIUM_TOTAL_WIDTH = Math.round(FIELD_TOTAL_WIDTH * 1.5f);
	public static final float STADIUM_TOTAL_LENGTH = Math.round(FIELD_TOTAL_LENGTH * 1.5f);
	public static final float STADIUM_ORIGIN_X = -Math.round(FIELD_TOTAL_WIDTH * .25f);
	public static final float STADIUM_ORIGIN_Y = -Math.round(FIELD_TOTAL_LENGTH * .25f);

	public static Location midfield()
	{
		return new DefaultLocation(FIELD_TOTAL_LENGTH / 2, FIELD_TOTAL_WIDTH / 2, 0.0);
	}
	
	public static final Plane thePlane = new Plane(new Vector3D(0, 0, 1).normalize(), LinearVelocity.EPSILON);
}
