package jef.actions.pathfinding;

import java.util.List;

import jef.core.Direction;
import jef.core.Location;
import jef.core.PlayerState;
import jef.core.movement.player.Path;

public interface Pathfinder
{
	public Path getPath();
	public PlayerState getPlayerState();
	public Direction getDirection();
	public boolean calculate(PlayerState runner, List<PlayerState> defenders, List<PlayerState> blockers, long deltaNanos);
}
