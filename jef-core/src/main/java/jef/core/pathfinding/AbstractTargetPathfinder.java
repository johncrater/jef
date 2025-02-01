package jef.core.pathfinding;

import java.util.List;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.core.Player;
import jef.core.events.Messages;
import jef.core.geometry.LineSegment;
import jef.core.movement.Location;
import jef.core.pathfinding.runners.TargetPathfinder;

public abstract class AbstractTargetPathfinder extends AbstractPathfinder implements TargetPathfinder
{
	private Direction direction;
	private PlayerStepsCalculator stepCalculator;
	private double deltaTime;

	public AbstractTargetPathfinder(Player player, Direction direction, double deltaTime)
	{
		super(player);
		
		this.direction = direction;
		this.deltaTime = deltaTime;
	}

	@Override
	public void reset()
	{
		stepCalculator = null;
	}

	protected boolean calculateSteps()
	{
		this.stepCalculator = new PlayerStepsCalculator(getPlayer(), deltaTime);
	
		MessageManager.getInstance().dispatchMessage(Messages.drawRunnerDestination, getPath().getDestination());
		MessageManager.getInstance().dispatchMessage(Messages.drawRunnerPath, new LineSegment(getPlayer().getLoc(), getPath().getDestination()));
	
		this.stepCalculator.setTimeRemaining(getTimeRemaining());
		while (this.stepCalculator.getTimeRemaining() > 0)
		{
			boolean ret = this.stepCalculator.calculate();
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
