package jef.actions.pathfinding;

import jef.core.Player;
import jef.core.PlayerState;

public interface IPlayerSteps
{
	public int getCapacity();
	public int getDestinationReachedSteps();
	public PlayerState getFirst();
	public PlayerState getLast();
	public PlayerState getState(int offset);
	public boolean hasReachedDestination();
	public Player getPlayer();
	public double getTimerInterval();
	public int getStartOffset();
	
}