package jef.actions.pathfinding;

import jef.core.Direction;
import jef.core.Player;
import jef.core.PlayerState;
import jef.core.movement.player.Path;

public class PathfinderBase implements Pathfinder
{
	private final PlayerStates playerStates;
	private final Player player;
	private IPlayerSteps playerSteps;
	private final Direction direction;

	public PathfinderBase(final PlayerStates playerStates, final Player player, final Direction direction)
	{
		this.playerStates = playerStates;
		this.player = player;
		this.direction = direction;
	}

	@Override
	public void calculate()
	{
		this.playerSteps = this.playerStates.getSteps(this.player);
	}

	public Direction getDirection()
	{
		return this.direction;
	}

	public Player getPlayer()
	{
		return this.player;
	}

	public PlayerStates getPlayerStates()
	{
		return this.playerStates;
	}

	public PlayerState getPlayerState()
	{
		return this.getPlayerStates().getState(this.getPlayer());
	}

	@Override
	public IPlayerSteps getSteps()
	{
		return this.playerSteps;
	}

	@Override
	public String toString()
	{
		return this.player.toString();
	}

	protected void setPlayerSteps(Path path)
	{
		this.setPlayerSteps(new PlayerSteps(
				getPlayerState().newFrom(null, null, null, path, null),
				getPlayerStates().getStepCapacity(), getPlayerStates().getTimerInterval(), getPlayerStates().getStartOffset()));	
	}
	
	protected void setPlayerSteps(final PlayerSteps playerSteps)
	{
		this.playerSteps = playerSteps;
	}
}
