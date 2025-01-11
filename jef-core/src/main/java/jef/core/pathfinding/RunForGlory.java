package jef.core.pathfinding;

import java.util.List;

import jef.core.Field;
import jef.core.Player;
import jef.core.movement.DefaultLocation;
import jef.core.movement.player.DefaultPath;
import jef.core.movement.player.Path;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;

public class RunForGlory implements Pathfinder
{
	private Player runner;
	private Direction direction;
	
	public RunForGlory(Player runner, Direction direction)
	{
		super();
		this.runner = runner;
		this.direction = direction;
	}

	@Override
	public Path findPath()
	{
		return new DefaultPath(new Waypoint(new DefaultLocation(Field.yardLine(100, direction), runner.getLoc().getY()),
					runner.getMaxSpeed(), DestinationAction.noStop));
	}

	@Override
	public Path findPath(double maximumTimeToSpend)
	{
		return findPath();
	}
}
