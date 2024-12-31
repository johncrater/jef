package jef.core.physics.ball;

import com.synerset.unitility.unitsystem.common.Area;
import com.synerset.unitility.unitsystem.common.Distance;
import com.synerset.unitility.unitsystem.common.Mass;
import com.synerset.unitility.unitsystem.common.MassUnits;
import com.synerset.unitility.unitsystem.common.Velocity;
import com.synerset.unitility.unitsystem.mechanical.Force;
import com.synerset.unitility.unitsystem.thermodynamic.Density;

import jef.core.Conversions;
import jef.core.Football;
import jef.core.units.AngularVelocity;
import jef.core.units.DUnits;
import jef.core.units.LinearVelocity;
import jef.core.units.Location;
import jef.core.units.VUnits;

public class BallPhysics
{
	// yards per tick per tick
	public static final Velocity gravity = Velocity.ofMetersPerSecond(-9.8);
	public static final Density densityOfAir = Density.ofKilogramPerCubicMeter(1.225);
	public static final Density fieldDensity = Density.ofKilogramPerCubicMeter(1600);

	public static final double coefficientOfRestitutionMax = .82;
	public static final double coefficientOfRestitutionMin = .75;

	public static final Distance lengthOfTheMajorAxis = Distance.ofInches(11.25f);
	public static final Distance lengthOfTheMinorAxis = Distance.ofInches(6.70f);

	public static final Mass mass = Mass.of(14.5, MassUnits.OUNCE);
	public static final Density density = Density.ofKilogramPerCubicMeter(.1);
	public static final Area areaOfTheMajorAxis = Area
			.ofSquareInches(((PhysicsBallBase.lengthOfTheMajorAxis.getInInches() / 2) * Math.PI
					* PhysicsBallBase.lengthOfTheMinorAxis.getInInches()) / 2);
	public static final Area areaOfTheMinorAxis = Area
			.ofSquareInches(Math.PI * PhysicsBallBase.lengthOfTheMinorAxis.getInInches());
	public static final double dragCoefficientSpiral = .19f;
	public static final double dragCoefficientEndOverEnd = (.75f + PhysicsBallBase.dragCoefficientSpiral) / 2;

	private Football ball;

	public BallPhysics(Football ball)
	{
		this.ball = ball;
	}

	public void update(float deltaTime)
	{
		if (ball.getLinearVelocity().magnitude() == 0 && ball.getLocation().getZ() == 0)
			return;

		LinearVelocity lv = ball.getLinearVelocity();
		Location loc = ball.getLocation();
		AngularVelocity av = ball.getAngularVelocity();

		LinearVelocity lvGravityAdjustment = calculateGravityAdjustment();
		LinearVelocity lvAfterAdjustments = lv.add(lvGravityAdjustment.multiply(deltaTime));

		// drag is in the opposite direction to the linearVelocity, so we multiply by -1
		LinearVelocity lvDragAdjustment = calculateDragAdjustment(lvAfterAdjustments, av).multiply(-1);

		lvAfterAdjustments = lvAfterAdjustments.add(lvDragAdjustment.multiply(deltaTime));
		Location locAfterAdjustments = loc.adjust(lvAfterAdjustments.multiply(deltaTime));

		System.out.print(String.format("Loc: %6.2f ", loc.getZ()));
		System.out.print(String.format("LV: %7.4f ", lv.multiply(deltaTime).getZ()));
		System.out.print(String.format("Gravity: %7.4f ", lvGravityAdjustment.multiply(deltaTime).multiply(deltaTime).getZ()));
		System.out.print(String.format("Drag: %7.4f ", lvDragAdjustment.multiply(deltaTime).multiply(deltaTime).getZ()));
		
		double distanceBelowGround = Math.max(0, 0 - locAfterAdjustments.getZ());

		if (distanceBelowGround > 0)
		{
			double zDistance = loc.getZ() - locAfterAdjustments.getZ();
			double distanceAboveGround = loc.getZ();

			LinearVelocity lvAtImpact = lv.add(lvGravityAdjustment.multiply(deltaTime)).add(lvDragAdjustment.multiply(deltaTime)).multiply(distanceAboveGround / zDistance);
			Location locAtImpact = loc.adjust(lvAtImpact.multiply(deltaTime));
			lvAtImpact = lv.add(lvAfterAdjustments.subtract(lv).multiply(distanceBelowGround / zDistance));
			
			assert Location.withinEpsilon(0, locAtImpact.getZ());

			AngularVelocity avFrictionAdjustment = Friction.calculateAVAdjustment(av, lvAtImpact);
			AngularVelocity avImpactAdjustment = AngularImpact.calculateAVAdjustment(av, lvAtImpact);
			av = avImpactAdjustment.adjust(avFrictionAdjustment);
			av = this.applyAngularVelocity(av, lv, deltaTime);

			LinearVelocity lvFrictionAdjustment = Friction.calculateLVAdjustment(av, lvAtImpact, mass);
			LinearVelocity lvImpactAdjustment = LinearImpact.calculateLVAdjustment(av, lvAtImpact);
			LinearVelocity lvPostImpactGravityAdjustment = calculateGravityAdjustment()
					.multiply(distanceBelowGround / zDistance);
			LinearVelocity lvPostImpactDragAdjustment = calculateDragAdjustment(lvAtImpact, av)
					.multiply(distanceBelowGround / zDistance);
			LinearVelocity lvPostImpactTotalAdjustment = lvFrictionAdjustment.add(lvImpactAdjustment)
					.add(lvPostImpactGravityAdjustment.multiply(deltaTime)).add(lvPostImpactDragAdjustment.multiply(deltaTime));

			System.out.print(String.format("Friction: %7.4f ", lvFrictionAdjustment.multiply(deltaTime).getZ()));
			System.out.print(String.format("Impact: %7.4f ", lvImpactAdjustment.multiply(deltaTime).getZ()));
			System.out.print(String.format("Gravity: %7.4f ", lvPostImpactGravityAdjustment.multiply(deltaTime).multiply(deltaTime).getZ()));
			System.out.print(String.format("Drag: %7.4f ", lvPostImpactDragAdjustment.multiply(deltaTime).multiply(deltaTime).getZ()));

			lv = lvPostImpactTotalAdjustment;
			loc = locAtImpact.adjust(lv.multiply(deltaTime));
		}
		else
		{
			lv = lvAfterAdjustments;
			loc = locAfterAdjustments;
			av = this.applyAngularVelocity(av, lv, deltaTime);
		}

		System.out.print(String.format("LV: %7.4f ", lv.multiply(deltaTime).getZ()));
		System.out.print(String.format("Loc: %6.2f ", loc.getZ()));
		System.out.println();

		if (LinearVelocity.withinEpsilon(0, lv.getX()))
			lv = lv.add(-lv.getX(), 0, 0);

		if (LinearVelocity.withinEpsilon(0, lv.getY()))
			lv = lv.add(0, -lv.getY(), 0);

		if (loc.getZ() < 0)
			loc = loc.adjust(0, 0, -loc.getZ());

		if (loc.getZ() <= 0 && lv.getZ() <= 0)
		{
			lv = lv.add(0, 0, -lv.getZ());
			loc = loc.adjust(0, 0, -loc.getZ());
		}

		if (Location.withinEpsilon(0, loc.getZ()) && LinearVelocity.withinEpsilon(0, lv.getZ()))
		{
			lv = lv.add(0, 0, -lv.getZ());
			loc = loc.adjust(0, 0, -loc.getZ());
		}

		if (lv.isCloseToZero() && Location.withinEpsilon(0, loc.getZ()))
		{
			lv = new LinearVelocity();
			av = new AngularVelocity();
		}

		ball.setAngularVelocity(av);
		ball.setLinearVelocity(lv);
		ball.setLocation(loc);
	}

