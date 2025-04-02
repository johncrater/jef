package jef.pathfinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jef.geometry.AngularVelocity;
import jef.geometry.Location;
import jef.movement.PlayerId;
import jef.movement.ball.BallPhysics;
import jef.movement.ball.BallTracker;
import jef.movement.ball.FootballState;
import jef.movement.player.ISteering;
import jef.movement.player.Path;
import jef.movement.player.PlayerState;
import jef.movement.player.PlayerTracker;
import jef.pathfinding.collisions.Collision;
import jef.pathfinding.collisions.CollisionResolution;
import jef.pathfinding.collisions.ICollisionResolver;

public class PathfindingState<T extends IPathfinderPlayer> implements IPathfinderState<T>
{
	public static final double LOOK_AHEAD_SECONDS = 5.0;

	// we subtract 1 because current movements are never considered in the same tick
	// they are calculated
	public static final int LOOK_BEHIND_STEPS = (int) Math.round(1.0 / IPathfinderPlayer.VISUAL_REACTION_TIME) - 1;

	private static Location toCanonicalLocation(final Location loc)
	{
		return new Location(Math.round(loc.getX()), Math.round(loc.getY()), 0);
	}

	private final Map<PlayerId, PlayerSteps> steps = new HashMap<>();
	private Map<PlayerId, T> idToPlayer = new HashMap<>();
	private final Map<PlayerId, Path> nextPaths = new HashMap<>();
	private FootballSteps footballSteps;

	private final LocationIndex locationIndex;

	private final double lookAheadSeconds;
	private final double timerInterval;
	private int startOffset;

	public PathfindingState(double timeInterval)
	{
		this(PathfindingState.LOOK_AHEAD_SECONDS, timeInterval);
	}

	public PathfindingState(final double lookAheadSeconds, final double timerInterval)
	{
		this.lookAheadSeconds = lookAheadSeconds;
		this.timerInterval = timerInterval;
		this.locationIndex = new LocationIndex();
	}

	public void validatePlayers()
	{
		for (PlayerId playerId : this.idToPlayer.keySet())
		{
			IPlayerSteps steps = this.getPlayerSteps(playerId);
			assert steps != null;

			for (int i = -LOOK_BEHIND_STEPS; i < this.getStepCapacity(); i++)
			{
				PlayerState state = steps.getState(i);
				assert state != null;

				List<PlayerId> occupiers = this.locationIndex.getOccupiers(state.getLoc(), i);
				boolean found = false;
				for (PlayerId playerId2 : occupiers)
				{
					if (playerId == playerId2)
					{
						found = true;
						break;
					}
				}

				assert found == true;
			}
		}

		for (Location loc : this.locationIndex.getLocations())
		{
			for (int i = -LOOK_BEHIND_STEPS; i < this.getStepCapacity(); i++)
			{
				List<PlayerId> occupiers = this.locationIndex.getOccupiers(loc, i);
				for (PlayerId playerId : occupiers)
				{
					PlayerState state = this.getPlayerState(playerId, i);
					final Location stateLoc = PathfindingState.toCanonicalLocation(state.getLoc());
					assert stateLoc.equals(loc);
				}
			}
		}
	}
	
	public void validateFootball()
	{
		for (int i = -LOOK_BEHIND_STEPS; i < this.getStepCapacity(); i++)
		{
			FootballState footballState = this.getFootballState(i);
			assert footballState != null;

			PlayerId playerInPossession = footballState.getPlayerInPossession();
			assert playerInPossession != null;

			if (playerInPossession == PlayerId.NONE)
				continue;

			PlayerState state = this.getPlayerState(playerInPossession, i);
			
			// we use compareTo here since the football is elevated off the ground when in possession and so the Z values don't match.
			assert state.getLoc().compareTo(footballState.getLoc()) == 0;
			assert state.getLV().equals(footballState.getLV());
//			assert state.getAV().equals(footballState.getAV());
		}
	}

	public void addFootball(FootballState startingState)
	{
		assert this.footballSteps == null;
		this.footballSteps = new FootballSteps(FootballState.beginGame());

		
		if (startingState != null && startingState.getPlayerInPossession() != PlayerId.NONE)
		{
			assert this.idToPlayer.containsKey(startingState.getPlayerInPossession());
			this.footballSteps.update(startingState);
		}
		
		validateFootball();
	}
	
