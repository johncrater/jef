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
	List<Steerable> getOccupiers(final int tick)
	{
		Object obj = this.occupiers[getIndex(tick)];
		if (obj == null)
			return Collections.emptyList();
		else if (obj instanceof Steerable)
			return Collections.singletonList((Steerable)obj);
		else
			return new ArrayList<Steerable>((Set<Steerable>)obj);
	}

	@SuppressWarnings("unchecked")
	int removeOccupier(final Steerable steerable, int tick)
	{
		int index = this.getIndex(tick);
		Object obj = this.occupiers[index];
		
		if (obj instanceof Steerable)
		{
			assert ((Steerable) obj).getPlayer().getPlayerID().equals(steerable.getPlayer().getPlayerID());
			this.occupiers[index] = null;
			occupierCount -= 1;
			assert occupierCount >= 0;
		}
		else 
		{
			Set<Steerable> set = (Set<Steerable>)obj;
			if (set.remove(steerable))
			{
				occupierCount -= 1;
				assert occupierCount >= 0;
			}
		}
		
		return this.occupierCount;
	}

	@SuppressWarnings("unchecked")
	void addOccupier(final Steerable steerable, final int tick)
	{
		int index = getIndex(tick);
		Object obj = this.occupiers[index];
		if (obj == null)
		{
			this.occupiers[index] = steerable;
			this.occupierCount += 1;
		}
		else if (obj instanceof Steerable)
		{
			Steerable pObj = (Steerable) obj;
			if (pObj.getPlayer().getPlayerID().equals(steerable.getPlayer().getPlayerID()) == false)
			{
				Set<Steerable> set = new HashSet<Steerable>();
				set.add(steerable);
				set.add(pObj);
				this.occupiers[index] = set;
				this.occupierCount += 1;
			}
		}
		else 
		{
			Set<Steerable> set = (Set<Steerable>)obj;
			if (set.add(steerable))
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