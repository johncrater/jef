package jef.actions.pathfinding;

import java.util.ArrayList;
import java.util.List;

import jef.actions.pathfinding.blocking.BlockerPathfinder;
import jef.actions.pathfinding.defenders.DefenderPathfinder;
import jef.actions.pathfinding.runners.RunnerPathfinder;
import jef.core.Location;
import jef.core.Performance;
import jef.core.PlayerState;
import jef.core.movement.player.AdvancedSteering;
import jef.core.movement.player.PlayerTracker;
import jef.core.movement.player.Steering;

public class PlayerStepsCalculator implements IterativeCalculation
{
	private final ArrayList<Location> steps = new ArrayList<>();
	private PlayerTracker tracker;
	private PlayerState playerState;
	private final int options;

	public PlayerStepsCalculator(PlayerState playerState)
	{
		super();
		this.playerState = playerState;
		options = AdvancedSteering.USE_ALL;
	}

	public PlayerStepsCalculator(PlayerState playerState, int options)
	{
		super();
		this.playerState = playerState;
		this.options = options;
	}

	public List<Location> getSteps()
	{
		return this.steps;
	}

	public boolean calculate(PlayerState runner, List<PlayerState> defenders,
			List<PlayerState> blockers, long deltaNanos)
	{
		long nanos = System.nanoTime();

		if (tracker == null)
			tracker = new PlayerTracker(playerState, Performance.frameInterval);

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
