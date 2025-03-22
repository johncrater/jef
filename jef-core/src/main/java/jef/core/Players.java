package jef;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jef.core.Football;
import jef.core.Location;
import jef.core.Performance;
import jef.core.Player;
import jef.core.PlayerState;
import jef.core.movement.player.Path;
import jef.core.movement.player.PlayerTracker;
import jef.core.movement.player.Steering;
import jef.pathfinding.collisions.Collision;
import jef.pathfinding.collisions.CollisionResolution;
import jef.pathfinding.collisions.CollisionResolver;

public abstract class Players implements IPlayers
{
	public class PlayerSteps
	{
		private final PlayerState[] steps;
		private Path path;
		private int destinationReachedSteps;

		public PlayerSteps(final PlayerState startingState, final Path path)
		{
			this.steps = new PlayerState[Players.this.getStepCapacity()];
			this.destinationReachedSteps = -1;
			this.reset(startingState, path);
		}

		public int getDestinationReachedSteps()
		{
			return this.destinationReachedSteps;
		}

		public PlayerState getFirst()
		{
			return this.getState(0);
		}

		public PlayerState getLast()
		{
			return this.getState(Players.this.getStepCapacity() - 1);
		}

		public Path getPath()
		{
			return this.path;
		}

		public Player getPlayer()
		{
			return this.steps[0].getPlayer();
		}

		public PlayerState getState(final int offset)
		{
			final int index = Players.this.getIndex(offset);
			return this.steps[index];
		}

		public int getStepCapacity()
		{
			return Players.this.stepCapacity;
		}

		public int getStepsToLocation(final Location loc)
		{
			for (int i = 0; i < Players.this.getStepCapacity(); i++)
			{
				final PlayerState tmpState = this.getState(i);
				final Location tmpLoc = tmpState.getLoc();
				if (tmpLoc.distanceBetween(loc) <= 1)
					return i;
			}

			return -1;
		}

		public boolean hasReachedDestination()
		{
			return this.destinationReachedSteps > -1;
		}

		void advance()
		{
			final PlayerTracker tracker = new PlayerTracker(
					this.steps[Players.this.getIndex(this.steps.length - 1)], this.path,
					Players.this.getTimerInterval());
			final Steering steering = Steering.getInstance();
			final boolean destinationReached = steering.next(tracker);
			this.steps[Players.this.getIndex(0)] = tracker.getState();

			if (destinationReached && (this.destinationReachedSteps == -1))
			{
				this.destinationReachedSteps = this.steps.length - 1;
			}
			else if (this.destinationReachedSteps > 0)
			{
				this.destinationReachedSteps -= 1;
			}
		}

		void reset(final PlayerState startingState, final Path path)
		{
			if (path == null)
			{
				if (this.path == null)
				{
					this.path = new Path(startingState.getLoc());
				}
			}
			else
			{
				this.path = path;
			}

			Arrays.fill(this.steps, null);

			this.destinationReachedSteps = -1;
			final Steering steering = Steering.getInstance();
			final PlayerTracker tracker = new PlayerTracker(startingState, this.path, Players.this.getTimerInterval());
			this.steps[Players.this.getIndex(0)] = tracker.getState();
			tracker.advance();
			if (tracker.destinationReached())
			{
				this.destinationReachedSteps = 0;
			}

			for (int i = 1; i < this.steps.length; i++)
			{
				final boolean destinationReached = steering.next(tracker);
				if (destinationReached && this.destinationReachedSteps == -1)
				{
					this.destinationReachedSteps = i;
				}

				tracker.advance();
				this.steps[Players.this.getIndex(i)] = tracker.getState();
			}
		}
	}

	private class LocationIndex
	{
		private class LocationIndexEntry
		{
			private final Object[] occupiers;
			private int occupierCount;
			private final Location canonicalLocation;

			public LocationIndexEntry(final Location canonicalLocation)
			{
				this.canonicalLocation = canonicalLocation;
				this.occupiers = new Object[Players.this.stepCapacity];
			}

