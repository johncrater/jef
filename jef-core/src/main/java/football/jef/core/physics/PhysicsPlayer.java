package football.jef.core.physics;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Vector2;

import com.synerset.unitility.unitsystem.common.Velocity;
import com.synerset.unitility.unitsystem.thermodynamic.Density;

import football.jef.core.Conversions;
import football.jef.core.Football;
import football.jef.core.Player;
import football.jef.core.units.VUnits;

public class PhysicsPlayer extends Body
{
	public static final Density density = Density.ofKilogramPerCubicMeter(.1096); // g/cm^3

	// coef of a polyester jersey
	public static final float coefficientOfFriction = 0.5f;
	public static final float coefficientOfRestitution = .09f; // a total guess

	// deceleration is in YPY^2. It is not a velocity
	public static final float maximumDecelerationRate = (float) Velocity.ofMetersPerSecond(6)
			.getInUnit(VUnits.YPS);
	public static final float normalDecelerationRate = (float) Velocity.ofMetersPerSecond(3)
			.getInUnit(VUnits.YPS);
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
		
		this.setLinearDamping(normalDecelerationRate);
		this.setMass(new Mass(new Vector2(0, 0), player.getMassInKilograms(), 0));
		this.setLinearVelocity(Conversions.yardsToMeters(player.getLinearVelocity().getX()),
				Conversions.yardsToMeters(player.getLinearVelocity().getY()));
		this.getTransform().translate(Conversions.yardsToMeters(player.getLocation().getX()),
				Conversions.yardsToMeters(player.getLocation().getY()));
	}

	public Player getPlayer()
	{
		return this.player;
	}

}
