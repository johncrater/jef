package jef.actions.pathfinding;

import java.util.List;

import jef.actions.pathfinding.blocking.BlockerPathfinder;
import jef.actions.pathfinding.defenders.DefenderPathfinder;
import jef.actions.pathfinding.runners.RunnerPathfinder;
import jef.core.Direction;
import jef.core.Location;
import jef.core.PlayerState;
import jef.core.movement.player.Path;

public abstract class AbstractPathfinder implements Pathfinder
{
	private PlayerState playerState;
	private Direction direction;
	private PlayerStepsCalculator stepCalculator;
	private Path path;

	public AbstractPathfinder(PlayerState playerState, Direction direction)
	{
		this.playerState = playerState;
		this.direction = direction;
	}

	public Path getPath()
	{
		return this.path;
	}

	public void setPath(Path path)
	{
		this.path = path;
	}

	@Override
	public PlayerState getPlayerState()
	{
		return this.playerState;
	}

	protected boolean calculateSteps(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders, List<? extends BlockerPathfinder> blockers, long deltaNanos)
	{
		long nanos = System.nanoTime();
		
		this.stepCalculator = new PlayerStepsCalculator(getPlayerState());
	
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
		return this.playerState.toString();
	}
}
