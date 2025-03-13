package jef.actions.collisions;

import jef.core.Conversions;
import jef.core.Location;
import jef.core.PlayerState;

public class BumpResolver extends CollisionResolverBase
{
	private static final double MAXIMUM_BUMP_ANGLE = Math.PI / 4;
	private static final double MAXIMUM_BUMP_DISTANCE = .5;
	
	public BumpResolver(PlayerState player1, PlayerState player2, Location location)
	{
		super(player1, player2);
		assert player1 != null;
		assert player2 != null;
	}

	@Override
	public void resolveCollision()
	{
		double distance = getPlayerState1().getLoc().distanceBetween(getPlayerState2().getLoc());
		double angle = Conversions.normalizeAngle(getPlayerState2().getLV().getAzimuth() - getPlayerState2().getLoc().angleTo(getPlayerState1().getLoc()));
		
		double distancePct = Math.clamp(0, (MAXIMUM_BUMP_DISTANCE - distance) / MAXIMUM_BUMP_DISTANCE, 1.0);
		double tackleAnglePct = Math.clamp(0, (MAXIMUM_BUMP_ANGLE - Math.abs(angle)) / MAXIMUM_BUMP_ANGLE, 1.0);
		
//		super.resolveCollision();

//		double pct = distancePct * tackleAnglePct;
//		if (Randomizer.nextDouble() < pct)
		{
//			getPlayerState1().setPosture(Posture.fallingDown);
//			getPlayerState2().setPosture(Posture.fallingDown);
		}
	}
}
