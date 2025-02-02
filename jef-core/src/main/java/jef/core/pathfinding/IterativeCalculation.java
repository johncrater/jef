package jef.core.pathfinding;

import java.util.List;

import jef.core.pathfinding.blocking.BlockerPathfinder;
import jef.core.pathfinding.defenders.DefenderPathfinder;
import jef.core.pathfinding.runners.RunnerPathfinder;

public interface IterativeCalculation
{
	public void addTime(double time);
	public double getTimeRemaining();
	public boolean calculate(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders, List<? extends BlockerPathfinder> blockers);
}
