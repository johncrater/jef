package jef.core.steering;

public interface Steerable extends Moveable
{
	public double getMaxSpeed();
	public Path getPath();
}
