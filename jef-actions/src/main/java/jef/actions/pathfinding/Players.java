package jef.actions.pathfinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jef.actions.collisions.Collision;
import jef.actions.collisions.CollisionResolution;
import jef.actions.collisions.CollisionResolver;
import jef.actions.pathfinding.blocking.BlockerPathfinder;
import jef.actions.pathfinding.blocking.BlockersAction;
import jef.actions.pathfinding.defenders.DefenderPathfinder;
import jef.actions.pathfinding.runners.RunnerPathfinder;
import jef.core.Football;
import jef.core.Location;
import jef.core.Player;
import jef.core.PlayerState;
import jef.core.movement.Posture;
import jef.core.movement.player.PlayerTracker;
import jef.core.movement.player.Steering;

public class Players
{

	private final Map<Player, PlayerState[]> steps = new HashMap<>();
	private final Map<Player, Pathfinder> pathfinders = new HashMap<>();
	private final Map<Location, LocationIndex> locationToIndex = new HashMap<>();

	private final int stepCapacity;
	private final double timerInterval;
	private int startOffset;

	public Players(final int stepCapacity, final double timerInterval)
	{
		this.stepCapacity = stepCapacity;
		this.timerInterval = timerInterval;
	}

	public void addPlayer(final PlayerState state)
	{
		assert this.getPlayers().contains(state.getPlayer()) == false;
		
		this.steps.put(state.getPlayer(), new PlayerState[this.stepCapacity]);
		this.reset(state);
	}

	public List<BlockerPathfinder> getBlockerPathfinders()
	{
		return this.pathfinders.values().stream().filter(BlockerPathfinder.class::isInstance)
				.map(bpf -> (BlockerPathfinder) bpf).toList();
	}

	public List<DefenderPathfinder> getDefenderPathfinders()
	{
		return this.pathfinders.values().stream().filter(DefenderPathfinder.class::isInstance)
				.map(dpf -> (DefenderPathfinder) dpf).toList();
	}

	public Set<Player> getPlayers()
	{
		return this.steps.keySet();
	}

	public RunnerPathfinder getRunnerPathfinder()
	{
		return this.pathfinders.values().stream().filter(RunnerPathfinder.class::isInstance)
				.map(rpf -> (RunnerPathfinder) rpf).findFirst().orElse(null);
	}

	public PlayerState getState(final Player player)
	{
		return this.getSteps(player)[this.getIndex(0)];
	}

	public PlayerState getState(final Player player, final int offset)
	{
		return this.getSteps(player)[this.getIndex(offset)];
	}

	public void process()
	{
		this.getPlayers().forEach(player -> update(getState(player)));
		
		final List<Collision> collisions = this.getCollisions(0);

		final Set<Player> playersInvolvedInAcollision = new HashSet<>();
		if (this.pathfinders.size() > 0)
		{
			final Set<CollisionResolver> resolvers = new HashSet<>();
			for (final Collision c : collisions)
			{
				resolvers.add(CollisionResolution.createResolution(c, Football.theFootball));
			}

			for (final CollisionResolver resolver : resolvers)
			{
				resolver.resolveCollision();

				PlayerState playerState = resolver.getPlayerState1();
				this.reset(playerState);
				playersInvolvedInAcollision.add(playerState.getPlayer());

				playerState = resolver.getPlayerState2();
				this.reset(playerState);
				playersInvolvedInAcollision.add(playerState.getPlayer());
			}

			final Set<Pathfinder> newPathfinders = new HashSet<>();
			for (final Player player : this.steps.keySet())
			{
				if (playersInvolvedInAcollision.contains(player))
					continue;
				
				final Pathfinder pathfinder = this.pathfinders.get(player);
				newPathfinders.add(pathfinder);
			}

			final RunnerPathfinder runnerPathfinder = this.getRunnerPathfinder();

			if (runnerPathfinder != null)
			{
				final long deltaNanos = (long) (this.timerInterval * 1000000);

				final List<DefenderPathfinder> defenderPathfinders = this.getDefenderPathfinders();
				final List<BlockerPathfinder> blockerPathfinders = this.getBlockerPathfinders();

				runnerPathfinder.calculate(runnerPathfinder, defenderPathfinders, blockerPathfinders, deltaNanos);

				defenderPathfinders.forEach(pf ->
				{
					pf.calculate(runnerPathfinder, defenderPathfinders, blockerPathfinders, deltaNanos);
				});

				new BlockersAction().move(runnerPathfinder, defenderPathfinders, blockerPathfinders, deltaNanos);

				for (final Pathfinder pathfinder : newPathfinders)
				{
					this.reset(pathfinder.getPlayerState());
				}

				final PlayerState runnerState = this.getState(runnerPathfinder.getPlayerState().getPlayer());
				if ((runnerState.getPosture() == Posture.onTheGround) || !runnerState.getLoc().isInBounds()
						|| runnerState.getLoc().isInEndZone(null))
				{
					this.pathfinders.clear();
				}
			}
		}

		this.advance();
	}

