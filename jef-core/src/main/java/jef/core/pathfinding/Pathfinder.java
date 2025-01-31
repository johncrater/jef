package jef.core.pathfinding;

import jef.core.movement.player.Path;

public interface Pathfinder extends IterativeCalculation
{
	public Path getPath();
	public void reset();
}
