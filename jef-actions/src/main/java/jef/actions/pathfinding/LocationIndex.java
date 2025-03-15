package jef.actions.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jef.core.Location;
import jef.core.PlayerState;

public class LocationIndex
{
	private final Location canonicalLocation;
	private final Object[] occupiers;
	private int occupierCount;
	private final int stepCapacity;
	private final int startOffset;

	public LocationIndex(final Location canonicalLocation, final int stepCapacity, final int startOffset)
	{
		this.stepCapacity = stepCapacity;
		this.startOffset = startOffset;
		this.canonicalLocation = canonicalLocation;
		this.occupiers = new Object[stepCapacity];
	}

	@SuppressWarnings("unchecked")
	public void addOccupier(final PlayerState playerState, final int tick)
	{
		final int index = this.getIndex(tick);
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
		final Object obj = this.occupiers[this.getIndex(tick)];
		if (obj == null)
			return Collections.emptyList();
		if (obj instanceof PlayerState)
			return Collections.singletonList((PlayerState) obj);
		return new ArrayList<>((Set<PlayerState>) obj);
	}

	@SuppressWarnings("unchecked")
	public int removeOccupier(final PlayerState playerState, final int tick)
	{
		final int index = this.getIndex(tick);
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

	private int getIndex(final int offset)
	{
		return (this.startOffset + offset) % this.stepCapacity;
	}

}
