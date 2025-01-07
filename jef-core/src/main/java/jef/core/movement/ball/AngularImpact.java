package jef.core.movement.ball;

import jef.core.BallUtils;
import jef.core.movement.AngularVelocity;
import jef.core.movement.LinearVelocity;

public class AngularImpact extends IndexedCalculator
{
	public AngularImpact()
	{
		// index is incident angle / 10.
		// value is rad/s
		verticalNoSpin = new double []
		{ -0, -6, -12, -14, -16, -16, -14, -12, -6, 0, 6, 12, 14, 16, 16, 14, 12, 6, -0 };

		// oblique angle is 68 degrees
		// index is incident angle / 10.
		// value is rad/s
		obliqueNoSpin = new double []
		{ -10, -12, -15, -18, -23, -24, -23, -18, -15, -10, -5, -0, 5, 7, 7, 5, 3, -3, -10 };

		// oblique angle is 70 degrees
		// av is -17.5
		// index is incident angle / 10.
		// value is rad/s
		obliqueBackspin = new double []
		{ .3, -0, -8, -15, -17, -17, -15, -12, -8, -3, -0, 4, 10, 13, 14, 14, 12, 7, 3 };

		// oblique angle is 40 degrees
		// av is 17.0
		// index is incident angle / 10.
		// value is w2 / w1 rad/s
		obliqueTopspin = new double []
				{ 1.9 * 17, 2.3 * 17, 2.6 * 17, 2.9 * 17, 3.2 * 17, 3.3 * 17, 3.2 * 17, 3.1 * 17, 2.8 * 17, 2.6 * 17, 2.3 * 17,
						1.8 * 17, 1.4 * 17, 1.2 * 17, 0.9 * 17, 0.8 * 17, 1.1 * 17, 1.6 * 17, 1.9 * 17 };

	}
	
	public double calculateAVAdjustment(final AngularVelocity av, final LinearVelocity lv)
	{
		double indexedValue = calculate(av, lv);

		if (BallUtils.hasBackSpin(av, lv))
			return Math.clamp(indexedValue / -17.0 * av.getRotation(), -30, 30);

		if (BallUtils.hasTopSpin(av, lv))
			return Math.clamp(indexedValue / 17.0 * av.getRotation(), -30, 30);
		
		return Math.clamp(indexedValue, -30, 30);
	}

}
