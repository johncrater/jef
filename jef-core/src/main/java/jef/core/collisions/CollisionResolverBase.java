package jef.core.collisions;

import java.util.Objects;

import jef.core.geometry.Vector;
import jef.core.movement.DefaultLinearVelocity;
import jef.core.movement.LinearVelocity;
import jef.core.movement.player.PlayerTracker;

public class CollisionResolverBase implements CollisionResolver
{
	private PlayerTracker playerTracker1;
	private PlayerTracker playerTracker2;

	public CollisionResolverBase(PlayerTracker playerTracker1, PlayerTracker playerTracker2)
	{
		super();
		this.playerTracker1 = playerTracker1;
		this.playerTracker2 = playerTracker2;
	}

	@Override
	public PlayerTracker getPlayerTracker1()
	{
		return this.playerTracker1;
	}

	@Override
	public PlayerTracker getPlayerTracker2()
	{
		return this.playerTracker2;
	}

	public void resolveCollision()
	{
		double playerTracker1Weight = playerTracker1.getPlayer().getWeight();
		double playerTracker2Weight = playerTracker2.getPlayer().getWeight();

		// m1 * v1 + m2 * v2 = (m1 + m2) * Vf
		// Vf = (m1 * v1 + m2 * v2) / (m1 + m2);
		double p1Mvx = playerTracker1Weight * playerTracker1.getLV().getX();
		double p2Mvx = playerTracker2Weight * playerTracker2.getLV().getX();
		double vxF = (p1Mvx + p2Mvx) / (playerTracker1Weight + playerTracker2Weight);

		double p1Mvy = playerTracker1Weight * playerTracker1.getLV().getY();
		double p2Mvy = playerTracker2Weight * playerTracker2.getLV().getY();
		double vyF = (p1Mvy + p2Mvy) / (playerTracker1Weight + playerTracker2Weight);

		LinearVelocity lv1 = new DefaultLinearVelocity(Vector.fromCartesianCoordinates(vxF, vyF, 0));
		LinearVelocity lv2 = new DefaultLinearVelocity(Vector.fromCartesianCoordinates(vxF, vyF, 0));

		playerTracker1.setLV(lv1);
		playerTracker2.setLV(lv2);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(playerTracker1, playerTracker2);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		CollisionResolverBase other = (CollisionResolverBase) obj;
		return Objects.equals(this.playerTracker1, other.playerTracker1)
				&& Objects.equals(this.playerTracker2, other.playerTracker2);
	}

}