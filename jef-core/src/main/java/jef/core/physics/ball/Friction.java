package jef.core.physics.ball;

import com.synerset.unitility.unitsystem.common.Mass;
import com.synerset.unitility.unitsystem.common.Velocity;
import com.synerset.unitility.unitsystem.mechanical.Force;

import jef.core.BallUtils;
import jef.core.Conversions;
import jef.core.units.AngularVelocity;
import jef.core.units.LinearVelocity;
import jef.core.units.VUnits;

public class Friction
{
	// index is incident angle / 10.
	private static double[] verticalNoSpinCoefficientOfFriction =
	{ 0, .1, .2, .23, .25, .25, .23, .2, .1, 0, .1, .2, .23, .25, .25, .23, .2, .1, 0 };
	// oblique angle is 68 degrees
	private static double[] obliqueNoSpinCoefficientOfFriction =
	{ .3, .2, .0, -.15, -.18, -.2, -.18, -.1, -.05, .05, .1, .2, .25, .3, .35, .35, .3, .25, .2 };
	// oblique angle is 70 degrees
	// av is -17.5
	private static double[] obliqueBackspinCoefficientOfFriction =
	{ .2, .0, -.1, -.1, -.1, -.1, -.0, .1, .2, .2, .3, .3, .4, .4, .4, .4, .3, .25, .2 };
	// oblique angle is 40 degrees
	// av is 17.0
	private static double[] obliqueTopspinCoefficientOfFriction =
	{ .3, .2, .1, .05, .05, .04, .04, .05, .06, .1, .13, .16, .21, .25, .32, .35, .32, .31, .3 };

	public static AngularVelocity calculateAVAdjustment(AngularVelocity av, final LinearVelocity lv)
	{
		final double coefficientOfFriction = Friction.calculate(av, lv);

		final double xySpeed = lv.getXYSpeed();
		if (xySpeed != 0)
			return new AngularVelocity(0, (xySpeed * coefficientOfFriction) / xySpeed);

		return new AngularVelocity();
	}

	public static LinearVelocity calculateLVAdjustment(final AngularVelocity av, LinearVelocity lv, final Mass mass)
	{
		final double coefficientOfFriction = Friction.calculate(av, lv);

		// friction for x
		var currentVelocity = Velocity.of(lv.getX(), VUnits.YPS);

		var frictionForce = Force
				.ofNewtons(mass.getInKilograms() * currentVelocity.getInMetersPerSecond() * coefficientOfFriction);

		double velocityReduction = Conversions.metersToYards(frictionForce.div(mass.getInKilograms()).getInNewtons());
		LinearVelocity ret = new LinearVelocity(-velocityReduction, 0, 0);

		// friction for y
		currentVelocity = Velocity.of(lv.getY(), VUnits.YPS);

		frictionForce = Force
				.ofNewtons(mass.getInKilograms() * currentVelocity.getInMetersPerSecond() * coefficientOfFriction);

		velocityReduction = Conversions.metersToYards(frictionForce.div(mass.getInKilograms()).getInNewtons());
		return ret.add(0, -velocityReduction, 0);
	}

	// psi is the angle of the major axis of the ball relative to the ground.
	// theta is the angle of the ball relative to the ground at impact.
	// the impact angle is xaAngle + PI since xzAngle is relative to the corrdinates of the ball
	// and the calculation we are doing is in coordinates relative to the ground at the point of impact
	private static double calculate(final AngularVelocity av, final LinearVelocity lv)
	{
		final double psi = Math.toDegrees(av.getCurrentAngleInRadians());
		double theta = Math.toDegrees(lv.calculateXZAngle() + Math.PI);
		
		final double[] cofValues = Friction.cofValuesForImpactAngle(theta);
		double indexValue = Friction.indexValue(psi, cofValues);

		if (BallUtils.hasBackSpin(av, lv))
			return (indexValue + Friction.indexValue(psi, Friction.obliqueBackspinCoefficientOfFriction))
					/ 2.0;

		if (BallUtils.hasTopSpin(av, lv))
			return (indexValue + Friction.indexValue(psi, Friction.obliqueTopspinCoefficientOfFriction))
					/ 2.0;

		if (lv.getXZSpeed() == 0)
			return Friction.indexValue(psi, Friction.verticalNoSpinCoefficientOfFriction);

		return (indexValue + Friction.indexValue(psi, Friction.obliqueNoSpinCoefficientOfFriction)) / 2.0;

	}

	private static double[] cofValuesForImpactAngle(final double impactAngleInDegrees)
	{
		final double[] values = new double[19];

		for (int i = 0; i < values.length; i++)
		{
			double verticalNoSpin = Friction.indexValue(i * 10, Friction.verticalNoSpinCoefficientOfFriction);
			double obliqueNoSpin = Friction.indexValue(i * 10, Friction.obliqueNoSpinCoefficientOfFriction);

			// 90 - impactAngle because the vertical drop starts with a 90 angle relative to the point of impact.
			values[i] = verticalNoSpin + (verticalNoSpin - obliqueNoSpin) / 22 * (90 - impactAngleInDegrees);
		}

		return values;
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
