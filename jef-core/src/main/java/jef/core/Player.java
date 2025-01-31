package jef.core;

import jef.core.movement.AngularVelocity;
import jef.core.movement.LinearVelocity;
import jef.core.movement.Location;
import jef.core.movement.Posture;
import jef.core.movement.player.Path;
import jef.core.movement.player.SpeedMatrix;

public interface Player extends PlayerInfo
{
	public enum DecelerationRate
	{
		MAXIMUM(Conversions.metersToYards(-6)),
		RAPID(Conversions.metersToYards(-5)),
		NORMAL(Conversions.metersToYards(-4)),
		LEISURELY(Conversions.metersToYards(-3)),
		SLOW(Conversions.metersToYards(-2)), 
		MINIMAL(Conversions.metersToYards(-1)), 
		NONE(Conversions.metersToYards(0));

		private double rate;

		private DecelerationRate(double rate)
		{
			this.rate = rate;
		}

		public double getRate()
		{
			return this.rate;
		}

	}

	// turning speed in milliseconds for changing orientation. A total guess 180
	// degree turn in .25 seconds
	public static final double MAXIMUM_ANGULAR_VELOCITY = 180 / .25;

	// suspect players are faster. But this is a general idea
	public static final double VISUAL_REACTION_TIME = .200;
	public static final double AUDITORY_REACTION_TIME = .150;

	public static final double SIZE = 1.0;

	// Grab him
	public static final double PLAYER_TACKLE_RADIUS_LOW_ODDS = SIZE / 2;

	// Arm Tackle
	public static final double PLAYER_TACKLE_RADIUS_MEDIUM_ODDS = SIZE / 3;

	// slam into him
	public static final double PLAYER_TACKLE_RADIUS_HIGH_ODDS = SIZE / 4;

	// hold him off
	public static final double PLAYER_BLOCKING_RANGE_MAXIMUM = SIZE * 3;

	// push him
	public static final double PLAYER_BLOCKING_RANGE_NORMAL = SIZE * 2;

	// slam into him
	public static final double PLAYER_BLOCKING_RANGE_MINIMAL = SIZE;

	// standard distance to run with the runner while blocking
	public static final double PLAYER_STANDARD_BLOCKING_DISTANCE = 3;

	public static final double PLAYER_CATCH_BALL_RADIUS_LOW_ODDS = SIZE * 2;
	public static final double PLAYER_CATCH_BALL_RADIUS_MEDIUM_ODDS = SIZE;
	public static final double PLAYER_CATCH_BALL_RADIUS_HIGH_ODDS = SIZE / 2;

	public static final double PLAYER_CATCH_BALL_IDEAL_HEIGHT = 1.0f;

	// suspect players are faster. But this is a general idea
	public static final double PLAYER_AVG_HUMAN_VISUAL_REACTION_TIME = .200f;
	public static final double PLAYER_AVG_HUMAN_AUDITORY_REACTION_TIME = .150f;

	public PlayerRatings getRatings();

	public AngularVelocity getAV();

	public void setAV(AngularVelocity angularVelocity);

	public Location getLoc();

	public void setLoc(Location location);

	public LinearVelocity getLV();

	public void setLV(LinearVelocity lv);

	public SpeedMatrix getSpeedMatrix();

	public double getMaxSpeed();

	public void setSpeedMatrix(SpeedMatrix matrix);

	public Path getPath();

	public void setPath(Path path);

	public Posture getPosture();

	public void setPosture(Posture posture);

	public double getAccelerationCoefficient();

	public void setAccelerationCoefficient(double coefficient);

	public PlayerPosition getCurrentPosition();

	public void setCurrentPosition(PlayerPosition pos);
}