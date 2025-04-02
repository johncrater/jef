package jef.pathfinding.collisions;

import jef.movement.player.PlayerState;
import jef.pathfinding.IPathfinderPlayer;
import jef.pathfinding.PathfindingState;

public class CollisionResolution<T extends IPathfinderPlayer>
{
	public ICollisionResolver createResolution(PathfindingState<T> pathfindingState, Collision collision)
	{
		PlayerState runner = getRunner(pathfindingState, collision.getOccupier1(), collision.getOccupier2());
		if (runner != null)
		{
			PlayerState defender = getDefender(pathfindingState, collision.getOccupier1(), collision.getOccupier2());
			if (defender != null)
				return new TackleResolver<T>(pathfindingState, runner, defender);
			else
				return new BumpResolver<T>(pathfindingState, collision.getOccupier1(), collision.getOccupier2(), collision.getCollisionLocation());
		}
		else
		{
			PlayerState defender = getDefender(pathfindingState, collision.getOccupier1(), collision.getOccupier2());
			if (defender != null)
			{
				PlayerState blocker = getBlocker(pathfindingState, collision.getOccupier1(), collision.getOccupier2());
				if (blocker != null)
					return new BlockingResolver<T>(pathfindingState, blocker, defender);
				else
					return new BumpResolver<T>(pathfindingState, collision.getOccupier1(), collision.getOccupier2(), collision.getCollisionLocation());
			}
			else
			{
				return new BumpResolver<T>(pathfindingState, collision.getOccupier1(), collision.getOccupier2(), collision.getCollisionLocation());
			}
		}
	}

	private PlayerState getDefender(PathfindingState<T> pathfindingState, PlayerState occupier1, PlayerState occupier2)
	{
		if (pathfindingState.getFootballState().getTeamInPossession() != pathfindingState.getPlayer(occupier1.getPlayerId()).getTeamId())
			return occupier1;

		if (pathfindingState.getFootballState().getTeamInPossession() != pathfindingState.getPlayer(occupier2.getPlayerId()).getTeamId())
			return occupier2;
		
		return null;
	}
	
	private PlayerState getBlocker(PathfindingState<T> pathfindingState, PlayerState occupier1, PlayerState occupier2)
	{
		PlayerState runner = getRunner(pathfindingState, occupier1, occupier2);
		if (runner != occupier1 && pathfindingState.getFootballState().getTeamInPossession() == pathfindingState.getPlayer(occupier1.getPlayerId()).getTeamId())
			return occupier1;

		if (runner != occupier2 && pathfindingState.getFootballState().getTeamInPossession() == pathfindingState.getPlayer(occupier2.getPlayerId()).getTeamId())
			return occupier2;
		
		return null;
	}
	
	private PlayerState getRunner(PathfindingState<T> pathfindingState, PlayerState occupier1, PlayerState occupier2)
	{
		if (pathfindingState.getFootballState().getPlayerInPossession() == occupier1.getPlayerId())
			return occupier1;
		
		if (pathfindingState.getFootballState().getPlayerInPossession() == occupier2.getPlayerId())
			return occupier2;
		
		return null;
	}
}
