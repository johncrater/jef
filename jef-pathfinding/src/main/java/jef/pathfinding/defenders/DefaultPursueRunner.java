package jef.pathfinding.defenders;

import jef.pathfinding.DefaultInterceptPlayer;
import jef.pathfinding.IPathfinderPlayer;
import jef.pathfinding.IPathfinderState;

public class DefaultPursueRunner<T extends IPathfinderPlayer> extends DefaultInterceptPlayer<T> implements IDefenderPathfinder
{
	public DefaultPursueRunner(IPathfinderState<T> pathfinderState, T player, T runner)
	{
		super(pathfinderState, player, runner);
	}
}
