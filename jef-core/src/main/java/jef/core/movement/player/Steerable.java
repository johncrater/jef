package jef.core.movement.player;

import jef.core.PlayerPosition;
import jef.core.movement.Moveable;
import jef.core.movement.Posture;

public interface Steerable extends Moveable
{
	public SpeedMatrix getSpeedMatrix();
	public double getMaxSpeed();
	public double getDesiredSpeed();
	
	public Path getPath();
	public Posture getPosture();
	public double getAccelerationCoefficient();
	
	public PlayerPosition getCurrentPosition();
}
