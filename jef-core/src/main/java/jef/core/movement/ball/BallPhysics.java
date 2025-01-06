package jef.core.movement.ball;

import com.synerset.unitility.unitsystem.common.Area;
import com.synerset.unitility.unitsystem.common.Mass;
import com.synerset.unitility.unitsystem.common.MassUnits;
import com.synerset.unitility.unitsystem.common.Velocity;
import com.synerset.unitility.unitsystem.mechanical.Force;
import com.synerset.unitility.unitsystem.thermodynamic.Density;

import jef.core.Conversions;
import jef.core.movement.AngularVelocity;
import jef.core.movement.DefaultLinearVelocity;
import jef.core.movement.LinearVelocity;
import jef.core.movement.VUnits;

public class BallPhysics
{
	// yards per tick per tick
	public static final Velocity gravity = Velocity.ofMetersPerSecond(9.8);
	public static final Density densityOfAir = Density.ofKilogramPerCubicMeter(1.225);
	public static final Density fieldDensity = Density.ofKilogramPerCubicMeter(1600);

	public static final double coefficientOfRestitutionMax = .82;
	public static final double coefficientOfRestitutionMin = .75;
	
	public static final double coefficientOfSlidingFriction = .41;


	public static final Mass mass = Mass.of(14.5, MassUnits.OUNCE);
	public static final Density density = Density.ofKilogramPerCubicMeter(.1);
	public static final Area areaOfTheMajorAxis = Area
			.ofSquareInches(((PhysicsBallBase.lengthOfTheMajorAxis.getInInches() / 2) * Math.PI
					* PhysicsBallBase.lengthOfTheMinorAxis.getInInches()) / 2);
	public static final Area areaOfTheMinorAxis = Area
			.ofSquareInches(Math.PI * PhysicsBallBase.lengthOfTheMinorAxis.getInInches());
	public static final double dragCoefficientSpiral = .19f;
	public static final double dragCoefficientEndOverEnd = (.75f + PhysicsBallBase.dragCoefficientSpiral) / 2;

	private Friction friction = new Friction();
	private LinearImpact linearImpact = new LinearImpact();
	private AngularImpact angularImpact = new AngularImpact();

	public BallPhysics()
	{
	}

	public void update(BallTracker tracker)
	{
		if (tracker.getLV().getSpeed() == 0 && tracker.getLoc().getZ() == 0)
			return;

		System.out.print(String.format("%s ", tracker));

		LinearVelocity accumulatedLV = calculateGravityAdjustment();
		
		// drag is in the opposite direction to the linearVelocity, so we multiply by -1
		// drag is a force that produces acceleration when multiplied by time. We need to this multiplication
		// here to convert it into a velocity before we use it.
		double dragAcceleration = -1 * tracker.getRemainingTime() * calculateDragAcceleration(tracker.getLV().getSpeed() + accumulatedLV.getSpeed(), tracker.getAV());
		System.out.print(String.format("Drag: %.2f ", dragAcceleration));
		accumulatedLV = accumulatedLV.add(dragAcceleration);
				
		tracker.moveToGround(accumulatedLV);
		if (tracker.getLoc().getZ() == 0 && LinearVelocity.equals(0, tracker.getLV().getElevation()))
		{
			// we are rolling on the ground
			tracker.setLV(tracker.getLV().newFrom(0.0, null, null));

			// sliding friction is different from rebounding friction. So we use a different constant
			double frictionAdjustment = friction.calculateLVSlidingFrictionAdjustment(tracker.getLV().add(accumulatedLV), mass, coefficientOfSlidingFriction);
			System.out.print(String.format("S-Friction: %.2f ", frictionAdjustment));
			tracker.moveRemaining(Math.max(frictionAdjustment, -tracker.getLV().getSpeed()));
		}
		else if (tracker.getLoc().getZ() == 0 && tracker.getLV().getElevation() < 0)
		{
			double avFrictionAdjustment = friction.calculateAVAdjustment(tracker.getAV(), tracker.getLV());
			double avImpactAdjustment = angularImpact.calculateAVAdjustment(tracker.getAV(), tracker.getLV());
			
			// we don't add deltaTime to impact adjustment as it does not represent acceleration 
			LinearVelocity reboundLV = linearImpact.calculateLVAdjustment(tracker.getAV(), tracker.getLV());
			System.out.print(String.format("Impact: %s ", reboundLV));

			// since we are rebounding, we need manually set the new LV
			tracker.setLV(reboundLV);
			
			accumulatedLV = new DefaultLinearVelocity();

			double frictionAdjustment = friction.calculateLVAdjustment(tracker.getAV(), tracker.getLV(), mass);
			System.out.print(String.format("Friction: %.2f ", frictionAdjustment));
			accumulatedLV = accumulatedLV.add(frictionAdjustment);

			LinearVelocity lvPostImpactGravityAdjustment = calculateGravityAdjustment()
					.multiply(tracker.getPctRemaining());
			accumulatedLV = accumulatedLV.add(lvPostImpactGravityAdjustment);
			
			// drag is in the opposite direction to the linearVelocity, so we multiply by -1
			// drag is a force that produces acceleration when multiplied by time. We need to this multiplication
			// here to convert it into a velocity before we use it.
			dragAcceleration = -1 * tracker.getRemainingTime() * calculateDragAcceleration(tracker.getLV().getSpeed() + accumulatedLV.getSpeed(), tracker.getAV());
			System.out.print(String.format("Drag: %.2f ", dragAcceleration));
			accumulatedLV = accumulatedLV.add(dragAcceleration);

			tracker.setRotation(avImpactAdjustment + avFrictionAdjustment);
			
			tracker.move(accumulatedLV, null);
		}

		// let's use the remaining time
		tracker.move();
		
		System.out.println(String.format("%s ", tracker));

		tracker.rationalize();
	}

	public LinearVelocity calculateGravityAdjustment()
	{
		return new DefaultLinearVelocity(-Math.PI / 2, 0, gravity.getInUnit(VUnits.YPS));
	}

	private static Force calculateDragForce(Velocity velocity, final double dragCoefficient, final Density ofAir,
			final Area area)
	{
		// 0.5 * air density (ρ) * velocity (v)^2 * drag coefficient (Cd) * reference
		// area (A)
		return Force.ofNewtons(.5 * ofAir.getInKilogramsPerCubicMeters() * Math.pow(velocity.getInMetersPerSecond(), 2)
				* dragCoefficient * area.getInSquareMeters());
	}

	private double calculateDragAcceleration(double incomingVelocity,
			AngularVelocity incomingAngularVelocity)
	{
		final Velocity currentVelocity = Velocity.of(incomingVelocity, VUnits.YPS);
		double currentVelocityInYPS = currentVelocity.getInUnit(VUnits.YPS);
		if (currentVelocityInYPS == 0)
			return 0;

		final Force dragForce = calculateDragForce(currentVelocity, incomingAngularVelocity.isNotRotating()

				? dragCoefficientSpiral
				: dragCoefficientEndOverEnd, densityOfAir,
				incomingAngularVelocity.isNotRotating() ? areaOfTheMinorAxis
						: areaOfTheMinorAxis.plus(areaOfTheMajorAxis).div(2));

		return Conversions.metersToYards(dragForce.div(mass.getInKilograms()).getInNewtons());
	}

}
