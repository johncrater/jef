package jef.core.pathfinding.blocking;

import java.util.List;

import jef.core.Player;
import jef.core.pathfinding.IterativeCalculation;
import jef.core.pathfinding.Pathfinder;
import jef.core.pathfinding.defenders.DefenderPathfinder;
import jef.core.pathfinding.runners.RunnerPathfinder;

public class FollowTheLeader implements IterativeCalculation
{
	private Player runner;
	private Player blocker;
	
	private Pathfinder runnerPathfinder;
	private Pathfinder blockerPathfinder;

	public FollowTheLeader(Player runner, Player blocker)
	{
		super();
		this.runner = runner;
		this.blocker = blocker;
	}

	public Player getRunner()
	{
		return this.runner;
	}

	public Player getBlocker()
	{
		return this.blocker;
	}

	public Pathfinder getRunnerPathfinder()
	{
		return this.runnerPathfinder;
	}
	
	public Pathfinder getBlockerPathfinder()
	{
		return this.blockerPathfinder;
	}

	@Override
	public boolean calculate(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders,
			List<? extends BlockerPathfinder> blockers, long deltaNanos)
	{

		return false;
	}

}
