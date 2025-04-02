package jef.pathfinding;

import jef.movement.player.Path;
import jef.movement.player.PlayerState;
import jef.movement.player.PlayerTracker;
import jef.movement.player.ISteering;
import jef.movement.player.Waypoint;
import jef.movement.player.Waypoint.DestinationAction;

public class DefaultInterceptPlayer<T extends IPathfinderPlayer> extends PathfinderBase<T>
{
	public static final int IDEAL_INTERCEPT_TICKS_AHEAD = 0;

	private T targetPlayer;

	public DefaultInterceptPlayer(IPathfinderState<T> pathfinderState, T player, T targetPlayer)
	{
		super(pathfinderState, player);
		this.targetPlayer = targetPlayer;
	}

	public T getTargetPlayer()
	{
		return this.targetPlayer;
	}

	@Override
	public Path calculatePath()
	{
		IPlayerSteps interceptionPoints = getPathfinderState().getPlayerSteps(targetPlayer.getId());

		for (int i = 0; i < interceptionPoints.getStepCapacity(); i++)
		{
			PlayerState targetPlayerState = interceptionPoints.getPerceivedState(i);
			Path path = new Path(new Waypoint(targetPlayerState.getLoc(), this.getPlayerState().getMaxSpeed(),
							DestinationAction.noStop));
			
			int ticks = ISteering.getInstance()
					.calculateTicks(new PlayerTracker(this.getPlayerState(), path, this.getPathfinderState().getTimerInterval()));

			if (ticks - i <= 0)
			{
				return path;
			}
		}

		// if we can't catch him at all, just run straight for him to put on a good show
		// of it
		PlayerState playerState = getPathfinderState().getPerceivedPlayerState(targetPlayer.getId());
		return new Path(new Waypoint(playerState.getLoc(), playerState.getMaxSpeed(), DestinationAction.noStop));
	}

}
