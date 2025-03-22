package jef;

import java.util.Set;

import jef.Players.PlayerSteps;
import jef.core.Player;
import jef.core.PlayerState;
import jef.core.movement.player.Path;

public interface IPlayers
{
	public PlayerSteps createSteps(PlayerState startingState, Path path);
	public Path getPath(Player player);
	public Set<Player> getPlayers();
	public int getStartOffset();
	public PlayerState getState(Player player);
	public PlayerState getState(Player player, int offset);
	public int getStepCapacity();
	public PlayerSteps getSteps(Player player);
	public double getTimerInterval();

}