package jef.core.physics.ball;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Vector2;

import com.synerset.unitility.unitsystem.common.Area;
import com.synerset.unitility.unitsystem.common.Distance;
import com.synerset.unitility.unitsystem.common.Mass;
import com.synerset.unitility.unitsystem.common.MassUnits;
import com.synerset.unitility.unitsystem.thermodynamic.Density;

import jef.core.Football;
import jef.core.units.DUnits;

class PhysicsBallBase extends Body
{
	public static final Distance lengthOfTheMajorAxis = Distance.ofInches(11.25f);
	public static final Distance lengthOfTheMinorAxis = Distance.ofInches(6.70f);

	public static final double estimatedCoefficientOfFriction = .41f;

	// supposed to be around .8. But this seems to bounce way too high. It may have
	// more to do with
	// the grass field which I have read will result in a .6 to .75 COR.
	// .4 seems more accurate.
	public static final double coefficientOfRestitution = .5f;
	public static final Mass mass = Mass.of(14.5, MassUnits.OUNCE);
	public static final Density density = Density.ofKilogramPerCubicMeter(.1);
	public static final Area areaOfMajorAxis = Area
			.ofSquareInches(((PhysicsBallBase.lengthOfTheMajorAxis.getInInches() / 2) * Math.PI
					* PhysicsBallBase.lengthOfTheMinorAxis.getInInches()) / 2);
	public static final Area areaOfMinorAxis = Area
			.ofSquareInches(Math.PI * PhysicsBallBase.lengthOfTheMinorAxis.getInInches());
	public static final double dragCoefficientSpiral = .19f;
	public static final double dragCoefficientEndOverEnd = (.75f + PhysicsBallBase.dragCoefficientSpiral) / 2;

	private final Football football;

	public PhysicsBallBase(final Football ball)
	{
		this.football = ball;
		this.setBullet(true);
		this.setAngularVelocity(ball.getAV().getRotation());

		this.setMass(new org.dyn4j.geometry.Mass(new Vector2(0, 0), PhysicsBallBase.mass.getInKilograms(), 0));

//		this.setMass(new Mass(
//				new Vector2(0, 0),
//				mass.getInKilograms(), mass.getInKilograms() * Math.pow(lengthOfTheMinorAxis.getInMeters() / 2, 2)));

		this.addFixture(new Circle(PhysicsBallBase.lengthOfTheMinorAxis.getInUnit(DUnits.YARD)));
		this.getFixture(0).setDensity(PhysicsBallBase.density.getInKilogramsPerCubicMeters());
		this.getFixture(0).setFriction(PhysicsBallBase.estimatedCoefficientOfFriction);
		this.getFixture(0).setRestitution(PhysicsBallBase.coefficientOfRestitution);
		this.getFixture(0).setRestitutionVelocity(4);
	}

	public Football getFootball()
	{
		return this.football;
	}

}