			@SuppressWarnings("unchecked")
			public void addOccupier(final PlayerState playerState, final int tick)
			{
				final int index = Players.this.getIndex(tick);
				final Object obj = this.occupiers[index];
				if (obj == null)
				{
					this.occupiers[index] = playerState;
					this.occupierCount += 1;
				}
				else if (obj instanceof final PlayerState pObj)
				{
					if (!pObj.equals(playerState))
					{
						final Set<PlayerState> set = new HashSet<>();
						set.add(playerState);
						set.add(pObj);
						this.occupiers[index] = set;
						this.occupierCount += 1;
					}
				}
				else
				{
					final Set<PlayerState> set = (Set<PlayerState>) obj;
					if (set.add(playerState))
					{
						this.occupierCount += 1;
					}
				}

			}

			public Location getCanonicalLocation()
			{
				return this.canonicalLocation;
			}

			@SuppressWarnings("unchecked")
			public List<PlayerState> getOccupiers(final int tick)
			{
				final Object obj = this.occupiers[Players.this.getIndex(tick)];
				if (obj == null)
					return Collections.emptyList();
				if (obj instanceof PlayerState)
					return Collections.singletonList((PlayerState) obj);
				return new ArrayList<>((Set<PlayerState>) obj);
			}

			@SuppressWarnings("unchecked")
			public int removeOccupier(final PlayerState playerState, final int tick)
			{
				final int index = Players.this.getIndex(tick);
				final Object obj = this.occupiers[index];

				if (obj instanceof PlayerState)
				{
					this.occupiers[index] = null;
					this.occupierCount -= 1;
					assert this.occupierCount >= 0;
				}
				else
				{
					final Set<PlayerState> set = (Set<PlayerState>) obj;
					if (set.remove(playerState))
					{
						this.occupierCount -= 1;
						assert this.occupierCount >= 0;
					}
				}

				return this.occupierCount;
			}
		}

		private final Map<Location, LocationIndexEntry> locationToIndex = new HashMap<>();

		public LocationIndex()
		{
		}

		public void clearLocation(final PlayerState playerState, final int index)
		{
			final Location loc = Players.toCanonicalLocation(playerState.getLoc());
			final LocationIndexEntry lEntry = this.locationToIndex.get(loc);
			if (lEntry == null)
				return;

			final int remainingOccupiers = lEntry.removeOccupier(playerState, index);
			if (remainingOccupiers == 0)
			{
				this.locationToIndex.remove(loc);
			}
		}

		public void clearLocations(final PlayerSteps steps)
		{
			for (int i = 0; i < Players.this.getStepCapacity(); i++)
			{
				final PlayerState playerState = steps.getState(i);
				this.clearLocation(playerState, i);
			}
		}