	public void addPlayer(T pathfinderPlayer, final PlayerState state)
	{
		assert !this.idToPlayer.containsKey(pathfinderPlayer.getId());
		assert !this.getPlayers().contains(pathfinderPlayer);

		this.idToPlayer.put(pathfinderPlayer.getId(), pathfinderPlayer);
		this.steps.put(pathfinderPlayer.getId(), new PlayerSteps(state, new Path(state.getLoc())));

		this.reset(state, new Path(state.getLoc()));
		this.validatePlayers();
	}

	@Override
	public FootballState getFootballState()
	{
		return this.getFootballState(0);
	}

	@Override
	public FootballState getFootballState(int offset)
	{
		return this.footballSteps.getState(offset);
	}

	@Override
	public T getPlayer(PlayerId playerId)
	{
		return this.idToPlayer.get(playerId);
	}

	public void advance(FootballState footballState)
	{
		for (final PlayerId playerId : this.nextPaths.keySet())
		{
			final Path newPath = this.nextPaths.get(playerId);
			this.steps.get(playerId).path = newPath;
			this.reset(this.getPlayerState(playerId), newPath);
		}

		final List<Collision> collisions = this.getCollisions(0);
		final Set<ICollisionResolver> resolvers = new HashSet<>();
		for (final Collision c : collisions)
		{
			resolvers.add(new CollisionResolution<T>().createResolution(this, c));
		}

		for (final ICollisionResolver resolver : resolvers)
		{
			resolver.resolveCollision();

			PlayerState playerState = resolver.getPlayerState1();
			this.reset(playerState, null);

			playerState = resolver.getPlayerState2();
			this.reset(playerState, null);
		}

		this.determinePaths();

		for (final PlayerId playerId : this.steps.keySet())
		{
			final PlayerSteps steps = this.steps.get(playerId);
			this.locationIndex.clearLocations(steps);
			steps.advance();
		}

		if (footballState == null)
			footballState = this.getFootballState();
		
		this.footballSteps.update(footballState);
		
		this.startOffset += 1;

		for (final PlayerId playerId : this.steps.keySet())
		{
			final IPlayerSteps steps = this.steps.get(playerId);
			this.locationIndex.setLocations(steps);
		}

		this.validatePlayers();
		this.validateFootball();

	}

	@Override
	public IPlayerSteps createSteps(final PlayerState startingState, final Path path)
	{
		assert path != null;
		return new PlayerSteps(startingState, path);
	}

	public List<Collision> getCollisions(final int ticksAhead)
	{
		return this.locationIndex.getCollisions(ticksAhead);
	}

	@Override
	public Path getPath(final PlayerId playerId)
	{
		return this.getPlayerSteps(playerId).getPath();
	}

	@Override
	public Collection<T> getPlayers()
	{
		return this.idToPlayer.values();
	}

	@Override
	public int getStartOffset()
	{
		return this.startOffset;
	}

	@Override
	public PlayerState getPlayerState(PlayerId playerId)
	{
		return this.getPlayerSteps(playerId).getState(0);
	}

	@Override
	public PlayerState getPerceivedPlayerState(PlayerId playerId)
	{
		return this.getPlayerSteps(playerId).getState(-LOOK_BEHIND_STEPS);
	}

	@Override
	public PlayerState getPerceivedPlayerState(PlayerId playerId, int offset)
	{
		return getPlayerState(playerId, offset - LOOK_BEHIND_STEPS);
	}

	@Override
	public PlayerState getPlayerState(PlayerId playerId, final int offset)
	{
		return this.getPlayerSteps(playerId).getState(offset);
	}

	@Override
	public double getLookAheadSeconds()
	{
		return this.lookAheadSeconds;
	}

	@Override
	public int getStepCapacity()
	{
		return (int) (this.lookAheadSeconds / this.timerInterval);
	}

	@Override
	public IPlayerSteps getPlayerSteps(PlayerId playerId)
	{
		return this.steps.get(playerId);
	}

	@Override
	public double getTimerInterval()
	{
		return this.timerInterval;
	}

	public void setPath(PlayerId playerId, final Path path)
	{
		assert path != null;
		this.nextPaths.put(playerId, path);
	}

	protected void determinePaths()
	{
	}

	private int getIndex(int offset)
	{
		assert offset < this.getStepCapacity();
		assert offset >= -LOOK_BEHIND_STEPS;

		offset += LOOK_BEHIND_STEPS;
		return (this.startOffset + offset) % (this.getStepCapacity() + LOOK_BEHIND_STEPS);
	}

