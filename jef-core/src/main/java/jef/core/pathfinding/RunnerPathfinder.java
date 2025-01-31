package jef.core.pathfinding;

import java.util.List;

import jef.core.movement.Location;

public interface RunnerPathfinder
{
	public List<Location> getSteps();
	public Direction getDirection();
}
