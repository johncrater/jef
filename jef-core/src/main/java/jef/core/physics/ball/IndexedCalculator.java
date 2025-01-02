package jef.core.physics.ball;

import java.util.Arrays;

import jef.core.BallUtils;
import jef.core.units.AngularVelocity;
import jef.core.units.LinearVelocity;

public class IndexedCalculator
{
	// index is incident angle / 10.
	// value is rad/s
	protected double[] verticalNoSpin;

	// oblique angle is 68 degrees
	// index is incident angle / 10.
	// value is rad/s
	protected double[] obliqueNoSpin;

	// oblique angle is 70 degrees
	// av is -17.5
	// index is incident angle / 10.
	// value is rad/s
	protected double[] obliqueBackspin;

	// oblique angle is 40 degrees
	// av is 17.0
	// index is incident angle / 10.
	// value is w2 / w1 rad/s
	protected double[] obliqueTopspin;


	public IndexedCalculator()
	{
		super();
	}

	protected double indexValue(double rotationAngleInDegrees, final double[] values)
	{
		if (rotationAngleInDegrees < 0)
			rotationAngleInDegrees = rotationAngleInDegrees * -1;
	
		rotationAngleInDegrees %= 180;
	
		assert (rotationAngleInDegrees >= 0) && (rotationAngleInDegrees <= 180);
	
		int index = (int) Math.floor(rotationAngleInDegrees / 10);
		if ((index + 1) >= values.length)
			index = index - 1;
	
		final double low = values[index];
		final double high = values[index + 1];
		return low + (((high - low) * (rotationAngleInDegrees - (10 * index))) / 10);
	}

	protected double calculatePhi(AngularVelocity av, LinearVelocity lv)
	{
		return Math.toDegrees(lv.movingLeft() ? Math.PI - av.getCurrentAngleInRadians() : av.getCurrentAngleInRadians());
	}
	
	protected double calculateTheta(LinearVelocity lv)
	{
		return 180 - Math.toDegrees(lv.getElevation() + Math.PI);
	}
	
	protected double[] buildIndex(final double impactAngleInDegrees)
	{
		final double[] values = Arrays.copyOf(verticalNoSpin,
				verticalNoSpin.length);

		if (impactAngleInDegrees == 90)
			return values;

		for (int i = 0; i < values.length; i++)
		{
			double vNoSpin = indexValue(i * 10, verticalNoSpin);
			double oNoSpin = indexValue(i * 10, obliqueNoSpin);

			// vertical is 90, oblique is 68, so 22 diff
			double diffPerDegree = (vNoSpin - oNoSpin) / 22;
			values[i] = vNoSpin - diffPerDegree * (90 - impactAngleInDegrees);
		}

		return values;
	}

	// phi is the angle of the major axis of the ball relative to the ground.
	// theta is the angle of the ball relative to the ground at impact.
	// the impact angle is xaAngle + PI since xzAngle is relative to the corrdinates
	// of the ball
	// and the calculation we are doing is in coordinates relative to the ground at
	// the point of impact
	protected double calculate(final AngularVelocity av, final LinearVelocity lv)
	{
		final double phi = calculatePhi(av, lv);
		final double theta = calculateTheta(lv);
		
		// make sure 0 <= theta <= 90

		final double[] avValues = buildIndex(theta);
		final double indexValue = indexValue(phi, avValues);

		if (BallUtils.hasBackSpin(av, lv))
			return (indexValue + indexValue(phi, obliqueBackspin)) / 2.0;

		if (BallUtils.hasTopSpin(av, lv))
			return (indexValue + indexValue(phi, obliqueTopspin)) / 2.0;

		if (LinearVelocity.withinEpsilon(lv.getElevation(), -Math.PI / 2))
			return indexValue(phi, verticalNoSpin);

		return (indexValue + indexValue(phi, obliqueNoSpin)) / 2.0;
	}

}