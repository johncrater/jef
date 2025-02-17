package jef.core.pathfinding;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.core.Performance;
import jef.core.Player;
import jef.core.events.DebugShape;
import jef.core.events.Messages;
import jef.core.movement.Location;
import jef.core.movement.player.PlayerTracker;
import jef.core.movement.player.Steering;
import jef.core.pathfinding.blocking.BlockerPathfinder;
import jef.core.pathfinding.defenders.DefenderPathfinder;
import jef.core.pathfinding.runners.RunnerPathfinder;

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
		options = Steering.USE_ALL;
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

		Steering steering = new Steering(options);
		boolean ret = false;

//		while (System.nanoTime() - nanos < deltaNanos && ret == false)
		while (ret == false)
		{
			System.out.println(tracker.getLoc());
			ret = steering.next(tracker);
			tracker.advance();
			steps.add(tracker.getLoc());
		}

		return ret;
	}

}
