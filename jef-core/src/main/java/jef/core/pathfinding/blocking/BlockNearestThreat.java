package jef.core.pathfinding.blocking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import jef.core.Player;
import jef.core.movement.Location;
import jef.core.movement.RelativeLocation;
import jef.core.pathfinding.AbstractPathfinder;
import jef.core.pathfinding.DefenderAssessment;
import jef.core.pathfinding.Direction;
import jef.core.pathfinding.InterceptPlayer;
import jef.core.pathfinding.Pathfinder;
import jef.core.pathfinding.defenders.DefenderPathfinder;
import jef.core.pathfinding.runners.RunnerPathfinder;

/**
 * A BlockingEscort stays between the runner and the biggest defensive threat
 */
public class BlockNearestThreat extends AbstractPathfinder implements BlockerPathfinder
{
	private Pathfinder interceptionPathfinder;

	public BlockNearestThreat(Player blocker, Direction direction, double deltaTime)
	{
		super(blocker, direction, deltaTime);
	}

	@Override
	public void reset()
	{
		super.reset();
		interceptionPathfinder = null;
	}

	@Override
	public boolean calculate(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders, List<? extends BlockerPathfinder> blockers)
	{
		if (interceptionPathfinder == null)
		{
			List<DefenderAssessment> threats = assessThreats(runner, runner.getPath().getDestination(), defenders);
			threats = threats.stream().sorted(Comparator.comparing(DefenderAssessment::threatLevel)).toList();

			DefenderAssessment biggestThreat = threats.get(0);
			interceptionPathfinder = new InterceptPlayer(getPlayer(), getDirection().opposite(), getDeltaTime(), biggestThreat.getDefender()); 
		}

		return interceptionPathfinder.calculate(runner, defenders, blockers);
	}

	private List<DefenderAssessment> assessThreats(final Pathfinder runner, final Location destination,
			final Collection<? extends Pathfinder> defenders)
	{
		final List<DefenderAssessment> assessments = new ArrayList<>();

		defenders.forEach(p ->
		{
			final double lvDistance = getLVDistance(p.getPlayer(), destination);
			final double a = runner.getPlayer().getLoc().angleTo(p.getPlayer().getLoc());
			assessments.add(new DefenderAssessment(p, lvDistance, RelativeLocation.getFromAngle(a, getDirection())));

		});

		return assessments.stream().sorted(Comparator.comparing(DefenderAssessment::threatLevel)).toList();
	}

	private static double getLVDistance(final Player player, final Location loc)
	{
		return player.getLoc().distanceBetween(loc) / player.getMaxSpeed();
	}

}
