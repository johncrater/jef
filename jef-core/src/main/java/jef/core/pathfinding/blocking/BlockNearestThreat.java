package jef.core.pathfinding.blocking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jef.core.Player;
import jef.core.movement.Location;
import jef.core.movement.RelativeLocation;
import jef.core.pathfinding.AbstractPathfinder;
import jef.core.pathfinding.AdvancedInterceptPlayer;
import jef.core.pathfinding.DefaultInterceptPlayer;
import jef.core.pathfinding.DefenderAssessment;
import jef.core.pathfinding.Direction;
import jef.core.pathfinding.Pathfinder;
import jef.core.pathfinding.defenders.DefenderPathfinder;
import jef.core.pathfinding.runners.RunnerPathfinder;

/**
 * A BlockingEscort stays between the runner and the biggest defensive threat
 */
public class BlockNearestThreat extends AbstractPathfinder implements BlockerPathfinder
{
	private DefaultInterceptPlayer interceptionPathfinder;

	public BlockNearestThreat(Player blocker, Direction direction)
	{
		super(blocker, direction);
	}

	@Override
	public void reset()
	{
		super.reset();
		interceptionPathfinder = null;
	}

	public Pathfinder getTargetPathfinder()
	{
		return this.interceptionPathfinder;
	}

	public List<Location> getSteps()
	{
		if (this.interceptionPathfinder != null)
			return this.interceptionPathfinder.getSteps();
		
		return Collections.singletonList(getPlayer().getLoc());
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
			interceptionPathfinder = new DefaultInterceptPlayer(getPlayer(), null, biggestThreat.getDefender()); 
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
			
			final double lvDistance = getLVDistance(getPlayer(), p.getPath().getDestination());
			assessments.add(new DefenderAssessment(p, lvDistance));
		});

		return assessments.stream().sorted(Comparator.comparing(DefenderAssessment::threatLevel)).toList();
	}

	private static double getLVDistance(final Player player, final Location loc)
	{
		return player.getLoc().distanceBetween(loc) / player.getMaxSpeed();
	}

}
