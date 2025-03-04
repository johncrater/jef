package jef.actions.pathfinding.blocking;

import java.util.List;

import jef.actions.pathfinding.IterativeCalculation;
import jef.actions.pathfinding.Pathfinder;
import jef.actions.pathfinding.defenders.DefenderPathfinder;
import jef.actions.pathfinding.runners.RunnerPathfinder;
import jef.core.Player;

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
