package jef.pathfinding;

import java.util.Collection;

import jef.movement.PlayerId;
import jef.movement.ball.FootballState;
import jef.movement.player.Path;
import jef.movement.player.PlayerState;

public interface IPathfinderState<T extends IPathfinderPlayer>
{
	public IPlayerSteps createSteps(PlayerState startingState, Path path);
	public IPlayerSteps getPlayerSteps(PlayerId playerId);
	
	public Path getPath(PlayerId playerId);
	public Collection<T> getPlayers();
	public T getPlayer(PlayerId playerId);

	public int getStartOffset();
	public int getStepCapacity();
	public double getLookAheadSeconds();
	public double getTimerInterval();
	
	public PlayerState getPlayerState(PlayerId playerId);
	public PlayerState getPerceivedPlayerState(PlayerId playerId);
	public PlayerState getPlayerState(PlayerId playerId, int offset);
	public PlayerState getPerceivedPlayerState(PlayerId playerId, int offset);
	
	public FootballState getFootballState();
	public FootballState getFootballState(int offset);
}