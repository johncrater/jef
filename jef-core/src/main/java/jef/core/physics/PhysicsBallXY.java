package jef.core.physics;

import jef.core.Conversions;
import jef.core.Football;

class PhysicsBallXY extends PhysicsBallBase
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
