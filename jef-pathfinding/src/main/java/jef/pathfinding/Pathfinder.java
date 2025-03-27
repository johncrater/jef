package jef.pathfinding;

import jef.core.Player;
import jef.movement.player.Path;

public interface Pathfinder
{
	public Path calculatePath();
	public Player getPlayer();
}
