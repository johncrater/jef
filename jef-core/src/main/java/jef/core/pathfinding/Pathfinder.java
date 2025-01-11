package jef.core.pathfinding;

import jef.core.movement.player.Path;

public interface Pathfinder
{
	public Path findPath();
	public Path findPath(double maximumTimeToSpend);
}
