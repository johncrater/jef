package jef.core.physics.ball;

import java.util.Arrays;

import jef.core.AngularVelocity;
import jef.core.BallUtils;
import jef.core.LinearVelocity;

public class LinearImpact extends IndexedCalculator
{
	private static final double coefficientOfRestitutionMin = .75;
	private static final double coefficientOfRestitutionMax = .82;

	public LinearImpact()
	{
		// index is incident angle / 10.
		// vertical angle is 90
		// value is y / z cot(theta2)
		verticalNoSpin = new double[]
		{ 90, 76, 64, 59, 53, 52, 56, 61, 73, 90, 107, 119, 124, 128, 127, 121, 116, 104, 90 };

		// oblique angle is 68 degrees
		// index is incident angle / 10.
		// value is 68 / new angle
//		private double [] obliqueNoSpinLinearVelocityRatios = 		{.5, .7, .9, 1.2, 1.6, 1.7, 1.6, 1.4, 1.2, .5, .2, -.1, -.6, -.9, -.7, -.6, -.2, .5};

		// oblique angle is 68 degrees
		// index is incident angle / 10.
		// value is theta2
		obliqueNoSpin = new double[]
		{ 68, 62, 58, 40, 35, 28, 40, 45, 55, 68, 82, 92, 102, 106, 109, 104, 100, 87, 68 };

		// oblique angle is 70 degrees
		// av is -17.5
		// index is incident angle / 10.
		// value is y / z
//		private double [] obliqueBackspinLinearVelocityRatios = 	{0, .3, .8, 1.4, 1.5, 1.4, 1.3, .7, .4, 0, -.4, -.7, -1.0, -1.1, -1.1, -1.0, -.7, -.4, 0};
		// oblique angle is 70 degrees
		// av is -17.5
		// index is incident angle / 10.
		// value is theta2
		obliqueBackspin = new double[]
		{ 70, 82, 94, 110, 110, 107, 100, 98, 88, 70, 53, 50, 42, 40, 40, 42, 48, 60, 70 };

		// oblique angle is 40 degrees
		// av is 17.0
		// index is incident angle / 10.
		// value is y / z
//		private double [] obliqueTopspinLinearVelocityRatios = 		{};
		// oblique angle is 40 degrees
		// av is -17.5
		// index is incident angle / 10.
		// value is theta2
		obliqueTopspin = new double[]
		{ 40, 60, 74, 90, 90, 90, 85, 78, 70, 65, 55, 40, 35, 20, 15, 12, 15, 20, 40 };
	}

	public LinearVelocity calculateLVAdjustment(final AngularVelocity av, final LinearVelocity lv)
	{
		double reboundAngle = Math.toRadians(calculate(av, lv));
		double cor = calculateCOR(av);
		return lv.newFrom(reboundAngle, null, lv.getSpeed() * cor);
	}

	private double calculateCOR(final AngularVelocity av)
	{
		return Math.max(coefficientOfRestitutionMin,
				(Math.abs(Math.cos(av.getOrientation()))
						* (coefficientOfRestitutionMax - coefficientOfRestitutionMin))
						+ coefficientOfRestitutionMin);
	}

}
