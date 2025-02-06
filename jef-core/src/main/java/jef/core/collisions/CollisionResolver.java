package jef.core.collisions;

import jef.core.movement.player.PlayerTracker;

public interface CollisionResolver
{
	public void resolveCollision();
	public PlayerTracker getPlayerTracker1();
	public PlayerTracker getPlayerTracker2();
}
