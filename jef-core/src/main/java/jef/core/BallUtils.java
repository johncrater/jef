package jef.core;

import jef.core.units.AngularVelocity;
import jef.core.units.LinearVelocity;

public class BallUtils
{
	public static boolean hasTopSpin(AngularVelocity av, LinearVelocity lv)
	{
		return lv.movingRight() && av.rotatingClockwise() || lv.movingLeft() && av.rotatingCounterClockwise();
	}

	public static boolean hasBackSpin(AngularVelocity av, LinearVelocity lv)
	{
		return lv.movingRight() && av.rotatingCounterClockwise() || lv.movingLeft() && av.rotatingClockwise();
	}

	public static boolean isForwardAngle(AngularVelocity av, LinearVelocity lv)
	{
		return (lv.movingRight()
				&& ((av.getCurrentAngleInRadians() > -Math.PI / 2 && av.getCurrentAngleInRadians() < Math.PI / 2)
						|| (lv.movingLeft() && ((av.getCurrentAngleInRadians() > Math.PI / 2
								&& av.getCurrentAngleInRadians() < -Math.PI / 2)))));
	}

	public static boolean isBackwarAngle(AngularVelocity av, LinearVelocity lv)
	{
		return isForwardAngle(av, lv) == false && av.getRadiansPerSecond() != 0;
	}

}