		public List<Collision> getCollisions(final int ticksAhead)
		{
			final HashSet<Collision> ret = new HashSet<>();

			for (final LocationIndexEntry entry : this.locationToIndex.values())
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

		public List<PlayerState> getOccupiers(final Location location, final int tick)
		{
			final LocationIndexEntry entries = this.locationToIndex.get(location);
			if (entries == null)
				return Collections.emptyList();

			return entries.getOccupiers(tick);
		}

		public void setLocations(final PlayerSteps steps)
		{
			for (int i = 0; i < Players.this.getStepCapacity(); i++)
			{
				final PlayerState playerState = steps.getState(i);
				if (playerState == null)
				{
					continue;
				}

				this.setLocation(playerState, i);
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

		private List<Location> getSurroundingLocations(final Location loc)
		{
			return Arrays.asList(new Location(loc.getX() - 1, loc.getY() - 1, 0),
					new Location(loc.getX(), loc.getY() - 1, 0), new Location(loc.getX() + 11, loc.getY() - 1, 0),
					new Location(loc.getX() - 1, loc.getY(), 0), new Location(loc.getX() + 1, loc.getY(), 0),
					new Location(loc.getX() - 1, loc.getY() + 1, 0), new Location(loc.getX(), loc.getY() + 1, 0),
					new Location(loc.getX() + 1, loc.getY() + 1, 0));
		}

		private void setLocation(final PlayerState playerState, final int i)
		{
			final Location canonicalLocation = Players.toCanonicalLocation(playerState.getLoc());
			LocationIndexEntry ret = this.locationToIndex.get(canonicalLocation);
			if (ret == null)
			{
				ret = new LocationIndexEntry(canonicalLocation);
				this.locationToIndex.put(canonicalLocation, ret);
			}

			ret.addOccupier(playerState, i);
		}
	}

	public static final int DEFAULT_STEP_CAPACITY = 200;

	private static Location toCanonicalLocation(final Location loc)
	{
		return new Location(Math.round(loc.getX()), Math.round(loc.getY()), 0);
	}

	private final Map<Player, PlayerSteps> steps = new HashMap<>();

	private final LocationIndex locationIndex;
	private final int stepCapacity;
	private final double timerInterval;
	private int startOffset;

	private final Map<Player, Path> nextPaths = new HashMap<>();

	public Players()
	{
		this(Players.DEFAULT_STEP_CAPACITY, Performance.frameInterval);
	}

	public Players(final int stepCapacity, final double timerInterval)
	{
		this.stepCapacity = stepCapacity;
		this.timerInterval = timerInterval;
		this.locationIndex = new LocationIndex();
	}

	public void addPlayer(final PlayerState state)
	{
		assert !this.getPlayers().contains(state.getPlayer());

		this.steps.put(state.getPlayer(), new PlayerSteps(state, null));

		this.reset(state, null);
	}

	public void addPlayer(final PlayerState state, final Path path)
	{
		assert !this.getPlayers().contains(state.getPlayer());
		this.reset(state, path);
	}

	public void advance()
	{
		for (final Player player : this.nextPaths.keySet())
		{
			final Path newPath = this.nextPaths.get(player);
			this.steps.get(player).path = newPath;
			this.reset(this.getState(player), newPath);
		}

		final List<Collision> collisions = this.getCollisions(0);
		final Set<CollisionResolver> resolvers = new HashSet<>();
		for (final Collision c : collisions)
		{
			resolvers.add(CollisionResolution.createResolution(c, Football.theFootball));
		}

		for (final CollisionResolver resolver : resolvers)
		{
			resolver.resolveCollision();

			PlayerState playerState = resolver.getPlayerState1();
			this.reset(playerState, null);

			playerState = resolver.getPlayerState2();
			this.reset(playerState, null);
		}

		this.determinePaths();
		
		for (final Player player : this.steps.keySet())
		{
			final PlayerSteps steps = this.steps.get(player);
			this.locationIndex.clearLocations(steps);
			steps.advance();
		}

		this.startOffset += 1;

		for (final Player player : this.steps.keySet())
		{
			final PlayerSteps steps = this.steps.get(player);
			this.locationIndex.setLocations(steps);
		}
	}

	protected abstract void determinePaths();

	@Override
	public PlayerSteps createSteps(final PlayerState startingState, final Path path)
	{
		return new PlayerSteps(startingState, path);
	}

	public List<Collision> getCollisions(final int ticksAhead)
	{
		return this.locationIndex.getCollisions(ticksAhead);
	}

	@Override
	public Path getPath(final Player player)
	{
		return this.getSteps(player).getPath();
	}

	@Override
	public Set<Player> getPlayers()
	{
		return this.steps.keySet();
	}

	@Override
	public int getStartOffset()
	{
		return this.startOffset;
	}

	@Override
	public PlayerState getState(final Player player)
	{
		return this.getSteps(player).getState(0);
	}

	@Override
	public PlayerState getState(final Player player, final int offset)
	{
		return this.getSteps(player).getState(this.getIndex(offset));
	}

	@Override
	public int getStepCapacity()
	{
		return this.stepCapacity;
	}

	@Override
	public PlayerSteps getSteps(final Player player)
	{
		return this.steps.get(player);
	}

	@Override
	public double getTimerInterval()
	{
		return this.timerInterval;
	}

	public void setPath(final Player player, final Path path)
	{
		this.nextPaths.put(player, path);
	}

	private int getIndex(final int offset)
	{
		return (this.startOffset + offset) % this.stepCapacity;
	}

	private void reset(final PlayerState startingState, final Path path)
	{
		final PlayerSteps steps = this.steps.get(startingState.getPlayer());

		this.locationIndex.clearLocations(steps);
		steps.reset(startingState, path);
		this.locationIndex.setLocations(steps);
	}
}
