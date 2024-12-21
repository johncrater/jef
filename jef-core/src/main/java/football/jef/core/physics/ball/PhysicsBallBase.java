package football.jef.core.physics.ball;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Vector2;

import football.jef.core.Football;
import football.jef.core.units.DUnits;

public class PhysicsBallBase extends Body
{
	private final Football ball;

	public PhysicsBallBase(final Football ball)
	{
		this.ball = ball;
		this.setBullet(true);
		this.setAngularVelocity(ball.getAngularVelocity().getRadiansPerSecond());

		this.setMass(new Mass(new Vector2(0, 0), Football.mass.getInKilograms(), 0));

//		this.setMass(new Mass(
//				new Vector2(0, 0),
//				Football.mass.getInKilograms(), Football.mass.getInKilograms() * Math.pow(Football.lengthOfTheMinorAxis.getInMeters() / 2, 2)));

		this.addFixture(new Circle(Football.lengthOfTheMinorAxis.getInUnit(DUnits.YARD)));
		this.getFixture(0).setDensity(Football.density.getInKilogramsPerCubicMeters());
		this.getFixture(0).setFriction(Football.estimatedCoefficientOfFriction);
		this.getFixture(0).setRestitution(Football.coefficientOfRestitution);
		this.getFixture(0).setRestitutionVelocity(4);
	}

	public Football getBall()
	{
		return this.ball;
	}

}