	private void reset(final PlayerState startingState, final Path path)
	{
		final PlayerSteps steps = this.steps.get(startingState.getPlayerId());

		this.locationIndex.clearLocations(steps);
		steps.reset(startingState, path);
		this.locationIndex.setLocations(steps);
	}

	public class FootballSteps implements IFootballSteps
	{
		private final FootballState[] steps;

		public FootballSteps(FootballState startingState)
		{
			super();
			this.steps = new FootballState[getStepCapacity() + LOOK_BEHIND_STEPS];
			Arrays.fill(steps, startingState);
		}

		@Override
		public double getTimerInterval()
		{
			return PathfindingState.this.getTimerInterval();
		}

		@Override
		public FootballState getFirst()
		{
			return this.getState(0);
		}

		@Override
		public FootballState getLast()
		{
			return this.getState(PathfindingState.this.getStepCapacity() - 1);
		}

		@Override
		public FootballState getState(final int offset)
		{
			final int index = PathfindingState.this.getIndex(offset);
			return this.steps[index];
		}

		@Override
		public FootballState getPerceivedState(final int offset)
		{
			final int index = PathfindingState.this.getIndex(offset - LOOK_BEHIND_STEPS);
			return this.steps[index];
		}

		@Override
		public int getStepCapacity()
		{
			return PathfindingState.this.getStepCapacity();
		}

		void update(final FootballState newState)
		{
			if (newState.getPlayerInPossession() == PlayerId.NONE)
			{
				FootballState currentState = this.getFirst();
				if (newState.getLoc().equals(currentState.getLoc()) && newState.getLV().equals(currentState.getLV()) && newState.getAV().equals(currentState.getAV()))
				{
					final BallTracker tracker = new BallTracker(this.getLast(), PathfindingState.this.getTimerInterval());
					BallPhysics physics = new BallPhysics();
					physics.update(tracker);

					this.steps[PathfindingState.this.getIndex(0)] = tracker.getState();
					return;
				}
					
				
				final BallTracker tracker = new BallTracker(newState, PathfindingState.this.getTimerInterval());
				for (int i = -LOOK_BEHIND_STEPS; i < 0; i++)
				{
					if (this.steps[PathfindingState.this.getIndex(i)] == null)
						this.steps[PathfindingState.this.getIndex(i)] = tracker.getState();
				}

				this.steps[PathfindingState.this.getIndex(0)] = tracker.getState();

				tracker.advance();

				for (int i = 1; i < this.getStepCapacity(); i++)
				{
					BallPhysics physics = new BallPhysics();
					physics.update(tracker);
					tracker.advance();
					this.steps[PathfindingState.this.getIndex(i)] = tracker.getState();
				}
			}
			else
			{
				Arrays.fill(this.steps, null);

				FootballState currentState = newState;
				for (int i = -LOOK_BEHIND_STEPS; i < this.getStepCapacity(); i++)
				{
					PlayerId playerId = currentState.getPlayerInPossession();
					PlayerState playerState = getPlayerState(playerId, i);
					IPathfinderPlayer player = getPlayer(playerId);
					currentState = newState.move(playerState.getLV(),
							playerState.getLoc().add(0, 0, player.getHeight() / 2.0),
							new AngularVelocity(playerState.getLV().getAzimuth(), 0));
					this.steps[PathfindingState.this.getIndex(i)] = currentState;
				}
			}
		}
	}

	public class PlayerSteps implements IPlayerSteps
	{
		private final PlayerState[] steps;
		private Path path;
		private int destinationReachedSteps;

		public PlayerSteps(final PlayerState startingState, final Path path)
		{
			assert path != null;

			this.steps = new PlayerState[getStepCapacity() + LOOK_BEHIND_STEPS];
			this.destinationReachedSteps = -1;
			this.reset(startingState, path);
		}

		@Override
		public double getTimerInterval()
		{
			return PathfindingState.this.getTimerInterval();
		}

		@Override
		public int getDestinationReachedSteps()
		{
			return this.destinationReachedSteps;
		}

		@Override
		public PlayerState getFirst()
		{
			return this.getState(0);
		}

		@Override
		public PlayerState getLast()
		{
			return this.getState(PathfindingState.this.getStepCapacity() - 1);
		}

		@Override
		public Path getPath()
		{
			return this.path;
		}

		@Override
		public PlayerId getPlayerId()
		{
			return this.steps[0].getPlayerId();
		}

