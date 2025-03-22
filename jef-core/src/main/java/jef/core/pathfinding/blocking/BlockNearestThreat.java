package jef.pathfinding.blocking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jef.Players;
import jef.Players.PlayerSteps;
import jef.core.Direction;
import jef.core.Player;
import jef.core.movement.player.Path;
import jef.pathfinding.DefaultInterceptPlayer;
import jef.pathfinding.PathfinderBase;
import jef.pathfinding.threats.ThreatAssessment;

/**
 * A BlockingEscort stays between the runner and the biggest defensive threat
 */
public class BlockNearestThreat extends PathfinderBase implements BlockerPathfinder
{
	public enum Option
	{
		distance, interception, distanceToRunner
	}

	private Player runner;
	private Collection<Player> defenders;
	private Option option;

	public BlockNearestThreat(Players playerStates, Player runner, Player blocker, Collection<Player> defenders,
			Option option, Direction direction)
	{
		super(playerStates, blocker, direction);
		this.runner = runner;
		this.defenders = defenders;
		this.option = option;
	}

	@Override
	public Path calculatePath()
	{
		List<ThreatAssessment> threats = assessThreats();
		if (threats.size() == 0)
			return null;

		threats.sort(null);
		
		ThreatAssessment biggestThreat = threats.get(0);
		return new DefaultInterceptPlayer(getPlayers(), getPlayer(), getDirection(), biggestThreat.getPlayer()).calculatePath();
	}

	private List<ThreatAssessment> assessThreats()
	{
		final List<ThreatAssessment> assessments = new ArrayList<>();

		defenders.forEach(p ->
		{
			assessments.add(getThreatAssessment(p));
		});

		return assessments;
	}

	private ThreatAssessment getThreatAssessment(final Player defender)
	{
		switch (option)
		{
			case distance:
				return new ThreatAssessment(defender, -1 * this.getPlayers().getState(getPlayer()).getLoc().distanceBetween(this.getPlayers().getState(defender).getLoc()));
			case distanceToRunner:
				new ThreatAssessment(defender, -1 * this.getPlayers().getState(runner).getLoc().distanceBetween(this.getPlayers().getState(defender).getLoc()));
			case interception:
				DefaultInterceptPlayer intercept = new DefaultInterceptPlayer(this.getPlayers(), this.getPlayer(), this.getDirection(), defender);
				Path pathToTarget = intercept.calculatePath();
				PlayerSteps steps = this.getPlayers().createSteps(getPlayerState(), pathToTarget);
				return new ThreatAssessment(defender, -1 * steps.getDestinationReachedSteps());
			default:
				assert false;
				return null;
		}
	}

}
