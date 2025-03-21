package jef.core.movement.index;

import java.util.List;

import jef.core.Player;
import jef.core.collisions.Collision;
import jef.core.movement.Location;
import jef.core.movement.player.PlayerTracker;

public interface LocationIndex
{
	/**
	 * Gets the occupiers at the given location at the given time interval
	 * @param location
	 * @param tickCount This is relative to the current tick. For example, 0 is the currentTick, 1 is the next one and so on up to getNumTicks()
	 * @return
	 */
	List<? extends PlayerTracker> getOccupiers(Location location, int tick);

	List<Collision> getCollisions(int ticksAhead);
	
	double getTimeInterval();
	int getNumTicks();
	
	/**
	 * Should be called after all updates are complete
	 */
	void advance();
	
	/**
	 * This should be called BEFORE player movement in the current turn. That way, collisions will be available before movement
	 * @param player
	 */
	void update(Player player);
}