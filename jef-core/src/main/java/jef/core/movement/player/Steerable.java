package jef.core.movement.player;

import jef.core.movement.Moveable;

public interface Steerable extends Moveable
{
	public double getMaxSpeed();
	public double getDesiredSpeed();
	public Path getPath();
}
