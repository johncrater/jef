package football.jef.core.physics;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.world.World;

import com.synerset.unitility.unitsystem.common.Distance;

import football.jef.core.Player;
import football.jef.core.units.DUnits;
import football.jef.core.units.Location;

public class PhysicsWorldXY extends World<Body>
{
	private PhysicsBallXY ball;

	public PhysicsWorldXY()
	{
		final var settings = new Settings();
		settings.setAtRestDetectionEnabled(true);
		settings.setLinearTolerance(Distance.of(Location.EPSILON, DUnits.YARD).getInMeters());
		settings.setWarmStartingEnabled(true);
		settings.setMaximumWarmStartDistance(Distance.ofInches(1).getInMeters());
		this.setSettings(settings);

		this.setGravity(0, 0);
	}

	public PhysicsWorldXY addBall(final PhysicsBallXY ball)
	{
		this.addBody(ball);
		this.ball = ball;
		return this;
	}

	public PhysicsWorldXY addPlayer(final PhysicsPlayer player)
	{
		this.addBody(player);
		return this;
	}

	public PhysicsWorldXY addPlayer(final Player player)
	{
		return this.addPlayer(new PhysicsPlayer(player));
	}

	public PhysicsBallXY getBall()
	{
		return this.ball;
	}
}
