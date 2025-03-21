package jef.pathfinding.runners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.IPlayers;
import jef.core.Direction;
import jef.core.Field;
import jef.core.LinearVelocity;
import jef.core.Location;
import jef.core.Performance;
import jef.core.Player;
import jef.core.PlayerState;
import jef.core.events.DebugShape;
import jef.core.events.Messages;
import jef.core.geometry.Circle;
import jef.core.geometry.LineSegment;
import jef.core.movement.Posture;
import jef.core.movement.player.Path;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;
import jef.pathfinding.PathfinderBase;

public class DefaultEvadeInterceptors extends PathfinderBase implements RunnerPathfinder
{
	public static double INBOUND_EPSILON = 1;

	private Collection<Player> defenders;
	private Collection<Player> blockers;

	public DefaultEvadeInterceptors(IPlayers players, Player player, Direction direction, Collection<Player> defenders,
			Collection<Player> blockers)
	{
		super(players, player, direction);
		this.defenders = defenders;
		this.blockers = blockers;
	}

	private DebugShape drawBorderLine(LineSegment ls, String color)
	{
		DebugShape ret = DebugShape.drawLineSegment(ls, color);
		ret.lineType = DebugShape.LineType.dash;
		return ret;
	}

	@Override
	public Path calculatePath()
	{
		List<PlayerState> interceptorPlayers = defenders.stream().map(p -> getPlayers().getState(p)).toList();

		Set<Borderline> neutralBorderlines = buildNeutralBorderlines();
		neutralBorderlines.stream().forEach(bl -> MessageManager.getInstance().dispatchMessage(Messages.drawDebugShape,
				drawBorderLine(bl.getLs(), "#00000000")));

		Set<Borderline> borderlines = buildCircularBorderlines(interceptorPlayers,
				blockers.stream().map(p -> getPlayers().getState(p)).toList());
//		Set<Borderline> borderlines = buildBorderlines(interceptorPlayers,
//				blockers.stream().map(p -> getPlayers().getState(p)).toList());
		borderlines.stream().filter(b1 -> b1.isRunnerLine()).forEach(bl -> MessageManager.getInstance()
				.dispatchMessage(Messages.drawDebugShape, drawBorderLine(bl.getLs(), "#FF000000")));
		borderlines.stream().filter(b1 -> b1.isBlockerLine()).forEach(bl -> MessageManager.getInstance()
				.dispatchMessage(Messages.drawDebugShape, drawBorderLine(bl.getLs(), "#0000FF00")));

		borderlines.addAll(neutralBorderlines);
		borderlines = this.splitLines(borderlines);

		List<Location> potentiallyReachableLocations = buildReachableLocations(borderlines);
		potentiallyReachableLocations.stream().forEach(rl -> MessageManager.getInstance()
				.dispatchMessage(Messages.drawDebugShape, DebugShape.fillLocation(rl, "#FF000000")));

		List<Location> reachableLocations = filterOutUnreachableLocations(getPlayers().getState(getPlayer()),
				potentiallyReachableLocations, borderlines);
		reachableLocations.stream().forEach(rl -> MessageManager.getInstance().dispatchMessage(Messages.drawDebugShape,
				DebugShape.fillLocation(rl, "#00FF0000")));

		reachableLocations = sortReachableLocations(reachableLocations);
		return assignPath(reachableLocations);
	}

