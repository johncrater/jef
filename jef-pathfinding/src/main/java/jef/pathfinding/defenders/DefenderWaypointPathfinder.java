package jef.pathfinding.defenders;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.core.Player;
import jef.core.events.Messages;
import jef.geometry.Direction;
import jef.movement.DebugShape;
import jef.movement.player.Path;
import jef.pathfinding.IPlayers;
import jef.pathfinding.PathfinderBase;

public class DefenderWaypointPathfinder extends PathfinderBase implements DefenderPathfinder
{

	public DefenderWaypointPathfinder(IPlayers players, Player player, Direction direction)
	{
		super(players, player, direction);
	}


	@Override
	public Path calculatePath()
	{
		Path path = this.getPlayers().getSteps(getPlayer()).getPath();
		MessageManager.getInstance().dispatchMessage(Messages.drawDebugShape, DebugShape.drawPath(this.getPlayerState().getLoc(), path, "#FF000000"));
		return path;
	}

}
