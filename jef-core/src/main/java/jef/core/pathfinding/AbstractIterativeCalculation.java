package jef.core.pathfinding;

public abstract class AbstractIterativeCalculation implements IterativeCalculation
{
	private double timeRemaining;

	public AbstractIterativeCalculation()
	{
	}

	@Override
	public void addTime(double time)
	{
		timeRemaining += time;
	}

	protected void setTimeRemaining(double time)
	{
		this.timeRemaining = time;
	}
	
	@Override
	public double getTimeRemaining()
	{
		return timeRemaining;
	}

	protected void useTime(long nanos)
	{
		this.timeRemaining -= nanos / 1000000000.0;
	}

}
