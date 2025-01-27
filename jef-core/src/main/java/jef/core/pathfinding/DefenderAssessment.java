package jef.core.pathfinding;

import jef.core.Player;
import jef.core.movement.RelativeLocation;

public class DefenderAssessment
{
	private final Player defender;
	private final double lvDistance;
	private final RelativeLocation relativeLocation;

	public DefenderAssessment(final Player defender, final double lvDistance, final RelativeLocation relativeLocation)
	{
		this.defender = defender;
		this.lvDistance = lvDistance;
		this.relativeLocation = relativeLocation;
	}

	public Player getDefender()
	{
		return this.defender;
	}

	public RelativeLocation getRelativeLocation()
	{
		return this.relativeLocation;
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
