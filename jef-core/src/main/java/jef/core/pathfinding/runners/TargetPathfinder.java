package jef.core.pathfinding.runners;

import java.util.List;

import jef.core.Player;
import jef.core.movement.Location;
import jef.core.pathfinding.Direction;
import jef.core.pathfinding.Pathfinder;

public interface TargetPathfinder extends Pathfinder
{
	public List<Location> getSteps();
	public Direction getDirection();
	double getDeltaTime();	
}
