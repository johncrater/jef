package jef.actions.pathfinding;

import java.util.List;

import jef.actions.pathfinding.blocking.BlockerPathfinder;
import jef.actions.pathfinding.defenders.DefenderPathfinder;
import jef.actions.pathfinding.runners.RunnerPathfinder;
import jef.core.Direction;
import jef.core.Location;
import jef.core.Player;
import jef.core.movement.player.Path;

public abstract class AbstractPathfinder implements Pathfinder
{
	private Player player;
	private Path path;
	private Direction direction;
	private PlayerStepsCalculator stepCalculator;

	public AbstractPathfinder(Player player, Direction direction)
	{
		this.player = player;
		this.direction = direction;
	}

	@Override
	public void reset()
	{
		path = null;
		stepCalculator = null;
	}

	@Override
	public Player getPlayer()
	{
		return this.player;
	}

	public void setPath(Path path)
	{
		this.path = path;
	}

	@Override
	public Path getPath()
	{
		return path;
	}

	protected boolean calculateSteps(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders, List<? extends BlockerPathfinder> blockers, long deltaNanos)
	{
		long nanos = System.nanoTime();
		
		this.stepCalculator = new PlayerStepsCalculator(getPlayer());
	
//		while (System.nanoTime() - nanos < deltaNanos)
		{
			boolean ret = this.stepCalculator.calculate(runner, defenders, blockers, deltaNanos - (System.nanoTime() - nanos));
			if (ret)
				return ret;
		}
		
		return false;
	}

	@Override
	public List<Location> getSteps()
	{
		if (this.stepCalculator == null)
			return null;
		
		return this.stepCalculator.getSteps();
	}

	
	@Override
	public Direction getDirection()
	{
		return direction;
	}

	@Override
	public String toString()
	{
		return this.player.toString();
	}
}
