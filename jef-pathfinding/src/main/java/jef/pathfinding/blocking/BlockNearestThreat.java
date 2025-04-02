package jef.pathfinding.blocking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jef.movement.player.Path;
import jef.pathfinding.DefaultInterceptPlayer;
import jef.pathfinding.IPathfinderPlayer;
import jef.pathfinding.IPlayerSteps;
import jef.pathfinding.PathfinderBase;
import jef.pathfinding.PathfindingState;
import jef.pathfinding.threats.ThreatAssessment;

/**
 * A BlockingEscort stays between the runner and the biggest defensive threat
 */
public class BlockNearestThreat<T extends IPathfinderPlayer> extends PathfinderBase<T> implements IBlockerPathfinder
{
	public enum Option
	{
		distance, interception, distanceToRunner
	}

	private T runner;
	private Collection<T> defenders;
	private Option option;

	public BlockNearestThreat(PathfindingState<T> playerStates, T runner, T blocker,
			Collection<T> defenders, Option option)
	{
		super(playerStates, blocker);
		this.runner = runner;
		this.defenders = defenders;
		this.option = option;
	}

	@Override
	public Path calculatePath()
	{
		List<ThreatAssessment<T>> threats = assessThreats();
		if (threats.size() == 0)
			return null;

		threats.sort(null);

		ThreatAssessment<T> biggestThreat = threats.get(0);
		return new DefaultInterceptPlayer<T>(getPathfinderState(), getPlayer(), biggestThreat.getPlayer()).calculatePath();
	}

	private List<ThreatAssessment<T>> assessThreats()
	{
		final List<ThreatAssessment<T>> assessments = new ArrayList<>();

		defenders.forEach(p ->
		{
			assessments.add(getThreatAssessment(p));
		});

		return assessments;
	}

	private ThreatAssessment<T> getThreatAssessment(final T defender)
	{
		switch (option)
		{
			case distance:
				return new ThreatAssessment<T>(defender, -1 * this.getPathfinderState().getPlayerState(getPlayer().getId())
						.getLoc().distanceBetween(this.getPathfinderState().getPlayerState(defender.getId()).getLoc()));
			case distanceToRunner:
				new ThreatAssessment<T>(defender, -1 * this.getPathfinderState().getPlayerState(runner.getId()).getLoc()
						.distanceBetween(this.getPathfinderState().getPlayerState(defender.getId()).getLoc()));
			case interception:
				DefaultInterceptPlayer<T> intercept = new DefaultInterceptPlayer<T>(this.getPathfinderState(), this.getPlayer(),
						defender);
				Path pathToTarget = intercept.calculatePath();
				IPlayerSteps steps = this.getPathfinderState().createSteps(getPlayerState(), pathToTarget);
				return new ThreatAssessment<T>(defender, -1 * steps.getDestinationReachedSteps());
			default:
				assert false;
				return null;
		}
	}

}
