package jef.pathfinding;

import jef.movement.player.Path;

public interface IPathfinder<T extends IPathfinderPlayer>
{
	public Path calculatePath();
	public T getPlayer();
}
