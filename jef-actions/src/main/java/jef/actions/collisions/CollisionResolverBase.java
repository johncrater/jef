package jef.actions.collisions;

import java.util.Objects;

import jef.core.LinearVelocity;
import jef.core.PlayerState;
import jef.core.geometry.Vector;

public class CollisionResolverBase implements CollisionResolver
{
	private PlayerState playerState1;
	private PlayerState playerState2;

	public CollisionResolverBase(PlayerState playerState1, PlayerState playerState2)
	{
		super();
		this.playerState1 = playerState1;
		this.playerState2 = playerState2;
	}

	@Override
	public PlayerState getPlayerState1()
	{
		return this.playerState1;
	}

	@Override
	public PlayerState getPlayerState2()
	{
		return this.playerState2;
	}

	protected void setPlayerState1(PlayerState playerState1)
	{
		this.playerState1 = playerState1;
	}

	protected void setPlayerState2(PlayerState playerState2)
	{
		this.playerState2 = playerState2;
	}

	public void resolveCollision()
	{
		double playerTracker1Weight = playerState1.getPlayer().getWeight();
		double playerTracker2Weight = playerState2.getPlayer().getWeight();

		// m1 * v1 + m2 * v2 = (m1 + m2) * Vf
		// Vf = (m1 * v1 + m2 * v2) / (m1 + m2);
		double p1Mvx = playerTracker1Weight * playerState1.getLV().getX();
		double p2Mvx = playerTracker2Weight * playerState2.getLV().getX();
		double vxF = (p1Mvx + p2Mvx) / (playerTracker1Weight + playerTracker2Weight);

		double p1Mvy = playerTracker1Weight * playerState1.getLV().getY();
		double p2Mvy = playerTracker2Weight * playerState2.getLV().getY();
		double vyF = (p1Mvy + p2Mvy) / (playerTracker1Weight + playerTracker2Weight);

		LinearVelocity lv1 = new LinearVelocity(Vector.fromCartesianCoordinates(vxF, vyF, 0));
		LinearVelocity lv2 = new LinearVelocity(Vector.fromCartesianCoordinates(vxF, vyF, 0));

		this.playerState1 = this.playerState1.newFrom(lv1, null, null, null, null);	
		this.playerState2 = this.playerState1.newFrom(lv2, null, null, null, null);	
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(playerState1, playerState2);
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
		return Objects.equals(this.playerState1, other.playerState1)
				&& Objects.equals(this.playerState2, other.playerState2);
	}

}