package jef.pathfinding.collisions;

import jef.core.Football;
import jef.core.PlayerState;

public class CollisionResolution
{
	public static CollisionResolver createResolution(Collision collision, Football football)
	{
		PlayerState runner = getRunner(collision.getOccupier1(), collision.getOccupier2(), football);
		if (runner != null)
		{
			PlayerState defender = getDefender(collision.getOccupier1(), collision.getOccupier2());
			if (defender != null)
				return new TackleResolver(runner, defender);
			else
				return new BumpResolver(collision.getOccupier1(), collision.getOccupier2(), collision.getCollisionLocation());
		}
		else
		{
			PlayerState defender = getDefender(collision.getOccupier1(), collision.getOccupier2());
			if (defender != null)
			{
				PlayerState blocker = getBlocker(collision.getOccupier1(), collision.getOccupier2(), football);
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

	private static PlayerState getDefender(PlayerState occupier1, PlayerState occupier2)
	{
		if (occupier1.getPlayer().getCurrentPosition().getUnitType().isDefense())
			return occupier1;

		if (occupier2.getPlayer().getCurrentPosition().getUnitType().isDefense())
			return occupier2;
		
		return null;
	}
	
	private static PlayerState getBlocker(PlayerState occupier1, PlayerState occupier2, Football football)
	{
		if (occupier1.getPlayer().getCurrentPosition().getUnitType().isOffense() && football.getPlayerInPossession() != occupier1.getPlayer())
			return occupier1;

		if (occupier2.getPlayer().getCurrentPosition().getUnitType().isOffense() && football.getPlayerInPossession() != occupier2.getPlayer())
			return occupier2;
		
		return null;
	}
	
	private static PlayerState getRunner(PlayerState occupier1, PlayerState occupier2, Football football)
	{
		if (football.getPlayerInPossession() == occupier1.getPlayer())
			return occupier1;
		
		if (football.getPlayerInPossession() == occupier2.getPlayer())
			return occupier2;
		
		return null;
	}
}
