package jef.pathfinding.threats;

import java.util.Collection;
import java.util.List;

import jef.pathfinding.IPathfinderPlayer;

public interface IThreatAssessor<T extends IPathfinderPlayer>
{
	public List<ThreatAssessment<T>> rankThreats(T runner, Collection<T> defenders, Collection<T> blockers);
}