	private Set<Borderline> buildCircularBorderlines(List<PlayerState> interceptorPlayers,
			Collection<PlayerState> blockers)
	{
		Set<Borderline> segments = new HashSet<>();

		double runnerRadius = this.getPlayerState().getLV().getSpeed() * Performance.frameInterval;
		for (PlayerState defender : interceptorPlayers)
		{
			Location runnerLoc = getPlayers().getState(getPlayer()).getLoc();

			double speedRatio = getPlayer().getSpeedMatrix().getSprintingSpeed()
					/ (getPlayer().getSpeedMatrix().getSprintingSpeed()
							+ defender.getPlayer().getSpeedMatrix().getSprintingSpeed());

			double denominator = defender.getLV().getSpeed() + this.getPlayerState().getLV().getSpeed();
			if (denominator != 0)
				speedRatio = this.getPlayerState().getLV().getSpeed() / denominator;

			LineSegment ls = new LineSegment(runnerLoc, defender.getLoc().moveTowards(runnerLoc, Player.SIZE));
			Location centerPoint = ls.getPoint(speedRatio);

			Location loc1 = centerPoint;
			Location loc2 = centerPoint;

			double defenderRadius = defender.getLV().getSpeed() * Performance.frameInterval;

			for (int i = 0; i < this.getPlayers().getStepCapacity(); i += 8)
			{
				Circle defenderCircle = new Circle(defender.getLoc(), defenderRadius * i);
				Circle runnerCircle = new Circle(this.getPlayers().getState(getPlayer()).getLoc(),
						runnerRadius * i - Player.SIZE);

				LineSegment intersectionSegment = runnerCircle.intersects(defenderCircle);
				if (intersectionSegment == null)
					continue;

				if (loc1.isInBounds(INBOUND_EPSILON))
				{

					LineSegment borderlineLineSegment = new LineSegment(loc1, intersectionSegment.getLoc1())
							.restrictToInBounds(INBOUND_EPSILON);
					if (borderlineLineSegment != null)
					{
						Borderline borderline = new Borderline(borderlineLineSegment,
								getPlayers().getState(getPlayer()), defender);
						segments.add(borderline);

						loc1 = intersectionSegment.getLoc1();
					}
					else
					{
						new LineSegment(loc1, intersectionSegment.getLoc1())
						.restrictToInBounds(INBOUND_EPSILON);					}
				}

				if (loc2.isInBounds(INBOUND_EPSILON))
				{
					LineSegment borderlineLineSegment = new LineSegment(loc2, intersectionSegment.getLoc2())
							.restrictToInBounds(INBOUND_EPSILON);
					if (borderlineLineSegment != null)
					{
						Borderline borderline = new Borderline(borderlineLineSegment,
								getPlayers().getState(getPlayer()), defender);
						segments.add(borderline);

						loc2 = intersectionSegment.getLoc2();
					}
					else
					{
						new LineSegment(loc2, intersectionSegment.getLoc2())
						.restrictToInBounds(INBOUND_EPSILON);					}
				}

//				MessageManager.getInstance().dispatchMessage(Messages.drawDebugShape, DebugShape.fillLocation(intersectionSegment.getLoc1(), "#FF000000"));
//				MessageManager.getInstance().dispatchMessage(Messages.drawDebugShape, DebugShape.fillLocation(intersectionSegment.getLoc2(), "#FF000000"));

			}
		}

//		final HashSet<Borderline> runnerInterceptorSegments = this.getBorderlines(getPlayerState(), interceptorPlayers);
//		segments.addAll(runnerInterceptorSegments);
//
//		final HashSet<Borderline> blockerInterceptorSegments = new HashSet<>();
//		for (final PlayerState interceptor : interceptorPlayers)
//			blockerInterceptorSegments
//					.addAll(this.getBorderlines(interceptor, blockers));
//
//		segments.addAll(blockerInterceptorSegments);

		return segments;
	}

	private boolean isBlocker(Player player)
	{
		return this.blockers.contains(player);
	}

	private boolean isDefender(Player player)
	{
		return this.defenders.contains(player);
	}

	private Path assignPath(List<Location> reachableLocations)
	{
		if (reachableLocations.size() > 0)
		{
			Waypoint wp1 = new Waypoint(reachableLocations.getFirst(), getPlayerState().getMaxSpeed(),
					DestinationAction.noStop);
			if (wp1.getDestination().isInEndZone(getDirection()))
			{
				return new Path(wp1);
			}
			else
			{
				Waypoint wp2 = new Waypoint(
						new Location(getDirection() == Direction.east ? Field.EAST_END_ZONE_X : Field.WEST_END_ZONE_X,
								wp1.getDestination().getY()),
						getPlayerState().getMaxSpeed(), DestinationAction.normalStop);
				return new Path(wp1, wp2);
			}
		}
		else
		{
			Waypoint wp1 = new Waypoint(
					new Location(getDirection() == Direction.east ? Field.EAST_END_ZONE_X : Field.WEST_END_ZONE_X,
							getPlayerState().getLoc().getY()),
					getPlayerState().getMaxSpeed(), DestinationAction.normalStop);
			return new Path(wp1);
		}
	}

	private List<Location> sortReachableLocations(List<Location> reachableLocations)
	{
		reachableLocations = reachableLocations.stream().sorted((rl1, rl2) ->
		{
			int ret = Double.compare(rl1.getX(), rl2.getX()) * (getDirection() == Direction.west ? 1 : -1);
			if (ret == 0)
				ret = Double.compare(getPlayerState().getLoc().distanceBetween(rl1),
						getPlayerState().getLoc().distanceBetween(rl2));

			if (ret == 0)
				ret = Double.compare(Math.abs(rl1.getY() - Field.MIDFIELD_Y), Math.abs(rl2.getY() - Field.MIDFIELD_Y));

			// if we are tied gotta choose something.
			if (ret == 0)
				ret = Double.compare(rl1.getY(), rl2.getY());

			return ret;
		}).toList();
		return reachableLocations;
	}

	private List<Location> buildReachableLocations(Set<Borderline> borderlines)
	{
		List<Location> reachableLocations = new ArrayList<>();
		reachableLocations.addAll(borderlines.stream().map(bl -> bl.getLocation1()).toList());
		reachableLocations.addAll(borderlines.stream().map(bl -> bl.getLocation2()).toList());
		reachableLocations
				.add(new Location(getDirection() == Direction.east ? Field.EAST_END_ZONE_X : Field.WEST_END_ZONE_X,
						getPlayerState().getLoc().getY()));
		return reachableLocations;
	}

