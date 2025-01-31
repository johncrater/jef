package jef.core.pathfinding;

import java.util.ArrayList;
import java.util.List;

import jef.core.Player;
import jef.core.movement.Location;
import jef.core.movement.player.PlayerTracker;
import jef.core.movement.player.Steerable;
import jef.core.movement.player.Steering;

public class PlayerStepsCalculator extends AbstractIterativeCalculation
{
	private final ArrayList<Location> steps = new ArrayList<>();
	private final PlayerTracker tracker;
	private final int options;
	
	public PlayerStepsCalculator(Player player, double deltaTime)
	{
		super();
		tracker = new PlayerTracker(player, deltaTime);
		options = Steering.USE_ALL;
	}

	public PlayerStepsCalculator(Player player, double deltaTime, int options)
	{
		super();
		tracker = new PlayerTracker(player, deltaTime);
		this.options = options;
	}

	public List<Location> getSteps()
	{
		return this.steps;
	}
	
	@Override
	public boolean calculate()
	{
		long nanos = System.nanoTime();
		
		Steering steering = new Steering(options);
		boolean ret = false;
		
		while (getTimeRemaining() > 0 && ret == false)
		{
			 ret = steering.next(tracker);
			 tracker.advance();
			 steps.add(tracker.getLoc());
//			 this.useTime(System.nanoTime() - nanos);
			 nanos = System.nanoTime();
		}
		
		return ret;
	}

}
