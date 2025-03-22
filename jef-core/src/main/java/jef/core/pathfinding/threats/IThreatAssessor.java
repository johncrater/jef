package jef.pathfinding.threats;

import java.util.Collection;
import java.util.List;

import jef.core.Player;

public interface IThreatAssessor
{
	public List<ThreatAssessment> rankThreats(Player runner, Collection<Player> defenders, Collection<Player> blockers);
}
