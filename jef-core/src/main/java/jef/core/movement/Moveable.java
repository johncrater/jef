package jef.core.movement;

import jef.core.AngularVelocity;
import jef.core.LinearVelocity;
import jef.core.Location;

public interface Moveable
{
	public AngularVelocity getAV();
	public Location getLoc();
	public LinearVelocity getLV();
}