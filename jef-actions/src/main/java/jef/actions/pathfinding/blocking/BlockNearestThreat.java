package jef.actions.pathfinding.blocking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jef.actions.pathfinding.AbstractPathfinder;
import jef.actions.pathfinding.DefaultInterceptPlayer;
import jef.actions.pathfinding.DefenderAssessment;
import jef.actions.pathfinding.Pathfinder;
import jef.actions.pathfinding.defenders.DefenderPathfinder;
import jef.actions.pathfinding.runners.RunnerPathfinder;
import jef.core.Direction;
import jef.core.Location;
import jef.core.PlayerState;

/**
 * A BlockingEscort stays between the runner and the biggest defensive threat
 */
public class BlockNearestThreat extends AbstractPathfinder implements BlockerPathfinder
{
	private DefaultInterceptPlayer interceptionPathfinder;

	public BlockNearestThreat(PlayerState blocker, Direction direction)
	{
		super(blocker, direction);
	}

	public Pathfinder getTargetPathfinder()
	{
		return this.interceptionPathfinder;
	}

	public List<Location> getSteps()
	{
		if (this.interceptionPathfinder != null)
			return this.interceptionPathfinder.getSteps();
		
		return Collections.singletonList(getPlayerState().getLoc());
	}

	@Override
	public boolean calculate(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders, List<? extends BlockerPathfinder> blockers, long deltaNanos)
	{
		if (runner.getPath() == null)
			return true;
		
		long nanos = System.nanoTime();
		
		if (interceptionPathfinder == null)
		{
			List<DefenderAssessment> threats = assessThreats(defenders);
			if (threats.size() == 0)
				return true;
			
			DefenderAssessment biggestThreat = threats.get(0);
			interceptionPathfinder = new DefaultInterceptPlayer(getPlayerState(), null, biggestThreat.getDefender()); 
		}

		boolean ret = interceptionPathfinder.calculate(runner, defenders, blockers, deltaNanos - (System.nanoTime() - nanos));
		this.setPath(interceptionPathfinder.getPath());
		
		return ret;
	}

	private List<DefenderAssessment> assessThreats(final Collection<? extends Pathfinder> defenders)
	{
		final List<DefenderAssessment> assessments = new ArrayList<>();

		defenders.forEach(p ->
		{
			if (p.getPath() == null)
				return;
			
			final double lvDistance = getLVDistance(getPlayerState(), p.getPath().getDestination());
			assessments.add(new DefenderAssessment(p, lvDistance));
		});

		return assessments.stream().sorted(Comparator.comparing(DefenderAssessment::threatLevel)).toList();
	}

	private static double getLVDistance(final PlayerState player, final Location loc)
	{
		return player.getLoc().distanceBetween(loc) / player.getMaxSpeed();
	}

}
