package jef.core.collisions;

import jef.core.Randomizer;
import jef.core.movement.player.PlayerTracker;

public class BlockingResolver implements CollisionResolver
{
	private PlayerTracker blocker;
	private PlayerTracker defender;
	
	public BlockingResolver(PlayerTracker blocker, PlayerTracker defender)
	{
		super();
		this.blocker = blocker;
		this.defender = defender;
	}

	@Override
	public void resolveCollision()
	{
		double distance = blocker.getLoc().distanceBetween(defender.getLoc());
		double angle = blocker.getLV().getAzimuth() - blocker.getLoc().angleTo(defender.getLoc());
		
		double pct = (1 - distance) - (angle / (Math.PI / 2));
		if (Randomizer.nextDouble() < pct)
			CollisionResolution.resolveCollision(defender, blocker);
	}

}