	private List<Location> filterOutUnreachableLocations(PlayerState playerState, List<Location> reachableLocations,
			Set<Borderline> borderlines)
	{
		Set<Location> ret = new HashSet<>();

		for (Location reachableLocation : reachableLocations)
		{
			LineSegment ls = new LineSegment(playerState.getLoc(), reachableLocation);
			boolean locationReachable = true;
			for (Borderline bl : borderlines)
			{
				Location intersectionPoint = ls.xyIntersection(bl.getLs());
				if (intersectionPoint == null)
					continue;

				if (intersectionPoint.isInBounds() == false)
					continue;

//				// this is a safety check
//				if (bl.getLs().intersects(intersectionPoint) == false || ls.intersects(intersectionPoint) == false)
//					continue;
//
				// this means they are essentially the same point
				if (intersectionPoint.equals(reachableLocation) || intersectionPoint.closeEnoughTo(reachableLocation))
					continue;

				locationReachable = false;
				break;
			}

			if (locationReachable == true)
				ret.add(reachableLocation);
		}

		return new ArrayList<>(ret);
	}

	private Set<Borderline> buildNeutralBorderlines()
	{
		return new HashSet<>(Arrays.asList(
				new Borderline(
						Field.GOAL_LINE_EAST.addLength(-2 * Location.EPSILON_VALUE).move(0, Location.EPSILON_VALUE, 0),
						null, null),
				new Borderline(
						Field.GOAL_LINE_WEST.addLength(-2 * Location.EPSILON_VALUE).move(0, Location.EPSILON_VALUE, 0),
						null, null),
				new Borderline(Field.SIDELINE_NORTH.move(0, -Location.EPSILON_VALUE, 0), null, null),
				new Borderline(Field.SIDELINE_SOUTH.move(0, Location.EPSILON_VALUE, 0), null, null)));
	}

	private Set<Borderline> buildBorderlines(List<PlayerState> interceptorPlayers, List<PlayerState> blockers)
	{
		Set<Borderline> segments = new HashSet<>();

		final HashSet<Borderline> runnerInterceptorSegments = this.getBorderlines(getPlayerState(), interceptorPlayers);
		segments.addAll(runnerInterceptorSegments);

		final HashSet<Borderline> blockerInterceptorSegments = new HashSet<>();
		for (final PlayerState interceptor : interceptorPlayers)
			blockerInterceptorSegments.addAll(this.getBorderlines(interceptor, blockers));

		segments.addAll(blockerInterceptorSegments);

		return segments;
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
			for (final Borderline bl2 : borderlinesQueue)
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
							bl1.getPlayerState1(), bl1.getPlayerState2());
					final var newBl2 = new Borderline(new LineSegment(bl1.getLs().getLoc2(), intersection),
							bl1.getPlayerState1(), bl1.getPlayerState2());

					borderlinesQueue.add(newBl1);
					borderlinesQueue.add(newBl2);
					found = true;
					break;
				}
			}

			if (!found)
				ret.add(bl1);
		}

		System.out.println(ret.size());
		return ret;
	}

	private class Borderline
	{
		private LineSegment ls;
		private PlayerState playerState1;
		private PlayerState playerState2;

		public Borderline(LineSegment ls, PlayerState playerState1, PlayerState playerState2)
		{
			super();
			this.ls = ls;
			this.playerState1 = playerState1;
			this.playerState2 = playerState2;
		}

		public LineSegment getLs()
		{
			return this.ls;
		}

		public PlayerState getPlayerState1()
		{
			return this.playerState1;
		}

		public PlayerState getPlayerState2()
		{
			return this.playerState2;
		}

		public Location getLocation1()
		{
			return ls.getLoc1();
		}

		public Location getLocation2()
		{
			return ls.getLoc2();
		}

		public boolean isBlockerLine()
		{
			return isBlocker(playerState1.getPlayer()) || isBlocker(playerState2.getPlayer());
		}

		public boolean isRunnerLine()
		{
			return getPlayer().equals(playerState1.getPlayer()) || getPlayer().equals(playerState2.getPlayer());
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + Objects.hash(ls, playerState1, playerState2);
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

			return Objects.equals(this.ls, other.ls) && Objects.equals(this.playerState1, other.playerState1)
					&& Objects.equals(this.playerState2, other.playerState2);
		}

		@Override
		public String toString()
		{
			return "Borderline [ls=" + this.ls + ", player1=" + this.playerState1 + ", player2=" + this.playerState2
					+ "]";
		}
	}

	private HashSet<Borderline> getBorderlines(PlayerState p1, Collection<PlayerState> players)
	{
		return new HashSet<>(players.stream().map(p2 ->
		{
			if (p2.getPosture() != Posture.upright)
				return new Borderline(null, p1, p2);

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
					pointAlong.add(segLV.add(Math.PI / 2, 0, 200))).restrictToBetweenEndZones(true), p1, p2);
		}).filter(l -> l.getLs() != null).toList());
	}

}
