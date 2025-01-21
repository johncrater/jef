package jef.core;

import java.util.List;

import com.synerset.unitility.unitsystem.common.Velocity;

import jef.core.movement.VUnits;
import jef.core.movement.player.Steerable;

public interface Player extends Steerable, PlayerInfo
{
	// deceleration is in YPY^2. It is not a velocity
	public static final double maximumDecelerationRate = (double) Velocity.ofMetersPerSecond(-6).getInUnit(VUnits.YPS);
	public static final double normalDecelerationRate = (double) Velocity.ofMetersPerSecond(-4).getInUnit(VUnits.YPS);
	public static final double slowDecelerationRate = (double) Velocity.ofMetersPerSecond(-2).getInUnit(VUnits.YPS);

	// turning speed in milliseconds for changing orientation. A total guess 180
	// degree turn in .25 seconds
	public static final double maximumAngularVelocity = 180 / .25;

	// suspect players are faster. But this is a general idea
	public static final double visualReactionTime = .200;
	public static final double auditoryReactionTime = .150;

	public static final double size = 1.0;

	// Grab him
	public static final double PLAYER_TACKLE_RADIUS_LOW_ODDS = size / 2;

	// Arm Tackle
	public static final double PLAYER_TACKLE_RADIUS_MEDIUM_ODDS = size / 3;

	// slam into him
	public static final double PLAYER_TACKLE_RADIUS_HIGH_ODDS = size / 4;

	// hold him off
	public static final double PLAYER_BLOCKING_RANGE_MINIMAL = size / 2;

	// push him
	public static final double PLAYER_BLOCKING_RANGE_NORMAL = size / 3;

	// slam into him
	public static final double PLAYER_BLOCKING_RANGE_OPTIMAL = size / 4;

	// standard distance to run with the runner while blocking
	public static final double PLAYER_STANDARD_BLOCKING_DISTANCE = 3;

	public static final double PLAYER_CATCH_BALL_RADIUS_LOW_ODDS = size * 2;
	public static final double PLAYER_CATCH_BALL_RADIUS_MEDIUM_ODDS = size;
	public static final double PLAYER_CATCH_BALL_RADIUS_HIGH_ODDS = size / 2;

	public static final double PLAYER_CATCH_BALL_IDEAL_HEIGHT = 1.0f;

	// suspect players are faster. But this is a general idea
	public static final double PLAYER_AVG_HUMAN_VISUAL_REACTION_TIME = .200f;
	public static final double PLAYER_AVG_HUMAN_AUDITORY_REACTION_TIME = .150f;

	public boolean hasBall();
	public void setHasBall(boolean hasBall);
	
	public PlayerRatings getRatings();
	public PlayerPosition getCurrentPosition();
}