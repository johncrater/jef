package jef.core.steering;

public interface Steerable extends MoveableObject
{
	public double getMaxSpeed();
	public Path getPath();
}
