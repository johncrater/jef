package jef.core.pathfinding.runners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.core.Field;
import jef.core.Player;
import jef.core.events.Messages;
import jef.core.geometry.LineSegment;
import jef.core.movement.LinearVelocity;
import jef.core.movement.Location;
import jef.core.pathfinding.AbstractPathfinder;
import jef.core.pathfinding.Direction;
import jef.core.pathfinding.blocking.BlockerPathfinder;
import jef.core.pathfinding.defenders.DefenderPathfinder;

public class EvadeIntercepters2 extends AbstractPathfinder implements RunnerPathfinder
{

	public EvadeIntercepters2(Player player, Direction direction)
	{
		super(player, direction);
	}

	@Override
	public boolean calculate(RunnerPathfinder runner, List<? extends DefenderPathfinder> defenders,
			List<? extends BlockerPathfinder> blockers, long deltaNanos)
	{
		List<Player> intercepterPlayers = new ArrayList<>(
				new HashSet<>(defenders.stream().map(pf -> pf.getPlayer()).toList()));

		Set<Borderline> borderlines = buildBorderlines(intercepterPlayers, blockers);

		TreeSet<ReachableLocation> reachableLocations = new TreeSet<>();
		reachableLocations.addAll(borderlines.stream().map(bl -> bl.getLocation1()).toList());
		reachableLocations.addAll(borderlines.stream().map(bl -> bl.getLocation2()).toList());

		Set<ReachableLocation> ret = new HashSet<>();
		
		while (reachableLocations.size() > 0)
		{
			ReachableLocation reachableLocation = reachableLocations.first();
			LineSegment ls = new LineSegment(runner.getPlayer().getLoc(), reachableLocation.getLoc());
			for (Borderline bl : borderlines)
			{
				Location intersectionPoint = ls.xyIntersection(bl.getLs());
				if (intersectionPoint == null)
					continue;

				if (intersectionPoint.isInBounds() == false)
					continue;

				// this is a safety check
				if (bl.getLs().intersects(intersectionPoint) == false || ls.intersects(intersectionPoint) == false)
					continue;
				
				if (intersectionPoint.equals(reachableLocation.getLoc()) || intersectionPoint.closeEnoughTo(reachableLocation.getLoc()))
					continue;
			}
		}

		return false;
	}

	private Set<Borderline> buildBorderlines(List<Player> intercepterPlayers,
			List<? extends BlockerPathfinder> blockers)
	{
		Set<Borderline> segments = new HashSet<>(Arrays.asList(new Borderline(Field.EAST_END_ZONE, null, null),
				new Borderline(Field.WEST_END_ZONE, null, null), new Borderline(Field.NORTH_SIDELINE, null, null),
				new Borderline(Field.SOUTH_SIDELINE, null, null)));

		final HashSet<Borderline> runnerInterceptorSegments = this.getBorderlines(getPlayer(), intercepterPlayers);
		runnerInterceptorSegments.stream().forEach(
				s -> MessageManager.getInstance().dispatchMessage(Messages.drawRunnerIntercepterBoundingSegments, s));

		segments.addAll(runnerInterceptorSegments);

		final HashSet<Borderline> blockerInterceptorSegments = new HashSet<>();
		for (final Player intercepter : intercepterPlayers)
			blockerInterceptorSegments
					.addAll(this.getBorderlines(intercepter, blockers.stream().map(pf -> pf.getPlayer()).toList()));

		blockerInterceptorSegments.stream().forEach(
				s -> MessageManager.getInstance().dispatchMessage(Messages.drawBlockerIntercepterBoundingSegments, s));

		segments.addAll(blockerInterceptorSegments);

		return splitLines(segments);
	}

	private Set<Borderline> splitLines(final Set<Borderline> segments)
	{
		final var borderlinesQueue = new HashSet<>(segments);
		final var ret = new HashSet<Borderline>();

		while (borderlinesQueue.size() > 0)
		{
			Borderline bl1 = borderlinesQueue.iterator().next();
			borderlinesQueue.remove(bl1);

			boolean found = false;
			for (final Borderline bl2 : segments)
			{
				if (bl1.equals(bl2))
					continue;

				final Location intersection = bl1.getLs().xyIntersection(bl2.getLs());
				if (intersection != null && bl1.getLs().intersects(intersection)
						&& bl2.getLs().intersects(intersection))
				{
					if (intersection.equals(bl1.getLs().getLoc1()) || intersection.equals(bl1.getLs().getLoc2()))
						continue;

					final var newBl1 = new Borderline(new LineSegment(bl1.getLs().getLoc1(), intersection),
							bl1.getPlayer1(), bl1.getPlayer2());
					final var newBl2 = new Borderline(new LineSegment(bl1.getLs().getLoc2(), intersection),
							bl1.getPlayer1(), bl1.getPlayer2());

					borderlinesQueue.add(newBl1);
					borderlinesQueue.add(newBl2);
					found = true;
					break;
				}
			}

			if (!found)
				ret.add(bl1);
		}

		return ret;
	}

	private class Borderline
	{
		private LineSegment ls;
		private Player player1;
		private Player player2;

		public Borderline(LineSegment ls, Player player1, Player player2)
		{
			super();
			this.ls = ls;
			this.player1 = player1;
			this.player2 = player2;
		}

		public LineSegment getLs()
		{
			return this.ls;
		}

		public Player getPlayer1()
		{
			return this.player1;
		}

		public Player getPlayer2()
		{
			return this.player2;
		}

		public ReachableLocation getLocation1()
		{
			return new ReachableLocation(ls.getLoc1(), player1, player2);
		}

		public ReachableLocation getLocation2()
		{
			return new ReachableLocation(ls.getLoc2(), player1, player2);
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + Objects.hash(ls, player1, player2);
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Borderline other = (Borderline) obj;

			return Objects.equals(this.ls, other.ls) && Objects.equals(this.player1, other.player1)
					&& Objects.equals(this.player2, other.player2);
		}
	}

	private class ReachableLocation
	{
		private Location loc;
		private Player player1;
		private Player player2;

		public ReachableLocation(Location loc, Player player1, Player player2)
		{
			super();
			this.loc = loc;
			this.player1 = player1;
			this.player2 = player2;
		}

		public Location getLoc()
		{
			return this.loc;
		}

		public Player getPlayer1()
		{
			return this.player1;
		}

		public Player getPlayer2()
		{
			return this.player2;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + Objects.hash(loc, player1, player2);
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ReachableLocation other = (ReachableLocation) obj;
			return Objects.equals(this.loc, other.loc) && Objects.equals(this.player1, other.player1)
					&& Objects.equals(this.player2, other.player2);
		}
	}

	private HashSet<Borderline> getBorderlines(Player p1, Collection<? extends Player> players)
	{
		return new HashSet<>(players.stream().map(p2 ->
		{
			double ratio = .5;
			final double denom = p1.getLV().getSpeed() + p2.getLV().getSpeed();
			if (denom != 0)
			{
				ratio = p1.getLV().getSpeed() / denom;
			}

			final LineSegment seg = new LineSegment(p1.getLoc(), p2.getLoc());
			LinearVelocity segLV = seg.getDirection();
			Location pointAlong = seg.getPoint(ratio);

			// make it longer than field distances and then chop it down to size to make
			// sure it fits
			return new Borderline(new LineSegment(pointAlong.add(segLV.add(-Math.PI / 2, 0, 200)),
					pointAlong.add(segLV.add(Math.PI / 2, 0, 200))).restrictToBetweenEndZones(), p1, p2);
		}).filter(l -> l != null).toList());
	}

}
