package jef.core.movement.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jef.core.movement.Location;
import jef.core.movement.player.PlayerTracker;
import jef.core.movement.player.Steerable;

class LocationIndexEntry
{
	private final Location canonicalLocation;
	private final Object[] occupiers;
	private int startOffset;
	private int occupierCount;

	LocationIndexEntry(final Location canonicalLocation, final int lookAheadTicks)
	{
		this.canonicalLocation = canonicalLocation;
		this.occupiers = new Object[lookAheadTicks];
	}

	Location getCanonicalLocation()
	{
		return this.canonicalLocation;
	}

	int getLookAheadTicks()
	{
		return this.occupiers.length;
	}

	@SuppressWarnings("unchecked")
	List<PlayerTracker> getOccupiers(final int tick)
	{
		Object obj = this.occupiers[getIndex(tick)];
		if (obj == null)
			return Collections.emptyList();
		else if (obj instanceof PlayerTracker)
			return Collections.singletonList((PlayerTracker)obj);
		else
			return new ArrayList<PlayerTracker>((Set<PlayerTracker>)obj);
	}

	@SuppressWarnings("unchecked")
	int removeOccupier(final PlayerTracker player, int tick)
	{
		int index = this.getIndex(tick);
		Object obj = this.occupiers[index];
		
		if (obj instanceof PlayerTracker)
		{
			this.occupiers[index] = null;
			occupierCount -= 1;
			assert occupierCount >= 0;
		}
		else 
		{
			Set<PlayerTracker> set = (Set<PlayerTracker>)obj;
			if (set.remove(player))
			{
				occupierCount -= 1;
				assert occupierCount >= 0;
			}
		}
		
		return this.occupierCount;
	}

	@SuppressWarnings("unchecked")
	void addOccupier(final PlayerTracker player, final int tick)
	{
		int index = getIndex(tick);
		Object obj = this.occupiers[index];
		if (obj == null)
		{
			this.occupiers[index] = player;
			this.occupierCount += 1;
		}
		else if (obj instanceof PlayerTracker)
		{
			PlayerTracker pObj = (PlayerTracker)obj;
			if (pObj.getPlayer().getPlayerID().equals(player.getPlayer().getPlayerID()) == false)
			{
				Set<PlayerTracker> set = new HashSet<PlayerTracker>();
				set.add(player);
				set.add(pObj);
				this.occupiers[index] = set;
				this.occupierCount += 1;
			}
		}
		else 
		{
			Set<PlayerTracker> set = (Set<PlayerTracker>)obj;
			if (set.add(player))
				this.occupierCount += 1;
		}

	}

	@SuppressWarnings("unchecked")
	int getOccupierCount(int tick)
	{
		int index = getIndex(tick);
		Object obj = this.occupiers[index];
		if (obj == null)
		{
			return 0;
		}
		else if (obj instanceof Steerable)
		{
			return 1;
		}
		else 
		{
			Set<Steerable> set = (Set<Steerable>)obj;
			return set.size();
		}
	}
	
	
	int advance()
	{
		this.startOffset += 1;
		this.startOffset = this.startOffset % this.occupiers.length;
		return this.occupierCount;
	}

	private int getIndex(int tickCount)
	{
		return (this.startOffset + tickCount) % this.occupiers.length;
	}
}