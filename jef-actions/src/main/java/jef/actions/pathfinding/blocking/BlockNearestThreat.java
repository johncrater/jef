package jef.actions.pathfinding.blocking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jef.actions.pathfinding.DefaultInterceptPlayer;
import jef.actions.pathfinding.DefenderAssessment;
import jef.actions.pathfinding.Pathfinder;
import jef.actions.pathfinding.PathfinderBase;
import jef.actions.pathfinding.PlayerStates;
import jef.actions.pathfinding.defenders.DefenderPathfinder;
import jef.actions.pathfinding.runners.RunnerPathfinder;
import jef.core.Direction;
import jef.core.Location;
import jef.core.Player;
import jef.core.PlayerState;

/**
 * A BlockingEscort stays between the runner and the biggest defensive threat
 */
public class BlockNearestThreat extends PathfinderBase implements BlockerPathfinder
{
	private Player runner;
	pri
	public BlockNearestThreat(PlayerStates playerStates, Player blocker, Direction direction)
	{
		super(playerStates, blocker, direction);
	}

	@Override
	public boolean calculate()
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
