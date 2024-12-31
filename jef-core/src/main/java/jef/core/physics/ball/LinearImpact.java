package jef.core.physics.ball;

import java.util.Arrays;

import com.synerset.unitility.unitsystem.common.Angle;

import jef.core.BallUtils;
import jef.core.units.AngularVelocity;
import jef.core.units.LinearVelocity;

public class LinearImpact
{
	private static final double coefficientOfRestitutionMin = .75;
	private static final double coefficientOfRestitutionMax = .81;

	// index is incident angle / 10.
	// value is y / z cot(theta2)
	private static double[] verticalNoSpinLinearVelocityRatios =
	{ 90, 76, 64, 59, 53, 52, 56, 61, 73, 90, 104, 116, 121, 127, 128, 124, 119, 107, 90 };

	// oblique angle is 68 degrees
	// index is incident angle / 10.
	// value is 68 / new angle
//	private double [] obliqueNoSpinLinearVelocityRatios = 		{.5, .7, .9, 1.2, 1.6, 1.7, 1.6, 1.4, 1.2, .5, .2, -.1, -.6, -.9, -.7, -.6, -.2, .5};

	// oblique angle is 68 degrees
	// index is incident angle / 10.
	// value is theta2
	private static double[] obliqueNoSpinOutgoingAngleRatios =
	{ 68, 62, 58, 40, 35, 28, 40, 45, 55, 68, 82, 92, 102, 106, 109, 104, 100, 87, 68 };

	// oblique angle is 70 degrees
	// av is -17.5
	// index is incident angle / 10.
	// value is y / z
//	private double [] obliqueBackspinLinearVelocityRatios = 	{0, .3, .8, 1.4, 1.5, 1.4, 1.3, .7, .4, 0, -.4, -.7, -1.0, -1.1, -1.1, -1.0, -.7, -.4, 0};
	// oblique angle is 70 degrees
	// av is -17.5
	// index is incident angle / 10.
	// value is theta2
	private static double[] obliqueBackspinOutgoingAngleRatios =
	{ 70, 82, 94, 110, 110, 107, 100, 98, 88, 70, 53, 50, 42, 40, 40, 42, 48, 60, 70 };

	// oblique angle is 40 degrees
	// av is 17.0
	// index is incident angle / 10.
	// value is y / z
//	private double [] obliqueTopspinLinearVelocityRatios = 		{};
	// oblique angle is 40 degrees
	// av is -17.5
	// index is incident angle / 10.
	// value is theta2
	private static double[] obliqueTopspinOutgoingAngleRatios =
	{ 40, 60, 74, 90, 90, 90, 85, 78, 70, 65, 55, 40, 35, 20, 15, 12, 15, 20, 40 };

	public static LinearVelocity calculateLVAdjustment(final AngularVelocity av, final LinearVelocity lv)
	{
		final Angle reboundAngle = Angle.ofDegrees(LinearImpact.calculateReboundAngle(av, lv));

		double newX = (lv.getXZSpeed() * reboundAngle.cos() + lv.getXYSpeed() * reboundAngle.cos()) / 2;
		double newY = (lv.getY() + lv.getXYSpeed() * reboundAngle.cos()) / 2;
		double newZ = lv.getXZSpeed() * reboundAngle.sin() * LinearImpact.calculateCOR(av);
		
		return new LinearVelocity(newX, newY, newZ);
	}

	private static double calculateCOR(final AngularVelocity av)
	{
		
		return Math.max(LinearImpact.coefficientOfRestitutionMin,
				(Math.abs(Math.cos(av.getCurrentAngleInRadians()))
						* (LinearImpact.coefficientOfRestitutionMax - LinearImpact.coefficientOfRestitutionMin))
						+ LinearImpact.coefficientOfRestitutionMin);
	}

	private static double calculateReboundAngle(final AngularVelocity av, final LinearVelocity lv)
	{
		final double psi = Math.toDegrees(av.getCurrentAngleInRadians());
		double theta = Math.toDegrees(lv.calculateXZAngle() + Math.PI);

		final double[] lvAngles = LinearImpact.reboundAnglesForImpactAngle(theta);
		final double indexValue = LinearImpact.indexValue(psi, lvAngles);

		if (BallUtils.hasBackSpin(av, lv))
		{
			final double angleForBackspin = LinearImpact.indexValue(psi,
					LinearImpact.obliqueBackspinOutgoingAngleRatios);
			return (indexValue + angleForBackspin) / 2;
		}
		
		if (BallUtils.hasTopSpin(av, lv))
		{
			final double angleForTopspin = LinearImpact.indexValue(psi, LinearImpact.obliqueTopspinOutgoingAngleRatios);
			return (indexValue + angleForTopspin) / 2;
		}
		
		if (lv.getXYSpeed() == 0)
		{
			final double[] lvValuesForAngle = LinearImpact.verticalNoSpinLinearVelocityRatios;
			return LinearImpact.indexValue(psi, lvValuesForAngle);
		}

		if (Math.abs(lv.getZ()) < LinearVelocity.EPSILON)
		{
			return 0;
		}
		
		return (indexValue + LinearImpact.indexValue(psi, LinearImpact.obliqueNoSpinOutgoingAngleRatios)) / 2.0;
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

	private static double[] reboundAnglesForImpactAngle(final double impactAngleInDegrees)
	{
		final double[] values = Arrays.copyOf(LinearImpact.verticalNoSpinLinearVelocityRatios,
				LinearImpact.verticalNoSpinLinearVelocityRatios.length);

		for (int i = 0; i < values.length; i++)
		{
			double verticalNoSpin = indexValue(i * 10, verticalNoSpinLinearVelocityRatios);
			double obliqueNoSpin = indexValue(i * 10, obliqueNoSpinOutgoingAngleRatios);
			values[i] = verticalNoSpin + (verticalNoSpin - obliqueNoSpin) / 22 * (90 - impactAngleInDegrees);
		}

		return values;
	}
}
