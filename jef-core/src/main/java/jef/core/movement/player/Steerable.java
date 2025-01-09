package jef.core.movement.player;

import jef.core.movement.Moveable;
import jef.core.movement.Posture;

public interface Steerable extends Moveable
{
	public enum Type {sprint, run, jog, walk, stop};
	
	public double getSpeed(Type type);
	public double getMaxSpeed();
	public double getDesiredSpeed();
	public Path getPath();
	public void setPath(Path path);
	public Posture getPosture();
	public void setPosture(Posture posture);
	public double getAccelerationCoefficient();
}