	public LinearVelocity calculateGravityAdjustment()
	{
		return new LinearVelocity(0, 0, gravity.getInUnit(VUnits.YPS));
	}

	private static Force calculateDragForce(Velocity velocity, final double dragCoefficient, final Density ofAir,
			final Area area)
	{
		// 0.5 * air density (ρ) * velocity (v)^2 * drag coefficient (Cd) * reference
		// area (A)
		return Force.ofNewtons(.5 * ofAir.getInKilogramsPerCubicMeters() * Math.pow(velocity.getInMetersPerSecond(), 2)
				* dragCoefficient * area.getInSquareMeters());
	}

	private LinearVelocity calculateDragAdjustment(LinearVelocity incomingVelocity,
			AngularVelocity incomingAngularVelocity)
	{
		// drag for x
		final Velocity currentVelocity = Velocity.of(incomingVelocity.magnitude(), VUnits.YPS);

		final Force dragForce = calculateDragForce(currentVelocity, incomingAngularVelocity.isCloseToZero()

				? dragCoefficientSpiral
				: dragCoefficientEndOverEnd, densityOfAir,
				incomingAngularVelocity.isCloseToZero() ? areaOfTheMinorAxis
						: areaOfTheMinorAxis.plus(areaOfTheMajorAxis).div(2));

		// we include * Constants.TIMER_INTERVAL because drag is a rate of acceleration
		// per second and we deal in ticks
		final var updatedVelocity = Velocity.ofMetersPerSecond(
				currentVelocity.getInMetersPerSecond() - (dragForce.div(mass.getInKilograms()).getInNewtons()));

		double currentVelocityInYPS = currentVelocity.getInUnit(VUnits.YPS);
		if (currentVelocityInYPS == 0)
			return new LinearVelocity();

		final var ratio = 1 - updatedVelocity.getInUnit(VUnits.YPS) / currentVelocityInYPS;

		return incomingVelocity.multiply(ratio);
	}

	private AngularVelocity applyAngularVelocity(AngularVelocity av, LinearVelocity lv, double deltaTime)
	{
		double spin = 0;
		spin = Conversions.normalizeAngle(av.getCurrentAngleInRadians() + av.getRadiansPerSecond() * deltaTime);

		if (av.getRadiansPerSecond() == 0 && av.getSpiralVelocity() > 0)
		{
			spin = lv.calculateXZAngle();
		}

		return new AngularVelocity(spin, av.getRadiansPerSecond(), av.getSpiralVelocity());
	}

}
