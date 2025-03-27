package jef.geometry;

public enum Direction
{
	east(0), west(Math.PI);
	
	private double angle;
	
	private Direction(double angle)
	{
		this.angle = angle;
	}
	
	public Direction opposite()
	{
		return this == east ? west : east;
	}
	
	public double getAngle()
	{
		return angle;
	}
}
