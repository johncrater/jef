package jef.actions.pathfinding;

public class DefenderAssessment
{
	private final Pathfinder defender;
	private final double lvDistance;

	public DefenderAssessment(final Pathfinder defender, final double lvDistance)
	{
		this.defender = defender;
		this.lvDistance = lvDistance;
	}

	public Pathfinder getDefender()
	{
		return this.defender;
	}

	public double getLVDistance()
	{
		return this.lvDistance;
	}

	public double threatLevel()
	{
		return this.lvDistance;
	}

	@Override
	public String toString()
	{
		return String.format("Threat: %s (%.4f)", this.defender, this.lvDistance);
	}
}
