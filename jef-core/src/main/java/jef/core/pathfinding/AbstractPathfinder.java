package jef.core.pathfinding;

import jef.core.movement.player.Path;

public abstract class AbstractPathfinder implements Pathfinder
{
	private Path path;
	private double timeRemaining;

	public AbstractPathfinder()
	{
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void reset()
	{
		timeRemaining = 0;
	}


	@Override
	public void addTime(double time)
	{
		timeRemaining += time;
	}

	@Override
	public double getTimeRemaining()
	{
		return timeRemaining;
	}

	protected void setTimeRemaining(double timeRemaining)
	{
		this.timeRemaining = timeRemaining;
	}
	
	protected void setPath(Path path)
	{
		this.path = path;
	}


	protected void useTime(long nanos)
	{
		this.timeRemaining -= nanos / 1000000000.0;
	}

	@Override
	public Path getPath()
	{
		return path;
	}

	
}
