package jef.core.movement.player;

import java.util.HashMap;
import java.util.Map;

public class SpeedMatrix
{
	public enum SpeedType {sprint, run, jog, walk, stop};
	
	private final Map<SpeedType, Double> speedMatrix = new HashMap<>();

	public SpeedMatrix()
	{
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
}
