package jef.actions.pathfinding;

import java.util.List;

import jef.core.Direction;
import jef.core.Location;
import jef.core.PlayerState;
import jef.core.movement.player.Path;

public interface Pathfinder extends IterativeCalculation
{
	public Path getPath();
	public PlayerState getPlayerState();
	public List<Location> getSteps();
	public Direction getDirection();
}
