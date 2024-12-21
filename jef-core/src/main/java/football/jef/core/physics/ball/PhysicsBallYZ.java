package football.jef.core.physics.ball;

import football.jef.core.Conversions;
import football.jef.core.Football;

public class PhysicsBallYZ extends PhysicsBallBase
{
	public PhysicsBallYZ(final Football ball)
	{
		super(ball);

		this.setLinearVelocity(Conversions.yardsToMeters(ball.getLinearVelocity().getY()),
				Conversions.yardsToMeters(ball.getLinearVelocity().getZ()));
		this.getTransform().translate(Conversions.yardsToMeters(ball.getLocation().getY()),
				Conversions.yardsToMeters(ball.getLocation().getZ()));
	}

}
