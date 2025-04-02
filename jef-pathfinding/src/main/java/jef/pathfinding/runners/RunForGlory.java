package jef.pathfinding.runners;

import jef.geometry.Field;
import jef.geometry.Location;
import jef.movement.player.Path;
import jef.movement.player.Waypoint;
import jef.movement.player.Waypoint.DestinationAction;
import jef.pathfinding.IPathfinderPlayer;
import jef.pathfinding.IPathfinderState;
import jef.pathfinding.PathfinderBase;

/**
 * Runner heads directly for the nearest part of the end zone
 */
public class RunForGlory<T extends IPathfinderPlayer> extends PathfinderBase<T> implements IRunnerPathfinder
{
	public RunForGlory(IPathfinderState<T> pathfinderState, T runner)
	{
		super(pathfinderState, runner);
	}

	@Override
	public Path calculatePath()
	{
		Path path = new Path(
				new Waypoint(
						new Location(Field.yardLine(110, getPathfinderState().getFootballState().getCurrentOffenseDirection()),
								getPlayerState().getLoc().getY()),
						getPlayerState().getMaxSpeed(), DestinationAction.noStop));

		return path;
	}

}
