package jef.core.movement.index;

import java.util.Arrays;

import jef.core.Player;
import jef.core.movement.player.PlayerTracker;

class PlayerIndexEntry
{
	private final Player player;
	private final PlayerTracker[] trackers;
	private int startOffset;

	PlayerIndexEntry(final Player player, final int lookAheadTicks)
	{
		this.player = player;
		this.trackers = new PlayerTracker[lookAheadTicks];
	}

	String getPlayerId()
	{
		return this.player.getPlayerID();
	}

	void clear()
	{
		Arrays.fill(trackers, null);
	}
	
	int getLookAheadTicks()
	{
		return this.trackers.length;
	}

	PlayerTracker getPlayerTracker(final int tick)
	{
		return trackers[getIndex(tick)];
	}

	void update(int tick, PlayerTracker tracker)
	{
		int index = getIndex(tick);
		this.trackers[index] = tracker;
	}
	
	void advance()
	{
		this.startOffset += 1;
		this.startOffset = this.startOffset % this.trackers.length;
	}

	private int getIndex(int tickCount)
	{
		return (this.startOffset + tickCount) % this.trackers.length;
	}
}