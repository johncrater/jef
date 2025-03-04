package jef.core.collisions;

import jef.core.DefaultLinearVelocity;
import jef.core.Football;
import jef.core.LinearVelocity;
import jef.core.geometry.Vector;
import jef.core.movement.player.PlayerTracker;

public class CollisionResolution
{
	public static CollisionResolver createResolution(Collision collision)
	{
		PlayerTracker runner = getRunner(collision.getOccupier1(), collision.getOccupier2());
		if (runner != null)
		{
			PlayerTracker defender = getDefender(collision.getOccupier1(), collision.getOccupier2());
			if (defender != null)
				return new TackleResolver(runner, defender);
			else
				return new BumpResolver(collision.getOccupier1(), collision.getOccupier2(), collision.getCollisionLocation());
		}
		else
		{
			PlayerTracker defender = getDefender(collision.getOccupier1(), collision.getOccupier2());
			if (defender != null)
			{
				PlayerTracker blocker = getBlocker(collision.getOccupier1(), collision.getOccupier2());
				if (blocker != null)
					return new BlockingResolver(blocker, defender);
				else
					return new BumpResolver(collision.getOccupier1(), collision.getOccupier2(), collision.getCollisionLocation());
			}
			else
			{
				return new BumpResolver(collision.getOccupier1(), collision.getOccupier2(), collision.getCollisionLocation());
			}
		}
	}

	private static PlayerTracker getDefender(PlayerTracker occupier1, PlayerTracker occupier2)
	{
		if (occupier1.getPlayer().getCurrentPosition().getUnitType().isDefense())
			return occupier1;

		if (occupier2.getPlayer().getCurrentPosition().getUnitType().isDefense())
			return occupier2;
		
		return null;
	}
	
	private static PlayerTracker getBlocker(PlayerTracker occupier1, PlayerTracker occupier2)
	{
		if (occupier1.getPlayer().getCurrentPosition().getUnitType().isOffense() && Football.theFootball.getPlayerInPossession() != occupier1)
			return occupier1;

		if (occupier2.getPlayer().getCurrentPosition().getUnitType().isOffense() && Football.theFootball.getPlayerInPossession() != occupier2)
			return occupier2;
		
		return null;
	}
	
	private static PlayerTracker getRunner(PlayerTracker occupier1, PlayerTracker occupier2)
	{
		if (Football.theFootball.getPlayerInPossession() == occupier1.getPlayer())
			return occupier1;
		
		if (Football.theFootball.getPlayerInPossession() == occupier2.getPlayer())
			return occupier2;
		
		return null;
	}
}
