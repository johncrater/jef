package jef.actions.pathfinding;

import java.util.ArrayList;
import java.util.List;

import jef.actions.pathfinding.blocking.BlockerPathfinder;
import jef.actions.pathfinding.defenders.DefenderPathfinder;
import jef.actions.pathfinding.runners.RunnerPathfinder;

public class CalculationScheduler
{
	private List<Pathfinder> calcs = new ArrayList<>();
	
	public CalculationScheduler()
	{
	}

	public void addCalculation(Pathfinder calc)
	{
		this.calcs.add(calc);
	}
	
	public void calculate(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders, List<? extends BlockerPathfinder> blockers, long deltaNanos)
	{
		long nanos = System.nanoTime();
		
//		while (System.nanoTime() - nanos < deltaNanos)
		{
			if (calcs.size() == 0)
				return;

			long timeAllotment = (deltaNanos - (System.nanoTime() - nanos)) / calcs.size();
			for (Pathfinder calc : new ArrayList<>(calcs))
			{
				boolean isComplete = calculate(calc, runner, defenders, blockers, timeAllotment);
				if (isComplete)
					calcs.remove(calc);
			}
		}
	}

	protected boolean calculate(Pathfinder pf, RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders, List<? extends BlockerPathfinder> blockers, long deltaNanos)
	{
		return pf.calculate(runner, defenders, blockers, deltaNanos);
	}
}
