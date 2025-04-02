package jef.pathfinding;

import jef.movement.PlayerId;
import jef.movement.TeamId;
import jef.movement.player.ISpeedMatrix;

public interface IPathfinderPlayer
{
	double VISUAL_REACTION_TIME = .200;
	double SIZE = 1.0;

	public int getWeight();		// pounds, although it is only used in a relative way.
	public double getHeight();  // yards
	public ISpeedMatrix getSpeedMatrix();
	public PlayerId getId();
	public TeamId getTeamId();
}
