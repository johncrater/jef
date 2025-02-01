package jef.core.pathfinding.old;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.core.Field;
import jef.core.Player;
import jef.core.events.Messages;
import jef.core.geometry.LineSegment;
import jef.core.movement.DefaultLinearVelocity;
import jef.core.movement.Location;
import jef.core.movement.RelativeLocation;
import jef.core.movement.player.DefaultPath;
import jef.core.movement.player.Path;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;
import jef.core.pathfinding.DefenderAssessment;
import jef.core.pathfinding.Direction;
import jef.core.pathfinding.Pathfinder;

/**
 * A BlockingEscort stays between the runner and the biggest defensive threat
 */
public class BlockingEscort implements Pathfinder
{
	private Player runner;
	private Collection<Player> defenders;
	private Direction direction;
	private Player blocker;

	public BlockingEscort(Player blocker, Player runner, Collection<Player> defenders, Direction direction)
	{
		super();
		this.runner = runner;
		this.blocker = blocker;
		this.defenders = defenders;
		this.direction = direction;
	}

	@Override
	public Path getPath()
	{
		return this.getPath(Integer.MAX_VALUE);
	}

	@Override
	public Path getPath(int maximumNanosecondsToSpend)
	{
		Location destination = getDestination(runner, defenders);
		return new DefaultPath(new Waypoint(destination, runner.getDesiredSpeed(), DestinationAction.noStop));
	}

	private Location getDestination(final Player runner, final Collection<Player> defenders)
	{
		if (runner == null)
			return null;

		List<DefenderAssessment> threats = assessThreats(runner, runner.getPath().getDestination(), defenders);
		threats = threats.stream().sorted(Comparator.comparing(DefenderAssessment::threatLevel)).toList();

		Location blockerDestination = null;
		if (threats.size() > 0)
		{
			final Player threat = threats.get(0).getDefender();

			LineSegment threatVector = new LineSegment(threat.getLoc(),
					threat.getLoc().add(new DefaultLinearVelocity(threat.getLV().getAzimuth(), 0, Field.FIELD_TOTAL_LENGTH)));
			LineSegment runnerVector = new LineSegment(runner.getLoc(),
					runner.getLoc().add(runner.getLV().newFrom(null, null, Field.FIELD_TOTAL_LENGTH)));
			Location intersection = threatVector.xyIntersection(runnerVector);
			if (intersection != null)
			{
				blockerDestination = intersection.add(new DefaultLinearVelocity(intersection.angleTo(threat.getLoc()), 0,
					Math.min(Player.PLAYER_BLOCKING_RANGE_MAXIMUM, intersection.distanceBetween(threat.getLoc()))));
			}
		}
		
		if (blockerDestination == null)
		{
			double angle = direction == Direction.west ? 0 : Math.PI;
			blockerDestination = runner.getLoc()
					.add(new DefaultLinearVelocity(angle, 0, Player.PLAYER_BLOCKING_RANGE_MAXIMUM));
		}

		MessageManager.getInstance().dispatchMessage(Messages.drawBlockerDestination, blockerDestination);
		MessageManager.getInstance().dispatchMessage(Messages.drawBlockerPath,
				new LineSegment(blocker.getLoc(), blockerDestination));

		return blockerDestination;
	}

	private List<DefenderAssessment> assessThreats(final Player runner, final Location destination,
			final Collection<Player> defenders)
	{
		final List<DefenderAssessment> assessments = new ArrayList<>();

		defenders.forEach(p ->
		{
			final double lvDistance = getLVDistance(p, destination);
			final double a = runner.getLoc().angleTo(p.getLoc());
			assessments.add(new DefenderAssessment(p, lvDistance, RelativeLocation.getFromAngle(a, direction)));

		});

		return assessments.stream().sorted(Comparator.comparing(DefenderAssessment::threatLevel)).toList();
	}

	@Override
	public List<Location> getSteps()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Direction getDirection()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getDeltaTime()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	private static double getLVDistance(final Player player, final Location loc)
	{
		return player.getLoc().distanceBetween(loc) / player.getMaxSpeed();
	}

}
