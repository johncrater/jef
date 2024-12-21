package football.jef.core.physics;

import football.jef.core.Conversions;
import football.jef.core.Football;

public class PhysicsBallXY extends PhysicsBallBase
{
	public PhysicsBallXY(final Football ball)
	{
		super(ball);
		this.setLinearVelocity(Conversions.yardsToMeters(ball.getLinearVelocity().getX()),
				Conversions.yardsToMeters(ball.getLinearVelocity().getY()));
		this.getTransform().translate(Conversions.yardsToMeters(ball.getLocation().getX()),
				Conversions.yardsToMeters(ball.getLocation().getY()));
	}
}
