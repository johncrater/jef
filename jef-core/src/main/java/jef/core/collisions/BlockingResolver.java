package jef.core.collisions;

import jef.core.Conversions;
import jef.core.Randomizer;
import jef.core.movement.Posture;
import jef.core.movement.player.PlayerTracker;

public class BlockingResolver extends CollisionResolverBase
{
	private static final double MAXIMUM_BLOCKING_ANGLE = Math.PI / 2;
	private static final double MAXIMUM_BLOCKING_DISTANCE = 1;

	public BlockingResolver(PlayerTracker blocker, PlayerTracker defender)
	{
		super(blocker, defender);
	}

	@Override
	public void resolveCollision()
	{
		double distance = getBlocker().getLoc().distanceBetween(getDefender().getLoc());
		double angle = Conversions
				.normalizeAngle(getDefender().getLV().getAzimuth() - getDefender().getLoc().angleTo(getBlocker().getLoc()));

		double distancePct = Math.clamp(0, (MAXIMUM_BLOCKING_DISTANCE - distance) / MAXIMUM_BLOCKING_DISTANCE, 1.0);
		double tackleAnglePct = Math.clamp(0, (MAXIMUM_BLOCKING_ANGLE - Math.abs(angle)) / MAXIMUM_BLOCKING_ANGLE, 1.0);

		super.resolveCollision();

//		double pct = distancePct * tackleAnglePct;
//		if (Randomizer.nextDouble() < pct)
		{
			getBlocker().setPosture(Posture.fallingDown);
			getDefender().setPosture(Posture.fallingDown);
		}
	}

	public PlayerTracker getBlocker()
	{
		return this.getPlayerTracker1();
	}

	public PlayerTracker getDefender()
	{
		return this.getPlayerTracker2();
	}

}
