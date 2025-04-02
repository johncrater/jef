package jef.pathfinding.collisions;

import jef.movement.player.PlayerState;

public interface ICollisionResolver
{
	public void resolveCollision();
	public PlayerState getPlayerState1();
	public PlayerState getPlayerState2();
}
