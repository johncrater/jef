package jef.pathfinding;

import jef.core.Direction;
import jef.core.Player;
import jef.core.PlayerState;

public abstract class PathfinderBase implements Pathfinder
{
	private final Players players;
	private final Player player;
	private final Direction direction;

	public PathfinderBase(final Players players, final Player player, final Direction direction)
	{
		this.players = players;
		this.player = player;
		this.direction = direction;
	}

	public Direction getDirection()
	{
		return this.direction;
	}

	public Player getPlayer()
	{
		return this.player;
	}

	public Players getPlayers()
	{
		return this.players;
	}

	public PlayerState getPlayerState()
	{
		return this.getPlayers().getState(this.getPlayer());
	}

	@Override
	public String toString()
	{
		return this.player.toString();
	}

}
