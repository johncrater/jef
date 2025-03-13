package jef.actions.pathfinding;

import jef.core.Direction;
import jef.core.PlayerState;
import jef.core.movement.player.Path;

public abstract class AbstractPathfinder implements Pathfinder
{
	private PlayerState playerState;
	private Direction direction;
	private Path path;

	public AbstractPathfinder(PlayerState playerState, Direction direction)
	{
		this.playerState = playerState;
		this.direction = direction;
	}

	public Path getPath()
	{
		return this.path;
	}

	public void setPath(Path path)
	{
		this.path = path;
	}

	@Override
	public PlayerState getPlayerState()
	{
		return this.playerState;
	}

	@Override
	public Direction getDirection()
	{
		return direction;
	}

	@Override
	public String toString()
	{
		return this.playerState.toString();
	}
}
