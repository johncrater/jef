package jef.pathfinding;

import java.util.Set;

import jef.core.Player;
import jef.movement.player.Path;
import jef.movement.player.PlayerState;
import jef.pathfinding.Players.PlayerSteps;

public interface IPlayers
{
	public PlayerSteps createSteps(PlayerState startingState, Path path);
	public Path getPath(Player player);
	public Set<Player> getPlayers();
	public int getStartOffset();
	public PlayerState getState(Player player);
	public PlayerState getPerceivedState(Player player);
	public PlayerState getState(Player player, int offset);
	public PlayerState getPerceivedState(Player player, int offset);
	public int getStepCapacity();
	public double getLookAheadSeconds();
	public PlayerSteps getSteps(Player player);
	public double getTimerInterval();

}