package jef.actions.pathfinding;

import java.util.ArrayList;
import java.util.List;

import jef.actions.pathfinding.blocking.BlockerPathfinder;
import jef.actions.pathfinding.defenders.DefenderPathfinder;
import jef.actions.pathfinding.runners.RunnerPathfinder;
import jef.core.Location;
import jef.core.Performance;
import jef.core.Player;
import jef.core.movement.player.AdvancedSteering;
import jef.core.movement.player.PlayerTracker;
import jef.core.movement.player.Steering;

public class PlayerStepsCalculator implements IterativeCalculation
{
	private final ArrayList<Location> steps = new ArrayList<>();
	private PlayerTracker tracker;
	private final Player player;
	private final int options;

	public PlayerStepsCalculator(Player player)
	{
		super();
		this.player = player;
		options = AdvancedSteering.USE_ALL;
	}

	public PlayerStepsCalculator(Player player, int options)
	{
		super();
		this.player = player;
		this.options = options;
	}

	public List<Location> getSteps()
	{
		return this.steps;
	}

	public boolean calculate(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders,
			List<? extends BlockerPathfinder> blockers, long deltaNanos)
	{
		long nanos = System.nanoTime();

		if (tracker == null)
			tracker = new PlayerTracker(player, Performance.frameInterval);

		Steering steering = Steering.getInstance(options);
		boolean ret = false;

//		while (System.nanoTime() - nanos < deltaNanos && ret == false)
		while (ret == false)
		{
//			System.out.println(tracker.getLoc());
			ret = steering.next(tracker);
			tracker.advance();
			steps.add(tracker.getLoc());
		}

		return ret;
	}

}
