package jef.core;

public enum Direction
{
	east, west;
	
	public Direction opposite()
	{
		return this == east ? west : east;
	}
}
