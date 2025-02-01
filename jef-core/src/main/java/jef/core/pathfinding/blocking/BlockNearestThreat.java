package jef.core.pathfinding.blocking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.core.Player;
import jef.core.events.Messages;
import jef.core.geometry.LineSegment;
import jef.core.movement.Location;
import jef.core.movement.RelativeLocation;
import jef.core.movement.player.DefaultPath;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;
import jef.core.pathfinding.AbstractPathfinder;
import jef.core.pathfinding.DefenderAssessment;
import jef.core.pathfinding.Direction;
import jef.core.pathfinding.InterceptPlayer;
import jef.core.pathfinding.Pathfinder;

/**
 * A BlockingEscort stays between the runner and the biggest defensive threat
 */
public class BlockNearestThreat extends AbstractPathfinder implements BlockerPathfinder
{
	private Player runner;
	private Collection<Player> defenders;
	
	private Pathfinder interceptionPathfinder;

	public BlockNearestThreat(Player blocker, Player runner, Collection<Player> defenders, Direction direction, double deltaTime)
	{
		super(blocker, direction, deltaTime);
		this.runner = runner;
		this.defenders = defenders;
	}

	@Override
	public void reset()
	{
		super.reset();
		interceptionPathfinder = null;
	}

	@Override
	public boolean calculate()
	{
		if (interceptionPathfinder == null)
		{
			List<DefenderAssessment> threats = assessThreats(runner, runner.getPath().getDestination(), defenders);
			threats = threats.stream().sorted(Comparator.comparing(DefenderAssessment::threatLevel)).toList();

			DefenderAssessment biggestThreat = threats.get(0);
			interceptionPathfinder = new InterceptPlayer(getPlayer(), new TargetPathfinder(biggestThreat.getDefender(), direction.opposite())); 
		}

		return interceptionPathfinder.calculate();

		MessageManager.getInstance().dispatchMessage(Messages.drawBlockerDestination, blockerDestination);
		MessageManager.getInstance().dispatchMessage(Messages.drawBlockerPath,
				new LineSegment(getPlayer().getLoc(), blockerDestination));

		return new DefaultPath(new Waypoint(blockerDestination, runner.getDesiredSpeed(), DestinationAction.noStop));
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

	private static double getLVDistance(final Player player, final Location loc)
	{
		return player.getLoc().distanceBetween(loc) / player.getMaxSpeed();
	}

}
