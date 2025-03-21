package jef.core.movement.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import jef.core.Player;
import jef.core.collisions.Collision;
import jef.core.movement.DefaultLocation;
import jef.core.movement.Location;
import jef.core.movement.player.DefaultSteerable;
import jef.core.movement.player.PlayerTracker;
import jef.core.movement.player.Steerable;

public class DefaultLocationIndex implements LocationIndex
{
	private final Map<Location, LocationIndexEntry> locationToIndexEntry = new HashMap<>();
	private final Map<String, PlayerIndexEntry> idToIndexEntry = new HashMap<>();

	private final double timeInterval;
	private final int numberOfTicks;

	public DefaultLocationIndex(final double timeInterval, final int numberOfTicks)
	{
		this.timeInterval = timeInterval;
		this.numberOfTicks = numberOfTicks;
	}

	@Override
	public void advance()
	{
		this.locationToIndexEntry.values().forEach(LocationIndexEntry::advance);
		this.idToIndexEntry.values().forEach(PlayerIndexEntry::advance);
	}

	@Override
	public List<Collision> getCollisions(final int ticksAhead)
	{
		final HashSet<Collision> ret = new HashSet<>();

		for (final LocationIndexEntry entry : this.locationToIndexEntry.values())
		{
			final List<PlayerTracker> neighbors = new ArrayList<>();
			final List<PlayerTracker> occupiers = entry.getOccupiers(ticksAhead);
			final List<Location> surroundingLocs = this.getSurroundingLocations(entry.getCanonicalLocation());
			for (final Location loc : surroundingLocs)
			{
				neighbors.addAll(this.getOccupiers(loc, ticksAhead));
			}

			ret.addAll(this.extractRealCollisions(occupiers, neighbors, ticksAhead));
		}

		return new ArrayList<>(ret);
	}

	@Override
	public int getNumTicks()
	{
		return this.numberOfTicks;
	}

	@Override
	public List<? extends PlayerTracker> getOccupiers(final Location location, final int tick)
	{
		final LocationIndexEntry entries = this.locationToIndexEntry.get(location);
		if (entries == null)
			return Collections.emptyList();

		return entries.getOccupiers(tick);
	}

	@Override
	public double getTimeInterval()
	{
		return this.timeInterval;
	}

	@Override
	public void update(final Player player)
	{
		initEntries(player);
		
		final int tick = 0;
		PlayerIndexEntry entry = this.idToIndexEntry.get(player.getPlayerID());
		final PlayerTracker currentTracker = entry.getPlayerTracker(tick);

		if (currentTracker != null)
		{
			if (currentTracker.getLV().equals(player.getLV())
				&& currentTracker.getLoc().equals(player.getLoc()))
			return; // no change, so no need to update entries

			clearEntries(player);
		}

		
		for (int i = 0; i < this.numberOfTicks; i++)
		{
			PlayerTracker tracker = new PlayerTracker(player, this.timeInterval);	
			tracker.move();
			entry.update(i, tracker);

			final Location canonicalLocation = this.toCanonicalLocation(tracker.getLoc());
			LocationIndexEntry locationEntry = this.locationToIndexEntry.get(canonicalLocation);
			if (locationEntry == null)
			{
				locationEntry = new LocationIndexEntry(canonicalLocation, this.getNumTicks());
				this.locationToIndexEntry.put(canonicalLocation, locationEntry);
			}

			locationEntry.addOccupier(tracker, i);
		}
	}

	private void clearEntries(Player player)
	{
		PlayerIndexEntry entry = this.idToIndexEntry.get(player.getPlayerID());
		for (int i = 0; i < this.numberOfTicks; i++)
		{
			Location loc = this.toCanonicalLocation(entry.getPlayerTracker(i).getLoc());
			LocationIndexEntry lEntry = this.locationToIndexEntry.get(loc);
			PlayerTracker pEntry = entry.getPlayerTracker(i);
			int remainingOccupiers = lEntry.removeOccupier(pEntry, i);
			if (remainingOccupiers == 0)
				this.locationToIndexEntry.remove(loc);
		}
		
		entry.clear();
	}
	
	private void initEntries(Player player)
	{
		PlayerIndexEntry entry = this.idToIndexEntry.get(player.getPlayerID());
		if (entry == null)
		{
			entry = new PlayerIndexEntry(player, this.getNumTicks());
			this.idToIndexEntry.put(player.getPlayerID(), entry);
		}
	}
	
	private List<Collision> extractRealCollisions(final List<? extends PlayerTracker> targetPlayers,
			final List<? extends PlayerTracker> surroundingPlayers, final int tick)
	{
		final List<Collision> ret = new ArrayList<>();

		for (int i = 0; i < targetPlayers.size(); i++)
		{
			for (int j = i + 1; j < targetPlayers.size(); j++)
			{
				final PlayerTracker p1 = targetPlayers.get(i);
				final PlayerTracker p2 = targetPlayers.get(j);

				if (p1 == p2)
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
				final PlayerTracker p1 = targetPlayers.get(i);
				final PlayerTracker p2 = surroundingPlayers.get(j);

				if (p1 == p2)
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
		return Arrays.asList(new DefaultLocation(loc.getX() - 1, loc.getY() - 1, 0),
				new DefaultLocation(loc.getX(), loc.getY() - 1, 0),
				new DefaultLocation(loc.getX() + 11, loc.getY() - 1, 0),
				new DefaultLocation(loc.getX() - 1, loc.getY(), 0), new DefaultLocation(loc.getX() + 1, loc.getY(), 0),
				new DefaultLocation(loc.getX() - 1, loc.getY() + 1, 0),
				new DefaultLocation(loc.getX(), loc.getY() + 1, 0),
				new DefaultLocation(loc.getX() + 1, loc.getY() + 1, 0));
	}

	private Location toCanonicalLocation(final Location loc)
	{
		return new DefaultLocation(Math.round(loc.getX()), Math.round(loc.getY()), 0);
	}
}
