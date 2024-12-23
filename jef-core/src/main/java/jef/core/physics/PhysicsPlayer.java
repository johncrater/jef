package jef.core.physics;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

import com.synerset.unitility.unitsystem.common.Velocity;
import com.synerset.unitility.unitsystem.thermodynamic.Density;

import jef.core.Conversions;
import jef.core.Player;
import jef.core.units.AngularVelocity;
import jef.core.units.VUnits;

public class PhysicsPlayer extends Body
{
	public static final Density density = Density.ofKilogramPerCubicMeter(.1096); // g/cm^3

	// coef of a polyester jersey
	public static final float coefficientOfFriction = 0.5f;
	public static final float coefficientOfRestitution = .09f; // a total guess

	// deceleration is in YPY^2. It is not a velocity
	public static final float maximumDecelerationRate = (float) Velocity.ofMetersPerSecond(6).getInUnit(VUnits.YPS);
	public static final float normalDecelerationRate = (float) Velocity.ofMetersPerSecond(3).getInUnit(VUnits.YPS);
	// turning speed in milliseconds for changing orientation. A total guess 180
	// degree turn in .25 seconds
	public static final float maximumAngularVelocity = 180 / .25f;

	// suspect players are faster. But this is a general idea
	public static final float visualReactionTime = .200f;
	public static final float auditoryReactionTime = .150f;

	private final Player player;

	public PhysicsPlayer(final Player player)
	{
		this.player = player;

		this.setLinearDamping(PhysicsPlayer.normalDecelerationRate);
		this.setMass(new Mass(new Vector2(0, 0), player.getMassInKilograms(), 0));
		this.setLinearVelocity(Conversions.yardsToMeters(player.getLinearVelocity().getX()),
				Conversions.yardsToMeters(player.getLinearVelocity().getY()));
		this.getTransform().translate(Conversions.yardsToMeters(player.getLocation().getX()),
				Conversions.yardsToMeters(player.getLocation().getY()));
	}

	public void afterUpdate(final double timeInterval)
	{
		final Vector2 newXYLocation = this.getTransform().getTranslation();
		final Vector2 newXYLinearVelocity = this.getLinearVelocity();

		this.player.setLocation(Conversions.metersToYards(newXYLocation.x), Conversions.metersToYards(newXYLocation.y),
				0.0);

		this.player.setLinearVelocity(Conversions.metersToYards(newXYLinearVelocity.y),
				Conversions.metersToYards(newXYLinearVelocity.y), 0.0);
		final double radians = this.player.getAngularVelocity().getRadiansPerSecond() * timeInterval;

		this.player.setAngularVelocity(
				new AngularVelocity((this.player.getAngularVelocity().getCurrentAngleInRadians() + radians) % Math.PI,
						this.player.getAngularVelocity().getRadiansPerSecond()));

		if (this.player.getLinearVelocity().getYZVelocity() == 0)
			this.player.setAngularVelocity(0.0, 0.0);
	}

	public void beforeUpdate(final double timeInterval)
	{
		this.setEnabled(true);
		this.setAtRest(false);

		final Vector2 vXy = new Vector2(Conversions.yardsToMeters(this.player.getLinearVelocity().getX()),
				Conversions.yardsToMeters(this.player.getLinearVelocity().getY()));
		this.setLinearVelocity(vXy);

		final Vector2 lXy = new Vector2(Conversions.yardsToMeters(this.player.getLocation().getX()),
				Conversions.yardsToMeters(this.player.getLocation().getY()));
		final Transform t = new Transform();
		t.translate(lXy);
		this.setTransform(t);
	}

	public Player getPlayer()
	{
		return this.player;
	}
}
