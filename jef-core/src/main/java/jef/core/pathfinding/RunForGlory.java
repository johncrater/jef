package jef.core.pathfinding;

import java.util.List;

import jef.core.Field;
import jef.core.Player;
import jef.core.movement.DefaultLocation;
import jef.core.movement.Location;
import jef.core.movement.player.DefaultPath;
import jef.core.movement.player.Steerable;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;

/**
 * Runner heads directly for the nearest part of the end zone
 */
public class RunForGlory extends AbstractPathfinder implements RunnerPathfinder
{
	private Steerable runner;
	private Direction direction;
	private PlayerStepsCalculator stepCalculator;
	
	public RunForGlory(Steerable runner, Direction direction, double deltaTime)
	{
		super();
		this.runner = runner;
		this.direction = direction;
		this.stepCalculator = new PlayerStepsCalculator(runner, deltaTime);
	}

	@Override
	public Direction getDirection()
	{
		return direction;
	}

	@Override
	public Player getRunner()
	{
		return runner.getPlayer();
	}

	@Override
	public boolean calculate()
	{
		if (getPath() == null)
		{
			long nanos = System.nanoTime();
			setPath(new DefaultPath(new Waypoint(new DefaultLocation(Field.yardLine(100, direction), runner.getLoc().getY()),
					runner.getMaxSpeed(), DestinationAction.noStop)));
			useTime(System.nanoTime() - nanos);
		}		

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
		return this.stepCalculator.getSteps();
	}
}
