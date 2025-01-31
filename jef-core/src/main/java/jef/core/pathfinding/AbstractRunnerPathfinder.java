package jef.core.pathfinding;

import java.util.List;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.core.Player;
import jef.core.events.Messages;
import jef.core.geometry.LineSegment;
import jef.core.movement.Location;

public abstract class AbstractRunnerPathfinder extends AbstractPathfinder implements RunnerPathfinder
{
	private Direction direction;
	private PlayerStepsCalculator stepCalculator;
	private double deltaTime;
	
	public AbstractRunnerPathfinder(Player runner, Direction direction, double deltaTime)
	{
		super(runner);
		this.direction = direction;
		this.deltaTime = deltaTime;
	}

	public double getDeltaTime()
	{
		return this.deltaTime;
	}

	@Override
	public Direction getDirection()
	{
		return direction;
	}

	protected boolean calculateSteps()
	{
		this.stepCalculator = new PlayerStepsCalculator(this.getPlayer(), deltaTime);

		MessageManager.getInstance().dispatchMessage(Messages.drawRunnerDestination, getPath().getDestination());
		MessageManager.getInstance().dispatchMessage(Messages.drawRunnerPath, new LineSegment(getPlayer().getLoc(), getPath().getDestination()));

		this.stepCalculator.setTimeRemaining(getTimeRemaining());
		while (this.stepCalculator.getTimeRemaining() > 0)
		{
			boolean ret = this.stepCalculator.calculate();
			this.setTimeRemaining(this.stepCalculator.getTimeRemaining());
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
}
