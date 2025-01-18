package jef.core.pathfinding;

import jef.core.Conversions;
import jef.core.Player;

public class DefenderAssessment
{
	public enum Quadrant
	{
		AHEAD, BEHIND, LEFT, RIGHT;

		public static Quadrant getFromAngle(final double a, Direction direction)
		{
			final var aInDegrees = Conversions.normalizeAngle(a);
			if ((aInDegrees > Math.PI / 4) && (aInDegrees <= Math.PI * 3 / 4))
				return direction == Direction.west ? RIGHT : LEFT;
			if (((aInDegrees > Math.PI * 3 / 4) && (aInDegrees < Math.PI)) || ((aInDegrees >= -Math.PI) && (aInDegrees <= -Math.PI * 3 / 4)))
				return direction == Direction.west ? AHEAD : BEHIND;
			else if ((aInDegrees > -Math.PI * 3 / 4) && (aInDegrees < -Math.PI / 4))
				return direction == Direction.west ? LEFT : RIGHT;
			else
				return direction == Direction.west ? LEFT : AHEAD;
		}
	}

	private final Player defender;
	private final double lvDistance;
	private final Quadrant quadrant;

	public DefenderAssessment(final Player defender, final double lvDistance, final Quadrant quadrant)
	{
		this.defender = defender;
		this.lvDistance = lvDistance;
		this.quadrant = quadrant;
	}

	public Player getDefender()
	{
		return this.defender;
	}

	public Quadrant getQuadrant()
	{
		return this.quadrant;
	}

	public double getLVDistance()
	{
		return this.lvDistance;
	}

	public double threatLevel()
	{
		return -1 * this.lvDistance;
	}

	@Override
	public String toString()
	{
		return String.format("Threat: %s (%.4f)", this.defender, this.lvDistance);
	}
}
