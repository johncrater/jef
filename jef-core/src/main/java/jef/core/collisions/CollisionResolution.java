package jef.core.collisions;

import jef.core.Football;
import jef.core.Player;
import jef.core.geometry.Vector;
import jef.core.movement.DefaultLinearVelocity;
import jef.core.movement.LinearVelocity;
import jef.core.movement.player.Steerable;

public class CollisionResolution
{
	public static void resolveCollision(Steerable occupier1, Steerable occupier2)
	{
		double occupier1Weight = occupier1.getPlayer().getWeight();
		double occupier2Weight = occupier2.getPlayer().getWeight();
		
		// m1 * v1 + m2 * v2 = (m1 + m2) * Vf
		// Vf = (m1 * v1 + m2 * v2) / (m1 + m2);
		double p1Mvx = occupier1Weight * occupier1.getLV().getX();
		double p2Mvx = occupier2Weight * occupier2.getLV().getX();
		double vxF = (p1Mvx + p2Mvx) / (occupier1Weight + occupier2Weight);

		double p1Mvy = occupier1Weight * occupier1.getLV().getY();
		double p2Mvy = occupier2Weight * occupier2.getLV().getY();
		double vyF = (p1Mvy + p2Mvy) / (occupier1Weight + occupier2Weight);

		LinearVelocity lv1 = new DefaultLinearVelocity(Vector.fromCartesianCoordinates(vxF, vyF, 0));
		LinearVelocity lv2 = new DefaultLinearVelocity(Vector.fromCartesianCoordinates(vxF, vyF, 0));

		occupier1.setLV(lv1);
		occupier2.setLV(lv2);
	}
	
	public static CollisionResolver createResolution(Collision collision)
	{
		Steerable runner = getRunner(collision.getOccupier1(), collision.getOccupier2());
		if (runner != null)
		{
			Steerable defender = getDefender(collision.getOccupier1(), collision.getOccupier2());
			if (defender != null)
				return new TackleResolver(runner, defender);
			else
				return new BumpResolver(collision.getOccupier1(), collision.getOccupier2(), collision.getCollisionLocation());
		}
		else
		{
			Steerable defender = getDefender(collision.getOccupier1(), collision.getOccupier2());
			if (defender != null)
			{
				Steerable blocker = getBlocker(collision.getOccupier1(), collision.getOccupier2());
				if (blocker != null)
					return new BlockingResolver(blocker, defender);
				else
					return new BumpResolver(collision.getOccupier1(), collision.getOccupier2(), collision.getCollisionLocation());
			}
			else
			{
				return new BumpResolver(runner, getBlocker(collision.getOccupier1(), collision.getOccupier2()), collision.getCollisionLocation());
			}
		}
	}

	private static Steerable getDefender(Steerable occupier1, Steerable occupier2)
	{
		if (occupier1.getCurrentPosition().getUnitType().isDefense())
			return occupier1;

		if (occupier2.getCurrentPosition().getUnitType().isDefense())
			return occupier2;
		
		return null;
	}
	
	private static Steerable getBlocker(Steerable occupier1, Steerable occupier2)
	{
		if (occupier1.getCurrentPosition().getUnitType().isOffense() && Football.theFootball.getPlayerInPossession() != occupier1)
			return occupier1;

		if (occupier1.getCurrentPosition().getUnitType().isOffense() && Football.theFootball.getPlayerInPossession() != occupier2)
			return occupier2;
		
		return null;
	}
	
	private static Steerable getRunner(Steerable occupier1, Steerable occupier2)
	{
		if (Football.theFootball.getPlayerInPossession() == occupier1)
			return occupier1;
		
		if (Football.theFootball.getPlayerInPossession() == occupier2)
			return occupier2;
		
		return null;
	}
}
