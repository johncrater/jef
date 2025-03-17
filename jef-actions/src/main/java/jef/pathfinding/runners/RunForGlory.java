package jef.pathfinding.runners;

import jef.core.Direction;
import jef.core.Field;
import jef.core.Location;
import jef.core.Player;
import jef.core.movement.player.Path;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;
import jef.pathfinding.PathfinderBase;
import jef.pathfinding.Players;

/**
 * Runner heads directly for the nearest part of the end zone
 */
public class RunForGlory extends PathfinderBase implements RunnerPathfinder
{
	public RunForGlory(Players players, Player runner, Direction direction)
	{
		super(players, runner, direction);
	}

	@Override
	public Path calculatePath()
	{
		Path path = new Path(
				new Waypoint(new Location(Field.yardLine(110, getDirection()), getPlayerState().getLoc().getY()),
						getPlayerState().getMaxSpeed(), getPlayerState().getMaxSpeed(), DestinationAction.noStop));

		return path;
	}

}
