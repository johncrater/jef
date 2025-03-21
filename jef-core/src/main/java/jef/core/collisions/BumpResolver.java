package jef.core.collisions;

import jef.core.Conversions;
import jef.core.Randomizer;
import jef.core.movement.Location;
import jef.core.movement.Posture;
import jef.core.movement.player.PlayerTracker;

public class BumpResolver extends CollisionResolverBase
{
	private static final double MAXIMUM_BUMP_ANGLE = Math.PI / 4;
	private static final double MAXIMUM_BUMP_DISTANCE = .5;
	
	public BumpResolver(PlayerTracker player1, PlayerTracker player2, Location location)
	{
		super(player1, player2);
		assert player1 != null;
		assert player2 != null;
	}

	@Override
	public void resolveCollision()
	{
		double distance = getPlayerTracker1().getLoc().distanceBetween(getPlayerTracker2().getLoc());
		double angle = Conversions.normalizeAngle(getPlayerTracker2().getLV().getAzimuth() - getPlayerTracker2().getLoc().angleTo(getPlayerTracker1().getLoc()));
		
		double distancePct = Math.clamp(0, (MAXIMUM_BUMP_DISTANCE - distance) / MAXIMUM_BUMP_DISTANCE, 1.0);
		double tackleAnglePct = Math.clamp(0, (MAXIMUM_BUMP_ANGLE - Math.abs(angle)) / MAXIMUM_BUMP_ANGLE, 1.0);
		
//		super.resolveCollision();

//		double pct = distancePct * tackleAnglePct;
//		if (Randomizer.nextDouble() < pct)
		{
//			getPlayerTracker1().setPosture(Posture.fallingDown);
//			getPlayerTracker2().setPosture(Posture.fallingDown);
		}
	}
}
