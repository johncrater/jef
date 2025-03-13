package jef.actions.pathfinding;

import java.util.List;

import jef.core.PlayerState;

public interface IterativeCalculation
{
	public boolean calculate(PlayerState runner, List<PlayerState> defenders, List<PlayerState> blockers, long deltaNanos);
}
