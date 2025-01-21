package jef.core.collisions;

import jef.core.Player;
import jef.core.Randomizer;

public class BlockingResolver implements CollisionResolver
{
	private Player blocker;
	private Player defender;
	
	public BlockingResolver(Player blocker, Player defender)
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
