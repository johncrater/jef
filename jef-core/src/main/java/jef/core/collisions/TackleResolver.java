package jef.core.collisions;

import jef.core.Conversions;
import jef.core.Randomizer;
import jef.core.movement.Posture;
import jef.core.movement.player.PlayerTracker;

public class TackleResolver extends CollisionResolverBase
{
	private static final double MAXIMUM_TACKLE_ANGLE = Math.PI / 4;
	private static final double MAXIMUM_TACKLE_DISTANCE = 1;
	private static final double MINIMUM_BUMP_DISTANCE = 1.0 / 3.0;

	public TackleResolver(PlayerTracker runner, PlayerTracker defender)
	{
		super(runner, defender);
	}

	@Override
	public void resolveCollision()
	{
		double distance = getRunner().getLoc().distanceBetween(getDefender().getLoc());
		double angle = Conversions.normalizeAngle(getDefender().getLV().getAzimuth() - getDefender().getLoc().angleTo(getRunner().getLoc()));
		
		double distancePct = Math.clamp(0, (MAXIMUM_TACKLE_DISTANCE - distance) / MAXIMUM_TACKLE_DISTANCE, 1.0);
		double tackleAnglePct = Math.clamp(0, (MAXIMUM_TACKLE_ANGLE - Math.abs(angle)) / MAXIMUM_TACKLE_ANGLE, 1.0);
		
		super.resolveCollision();

//		double pct = distancePct * tackleAnglePct;
//		if (Randomizer.nextDouble() < pct)
		{
			getRunner().setPosture(Posture.fallingDown);
			getDefender().setPosture(Posture.fallingDown);
		}
	}

	public PlayerTracker getRunner()
	{
		return this.getPlayerTracker1();
	}
	
	public PlayerTracker getDefender()
	{
		return this.getPlayerTracker2();
	}
}
