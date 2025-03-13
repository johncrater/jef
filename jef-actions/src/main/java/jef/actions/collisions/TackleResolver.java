package jef.actions.collisions;

import jef.core.Conversions;
import jef.core.PlayerState;
import jef.core.movement.Posture;

public class TackleResolver extends CollisionResolverBase
{
	private static final double MAXIMUM_TACKLE_ANGLE = Math.PI / 4;
	private static final double MAXIMUM_TACKLE_DISTANCE = 1;
	private static final double MINIMUM_BUMP_DISTANCE = 1.0 / 3.0;

	public TackleResolver(PlayerState runner, PlayerState defender)
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
			this.setPlayerState1(getRunner().newFrom(null, null, null, null, Posture.fallingDown));
			this.setPlayerState2(getDefender().newFrom(null, null, null, null, Posture.fallingDown));
		}
	}

	public PlayerState getRunner()
	{
		return this.getPlayerState1();
	}
	
	public PlayerState getDefender()
	{
		return this.getPlayerState2();
	}
}
