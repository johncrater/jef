package jef.pathfinding;

import jef.geometry.Location;
import jef.movement.PlayerId;
import jef.movement.player.Path;
import jef.movement.player.PlayerState;

public interface IPlayerSteps
{
	public int getDestinationReachedSteps();
	public boolean hasReachedDestination();

	public PlayerState getFirst();
	public PlayerState getLast();

	public Path getPath();
	public int getStepCapacity();
	public PlayerId getPlayerId();
	public double getTimerInterval();

	public PlayerState getState(int offset);
	public PlayerState getPerceivedState(int offset);
	public int getStepsToLocation(Location loc);
	public int getPerceivedStepsToLocation(Location loc);
}