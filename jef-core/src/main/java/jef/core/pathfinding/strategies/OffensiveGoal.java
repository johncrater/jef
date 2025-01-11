package jef.core.pathfinding.strategies;

public class OffensiveGoal
{
	public enum OutOfBounds
	{
		stayInBounds, goOutOfBounds, doesNotmatter
	}
	
	private OutOfBounds outOfBounds;
	private double yardLine;
	
	public OffensiveGoal(double yardLine, OutOfBounds outOfBounds)
	{
		super();
		this.yardLine = yardLine;
		this.outOfBounds = outOfBounds;
	}

	public OutOfBounds getOutOfBounds()
	{
		return this.outOfBounds;
	}

	public double getYardLine()
	{
		return this.yardLine;
	}


}
