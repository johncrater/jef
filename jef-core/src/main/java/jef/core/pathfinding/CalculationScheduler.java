package jef.core.pathfinding;

import java.util.ArrayList;
import java.util.List;

import jef.core.pathfinding.blocking.BlockerPathfinder;
import jef.core.pathfinding.defenders.DefenderPathfinder;
import jef.core.pathfinding.runners.RunnerPathfinder;

public class CalculationScheduler
{
	private List<IterativeCalculation> calcs = new ArrayList<>();
	
	public CalculationScheduler()
	{
	}

	public void addCalculation(IterativeCalculation calc)
	{
		this.addCalculation(calc);
	}
	
	public double calculate(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders, List<? extends BlockerPathfinder> blockers, double time)
	{
		if (calcs.size() == 0)
			return time;

		// while there is more than 10 nanosecond
		while (time > 10.0 / 1000000000.0)
		{
			double timeAllotment = time / calcs.size();
			for (IterativeCalculation calc : new ArrayList<>(calcs))
			{
				calc.addTime(timeAllotment);
				boolean isComplete = calc.calculate(runner, defenders, blockers);
				if (isComplete)
					calcs.remove(calc);
				
				time -= timeAllotment;
				time += calc.getTimeRemaining();
			}
		}
		
		return time;
	}
}
