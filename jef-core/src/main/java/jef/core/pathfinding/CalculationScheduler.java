package jef.core.pathfinding;

import java.util.ArrayList;
import java.util.List;

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
	
	public double calculate(double time)
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
				boolean isComplete = calc.calculate();
				if (isComplete)
					calcs.remove(calc);
				
				time -= timeAllotment;
				time += calc.getTimeRemaining();
			}
		}
		
		return time;
	}
}
