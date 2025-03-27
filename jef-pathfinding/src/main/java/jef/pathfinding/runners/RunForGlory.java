package jef.pathfinding.runners;

import jef.core.Player;
import jef.geometry.Direction;
import jef.geometry.Field;
import jef.geometry.Location;
import jef.movement.player.Path;
import jef.movement.player.Waypoint;
import jef.movement.player.Waypoint.DestinationAction;
import jef.pathfinding.IPlayers;
import jef.pathfinding.PathfinderBase;

/**
 * Runner heads directly for the nearest part of the end zone
 */
public class RunForGlory extends PathfinderBase implements RunnerPathfinder
{
	public RunForGlory(IPlayers players, Player runner, Direction direction)
	{
		super(players, runner, direction);
	}

	@Override
	public Path calculatePath()
	{
		Path path = new Path(
				new Waypoint(new Location(Field.yardLine(110, getDirection()), getPlayerState().getLoc().getY()),
						getPlayerState().getPlayer().getMaxSpeed(), DestinationAction.noStop));

		return path;
	}

}
