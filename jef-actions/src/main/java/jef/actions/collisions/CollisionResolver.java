package jef.actions.collisions;

import jef.core.PlayerState;

public interface CollisionResolver
{
	public void resolveCollision();
	public PlayerState getPlayerState1();
	public PlayerState getPlayerState2();
}
