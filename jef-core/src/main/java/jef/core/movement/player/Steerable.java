package jef.core.movement.player;

import jef.core.Player;
import jef.core.PlayerPosition;
import jef.core.movement.Moveable;
import jef.core.movement.Posture;

public interface Steerable extends Moveable
{
	public SpeedMatrix getSpeedMatrix();
	public double getMaxSpeed();
	public double getDesiredSpeed();
//	public void setDesiredSpeed(double speed);
//	public void setSpeedMatrix(SpeedMatrix matrix);
	
	public Path getPath();
//	public void setPath(Path path);
	public Posture getPosture();
//	public void setPosture(Posture posture);
	public double getAccelerationCoefficient();
//	public void setAccelerationCoefficient(double coefficient);
	
	public Player getPlayer();
	
	public PlayerPosition getCurrentPosition();
//	public void setCurrentPosition(PlayerPosition pos);
}
