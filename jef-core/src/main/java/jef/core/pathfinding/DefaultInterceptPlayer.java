package jef.pathfinding;

import jef.IPlayers;
import jef.Players.PlayerSteps;
import jef.core.Direction;
import jef.core.Performance;
import jef.core.Player;
import jef.core.PlayerState;
import jef.core.movement.player.Path;
import jef.core.movement.player.PlayerTracker;
import jef.core.movement.player.Steering;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;

public class DefaultInterceptPlayer extends PathfinderBase
{
	public static final int IDEAL_INTERCEPT_TICKS_AHEAD = 0;

	private Player targetPlayer;

	public DefaultInterceptPlayer(IPlayers players, Player player, Direction direction, Player targetPlayer)
	{
		super(players, player, direction);
		this.targetPlayer = targetPlayer;
	}

	public Player getTargetPlayer()
	{
		return this.targetPlayer;
	}

	@Override
	public Path calculatePath()
	{
		PlayerSteps interceptionPoints = getPlayers().getSteps(targetPlayer);

		for (int i = 0; i < interceptionPoints.getStepCapacity(); i++)
		{
			PlayerState targetPlayerState = interceptionPoints.getState(i);
			Path path = new Path(new Waypoint(targetPlayerState.getLoc(), this.getPlayerState().getMaxSpeed(),
							DestinationAction.noStop));
			
			int ticks = Steering.getInstance()
					.calculateTicks(new PlayerTracker(this.getPlayerState(), path, Performance.frameInterval));

			if (ticks - i <= 0)
			{
				return path;
			}
		}

		// if we can't catch him at all, just run straight for him to put on a good show
		// of it
		PlayerState playerState = getPlayers().getState(targetPlayer);
		return new Path(new Waypoint(playerState.getLoc(), playerState.getMaxSpeed(), DestinationAction.noStop));
	}

}
