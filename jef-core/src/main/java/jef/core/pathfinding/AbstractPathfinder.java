package jef.core.pathfinding;

import jef.core.Player;
import jef.core.movement.player.Path;

public abstract class AbstractPathfinder implements Pathfinder
{
	private Player player;
	private Path path;
	private double timeRemaining;

	public AbstractPathfinder(Player player)
	{
		this.player = player;
	}

	@Override
	public void reset()
	{
		timeRemaining = 0;
		path = null;
	}

	@Override
	public Player getPlayer()
	{
		return this.player;
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
