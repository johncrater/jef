package jef.pathfinding;

import jef.geometry.Direction;
import jef.movement.player.PlayerState;

public abstract class PathfinderBase<T extends IPathfinderPlayer> implements IPathfinder<T>
{
	private final IPathfinderState<T> pathfinderState;
	private final T player;

	public PathfinderBase(final IPathfinderState<T> pathfinderState, final T player)
	{
		this.pathfinderState = pathfinderState;
		this.player = player;
	}

	public T getPlayer()
	{
		return this.player;
	}

	public IPathfinderState<T> getPathfinderState()
	{
		return this.pathfinderState;
	}

	public Direction getDirection()
	{
		return getPathfinderState().getFootballState().getCurrentOffenseDirection();
	}
	
	public PlayerState getPlayerState()
	{
		return this.getPathfinderState().getPlayerState(this.getPlayer().getId());
	}

	public double getMaxSpeed()
	{
		return this.player.getSpeedMatrix().getSprintingSpeed();
	}
	
	@Override
	public String toString()
	{
		return this.player.toString();
	}

}
