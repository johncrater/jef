package jef.core.physics;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import com.synerset.unitility.unitsystem.common.Angle;
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
	public static final double coefficientOfFriction = .8;
	
	// supposed to be around .8. But this seems to bounce way too high. It may have
	// more to do with
	// the grass field which I have read will result in a .6 to .75 COR.
	// .4 seems more accurate.
	public static final double coefficientOfRestitution = .7;

	public static final Distance lengthOfTheMajorAxis = Distance.ofInches(11.25f);
	public static final Distance lengthOfTheMinorAxis = Distance.ofInches(6.70f);

	public static final double estimatedCoefficientOfFriction = .41f;

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
		LinearVelocity lv = ball.getLinearVelocity();
		Location loc = ball.getLocation();
		AngularVelocity av = ball.getAngularVelocity();
		
		if (lv.isCloseToZero()
				&& (loc.getZ() <= lengthOfTheMinorAxis.getInUnit(DUnits.YARD)))
		{
			lv = new LinearVelocity();
			av = new AngularVelocity();
			loc = new Location(loc.getX(), loc.getY(), 0.0);
		}
		else
		{
			LinearVelocity hypotheticalLV = lv.adjust(0, 0, gravity.getInUnit(VUnits.YPS) * deltaTime);
			Location hypotheticalLocation = loc.adjust(hypotheticalLV.getX() * deltaTime, hypotheticalLV.getY() * deltaTime, hypotheticalLV.getZ() * deltaTime);

			hypotheticalLV = this.getDrag(hypotheticalLV, av, deltaTime);
			if (hypotheticalLocation.getZ() < 0)
			{
				double timeBelowGround = Math.abs(hypotheticalLocation.getZ()) / (Math.abs(hypotheticalLocation.getZ()) + loc.getZ()) * deltaTime;
				
				// lv and loc adjustments before impact
				lv = lv.adjust(0, 0, lv.getZ() * (deltaTime - timeBelowGround));
				loc = loc.adjust(lv.getX() * (deltaTime - timeBelowGround), lv.getY() * (deltaTime - timeBelowGround), lv.getZ() * (deltaTime - timeBelowGround));

				// friction also affects the angular velocity as the speed of the point of the
				// ball in contact with the ground slows and the speed of the
				// opposite side maintains its momentum. We will assume that this only happens
				// for one tick by which time the ball has rebounded.
				// we calculate this first so we capture the speed of the ball prior to impact
				// with the ground;
				final double xySpeed = lv.magnitude();
				if (xySpeed == 0)
					av = new AngularVelocity();
				else
					av = av.multiply((xySpeed * coefficientOfFriction) / xySpeed);

				
				lv = applyFriction(lv, deltaTime);
				
				// lv and loc adjustments after impact
				lv = new LinearVelocity(lv.getX(),  lv.getY(), Math.max(0, (lv.getZ() * coefficientOfRestitution * -1) + (gravity.getInUnit(VUnits.YPS) * timeBelowGround)));
				loc = loc.adjust(lv.getX() * timeBelowGround, lv.getY() * timeBelowGround, lv.getZ() * timeBelowGround);
			}
			else
			{
				lv = hypotheticalLV;
				loc = hypotheticalLocation;
			}
			
			if (loc.getZ() > 0)
				av = this.applySpin(av, deltaTime);
	
			if (Math.abs(lv.getX()) < LinearVelocity.EPSILON)
			{
				lv = new LinearVelocity(0, lv.getY(), lv.getZ());
				loc = new Location(0, loc.getY(), loc.getZ());
			}
			
			if (Math.abs(lv.getY()) < LinearVelocity.EPSILON)
			{
				lv = new LinearVelocity(lv.getX(), 0, lv.getZ());
				loc = new Location(loc.getX(), 0, loc.getZ());
			}

			if (Math.abs(lv.getZ()) < LinearVelocity.EPSILON)
			{
				lv = new LinearVelocity(lv.getX(), lv.getY(), 0);
				loc = new Location(loc.getX(), loc.getY(), 0);
				av = new AngularVelocity();
			}

			if (lv.isCloseToZero() && (loc.getZ() <= lengthOfTheMinorAxis.getInUnit(DUnits.YARD)))
			{
				lv = new LinearVelocity();
				av = new AngularVelocity();
				loc = new Location(loc.getX(), loc.getY(), 0.0);
			}
		}
		
		ball.setLinearVelocity(lv);
		ball.setAngularVelocity(av);
		ball.setLocation(loc);
	}

	private static Force calculateDragForce(Velocity velocity, final double dragCoefficient, final Density ofAir,
			final Area area)
	{
		// 0.5 * air density (ρ) * velocity (v)^2 * drag coefficient (Cd) * reference
		// area (A)
		return Force.ofNewtons(.5 * ofAir.getInKilogramsPerCubicMeters() * Math.pow(velocity.getInMetersPerSecond(), 2)
				* dragCoefficient * area.getInSquareMeters());
	}

	private LinearVelocity getDrag(LinearVelocity incomingVelocity, AngularVelocity incomingAngularVelocity, double deltaTime)
	{
		// drag for x
		final Velocity currentVelocity = Velocity.of(incomingVelocity.magnitude(), VUnits.YPS);

		final Force dragForce = calculateDragForce(currentVelocity,
				incomingAngularVelocity.isCloseToZero() ? dragCoefficientSpiral : dragCoefficientEndOverEnd,
				densityOfAir, incomingAngularVelocity.isCloseToZero() ? areaOfTheMinorAxis
						: areaOfTheMinorAxis.plus(areaOfTheMajorAxis).div(2));

		// we include * Constants.TIMER_INTERVAL because drag is a rate of acceleration
		// per second and we deal in ticks
		final var updatedVelocity = Velocity.ofMetersPerSecond(currentVelocity.getInMetersPerSecond()
				- (dragForce.div(mass.getInKilograms()).getInNewtons() * deltaTime));

		final var ratio = updatedVelocity.getInUnit(VUnits.YPS) / currentVelocity.getInUnit(VUnits.YPS);

		return incomingVelocity.multiply(ratio);
	}

	private LinearVelocity applyFriction(LinearVelocity lv, float deltaTime)
	{
		// friction for x
		var currentVelocity = Velocity.of(lv.getX(), VUnits.YPS);

		var frictionForce = Force
				.ofNewtons(mass.getInKilograms() * currentVelocity.getInMetersPerSecond() * coefficientOfFriction);
		lv = new LinearVelocity(
				lv.getX() - (frictionForce.div(mass.getInKilograms()).getInNewtons() * deltaTime),
				lv.getY(), lv.getZ());

		// friction for y
		currentVelocity = Velocity.of(lv.getY(), VUnits.YPS);

		frictionForce = Force
				.ofNewtons(mass.getInKilograms() * currentVelocity.getInMetersPerSecond() * coefficientOfFriction);

		return new LinearVelocity(lv.getX(),
				lv.getY() - (frictionForce.div(mass.getInKilograms()).getInNewtons() * deltaTime),
				lv.getZ());
	}

	private AngularVelocity applySpin(AngularVelocity av, double deltaTime)
	{
		double spin = 0;
		spin = Conversions.normalizeAngle(av.getCurrentAngleInRadians()
				+ av.getRadiansPerSecond() * deltaTime);

		return new AngularVelocity(spin, av.getRadiansPerSecond());
	}

}
