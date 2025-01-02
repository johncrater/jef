package jef.core.physics.ball;

import jef.core.BallUtils;
import jef.core.units.AngularVelocity;
import jef.core.units.LinearVelocity;

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
		{ 1.7 * -17, 2.1 * -17, 2.6 * -17, 2.7 * -17, 2.9 * -17, 3.2 * -17, 3.1 * -17, 3.0 * -17, 2.8 * -17, 2.6 * -17,
				2.4 * -17, 1.8 * -17, 1.4 * -17, 1.0 * -17, 1.2 * -17, 1.5 * -17, 1.7 * -17, 2.1 * -17 };

	}
	
	public AngularVelocity calculateAVAdjustment(final AngularVelocity av, final LinearVelocity lv)
	{
		final double newVelocity = calculate(av, lv);
		return new AngularVelocity(av.getCurrentAngleInRadians(), newVelocity);
	}

}
