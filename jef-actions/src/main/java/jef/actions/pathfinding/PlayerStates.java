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
import jef.core.Location;
import jef.core.Performance;
import jef.core.Player;
import jef.core.PlayerState;

public class PlayerStates
{
	public static final int DEFAULT_STEP_CAPACITY = 50;

	private final Map<Player, PlayerSteps> steps = new HashMap<>();
	private final Map<Location, LocationIndex> locationToIndex = new HashMap<>();

	private final int stepCapacity;
	private final double timerInterval;
	private int startOffset;

	public PlayerStates()
	{
		this(PlayerStates.DEFAULT_STEP_CAPACITY, Performance.desiredFrameRate);
	}

	public PlayerStates(final int stepCapacity, final double timerInterval)
	{
		this.stepCapacity = stepCapacity;
		this.timerInterval = timerInterval;
	}

	public int getStepCapacity()
	{
		return this.stepCapacity;
	}

	public double getTimerInterval()
	{
		return this.timerInterval;
	}

	public int getStartOffset()
	{
		return this.startOffset;
	}

	public void addPlayer(final PlayerState state)
	{
		assert !this.getPlayers().contains(state.getPlayer());

		this.steps.put(state.getPlayer(),
				new PlayerSteps(state, this.stepCapacity, this.timerInterval, this.startOffset));
		this.reset(state);
	}

	public Set<Player> getPlayers()
	{
		return this.steps.keySet();
	}

	public PlayerState getState(final Player player)
	{
		return this.getSteps(player).getState(0);
	}

	public PlayerState getState(final Player player, final int offset)
	{
		return this.getSteps(player).getState(this.getIndex(offset));
	}

	public IPlayerSteps getSteps(final Player player)
	{
		return this.steps.get(player);
	}

	public void reset(final PlayerState startingState)
	{
		final PlayerSteps steps = this.steps.get(startingState.getPlayer());

		this.clearLocations(steps);
		steps.reset(startingState);
		this.setLocations(steps);
	}

	public void setSteps(final PlayerSteps playerSteps)
	{
		final PlayerSteps steps = this.steps.get(playerSteps.getPlayer());

		this.clearLocations(steps);
		this.steps.put(playerSteps.getPlayer(), playerSteps);
		this.setLocations(playerSteps);
	}

	public void advance()
	{
		for (final Player player : this.steps.keySet())
		{
			final PlayerSteps steps = this.steps.get(player);
			final PlayerState oldPlayerState = steps.getState(0);

			Location canonicalLocation = this.toCanonicalLocation(oldPlayerState.getLoc());
			LocationIndex ret = this.locationToIndex.get(canonicalLocation);
			if (ret == null)
			{
				continue;
			}

			if (ret.removeOccupier(oldPlayerState, 0) == 0)
			{
				this.locationToIndex.remove(canonicalLocation);
			}

			steps.advance();

			final PlayerState currentState = steps.getState(0);
			canonicalLocation = this.toCanonicalLocation(currentState.getLoc());
			ret = this.locationToIndex.get(canonicalLocation);
			if (ret == null)
			{
				ret = new LocationIndex(canonicalLocation, this.stepCapacity, this.startOffset);
				this.locationToIndex.put(canonicalLocation, ret);
			}

			ret.addOccupier(currentState, 0);
		}

		this.startOffset += 1;
	}

	private void clearLocations(final PlayerSteps steps)
	{
		for (int i = 0; i < steps.getCapacity(); i++)
		{
			final PlayerState playerState = steps.getState(i);
			final Location loc = this.toCanonicalLocation(playerState.getLoc());
			final LocationIndex lEntry = this.locationToIndex.get(loc);
			if (lEntry == null)
				continue;
			
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

	public List<Collision> getCollisions(final int ticksAhead)
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

	private List<Location> getSurroundingLocations(final Location loc)
	{
		return Arrays.asList(new Location(loc.getX() - 1, loc.getY() - 1, 0),
				new Location(loc.getX(), loc.getY() - 1, 0), new Location(loc.getX() + 11, loc.getY() - 1, 0),
				new Location(loc.getX() - 1, loc.getY(), 0), new Location(loc.getX() + 1, loc.getY(), 0),
				new Location(loc.getX() - 1, loc.getY() + 1, 0), new Location(loc.getX(), loc.getY() + 1, 0),
				new Location(loc.getX() + 1, loc.getY() + 1, 0));
	}

	private void setLocations(final PlayerSteps steps)
	{
		for (int i = 0; i < steps.getCapacity(); i++)
		{
			final PlayerState playerState = steps.getState(i);
			if (playerState == null)
			{
				continue;
			}

			final Location canonicalLocation = this.toCanonicalLocation(playerState.getLoc());
			LocationIndex ret = this.locationToIndex.get(canonicalLocation);
			if (ret == null)
			{
				ret = new LocationIndex(canonicalLocation, this.stepCapacity, this.startOffset);
				this.locationToIndex.put(canonicalLocation, ret);
			}

			ret.addOccupier(playerState, i);
		}
	}

	private Location toCanonicalLocation(final Location loc)
	{
		return new Location(Math.round(loc.getX()), Math.round(loc.getY()), 0);
	}

	public void update(final PlayerState newState)
	{
		Player player = newState.getPlayer();
		PlayerState currentState = this.getState(player);
		if (newState.equals(currentState))
			return;

		this.reset(newState);
	}
}
