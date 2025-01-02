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
import jef.core.units.LinearVelocity;
import jef.core.units.Location;
import jef.core.units.VUnits;

public class BallPhysics
{
	// yards per tick per tick
	public static final Velocity gravity = Velocity.ofMetersPerSecond(9.8);
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
	private Friction friction = new Friction();
	private LinearImpact linearImpact = new LinearImpact();
	private AngularImpact angularImpact = new AngularImpact();

	public BallPhysics(Football ball)
	{
		this.ball = ball;
	}

	public void update(float deltaTime)
	{
		if (ball.getLinearVelocity().getDistance() == 0 && ball.getLocation().getZ() == 0)
			return;

		LinearVelocity lv = ball.getLinearVelocity();
		Location loc = ball.getLocation();
		AngularVelocity av = ball.getAngularVelocity();
		
//		System.out.print(String.format("Loc: %s ", loc));
//		System.out.print(String.format("LV: %s ", lv));
//		System.out.print(String.format("AV: %s ", av.multiply(deltaTime)));

		LinearVelocity accumulatedLV = new LinearVelocity();
		
		LinearVelocity lvGravityAdjustment = calculateGravityAdjustment().multiply(deltaTime);
		accumulatedLV = accumulatedLV.add(lvGravityAdjustment);
		
		// drag is in the opposite direction to the linearVelocity, so we multiply by -1
		double dragAdjustment = calculateDragAdjustment(lv.getDistance() + accumulatedLV.getDistance(), av) * deltaTime;
//		System.out.print(String.format("Drag: %.2f ", dragAdjustment));
		accumulatedLV = accumulatedLV.add(dragAdjustment);
				
		Location locTmp = loc.adjust(lv.add(accumulatedLV).multiply(deltaTime));

		double pctBelowGround = 0;
		if (locTmp.getZ() < 0)
		{
			double zDistance = loc.getZ() - locTmp.getZ();
			double distanceAboveGround = loc.getZ();
			
			pctBelowGround = 1 - distanceAboveGround / zDistance;
			accumulatedLV = accumulatedLV.multiply(distanceAboveGround / zDistance);
		}
		
		if (pctBelowGround == 1 || (locTmp.getZ() == 0 && lv.getElevation() == 0))
		{
			// we are rolling on the ground
			double frictionAdjustment = friction.calculateLVAdjustment(av, lv.add(accumulatedLV), mass) * deltaTime;
//			System.out.print(String.format("Friction: %.2f ", frictionAdjustment));

			lv = lv.add(frictionAdjustment);
			loc = loc.adjust(lv.multiply(deltaTime));
			av = this.applyAngularVelocity(av, lv, deltaTime);

			av = new AngularVelocity();
		}
		
		if (pctBelowGround > 0 && pctBelowGround < 1)
		{
			System.out.print(String.format("Loc: %s ", loc));
			System.out.print(String.format("LV: %s ", lv));
			System.out.print(String.format("AV: %s ", av.multiply(deltaTime)));

			loc = loc.adjust(lv.add(accumulatedLV).multiply(deltaTime).multiply(1 - pctBelowGround));
			lv = lv.add(accumulatedLV.multiply(1 - pctBelowGround));
			
			assert Location.withinEpsilon(0, loc.getZ());
			
			accumulatedLV = new LinearVelocity();

			AngularVelocity avFrictionAdjustment = friction.calculateAVAdjustment(av, lv);
			AngularVelocity avImpactAdjustment = angularImpact.calculateAVAdjustment(av, lv);
			
			// we don't add deltaTime to impact adjustment as it does not represent acceleration 
			LinearVelocity lvImpactAdjustment = linearImpact.calculateLVAdjustment(av, lv);
			System.out.print(String.format("Impact: %s ", lvImpactAdjustment));
			accumulatedLV = lvImpactAdjustment;
			
			double frictionAdjustment = friction.calculateLVAdjustment(av, lv, mass) * deltaTime;
			System.out.print(String.format("Friction: %.2f ", frictionAdjustment));
			accumulatedLV = accumulatedLV.add(frictionAdjustment);

			LinearVelocity lvPostImpactGravityAdjustment = calculateGravityAdjustment()
					.multiply(pctBelowGround).multiply(deltaTime);
			accumulatedLV = accumulatedLV.add(lvPostImpactGravityAdjustment);
			
			double postImpactDragAdjustment = calculateDragAdjustment(lv.getDistance() + accumulatedLV.getDistance(), av) * pctBelowGround * deltaTime;
			System.out.print(String.format("Drag: %.2f ", postImpactDragAdjustment));
			accumulatedLV = accumulatedLV.add(postImpactDragAdjustment);

			lv = accumulatedLV;
			loc = loc.adjust(lv.multiply(deltaTime));
			av = this.applyAngularVelocity(avImpactAdjustment.adjust(avFrictionAdjustment), lv, deltaTime);

			System.out.println();
		}
		else if (pctBelowGround == 0)
		{
			lv = lv.add(accumulatedLV);
			loc = loc.adjust(lv.multiply(deltaTime));
			av = this.applyAngularVelocity(av, lv, deltaTime);
		}

		if (loc.getZ() < 0)
			loc = loc.adjust(0, 0, -loc.getZ());

		if (loc.getZ() <= 0 && lv.getZ() <= 0)
		{
			lv = lv.set(0.0, null, null);
			loc = loc.adjust(0, 0, -loc.getZ());
		}

		if (Location.withinEpsilon(0, loc.getZ()) && LinearVelocity.withinEpsilon(0, lv.getZ()))
		{
			lv = lv.set(0.0, null, null);
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
		return new LinearVelocity(-Math.PI / 2, 0, gravity.getInUnit(VUnits.YPS));
	}

	private static Force calculateDragForce(Velocity velocity, final double dragCoefficient, final Density ofAir,
			final Area area)
	{
		// 0.5 * air density (ρ) * velocity (v)^2 * drag coefficient (Cd) * reference
		// area (A)
		return Force.ofNewtons(.5 * ofAir.getInKilogramsPerCubicMeters() * Math.pow(velocity.getInMetersPerSecond(), 2)
				* dragCoefficient * area.getInSquareMeters());
	}

	private double calculateDragAdjustment(double incomingVelocity,
			AngularVelocity incomingAngularVelocity)
	{
		final Velocity currentVelocity = Velocity.of(incomingVelocity, VUnits.YPS);

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
			return 0;

		final var ratio = 1 - updatedVelocity.getInUnit(VUnits.YPS) / currentVelocityInYPS;

		return -1 * incomingVelocity * ratio;
	}

	private AngularVelocity applyAngularVelocity(AngularVelocity av, LinearVelocity lv, double deltaTime)
	{
		double spin = 0;
		spin = Conversions.normalizeAngle(av.getCurrentAngleInRadians() + av.getRadiansPerSecond() * deltaTime);

		if (av.getRadiansPerSecond() == 0 && av.getSpiralVelocity() > 0)
		{
			spin = lv.getElevation();
		}

		return new AngularVelocity(spin, av.getRadiansPerSecond(), av.getSpiralVelocity());
	}

}
