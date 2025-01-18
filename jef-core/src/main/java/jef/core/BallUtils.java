package jef.core;

import jef.core.movement.AngularVelocity;
import jef.core.movement.LinearVelocity;

public class BallUtils
{
	public static boolean hasTopSpin(AngularVelocity av, LinearVelocity lv)
	{
		return lv.movingRight() && av.isRotatingClockwise() || lv.movingLeft() && av.isRotatingCounterClockwise();
	}

	public static boolean hasBackSpin(AngularVelocity av, LinearVelocity lv)
	{
		return lv.movingRight() && av.isRotatingCounterClockwise() || lv.movingLeft() && av.isRotatingClockwise();
	}

	public static boolean isForwardAngle(AngularVelocity av, LinearVelocity lv)
	{
		return (lv.movingRight()
				&& ((av.getOrientation() > -Math.PI / 2 && av.getOrientation() < Math.PI / 2)
						|| (lv.movingLeft() && ((av.getOrientation() > Math.PI / 2
								&& av.getOrientation() < -Math.PI / 2)))));
	}

	public static boolean isBackwardAngle(AngularVelocity av, LinearVelocity lv)
	{
		return isForwardAngle(av, lv) == false && av.getRotation() != 0;
	}

}