	public void setPathfinder(final Pathfinder pathfinder)
	{
		assert this.steps.get(pathfinder.getPlayerState().getPlayer()) != null;
		this.pathfinders.put(pathfinder.getPlayerState().getPlayer(), pathfinder);
	}

	private void addPlayerAtLocation(final PlayerState playerState, final int tick)
	{
		this.steps.get(playerState.getPlayer())[this.getIndex(tick)] = playerState;

		final Location canonicalLocation = this.toCanonicalLocation(playerState.getLoc());
		LocationIndex ret = this.locationToIndex.get(canonicalLocation);
		if (ret == null)
		{
			ret = new LocationIndex(canonicalLocation);
			this.locationToIndex.put(canonicalLocation, ret);
		}

		ret.addOccupier(playerState, tick);
	}

	private void removePlayerAtLocation(PlayerState playerState, int tick)
	{
		this.steps.get(playerState.getPlayer())[this.getIndex(tick)] = null;

		final Location canonicalLocation = this.toCanonicalLocation(playerState.getLoc());
		LocationIndex ret = this.locationToIndex.get(canonicalLocation);
		if (ret == null)
			return;

		if (ret.removeOccupier(playerState, tick) == 0)
			this.locationToIndex.remove(canonicalLocation);
	}

	private void advance()
	{
		for (Player player : this.steps.keySet())
		{
			PlayerState oldPlayerState = this.getState(player, 0);
			removePlayerAtLocation(oldPlayerState, 0);
			
			final PlayerTracker tracker = new PlayerTracker(this.getState(player, stepCapacity - 1), 
					this.timerInterval);
			final Steering steering = Steering.getInstance();
			steering.next(tracker);

			this.addPlayerAtLocation(tracker.getState(), 0);
		};

		this.startOffset += 1;
	}

	private void clearEntries(final Player player)
	{
		final PlayerState[] steps = this.getSteps(player);
		for (int i = 0; i < this.stepCapacity; i++)
		{
			final PlayerState playerState = steps[i];
			if (playerState == null)
				continue;
			
			final Location loc = this.toCanonicalLocation(playerState.getLoc());
			final LocationIndex lEntry = this.locationToIndex.get(loc);
			final int remainingOccupiers = lEntry.removeOccupier(playerState, i);
			if (remainingOccupiers == 0)
			{
				this.locationToIndex.remove(loc);
			}
		}
	}

	private List<Collision> extractRealCollisions(final List<PlayerState> targetPlayers,
			final List<PlayerState> surroundingPlayers, final int tick)
	{
		final List<Collision> ret = new ArrayList<>();

		for (int i = 0; i < targetPlayers.size(); i++)
		{
			for (int j = i + 1; j < targetPlayers.size(); j++)
			{
				final PlayerState p1 = targetPlayers.get(i);
				final PlayerState p2 = targetPlayers.get(j);

				if (p1.equals(p2))
				{
					continue;
				}

				if (p1.getLoc().distanceBetween(p2.getLoc()) < Player.SIZE)
				{
					ret.add(new Collision(p1, p2, tick));
				}
			}
		}

		for (int i = 0; i < targetPlayers.size(); i++)
		{
			for (int j = 0; j < surroundingPlayers.size(); j++)
			{
				final PlayerState p1 = targetPlayers.get(i);
				final PlayerState p2 = surroundingPlayers.get(j);

				if (p1.equals(p2))
				{
					continue;
				}

				if (p1.getLoc().distanceBetween(p2.getLoc()) < Player.SIZE)
				{
					ret.add(new Collision(p1, p2, tick));
				}
			}
		}

		return ret;
	}

	private List<Collision> getCollisions(final int ticksAhead)
	{
		final HashSet<Collision> ret = new HashSet<>();

		for (final LocationIndex entry : this.locationToIndex.values())
		{
			final List<PlayerState> neighbors = new ArrayList<>();
			final List<PlayerState> occupiers = entry.getOccupiers(ticksAhead);
			final List<Location> surroundingLocs = this.getSurroundingLocations(entry.getCanonicalLocation());
			for (final Location loc : surroundingLocs)
			{
				neighbors.addAll(this.getOccupiers(loc, ticksAhead));
			}

			ret.addAll(this.extractRealCollisions(occupiers, neighbors, ticksAhead));
		}

		return new ArrayList<>(ret);
	}

