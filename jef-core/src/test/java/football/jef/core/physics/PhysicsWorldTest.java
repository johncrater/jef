package football.jef.core.physics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import football.jef.core.Football;
import football.jef.core.TestBall;

class PhysicsWorldTest
{
	private static final double TIME_INTERVAL = .01;

	@Test
	void testBall()
	{
		PhysicsWorld physicsWorld = new PhysicsWorld();
		Football ball = new TestBall();
		ball.setLinearVelocity(5.0, 5.0, 5.0);
		physicsWorld.addBall(ball);
		
		assertEquals(5, ball.getLinearVelocity().getX());
		assertEquals(5, ball.getLinearVelocity().getY());
		assertEquals(5, ball.getLinearVelocity().getZ());

		// we must use a small step amount due to max translation setting in world.
		physicsWorld.update(TIME_INTERVAL);
		
		assertEquals(5, ball.getLinearVelocity().getX());
		assertEquals(5, ball.getLinearVelocity().getY());
		assertEquals(4.89, ball.getLinearVelocity().getZ());

		assertEquals(5 * TIME_INTERVAL, ball.getLocation().getX());
		assertEquals(5 * TIME_INTERVAL, ball.getLocation().getY());
		assertEquals(5 * TIME_INTERVAL, ball.getLocation().getZ());

		// we must use a small step amount due to max translation setting in world.
		physicsWorld.update(TIME_INTERVAL * 10);
		
		assertEquals(5, ball.getLinearVelocity().getX());
		assertEquals(5, ball.getLinearVelocity().getY());
		assertEquals(3.93, ball.getLinearVelocity().getZ());

		assertEquals(5 * 11 * TIME_INTERVAL, ball.getLocation().getX());
		assertEquals(5 * 11 * TIME_INTERVAL, ball.getLocation().getY());
		assertEquals(.44, ball.getLocation().getZ());

	}

}
