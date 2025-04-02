package jef.movement.player;

public interface ISpeedMatrix
{
	public enum SpeedType
	{
		sprint, run, jog, walk, stop
	};


	public double getSpeed(SpeedType type);
	public void setSpeed(SpeedType type, double speed);
	public double getWalkingSpeed();
	public double getJoggingSpeed();
	public double getRunningSpeed();
	public double getSprintingSpeed();
}