package jef.core.movement;

import jef.core.Conversions;
import jef.core.pathfinding.Direction;

public enum RelativeLocation
{
	FRONT_LEFT, FRONT_RIGHT, LEFT_FRONT, LEFT_REAR, REAR_RIGHT, REAR_LEFT, RIGHT_FRONT, RIGHT_REAR;

	public static RelativeLocation getFromAngle(double a, Direction playerDirection)
	{
		if (playerDirection == Direction.west)
			a -= Math.PI;

		final var aInDegrees = Math.toDegrees(Conversions.normalizeAngle(a));
		if (aInDegrees > 0 && aInDegrees <= 45)
			return FRONT_LEFT;
		else if (aInDegrees > 45 && aInDegrees <= 90)
			return LEFT_FRONT;
		else if (aInDegrees > 90 && aInDegrees <= 135)
			return LEFT_REAR;
		else if (aInDegrees > 135 && aInDegrees <= 180)
			return REAR_LEFT;
		else if (aInDegrees > -180 && aInDegrees <= -135)
			return REAR_RIGHT;
		else if (aInDegrees > -135 && aInDegrees <= -90)
			return RIGHT_REAR;
		else if (aInDegrees > -90 && aInDegrees <= -45)
			return RIGHT_FRONT;
		else if (aInDegrees > -45 && aInDegrees <= 0)
			return FRONT_RIGHT;

		return null;
	}

	public boolean isFront()
	{
		return this == FRONT_RIGHT || this == FRONT_LEFT;
	}

	public boolean isRear()
	{
		return this == REAR_RIGHT || this == REAR_LEFT;
	}

	public boolean isLeft()
	{
		return this == LEFT_FRONT || this == LEFT_REAR;
	}

	public boolean isRight()
	{
		return this == RIGHT_FRONT || this == RIGHT_REAR;
	}

	public boolean isAhead()
	{
		return this.isFront() || this == LEFT_FRONT || this == RIGHT_FRONT;
	}

	public boolean isBehind()
	{
		return this.isRear() || this == LEFT_REAR || this == RIGHT_REAR;
	}
}