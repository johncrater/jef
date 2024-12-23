package football.jef.core.physics;

import com.synerset.unitility.unitsystem.common.Area;
import com.synerset.unitility.unitsystem.common.Velocity;
import com.synerset.unitility.unitsystem.mechanical.Force;
import com.synerset.unitility.unitsystem.thermodynamic.Density;

import football.jef.core.Conversions;
import football.jef.core.Football;
import football.jef.core.units.VUnits;

class PhysicsBallYZ extends PhysicsBallBase
{
	public static Force calculateDragForce(final Velocity velocity, final double dragCoefficient, final Density ofAir,
			final Area area)
	{
		// 0.5 * air density (ρ) * velocity (v)^2 * drag coefficient (Cd) * reference
		// area (A)
		return Force.ofNewtons(.5 * ofAir.getInKilogramsPerCubicMeters() * Math.pow(velocity.getInMetersPerSecond(), 2)
				* dragCoefficient * area.getInSquareMeters());
	}

	public Force calculateDragForce()
	{
		final Velocity currentVelocity = Velocity.of(this.getFootball().getLinearVelocity().getY(), VUnits.YPS);
		if (currentVelocity.isEqualZero())
			return Force.ofNewtons(0);

		final double dragCoefficient = this.getFootball().getAngularVelocity().isCloseToZero()
				? dragCoefficientSpiral
				: dragCoefficientEndOverEnd;
		final Area areaCovered = this.getFootball().getAngularVelocity().isCloseToZero() ? areaOfMinorAxis
				: areaOfMajorAxis.plus(areaOfMinorAxis).div(2);

		return calculateDragForce(currentVelocity, dragCoefficient, PhysicsWorld.densityOfAir, areaCovered);
	}

	public PhysicsBallYZ(final Football ball)
	{
		super(ball);

		this.setLinearVelocity(Conversions.yardsToMeters(ball.getLinearVelocity().getY()),
				Conversions.yardsToMeters(ball.getLinearVelocity().getZ()));
		this.getTransform().translate(Conversions.yardsToMeters(ball.getLocation().getY()),
				Conversions.yardsToMeters(ball.getLocation().getZ()));
	}

}
