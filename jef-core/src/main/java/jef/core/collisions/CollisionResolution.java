package jef.core.collisions;

import jef.core.Player;
import jef.core.geometry.Vector;
import jef.core.movement.DefaultLinearVelocity;
import jef.core.movement.LinearVelocity;

public class CollisionResolution
{
	public static void resolveCollision(Player occupier1, Player occupier2)
	{
		// m1 * v1 + m2 * v2 = (m1 + m2) * Vf
		// Vf = (m1 * v1 + m2 * v2) / (m1 + m2);
		double p1Mvx = occupier1.getWeight() * occupier1.getLV().getX();
		double p2Mvx = occupier2.getWeight() * occupier2.getLV().getX();
		double vxF = (p1Mvx + p2Mvx) / (occupier1.getWeight() + occupier2.getWeight());

		double p1Mvy = occupier1.getWeight() * occupier1.getLV().getY();
		double p2Mvy = occupier2.getWeight() * occupier2.getLV().getY();
		double vyF = (p1Mvy + p2Mvy) / (occupier1.getWeight() + occupier2.getWeight());

		LinearVelocity lv1 = new DefaultLinearVelocity(Vector.fromCartesianCoordinates(vxF, vyF, 0));
		LinearVelocity lv2 = new DefaultLinearVelocity(Vector.fromCartesianCoordinates(vxF, vyF, 0));

		occupier1.setLV(lv1);
		occupier2.setLV(lv2);
	}
	
	public static CollisionResolver createResolution(Collision collision)
	{
		Player runner = getRunner(collision.getOccupier1(), collision.getOccupier2());
		if (runner != null)
		{
			Player defender = getDefender(collision.getOccupier1(), collision.getOccupier2());
			if (defender != null)
				return new TackleResolver(runner, defender);
			else
				return new BumpResolver(collision.getOccupier1(), collision.getOccupier2(), collision.getCollisionLocation());
		}
		else
		{
			Player defender = getDefender(collision.getOccupier1(), collision.getOccupier2());
			if (defender != null)
			{
				Player blocker = getBlocker(collision.getOccupier1(), collision.getOccupier2());
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

	private static Player getDefender(Player occupier1, Player occupier2)
	{
		if (occupier1.getCurrentPosition().getUnitType().isDefense())
			return occupier1;

		if (occupier2.getCurrentPosition().getUnitType().isDefense())
			return occupier2;
		
		return null;
	}
	
	private static Player getBlocker(Player occupier1, Player occupier2)
	{
		if (occupier1.getCurrentPosition().getUnitType().isOffense() && occupier1.hasBall() == false)
			return occupier1;

		if (occupier1.getCurrentPosition().getUnitType().isOffense() && occupier2.hasBall() == false)
			return occupier2;
		
		return null;
	}
	
	private static Player getRunner(Player occupier1, Player occupier2)
	{
		if (occupier1.hasBall())
			return occupier1;
		
		if (occupier2.hasBall())
			return occupier2;
		
		return null;
	}
}
