package jef.core.pathfinding;

import java.util.List;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.core.Player;
import jef.core.events.Messages;
import jef.core.geometry.LineSegment;
import jef.core.movement.Location;
import jef.core.movement.player.Path;
import jef.core.pathfinding.blocking.BlockerPathfinder;
import jef.core.pathfinding.defenders.DefenderPathfinder;
import jef.core.pathfinding.runners.RunnerPathfinder;

public abstract class AbstractPathfinder implements Pathfinder
{
	private Player player;
	private Path path;
	private double timeRemaining;
	private Direction direction;
	private PlayerStepsCalculator stepCalculator;
	private double deltaTime;

	public AbstractPathfinder(Player player, Direction direction, double deltaTime)
	{
		this.player = player;
		this.direction = direction;
		this.deltaTime = deltaTime;
	}

	@Override
	public void reset()
	{
		timeRemaining = 0;
		path = null;
		stepCalculator = null;
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

	public void setTimeRemaining(double timeRemaining)
	{
		this.timeRemaining = timeRemaining;
	}
	
	public void setPath(Path path)
	{
		this.path = path;
	}

	public void useTime(long nanos)
	{
		this.timeRemaining -= nanos / 1000000000.0;
	}

	@Override
	public Path getPath()
	{
		return path;
	}

	protected boolean calculateSteps(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders, List<? extends BlockerPathfinder> blockers)
	{
		this.stepCalculator = new PlayerStepsCalculator(getPlayer(), deltaTime);
	
		MessageManager.getInstance().dispatchMessage(Messages.drawRunnerDestination, getPath().getDestination());
		MessageManager.getInstance().dispatchMessage(Messages.drawRunnerPath, new LineSegment(getPlayer().getLoc(), getPath().getDestination()));
	
		this.stepCalculator.setTimeRemaining(getTimeRemaining());
		while (this.stepCalculator.getTimeRemaining() > 0)
		{
			boolean ret = this.stepCalculator.calculate(runner, defenders, blockers);
			setTimeRemaining(this.stepCalculator.getTimeRemaining());
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
	public double getDeltaTime()
	{
		return deltaTime;
	}

}
