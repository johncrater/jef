package jef.core.movement.player;

public interface Steerable
{
	public double getMaxSpeed();
	public double getDesiredSpeed();
	public double getTurningSpeed();
	public Path getPath();
}
