package jef.core.physics.ball;

import jef.core.BallUtils;
import jef.core.units.AngularVelocity;
import jef.core.units.LinearVelocity;

public class AngularImpact
{
	// index is incident angle / 10.
	// value is rad/s
	private static double[] verticalNoSpinAngularVelocity =
	{ -0, -6, -12, -14, -16, -16, -14, -12, -6, 0, 6, 12, 14, 16, 16, 14, 12, 6, -0 };

	// oblique angle is 68 degrees
	// index is incident angle / 10.
	// value is rad/s
	private static double[] obliqueNoSpinAngularVelocity =
	{ -10, -12, -15, -18, -23, -24, -23, -18, -15, -10, -5, -0, 5, 7, 7, 5, 3, -3, -10 };

	// oblique angle is 70 degrees
	// av is -17.5
	// index is incident angle / 10.
	// value is rad/s
	private static double[] obliqueBackspinAngularVelocity =
	{ .3, -0, -8, -15, -17, -17, -15, -12, -8, -3, -0, 4, 10, 13, 14, 14, 12, 7, 3 };

	// oblique angle is 40 degrees
	// av is 17.0
	// index is incident angle / 10.
	// value is w2 / w1 rad/s
	private static double[] obliqueTopspinAngularVelocity =
	{ 1.7 * -17, 2.1 * -17, 2.6 * -17, 2.7 * -17, 2.9 * -17, 3.2 * -17, 3.1 * -17, 3.0 * -17, 2.8 * -17, 2.6 * -17,
			2.4 * -17, 1.8 * -17, 1.4 * -17, 1.0 * -17, 1.2 * -17, 1.5 * -17, 1.7 * -17, 2.1 * -17 };

	public static AngularVelocity calculateAVAdjustment(final AngularVelocity av, final LinearVelocity lv)
	{
		final double newVelocity = AngularImpact.calculate(av, lv);
		return new AngularVelocity(av.getCurrentAngleInRadians(), newVelocity);
	}

	private static double[] avValuesForImpactAngle(final double impactAngleInDegrees)
	{
		final double[] values = new double[19];

		for (int i = 0; i < values.length; i++)
		{
			double verticalNoSpin = indexValue(i * 10, verticalNoSpinAngularVelocity);
			double obliqueNoSpin = indexValue(i * 10, obliqueNoSpinAngularVelocity);

			values[i] = verticalNoSpin + (verticalNoSpin - obliqueNoSpin) / 22 * (90 - impactAngleInDegrees);
		}

		return values;
	}

	private static double calculate(final AngularVelocity av, final LinearVelocity lv)
	{
		final double psi = Math.toDegrees(av.getCurrentAngleInRadians());
		double theta = Math.toDegrees(lv.calculateXZAngle() + Math.PI);

		final double[] avValues = AngularImpact.avValuesForImpactAngle(theta);
		final double indexValue = AngularImpact.indexValue(psi, avValues);

		if (BallUtils.hasBackSpin(av, lv))
			return (indexValue + AngularImpact.indexValue(psi, AngularImpact.obliqueBackspinAngularVelocity)) / 2.0;

		if (BallUtils.hasTopSpin(av, lv))
			return (indexValue + AngularImpact.indexValue(psi, AngularImpact.obliqueTopspinAngularVelocity)) / 2.0;

		if (lv.getXYSpeed() == 0)
		{
			final double[] avValuesForAngle = AngularImpact.verticalNoSpinAngularVelocity;
			return AngularImpact.indexValue(psi, avValuesForAngle);
		}

		return (indexValue + AngularImpact.indexValue(psi, AngularImpact.obliqueNoSpinAngularVelocity)) / 2.0;
	}

	private static double indexValue(double rotationAngleInDegrees, final double[] values)
	{
		if (rotationAngleInDegrees < 0)
			rotationAngleInDegrees = rotationAngleInDegrees * -1;

		assert (rotationAngleInDegrees >= 0) && (rotationAngleInDegrees <= 180);

		int index = (int) Math.floor(rotationAngleInDegrees / 10);
		if ((index + 1) >= values.length)
			index = index - 1;

		final double low = values[index];
		final double high = values[index + 1];
		return low + (((high - low) * (rotationAngleInDegrees - (10 * index))) / 10);
	}
}
