package jef.core.movement.index;

import java.util.Arrays;

import jef.core.movement.player.PlayerTracker;

class PlayerIndexEntry
{
	private final String playerId;
	private final PlayerTracker[] players;
	private int startOffset;

	PlayerIndexEntry(final String playerId, final int lookAheadTicks)
	{
		this.playerId = playerId;
		this.players = new PlayerTracker[lookAheadTicks];
	}

	String getPlayerId()
	{
		return this.playerId;
	}

	void clear()
	{
		Arrays.fill(players, null);
	}
	
	int getLookAheadTicks()
	{
		return this.players.length;
	}

	PlayerTracker getPlayerTracker(final int tick)
	{
		return players[getIndex(tick)];
	}

	void update(int tick, PlayerTracker player)
	{
		int index = getIndex(tick);
		this.players[index] = player;
	}
	
	void advance()
	{
		this.startOffset += 1;
		this.startOffset = this.startOffset % this.players.length;
	}

	private int getIndex(int tickCount)
	{
		return (this.startOffset + tickCount) % this.players.length;
	}
}