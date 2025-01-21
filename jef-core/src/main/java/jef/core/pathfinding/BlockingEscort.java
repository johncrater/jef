  package jef.core.pathfinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import jef.core.Player;
import jef.core.movement.DefaultLinearVelocity;
import jef.core.movement.Location;
import jef.core.movement.player.DefaultPath;
import jef.core.movement.player.Path;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;

public class BlockingEscort implements Pathfinder
{
	private Player runner;
	private Collection<Player> defenders;
	private Direction direction;
	
	public BlockingEscort(Player runner, Collection<Player> defenders, Direction direction)
	{
		super();
		this.runner = runner;
		this.defenders = defenders;
		this.direction = direction;
	}

	@Override
	public Path findPath()
	{
		return this.findPath(Double.MAX_VALUE);
	}

	@Override
	public Path findPath(double maximumTimeToSpend)
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

		if (threats.size() > 0)
		{
			final Player threat = threats.get(0).getDefender();
			final Location threatLocation = threat.getLoc();

			final var fromRunnerToDefender = runner.getLoc()
					.angleTo(threats.get(0).getDefender().getLoc());
			final var distanceFromRunnerToDefender = runner.getLoc().distanceBetween(threatLocation);
			return runner.getLoc().add(new DefaultLinearVelocity(fromRunnerToDefender, 0, distanceFromRunnerToDefender / 2.0));
		}
		else
		{
			double angle = direction == Direction.west ? 0 : Math.PI;
			return runner.getLoc().add(new DefaultLinearVelocity(angle, 0, Player.PLAYER_BLOCKING_RANGE_MINIMAL));
		}
	}

	private List<DefenderAssessment> assessThreats(final Player runner, final Location destination,
			final Collection<Player> defenders)
	{
		final List<DefenderAssessment> assessments = new ArrayList<>();

		defenders.forEach(p ->
		{
			final double lvDistance = getLVDistance(p, destination);
			final double a = runner.getLoc().angleTo(p.getLoc());
			assessments.add(new DefenderAssessment(p, lvDistance, DefenderAssessment.Quadrant.getFromAngle(a, direction)));

		});

		return assessments.stream().sorted(Comparator.comparing(DefenderAssessment::threatLevel)).toList();
	}

	private static double getLVDistance(final Player player, final Location loc)
	{
		return player.getLoc().distanceBetween(loc) / player.getMaxSpeed();
	}

}