		@Override
		public PlayerState getState(final int offset)
		{
			final int index = PathfindingState.this.getIndex(offset);
			return this.steps[index];
		}

		@Override
		public PlayerState getPerceivedState(final int offset)
		{
			final int index = PathfindingState.this.getIndex(offset - LOOK_BEHIND_STEPS);
			return this.steps[index];
		}

		@Override
		public int getStepCapacity()
		{
			return PathfindingState.this.getStepCapacity();
		}

		@Override
		public int getStepsToLocation(final Location loc)
		{
			for (int i = 0; i < PathfindingState.this.getStepCapacity(); i++)
			{
				final PlayerState tmpState = this.getState(i);
				final Location tmpLoc = tmpState.getLoc();
				if (tmpLoc.distanceBetween(loc) <= 1)
					return i;
			}

			return -1;
		}

		@Override
		public int getPerceivedStepsToLocation(final Location loc)
		{
			for (int i = -LOOK_BEHIND_STEPS; i < PathfindingState.this.getStepCapacity(); i++)
			{
				final PlayerState tmpState = this.getState(i);
				final Location tmpLoc = tmpState.getLoc();
				if (tmpLoc.distanceBetween(loc) <= 1)
					return Math.max(i, 0);
			}

			return -1;
		}

		@Override
		public boolean hasReachedDestination()
		{
			return this.destinationReachedSteps > -1;
		}

		void advance()
		{
			final PlayerTracker tracker = new PlayerTracker(this.getLast(), this.path,
					PathfindingState.this.getTimerInterval());
			final ISteering iSteering = ISteering.getInstance();
			final boolean destinationReached = iSteering.next(tracker);
			this.steps[PathfindingState.this.getIndex(0)] = tracker.getState();

			if (destinationReached && (this.destinationReachedSteps == -1))
			{
				this.destinationReachedSteps = this.getStepCapacity() - 1;
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

			assert this.path != null;

			Arrays.fill(this.steps, null);

			this.destinationReachedSteps = -1;
			final ISteering iSteering = ISteering.getInstance();
			final PlayerTracker tracker = new PlayerTracker(startingState, this.path,
					PathfindingState.this.getTimerInterval());
			for (int i = -LOOK_BEHIND_STEPS; i < 0; i++)
			{
				if (this.steps[PathfindingState.this.getIndex(i)] == null)
					this.steps[PathfindingState.this.getIndex(i)] = tracker.getState();
			}

			this.steps[PathfindingState.this.getIndex(0)] = tracker.getState();

			tracker.advance();
			if (tracker.waypointDestinationReached())
			{
				this.destinationReachedSteps = 0;
			}

			for (int i = 1; i < this.getStepCapacity(); i++)
			{
				final boolean destinationReached = iSteering.next(tracker);
				if (destinationReached && (this.destinationReachedSteps == -1))
				{
					this.destinationReachedSteps = i;
				}

				tracker.advance();
				this.steps[PathfindingState.this.getIndex(i)] = tracker.getState();
			}
		}
	}

	private class LocationIndex
	{
		private final Map<Location, LocationIndexEntry> locationToIndex = new HashMap<>();

		public LocationIndex()
		{
		}

		public Collection<Location> getLocations()
		{
			return this.locationToIndex.keySet();
		}

		public void clearLocation(final PlayerState playerState, final int index)
		{
			final Location loc = PathfindingState.toCanonicalLocation(playerState.getLoc());
			final LocationIndexEntry lEntry = this.locationToIndex.get(loc);
			if (lEntry == null)
				return;

			final int remainingOccupiers = lEntry.removeOccupier(playerState.getPlayerId(), index);
			if (remainingOccupiers == 0)
			{
				this.locationToIndex.remove(loc);
			}
		}

		public void clearLocations(final IPlayerSteps steps)
		{
			for (int i = -LOOK_BEHIND_STEPS; i < PathfindingState.this.getStepCapacity(); i++)
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
				final List<PlayerId> neighbors = new ArrayList<>();
				final List<PlayerId> occupiers = entry.getOccupiers(ticksAhead);
				final List<Location> surroundingLocs = this.getSurroundingLocations(entry.getCanonicalLocation());
				for (final Location loc : surroundingLocs)
				{
					neighbors.addAll(this.getOccupiers(loc, ticksAhead));
				}

				ret.addAll(this.extractRealCollisions(occupiers, neighbors, ticksAhead));
			}

