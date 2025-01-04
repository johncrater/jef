package jef.core.steering;

import jef.core.Location;

public interface Steerable
{
	public double getMaxSpeed();
	public double getDesiredSpeed();
	public double getTurningSpeed();
	public Path getPath();
}
