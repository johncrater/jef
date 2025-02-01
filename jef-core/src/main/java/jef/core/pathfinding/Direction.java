package jef.core.pathfinding;

public enum Direction
{
	east, west;
	
	public Direction opposite()
	{
		return this == east ? west : east;
	}
}
