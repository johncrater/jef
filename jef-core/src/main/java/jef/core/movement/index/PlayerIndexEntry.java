package jef.core.movement.index;

import java.util.Arrays;

import jef.core.Player;
import jef.core.movement.player.Steerable;

class PlayerIndexEntry
{
	private final Player player;
	private final Steerable[] steerables;
	private int startOffset;

	PlayerIndexEntry(final Player player, final int lookAheadTicks)
	{
		this.player = player;
		this.steerables = new Steerable[lookAheadTicks];
	}

	String getPlayerId()
	{
		return this.player.getPlayerID();
	}

	void clear()
	{
		Arrays.fill(steerables, null);
	}
	
	int getLookAheadTicks()
	{
		return this.steerables.length;
	}

	Steerable getSteerable(final int tick)
	{
		return steerables[getIndex(tick)];
	}

	void update(int tick, Steerable steerable)
	{
		int index = getIndex(tick);
		this.steerables[index] = steerable;
	}
	
	void advance()
	{
		this.startOffset += 1;
		this.startOffset = this.startOffset % this.steerables.length;
	}

	private int getIndex(int tickCount)
	{
		return (this.startOffset + tickCount) % this.steerables.length;
	}
}