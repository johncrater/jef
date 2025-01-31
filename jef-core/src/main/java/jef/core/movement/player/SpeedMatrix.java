package jef.core.movement.player;

import java.util.HashMap;
import java.util.Map;

import com.synerset.unitility.unitsystem.common.Velocity;

import jef.core.Conversions;
import jef.core.PlayerPosition;
import jef.core.movement.VUnits;

public class SpeedMatrix
{
	public enum SpeedType
	{
		sprint, run, jog, walk, stop
	};

	private final Map<SpeedType, Double> speedMatrix = new HashMap<>();

	public SpeedMatrix()
	{
	}

	public SpeedMatrix(PlayerPosition pos)
	{
		this(SpeedMatrix.getAdjustedSprintingSpeed(pos), SpeedMatrix.getAdjustedRunningSpeed(pos),
				SpeedMatrix.getAdjustedJoggingSpeed(pos), SpeedMatrix.getRandomWalkingSpeed(pos));
	}

	public SpeedMatrix(double sprintingSpeed, double runningSpeed, double joggingSpeed, double walkingSpeed)
	{
		this.speedMatrix.put(SpeedType.sprint, sprintingSpeed);
		this.speedMatrix.put(SpeedType.run, runningSpeed);
		this.speedMatrix.put(SpeedType.jog, joggingSpeed);
		this.speedMatrix.put(SpeedType.walk, walkingSpeed);
		this.speedMatrix.put(SpeedType.stop, 0.0);
	}

	public SpeedMatrix(SpeedMatrix matrix)
	{
		for (SpeedType type : matrix.speedMatrix.keySet())
			speedMatrix.put(type, matrix.speedMatrix.get(type));
	}

	public double getSpeed(SpeedType type)
	{
		return this.speedMatrix.get(type);
	}

	public void setSpeed(SpeedType type, double speed)
	{
		this.speedMatrix.put(type, speed);
	}

	public double getWalkingSpeed()
	{
		return this.speedMatrix.get(SpeedType.walk);
	}

	public double getJoggingSpeed()
	{
		return this.speedMatrix.get(SpeedType.jog);
	}

	public double getRunningSpeed()
	{
		return this.speedMatrix.get(SpeedType.run);
	}

	public double getSprintingSpeed()
	{
		return this.speedMatrix.get(SpeedType.sprint);
	}

	private static double getAdjustedJoggingSpeed(final PlayerPosition pos)
	{
		// 4-6 is average. Given that players are larger than average, its a guess
		return Conversions.milesPerHourToYardsPerSecond(7.0);
	}

	private static double getAdjustedRunningSpeed(final PlayerPosition pos)
	{
		return getAdjustedSprintingSpeed(pos) * 0.9f;
	}

	private static double getAdjustedSprintingSpeed(final PlayerPosition pos)
	{
		// the 40 yard dash measures acceleration to top speed... sort of
		// it does not measure top speed itself since at the beginning they
		// are moving a 0 y/s.
		// Data I have found says that the top speed of an NFL player is about 21mph.
		// which takes about 4.5 seconds to reach for very fast humans. (4.1 for world
		// class sprinters.
		final var speed = pos.getAverage40YardDashTime();
		final var topV = 21;
		return Conversions.milesPerHourToYardsPerSecond((topV * 4.48f) / speed);
	}

	private static double getRandomWalkingSpeed(final PlayerPosition pos)
	{
		// 3.2 is avergae. Given that players are larger than average, its a guess
		return Conversions.milesPerHourToYardsPerSecond(4.0);
	}
}
