package jef.core.steering;

import jef.core.units.Location;

public interface Steerable extends Moveable
{
	public double getMaxSpeed();
	public double getDesiredSpeed();
	public double getTurningSpeed();
	public Path getPath();
	public Location getDestination();
}
