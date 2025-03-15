package jef.actions.pathfinding;

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
	public static final int IDEAL_INTERCEPT_TICKS_AHEAD = 2;

	private Player targetPlayer;

	public DefaultInterceptPlayer(PlayerStates players, Player player, Direction direction, Player targetPlayer)
	{
		super(players, player, direction);
		this.targetPlayer = targetPlayer;
	}

	public Player getTargetPlayer()
	{
		return this.targetPlayer;
	}

	@Override
	public void calculate()
	{
		IPlayerSteps interceptionPoints = getPlayerStates().getSteps(targetPlayer);
		
		for (int i = 0; i < interceptionPoints.getCapacity(); i++)
		{
			PlayerState playerState = interceptionPoints.getState(i);
			playerState = playerState.newFrom(null, null, null, new Path(new Waypoint(playerState.getLoc(), playerState.getMaxSpeed(), DestinationAction.noStop)), null);
			int ticks = Steering.getInstance().calculateTicks(new PlayerTracker(playerState,
					Performance.frameInterval));
			
			if (ticks - i <= IDEAL_INTERCEPT_TICKS_AHEAD)
			{
				this.setPlayerSteps(playerState.getPath());
				return;
			}
		}
		
		PlayerState playerState = getPlayerStates().getState(targetPlayer);
		this.setPlayerSteps(new Path(new Waypoint(playerState.getLoc(), playerState.getMaxSpeed(), DestinationAction.noStop)));
	}

}
