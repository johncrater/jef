package jef.core.physics.ball;

import com.synerset.unitility.unitsystem.common.Mass;
import com.synerset.unitility.unitsystem.common.Velocity;
import com.synerset.unitility.unitsystem.mechanical.Force;

import jef.core.BallUtils;
import jef.core.Conversions;
import jef.core.units.AngularVelocity;
import jef.core.units.LinearVelocity;
import jef.core.units.VUnits;

public class Friction extends IndexedCalculator
{
	public Friction()
	{
		// index is incident angle / 10.
		verticalNoSpin = new double []
		{ 0, .1, .2, .23, .25, .25, .23, .2, .1, 0, .1, .2, .23, .25, .25, .23, .2, .1, 0 };
		// oblique angle is 68 degrees
		obliqueNoSpin = new double []
		{ .3, .2, .0, -.15, -.18, -.2, -.18, -.1, -.05, .05, .1, .2, .25, .3, .35, .35, .3, .25, .2 };
		// oblique angle is 70 degrees
		// av is -17.5
		obliqueBackspin = new double []
		{ .2, .0, -.1, -.1, -.1, -.1, -.0, .1, .2, .2, .3, .3, .4, .4, .4, .4, .3, .25, .2 };
		// oblique angle is 40 degrees
		// av is 17.0
		obliqueTopspin = new double []
		{ .3, .2, .1, .05, .05, .04, .04, .05, .06, .1, .13, .16, .21, .25, .32, .35, .32, .31, .3 };

	}

	public AngularVelocity calculateAVAdjustment(final AngularVelocity av, final LinearVelocity lv)
	{
		final double coefficientOfFriction = calculate(av, lv);

		final double xyDistance = lv.getXYDistance();
		if (!LinearVelocity.withinEpsilon(0, xyDistance))
			return new AngularVelocity(0, (-1 * (xyDistance * coefficientOfFriction)) / xyDistance);

		return new AngularVelocity();
	}

	public double calculateLVAdjustment(final AngularVelocity av, final LinearVelocity lv, final Mass mass)
	{
		final double coefficientOfFriction = calculate(av, lv);

		final var currentVelocity = Velocity.of(lv.getDistance(), VUnits.YPS);

		final var frictionForce = Force
				.ofNewtons(mass.getInKilograms() * currentVelocity.getInMetersPerSecond() * coefficientOfFriction);

		final double velocityReduction = Conversions
				.metersToYards(frictionForce.div(mass.getInKilograms()).getInNewtons());
		return -velocityReduction;
	}

}