	private int getIndex(final int offset)
	{
		return (this.startOffset + offset) % this.stepCapacity;
	}

	private List<PlayerState> getOccupiers(final Location location, final int tick)
	{
		final LocationIndex entries = this.locationToIndex.get(location);
		if (entries == null)
			return Collections.emptyList();

		return entries.getOccupiers(tick);
	}

	private PlayerState[] getSteps(final Player player)
	{
		return this.steps.get(player);
	}

	private List<Location> getSurroundingLocations(final Location loc)
	{
		return Arrays.asList(new Location(loc.getX() - 1, loc.getY() - 1, 0),
				new Location(loc.getX(), loc.getY() - 1, 0), new Location(loc.getX() + 11, loc.getY() - 1, 0),
				new Location(loc.getX() - 1, loc.getY(), 0), new Location(loc.getX() + 1, loc.getY(), 0),
				new Location(loc.getX() - 1, loc.getY() + 1, 0), new Location(loc.getX(), loc.getY() + 1, 0),
				new Location(loc.getX() + 1, loc.getY() + 1, 0));
	}

	public void reset(final PlayerState startingState)
	{
		this.clearEntries(startingState.getPlayer());

		final PlayerState[] steps = this.steps.get(startingState.getPlayer());
		Arrays.fill(steps, null);

		this.addPlayerAtLocation(startingState, 0);

		final PlayerTracker tracker = new PlayerTracker(startingState, this.timerInterval);
		for (int i = 1; i < this.stepCapacity; i++)
		{
			final Steering steering = Steering.getInstance();
			steering.next(tracker);
			tracker.advance();

			final PlayerState playerState = tracker.getState();
			this.addPlayerAtLocation(playerState, i);
		}
	}

	private Location toCanonicalLocation(final Location loc)
	{
		return new Location(Math.round(loc.getX()), Math.round(loc.getY()), 0);
	}

	private void update(final PlayerState playerState)
	{
		if (playerState.equals(this.getState(playerState.getPlayer())))
			return;

		reset(playerState);
	}

	public class LocationIndex
	{
		private final Location canonicalLocation;
		private final Object[] occupiers;
		private int occupierCount;

		public LocationIndex(final Location canonicalLocation)
		{
			this.canonicalLocation = canonicalLocation;
			this.occupiers = new Object[stepCapacity];
		}

		public Location getCanonicalLocation()
		{
			return this.canonicalLocation;
		}

		public int getLookAheadTicks()
		{
			return this.occupiers.length;
		}

		@SuppressWarnings("unchecked")
		public List<PlayerState> getOccupiers(final int tick)
		{
			Object obj = this.occupiers[getIndex(tick)];
			if (obj == null)
				return Collections.emptyList();
			else if (obj instanceof PlayerState)
				return Collections.singletonList((PlayerState) obj);
			else
				return new ArrayList<>((Set<PlayerState>) obj);
		}

		@SuppressWarnings("unchecked")
		public int removeOccupier(final PlayerState playerState, int tick)
		{
			int index = getIndex(tick);
			Object obj = this.occupiers[index];

			if (obj instanceof PlayerState)
			{
				this.occupiers[index] = null;
				occupierCount -= 1;
				assert occupierCount >= 0;
			}
			else
			{
				Set<PlayerState> set = (Set<PlayerState>) obj;
				if (set.remove(playerState))
				{
					occupierCount -= 1;
					assert occupierCount >= 0;
				}
			}

			return this.occupierCount;
		}

		@SuppressWarnings("unchecked")
		public void addOccupier(final PlayerState playerState, final int tick)
		{
			int index = getIndex(tick);
			Object obj = this.occupiers[index];
			if (obj == null)
			{
				this.occupiers[index] = playerState;
				this.occupierCount += 1;
			}
			else if (obj instanceof PlayerState)
			{
				PlayerState pObj = (PlayerState) obj;
				if (pObj.equals(playerState) == false)
				{
					Set<PlayerState> set = new HashSet<PlayerState>();
					set.add(playerState);
					set.add(pObj);
					this.occupiers[index] = set;
					this.occupierCount += 1;
				}
			}
			else
			{
				Set<PlayerState> set = (Set<PlayerState>) obj;
				if (set.add(playerState))
					this.occupierCount += 1;
			}

		}

		@SuppressWarnings("unchecked")
		public int getOccupierCount(int tick)
		{
			int index = getIndex(tick);
			Object obj = this.occupiers[index];
			if (obj == null)
			{
				return 0;
			}
			else if (obj instanceof PlayerState)
			{
				return 1;
			}
			else
			{
				Set<PlayerState> set = (Set<PlayerState>) obj;
				return set.size();
			}
		}

	}
}
