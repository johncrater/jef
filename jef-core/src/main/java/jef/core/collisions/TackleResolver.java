package jef.core.collisions;

import jef.core.Randomizer;
import jef.core.movement.Posture;
import jef.core.movement.player.PlayerTracker;

public class TackleResolver implements CollisionResolver
{
	private static final double MAXIMUM_TACKLE_ANGLE = Math.PI / 4;
	private static final double MAXIMUM_TACKLE_DISTANCE = 1;

	private PlayerTracker runner;
	private PlayerTracker defender;
	
	public TackleResolver(PlayerTracker runner, PlayerTracker defender)
	{
		super();
		this.runner = runner;
		this.defender = defender;
	}

	@Override
	public void resolveCollision()
	{
		double locAngle = defender.getLoc().angleTo(runner.getLoc());
		double lv1Angle = defender.getLV().getAzimuth();
		double lv2Angle = runner.getLV().getAzimuth();
		
		if (Math.abs(locAngle - lv1Angle) > MAXIMUM_TACKLE_ANGLE && Math.abs(locAngle - lv2Angle) > MAXIMUM_TACKLE_ANGLE)
			return;
			
		double distance = runner.getLoc().distanceBetween(defender.getLoc());
		double angle = defender.getLV().getAzimuth() - defender.getLoc().angleTo(runner.getLoc());
		
		double pct = (MAXIMUM_TACKLE_DISTANCE - distance) - (angle / (Math.PI / 2));
		if (Randomizer.nextDouble() < pct)
		{
			CollisionResolution.resolveCollision(runner, defender);
			runner.setPosture(Posture.fallingDown);
			defender.setPosture(Posture.fallingDown);
		}
	}

}
