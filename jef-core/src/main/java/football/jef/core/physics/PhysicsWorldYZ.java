package football.jef.core.physics;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.world.ContactCollisionData;
import org.dyn4j.world.World;
import org.dyn4j.world.listener.ContactListenerAdapter;

import com.synerset.unitility.unitsystem.common.Distance;

import football.jef.core.Conversions;
import football.jef.core.Player;
import football.jef.core.units.AngularVelocity;
import football.jef.core.units.DUnits;
import football.jef.core.units.Location;

class PhysicsWorldYZ extends World<Body>
{
	private class Contacts extends ContactListenerAdapter<Body>
	{

		@Override
		public void collision(final ContactCollisionData<Body> collision)
		{
			final double newAngularVelocity = PhysicsWorldYZ.this.ball.getFootball().getAngularVelocity()
					.getRadiansPerSecond() / 2;
			PhysicsWorldYZ.this.ball.getFootball()
					.setAngularVelocity(new AngularVelocity(
							PhysicsWorldYZ.this.ball.getFootball().getAngularVelocity().getCurrentAngleInRadians(),
							newAngularVelocity));
			PhysicsWorldYZ.this.ball.setAngularVelocity(newAngularVelocity);
			super.collision(collision);
		}
	}

	private PhysicsBallYZ ball;
	private final Body floor;

	public PhysicsWorldYZ()
	{
		final var settings = new Settings();
		settings.setAtRestDetectionEnabled(false);
		settings.setLinearTolerance(Distance.of(Location.EPSILON, DUnits.YARD).getInMeters());
		settings.setWarmStartingEnabled(true);
		settings.setMaximumWarmStartDistance(Distance.ofInches(1).getInMeters());
		this.setSettings(settings);

		this.setGravity(0, PhysicsWorld.gravity.getInMetersPerSecond());

		this.floor = new Body();
//		floor.setMass(new Mass(new Vector2(), 100, 0));
		this.floor.setMass(MassType.INFINITE);
		this.floor.addFixture(
				new Rectangle((float) Conversions.yardsToMeters(400), (float) Conversions.yardsToMeters(1)));
		this.floor.getTransform().translate((float) Conversions.yardsToMeters(-50),
				(float) Conversions.yardsToMeters(-.69));
		this.floor.getFixture(0).setDensity(PhysicsWorld.fieldDensity.getInKilogramsPerCubicMeters());
		this.floor.getFixture(0).setFriction(PhysicsWorld.coefficientOfFriction);
		this.floor.getFixture(0).setRestitution(PhysicsWorld.coefficientOfRestitution);
		this.floor.getFixture(0).setRestitutionVelocity(4);
		this.addBody(this.floor);

		this.addContactListener(new Contacts());
	}

	public PhysicsWorldYZ addBall(final PhysicsBallYZ ball)
	{
		this.addBody(ball);
		this.ball = ball;
		return this;
	}

	public PhysicsWorldYZ addPlayer(final PhysicsPlayer player)
	{
		this.addBody(player);
		return this;
	}

	public PhysicsWorldYZ addPlayer(final Player player)
	{
		return this.addPlayer(new PhysicsPlayer(player));
	}

	public PhysicsBallYZ getBall()
	{
		return this.ball;
	}

	public Body getFloor()
	{
		return this.floor;
	}

}
