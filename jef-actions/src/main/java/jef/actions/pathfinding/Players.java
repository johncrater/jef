package jef.actions.pathfinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jef.actions.pathfinding.blocking.BlockerPathfinder;
import jef.actions.pathfinding.defenders.DefenderPathfinder;
import jef.actions.pathfinding.runners.RunnerPathfinder;
import jef.core.Player;
import jef.core.PlayerState;
import jef.core.PlayerSteps;
import jef.core.movement.index.DefaultLocationIndex;
import jef.core.movement.index.LocationIndex;

public class Players
{
	private Map<Player, PlayerSteps> steps = new HashMap<>();
	private Map<Player, Pathfinder> pathfinders = new HashMap<>();
	private int stepCapacity;
	private final LocationIndex index;

	public Players(int stepCapacity, double timerInterval)
	{
		this.stepCapacity = stepCapacity;
		index = new DefaultLocationIndex(timerInterval, stepCapacity);
	}

	public void addPlayer(PlayerState state)
	{
		steps.put(state.getPlayer(), new PlayerSteps(state, this.stepCapacity));
	}

	public void setPathfinder(Pathfinder pathfinder)
	{
		pathfinders.put(pathfinder.getPlayerState().getPlayer(), pathfinder);
	}

	public PlayerSteps getSteps(Player player)
	{
		return steps.get(player);
	}

	public Pathfinder getPathfinder(Player player)
	{
		return this.pathfinders.get(player);
	}

	public RunnerPathfinder getRunner()
	{
		return pathfinders.values().stream().filter(rpf -> rpf instanceof RunnerPathfinder)
				.map(rpf -> (RunnerPathfinder) rpf).findFirst().orElse(null);
	}

	public List<DefenderPathfinder> getDefenders()
	{
		return pathfinders.values().stream().filter(DefenderPathfinder.class::isInstance)
				.map(dpf -> (DefenderPathfinder) dpf).toList();
	}

	public List<BlockerPathfinder> getBlockers()
	{
		return pathfinders.values().stream().filter(BlockerPathfinder.class::isInstance)
				.map(bpf -> (BlockerPathfinder) bpf).toList();
	}
	
	public void process()
	{
		
	}
}