			return new ArrayList<>(ret);
		}

		public List<PlayerId> getOccupiers(final Location location, final int tick)
		{
			final Location loc = PathfindingState.toCanonicalLocation(location);
			final LocationIndexEntry entries = this.locationToIndex.get(loc);
			if (entries == null)
				return Collections.emptyList();

			return entries.getOccupiers(tick);
		}

		public void setLocations(final IPlayerSteps steps)
		{
			for (int i = -LOOK_BEHIND_STEPS; i < PathfindingState.this.getStepCapacity(); i++)
			{
				final PlayerState playerState = steps.getState(i);
				this.setLocation(playerState, i);
			}
		}

		private List<Collision> extractRealCollisions(final List<PlayerId> targetPlayers,
				final List<PlayerId> surroundingPlayers, final int tick)
		{
			final List<Collision> ret = new ArrayList<>();

			for (int i = 0; i < targetPlayers.size(); i++)
			{
				for (int j = i + 1; j < targetPlayers.size(); j++)
				{
					final PlayerState p1 = getPlayerState(targetPlayers.get(i), tick);
					final PlayerState p2 = getPlayerState(targetPlayers.get(j), tick);

					if (p1.equals(p2))
					{
						continue;
					}

					if (p1.getLoc().distanceBetween(p2.getLoc()) < T.SIZE)
					{
						ret.add(new Collision(p1, p2, tick));
					}
				}
			}

			for (int i = 0; i < targetPlayers.size(); i++)
			{
				for (int j = 0; j < surroundingPlayers.size(); j++)
				{
					final PlayerState p1 = getPlayerState(targetPlayers.get(i), tick);
					final PlayerState p2 = getPlayerState(surroundingPlayers.get(j), tick);

					if (p1.equals(p2))
					{
						continue;
					}

					if (p1.getLoc().distanceBetween(p2.getLoc()) < T.SIZE)
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
			final Location canonicalLocation = PathfindingState.toCanonicalLocation(playerState.getLoc());
			LocationIndexEntry ret = this.locationToIndex.get(canonicalLocation);
			if (ret == null)
			{
				ret = new LocationIndexEntry(canonicalLocation);
				this.locationToIndex.put(canonicalLocation, ret);
			}

			ret.addOccupier(playerState.getPlayerId(), i);
		}

		private class LocationIndexEntry
		{
			private final Object[] occupiers;
			private int occupierCount;
			private final Location canonicalLocation;

			public LocationIndexEntry(final Location canonicalLocation)
			{
				this.canonicalLocation = canonicalLocation;
				this.occupiers = new Object[PathfindingState.this.getStepCapacity() + LOOK_BEHIND_STEPS];
			}

			@SuppressWarnings("unchecked")
			public void addOccupier(final PlayerId playerId, final int tick)
			{
				final int index = PathfindingState.this.getIndex(tick);
				final Object obj = this.occupiers[index];
				if (obj == null)
				{
					this.occupiers[index] = playerId;
					this.occupierCount += 1;
				}
				else if (obj instanceof final PlayerId pObj)
				{
					if (!pObj.equals(playerId))
					{
						final Set<PlayerId> set = new HashSet<>();
						set.add(playerId);
						set.add(pObj);
						this.occupiers[index] = set;
						this.occupierCount += 1;
					}
				}
				else
				{
					final Set<PlayerId> set = (Set<PlayerId>) obj;
					if (set.add(playerId))
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
			public List<PlayerId> getOccupiers(final int tick)
			{
				final Object obj = this.occupiers[PathfindingState.this.getIndex(tick)];
				if (obj == null)
					return Collections.emptyList();
				if (obj instanceof PlayerId)
					return Collections.singletonList((PlayerId) obj);
				return new ArrayList<>((Set<PlayerId>) obj);
			}

			@SuppressWarnings("unchecked")
			public int removeOccupier(final PlayerId playerId, final int tick)
			{
				final int index = PathfindingState.this.getIndex(tick);
				final Object obj = this.occupiers[index];

				if (obj instanceof PlayerId)
				{
					this.occupiers[index] = null;
					this.occupierCount -= 1;
					assert this.occupierCount >= 0;
				}
				else
				{
					final Set<PlayerId> set = (Set<PlayerId>) obj;
					if (set.remove(playerId))
					{
						this.occupierCount -= 1;
						assert this.occupierCount >= 0;
					}
				}

				return this.occupierCount;
			}
		}
	}
}
